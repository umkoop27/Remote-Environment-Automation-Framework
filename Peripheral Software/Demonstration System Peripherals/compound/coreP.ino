/*************
  This file contains functions required by the peripheral
  core. This file should not be changed when creating custom
  peripherals.
  
  Author: Valerie Beynon
  Date: February 2014

*/


/******************** sleep

Purpose: This function is called by the main loop and is used to sleep
the cpu in power down mode. The tranciever oscilator remains on so that
the board is still able to receive messages. The processor is woken when
the transceiver receives a message.

*/
void sleep(){
    
  set_sleep_mode(SLEEP_MODE_PWR_DOWN);
  sleep_enable();
  sleep_cpu();
  sleep_disable();

}

/*****************safeDelay

This delay function is to be used by developers when
needing to delay in their custom functions to allow the cpu to
be interrupted every half second to to check for new RF messages
and allow acknowledgements to be sent in a timely manner.
This function should be used when TASK is defined instead of the
standard arduino delay() command.

Parameter: wait, the length of the delay in milliseconds

*/

void safeDelay(unsigned long wait)
{
    safe=1;
    TIMSK2 = 1<<TOIE2;
    delay(wait);
    TIMSK2 = 0<<TOIE2;
    safe=0;
}

/********* TC2 overflow IRQ service routine

This interupt service routine is used in monitoring applications when
TIME_SLEEP is defined, which uses timeSleep(), and the cpu is woken from sleep by the 
timer. This function disables sleep so that the peripherals continues through the main
loop before returning to sleep.

This ISR is also used when TASK is defined and safeDelay() is used to allow
for the cpu to be interrupted during long running tasks to handle wireless
packets. That is why receivePacket() is called within this function to send
any acknowledgements even if the cpu is in the middle of another task
*/

ISR( TIMER2_OVF_vect ) { 
  
  //Serial.println("interrupt");
  //delay(15);
  unsigned char sreg = SREG;
  cli();
  sleep_disable();
  receivePacket();
  SREG=sreg;
  if(safe) //ISR was called from safe delay not from sleep
    TIMSK2 = 1<<TOIE2; //turn the interrupt back on
}

/******* timerSleep

This function is called from the main function instead of sleep()
when TIME_SLEEP is defined. This function is woken by either receiving
a message on the transceiver or when the TC2 timer expires. When the timer
expires the ISR runs. This is used for peripherals that need to monitor
inputs periodically without being told to do so by the base station.

*/

void timerSleep(){
  Serial.println("sleep");
  delay(15);
  ASSR = 1<<AS2; // turn on 32kHz crystal oscillator
  TIMSK2 = 1<<TOIE2; // enable TC2 overflow interrupt
  TCCR2B = 0x04; // .5 second interrupts
  
  
  set_sleep_mode(SLEEP_MODE_PWR_SAVE);
  sleep_enable();
  sleep_cpu();
  sleep_disable();
  Serial.println("wakeup");
  delay(15);
}


/***************** joinNetwork

This function will be called from the main loop when the device 
does not have a network ID, indicating that it has not joined a network.

Return: An integer that is 1 if the device joined successfully and 0 if the
        device was unable to join.
*/

int joinNetwork(){
	int success = 0;
	int timer=0;
	int counter1=0; //counts waiting for the nexus auth query
        int counter1_2 = 0;
        int counter2=0;
        int counter2_2 = 0;
	int done =0;
	int currPos =0;
	uint8_t * currPkt;
	uint32_t pktLen;
        uint8_t * data;
        uint8_t dataLen;
        uint8_t * pkt;
        uint16_t id;
        uint8_t zero =0;
        long tCodeResponseTimer;
        uint8_t waitingOnAck=0;
        uint8_t* block;

	while (!done){
		switch(joinNtwkState){
			
			case WAIT_FOR_NEXUS:
				currPkt = readPktRF();
        
			       if(currPkt!=NULL && testIfNexusNewBroadcast(currPkt)){
					joinNtwkState = GET_EXT_AUTH;
                                        Serial.println("wait for auth");
                                        free(currPkt);
                                }
                                
                                else if (currPkt!=NULL)
                                   free(currPkt);
			
			    break;
                        
                        case GET_EXT_AUTH: 
                            #ifndef AUTO_JOIN
                            getExtAuth();
                            #endif
                            joinNtwkState = SEND_TYPE_CODE;
                            break;
				
			case SEND_TYPE_CODE:
                                //Serial.println("sending type code");
                                dataLen = 4;
                                data = (uint8_t*)malloc(dataLen);
                                data[0] = (uint8_t)(typeCode>>24);
                                data[1] = (uint8_t)(typeCode>>16);
                                data[2] = (uint8_t)(typeCode>>8);
                                data[3] = (uint8_t)(typeCode);
                                
                                pkt = createPkt('N', nexusID, zero, zero,(uint8_t *)"TC", dataLen, data);
                                free(data);
                                block = encrypt(pkt, HEADER_LEN+dataLen);
                                free(pkt);
                                Serial.println("\n**********SENDING***********");
                                RF.beginTransmission();
                                for (uint16_t i=0; i<BLOCK_SIZE; i++){
                                  RF.write(block[i]);
                                  Serial.println(block[i]);
                                }
                                RF.endTransmission();
   
                                Serial.println("****************************");
                               // for(int i=0; i<HEADER_LEN+dataLen; i++)
                                //  Serial.println(pkt[i]);
                                lastSent=0; //this packet does not count as one because the peripheral has not joined the network
                                tCodeResponseTimer=millis();
                                
                                joinNtwkState = WAIT_FOR_ID_FROM_NEXUS;
                                counter1++;
                                break;
                         
                         case WAIT_FOR_ID_FROM_NEXUS:
                            currPkt = readPktRF();
                               //Serial.println("waiting for id");

                               if(currPkt!=NULL) {
                                  id = testIfID(currPkt);
			          if(id){
                                    ntwkID = id;
                                    joinNtwkState = EXIT;
                                    Serial.println("state = EXIT");
                                    success = 1;
                                    sendACK();
                                  }
                                  free(currPkt);     
                                }
                                else { // (currPkt==NULL)
                                  if ((millis()-tCodeResponseTimer)>RESPONSE_LIMIT){
                                    if(counter1<= RESEND_LIMIT){
                                      joinNtwkState = SEND_TYPE_CODE; //resend type code
                                    }
                                    else{
                                      Serial.println("resend tcode limit reached");
                                      joinNtwkState = WAIT_FOR_NEXUS;
                                      counter1 =0;
                                    }
                                  }
                                }
                            break;
  
                   case EXIT:
                     done = 1;
                     lastSentTimer=0;
		}
	
	}
    return success;
}
/************ testIfNexusNewBroadcast
	Testing if the received message is a broadcast for new peripherals
	
	To be a Nexus Broadcast it must be sent to a peripheral, the target ID is 0xFF,
	data should be "NP" for "new peripheral"

        Parameter: A pointer to the packet to check
	
	Return: 1 if it is a nexus broadcast and 0 if it is not
	
*/

int testIfNexusNewBroadcast(uint8_t * pkt){
	int result = 0;
	if(pkt[DEST_TYPE] == 'P' && pkt[DEST_ID]==0xFF && pkt[PKT_TYPE_HB]=='N' && pkt[PKT_TYPE_LB]=='P'){
	  result = 1;
	  nexusID = pkt[SOURCE_ID];
          Serial.println("Received NP Broadcast");
	}
	return result;
}

/****************testIfID
  This function tests if a packet is an ID packet.
  
  Parameter: a pointer to the packet to check
  
  Return: 0 if the packet is not an ID packet and the assigned 8 bit ID if it is
*/

uint16_t testIfID(uint8_t * pkt){
  
  
  uint32_t tCode;
  uint16_t id=0;
  
  if(pkt[DEST_TYPE]=='P'&& pkt[PKT_NUM]==1 &&  pkt[SOURCE_ID]==nexusID && pkt[PKT_TYPE_HB]=='I' && pkt[PKT_TYPE_LB]=='D' && pkt[DATA_LEN]==4)
  {
                tCode = (pkt[DATA]<<24) + (pkt[DATA+1]<<16) + (pkt[DATA+2]<<8) + pkt[DATA+3];
                if(tCode==typeCode){
                   id = pkt[DEST_ID];
                   lastRcvd=pkt[PKT_NUM];
                   Serial.print("Received ID: ");
                   Serial.print(id);
                   Serial.print(" in packet number: ");
                   Serial.println(lastRcvd);
                   delay(500);
                }        
  }
  return id;
}

/***************** reset

  This function can be called if the device needs to be removed from a network.
  In the current code this is not called, however if a developer wishes to create
  a function to do so they may call this function. All networking variables are
  reset to the initial values to be used if the peripheral joins a new network
  and the network ID is set to 0 to indicate that it is no longer part of a network.

*/

void reset(){
  ntwkID=0; //no longer on a network
  lastSent = 0;
  lastRcvd = 0;
  prevPkt = NULL;
  lastSentTimer;
  buffer[MAX_BUFFERED];
  buffStart=0;
  buffEnd=0;//points to next open space in the buffer  
}

/************ sendResult
  This function sends a results packet back to the base station.
  This will usually include any sensor readings that were taken.
  
  Parameters:
    data - a packet containg the data to be forwarded to the base
           station computer
    dataLen - the length of the data
    fcn - the function code of the function that was called by the 
          base station requesting the data
*/

void sendResult(const uint8_t* data, uint8_t dataLen, uint8_t fcn){
  #ifdef TIME_SLEEP
  TIMSK2 = 0<<TOIE2; // disable TC2 overflow interrupt
  #endif

  uint8_t pktData [dataLen+1];
  uint8_t* pkt;
  uint8_t pktNum = lastSent+1;
  
  pktData[0] = fcn;
  for(int i=0; i<dataLen; i++){
    pktData[i+1] = data[i];
  }
   pkt = createPkt('N', nexusID, ntwkID, pktNum, (uint8_t *)"RT", dataLen+1, pktData);
   sendPacket(pkt, HEADER_LEN+dataLen+1);
}

/******************** receivePacket
   
  A buffer is used to store instruction packets that are received while it is waiting on
  and ACK. When not waiting on an ACK the buffer is checked first before the RF, because
  it contains any previously received instruction packets.

  This function receives a packet and checks that it is addressed to this peripheral.  
  It then checks if it is an ACK. If it is waiting on an ACK and it is the correct
  ACK, it stops its timer, and if it is an incorrect ACK it resends the previous packet.
  If the packet is not an ACK and it is waiting on an ACK then it buffers the packet and
  checks its timer and resends the previous packet if the timer expired. If not waiting on
  an ACK and the packet is an instruction packet, it is made available to the main loop.
  
  This repeats until an instruction packet is received or there are no more messages
  available.
  

*/

void receivePacket(){
 #ifdef TIME_SLEEP
 TIMSK2 = 0<<TOIE2; // disable TC2 overflow interrupt
#endif

 #ifdef TASK
 TIMSK2 = 0<<TOIE2; // disable TC2 overflow interrupt
#endif

 uint8_t pos=0;
 uint8_t * pkt;
  
do{
  
  if(!lastSentTimer && buffStart!=buffEnd){ //not waiting on an ack and pkt is buffered
  //Serial.print("buffer");
    pkt = buffer[buffStart];
    buffStart=(buffStart+1)%MAX_BUFFERED;
  }
  else //waiting on ack xor nothing buffered
    pkt= readPktRF();
    
 if(pkt!=NULL&& pkt[DEST_TYPE]=='P' && pkt[DEST_ID]==ntwkID &&  pkt[SOURCE_ID]==nexusID)
 {
   
    if(checkIfACK(pkt)){
      if(lastSentTimer!=0){ //waiting on an ack
       if(pkt[PKT_NUM]==(lastSent)){ //it is the correct ack
        free(prevPkt); 
        stopTimer();
        free(pkt);
       }
      else{ // the Ack number was incorrect
        resend();
        free(pkt);
      }
      }
    }
    
    else if (lastSentTimer!=0){
       
       if((buffEnd+1)%MAX_BUFFERED!=buffStart){ //still room in the array
         buffer[buffEnd]=pkt;
         buffEnd=(buffEnd+1)%MAX_BUFFERED; 
       } //else the packet is lost once the max number buffered is reached
       
       if ((millis()-lastSentTimer)>RESEND_TIMER){
         resend();
       }
       
    }
    
    else if(lastSentTimer==0 && checkIfInst(pkt)){ //not waiting on an ack and recieved instructions    
        instructionPkt = pkt;
       // Serial.println("INSTRUCTIONS");
  }  
   
 }
} while((pkt!=NULL) && (instructionPkt==NULL)); 

}

/********* checkIfInst
  This function checks if the given packet is an instruction packet. The packet type field
  should contain "IN" and it checks that the packet number is the one it is expecting. If these
  are correct then an ACK for the packet is sent, else the previous ACK is resent.
  
  Parameters: A pointer to the packet to check
  
  Return: 1 if the packet is an instruction packet with the correct packet number else
          0 is returned.
  
  */


int checkIfInst(uint8_t *pkt){
  int result=0;
  
  if(pkt[PKT_TYPE_HB]=='I' && pkt[PKT_TYPE_LB] =='N' && pkt[PKT_NUM]==(uint8_t)(lastRcvd+1)){
      lastRcvd++;
      sendACK();
      result=1; 
    }
    else{
      sendACK();
    }        
  
  return result;
}

/**************** checkIfAck
  This function tests if the packet is an acknowledgement packet, as
  indicated by the Packet Type field.
  
  Parameter: a pointer to the packet to test
  
  Return: 1 if the packet is and ACK else returns 0

*/

int checkIfACK(uint8_t *pkt){
  int result=0;
  
  if(pkt[PKT_TYPE_HB]=='A' && pkt[PKT_TYPE_LB]=='K')
    result=1;
    
  return result;
}

/************ getExtAuth
  This method is called from the joinNetwork() function and
  waits for the push button to be pressed. It only returns once the
  button is pressed indicating the user has given external authentication.

*/

void getExtAuth(){
  Serial.println("checking push button");
  uint8_t auth=1; //set to zero when the button is pressed
  pinMode(PUSH_BUTTON, INPUT);
  while(auth){
    auth = digitalRead(PUSH_BUTTON);
  }
}


