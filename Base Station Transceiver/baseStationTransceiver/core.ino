/***************
  This file contains the core functions used by the base station transceiver.
  
  Author: Valerie Beynon
  Date: February 2014

*/

/********* addNewPeripheral

  This function is called from checkSerial() when a command to search for new peripherals
  is received. This function searches for new peripherals and if one is found it is assigned
  an ID and its ID and type code is forwarded to the base station computer.
  
  Return: 1 if a new peripheral is successfully added, 0 if a peripheral is not added

*/

int addNewPeripheral(){
 int success = 0;
 int result;
 int done=0;
 int counter=0;
 joinState = NEW_PERIPH_BROADCAST;
 
 while(!done){
   switch(joinState){
    case NEW_PERIPH_BROADCAST:
         newPeriphBroadcast();
         searching=1;
         joinState = WAIT_FOR_REPLY;
         waitingOnTCode = 1;
        break;
    
    case WAIT_FOR_REPLY:
         waitForReply();
        if(!waitingOnTCode){ //type code was received
          joinState = SEND_TO_JAVA;
        }
        else if(!searching){
          joinState=EXIT;
        }
        else //send the search broadcast again
         joinState = NEW_PERIPH_BROADCAST;
        break;
     
     case SEND_TO_JAVA: //send info to base station computer
       nextAvailID = getNextAvailID();
       sendNexusNewPeriphInfo();
       joinState = SEND_PERIPH_ID;
       break; 
        
     case SEND_PERIPH_ID:
       sendPeriphID();
       success = 1;
       joinState = EXIT;
       break;
       
     case EXIT:
       waitingOnTCode=0; //no longer waiting on a type code
       searching=0;
       done=1; 
   }
 }
 return success;
}
  

/************ newPeriphBroadcast
  This fucntion sends out the new peripheral search broadcast as indicated by a packet type
  of "NP" and a destination address of 0xFF.

*/

void newPeriphBroadcast(){
	uint8_t * pkt;
        uint8_t * toSend;
	uint8_t msg[2] = {'N','P'};
        uint8_t dataLen = 0;
	
	pkt = createPkt('P', 0xFF, nexusID, 0, msg, dataLen, (uint8_t*)"");

	toSend = encrypt(pkt, HEADER_LEN+dataLen);
	
	RF.beginTransmission();
        
        for (uint16_t i=0; i<BLOCK_SIZE; i++){
          RF.write(toSend[i]);
        }
	RF.endTransmission();
        free(toSend);
        free(pkt);
}

/************sendPeriphID
  This function creates and then sends a peripheral ID packet. The ID it is assigning
  is in the destination ID, the packet type is "ID" and the type code of the peripheral
  is included in the data so that the peripheral can verify that the packet is for them.
  
  This is always packet number 1 because it will be the first real transmission (not broadcast)
  and the correct ACK and retransmission protocol can begin.

*/

void sendPeriphID(){
  
  uint8_t dataLen = 4;
  uint8_t * pkt;
  uint8_t* data = (uint8_t*)malloc(dataLen);

  data[0] = (uint8_t)(newPeriphTCode>>24);
  data[1] = (uint8_t)(newPeriphTCode>>16);
  data[2] = (uint8_t)(newPeriphTCode>>8);
  data[3] = (uint8_t)(newPeriphTCode);
       
  pkt = createPkt('P', nextAvailID, nexusID, 1, (uint8_t *)"ID", dataLen, data);
  free(data);
  idTaken[nextAvailID]=1;
  sendPacket(pkt, HEADER_LEN+dataLen);
}

/******** sendNexusNewPeriphInfo

  This function creates and writes to serial the packet containing the information of a new
  peripheral that has been added.

*/


void sendNexusNewPeriphInfo(){
  uint8_t data[4]={(uint8_t)(newPeriphTCode>>24), (uint8_t)(newPeriphTCode>>16), (uint8_t)(newPeriphTCode>>8), (uint8_t)newPeriphTCode};
  uint8_t * toSend = createPktForNexus('P', nextAvailID, 4, data);
  Serial.write(toSend, NEXUS_HEADER_LEN+toSend[DATA_LEN_N]);
  free(toSend);
}

/************ createPktForNexus

  This function creates a packet structured correctly for being send to the base station computer.
  
  Parameters: pktType - one char indicating the contents of the packet
              periphId - the ID of the peripheral the data is coming from
              dataLen - the length of the data field in bytes
              data - a pointer to the data to be sent
              
  Return: a pointer to the created packet

*/

uint8_t * createPktForNexus(uint8_t pktType, uint8_t periphId, 
                            uint8_t dataLen, uint8_t * data)
{
  uint8_t length = NEXUS_HEADER_LEN + dataLen;
  uint8_t * pkt =(uint8_t*) malloc(length);
  pkt[PKT_TYPE]=pktType;
  pkt[PERIPH_ID]=periphId;
  pkt[DATA_LEN_N] = dataLen;
  
  for (int i=0; i<dataLen; i++){
    pkt[DATA_N+i]=data[i];
  }
  return pkt;
}


/************ getNextAvailID

  This function finds the next available ID. Zero is not assigned as an ID because
  when devices do not yet have an id they are ID 0.
  
  Return: the next available ID or 0 if there are no more IDs available.
  
*/

uint8_t getNextAvailID()
{
  int done=0;
  uint8_t start=nextAvailID+1; //nextAvail contains the previously given ID
  uint8_t i;
  uint8_t nextAvail;
  
  if (start<=MAX_ID)
    i=start;
  else
    i=1; //no one is assigned an id of 0  
  
  do {
    if(idTaken[i]==0){ //the id is available
      done = 1;
      nextAvail = i;
    }
    else{
      i++;
      if(i>MAX_ID){
        i=1; //zero is not as an id assigned to a peripheral
      }
    }
  } while(!done && i!=start);
  
  return nextAvail;
}


/*********** waitForReply
  This function is called when adding new devices and is waiting for a response from
  a new peripheral.This function switches between checking the RF for a response and 
  checking the Serial for a stop searching message from the base station computer. It
  returns after a set amount of time if it is unsuccessful so that the broadcast can
  be sent again.

*/

void waitForReply(){
  long timer2 = millis();
  long serialTimer;
  int stopWaiting=0;

  while(waitingOnTCode && searching &&((millis()-timer2) < T_CODE_TIMEOUT)){
    serialTimer=millis();
    while (waitingOnTCode && millis()-serialTimer<CHECK_FOR_STOP_TIMER){
      receivePacket();
    }
    checkSerial(); // if recieves stop searching pkt will set searching to 0
  }
}

/************ testIfTypeCode

  This function tests if a packet is a type code packet addressed to the base station with
  packet type "TC".
  
  Parameter: a pointer to the packet to check
  Return: the 4 byte type code or 0 if the packet is not type code packet

*/

uint32_t testIfTypeCode (uint8_t * pkt){
  uint32_t typeCode = 0;
  
  if(pkt[DEST_TYPE]=='N' && pkt[DEST_ID]==nexusID && pkt[PKT_TYPE_HB]=='T' && pkt[PKT_TYPE_LB]=='C' && pkt[DATA_LEN]==4){
                typeCode = (pkt[DATA]<<24) + (pkt[DATA+1]<<16) + (pkt[DATA+2]<<8) + pkt[DATA+3];
  }
  
  return typeCode;
}


/******************* receivePacket
  This function handles packets that were received wirelessly. This function continues to receive 
  and handle packets until no more packets are available, except in the case it is waiting on a
  type code and the type code is received, then it returns immediately.
  
  
*/

void receivePacket(){
 uint8_t pos=0; 
 uint8_t result=0;
 uint8_t* pkt;
 uint8_t sourceID;
 uint32_t typeCode;
 uint8_t TCode = 0;
 
 do{
 
 pkt=readPktRF();

 if(pkt!=NULL && pkt[DEST_TYPE]=='N' && pkt[DEST_ID]==nexusID)
 {
   result=1;
   sourceID = pkt[SOURCE_ID];
   if(idTaken[sourceID]||sourceID==0){ //check if it is an assigned id or 0 if it is a type code
    if(sourceID && checkIfACK(pkt)){

        if(waitingOnAck && sourceID==expAckID && pkt[PKT_NUM]==expAck){ //it is the ack the nexus is waiting on
          waitingOnAck=0; //the nexus is no longer waiting on an ack
          free(lastSentPkt[sourceID]); 
          stopTimer();
          ackCounter[sourceID]=0;
          commEst[sourceID]=1;
        }
    }
    else if(sourceID && checkIfResults(pkt)){  
        if(sendACK(pkt)); //sendAck returns 1 if it was the expected packet
          forwardResults(pkt);   
    }   
   
   else if(waitingOnTCode&& !sourceID){ 
      typeCode = testIfTypeCode(pkt);
      if (typeCode){
        newPeriphTCode = typeCode;
        waitingOnTCode =0;
        TCode =1;
      }
   }
    //else the pkt is ignored   
 }
 }
  free(pkt); 
 }while(!TCode && RF.available());

}

/********** forwardResults

  This function is called when a results packet is received from a peripheral. The data
  is then forwarded to the base station computer on the serial port.
  
  parameter: a pointer to the wireless packet received from the peripheral
 */

void forwardResults(uint8_t* pkt){
  uint8_t* nexusPkt;
  uint8_t length = pkt[DATA_LEN];
  
  nexusPkt = createPktForNexus('E', pkt[SOURCE_ID], length, &pkt[DATA]);
  Serial.write(nexusPkt, NEXUS_HEADER_LEN+nexusPkt[DATA_LEN_N]);
  free(nexusPkt);
}

/********** checkIfResults
  This function checks if a wireless packet is a results packet idicated by a packet type of "RT".
  
  parameter: a pointer to the packet to test
  
*/

int checkIfResults( uint8_t* pkt){
  int result=0;
  
  if(pkt[DEST_TYPE]=='N' && pkt[DEST_ID]==nexusID && pkt[PKT_TYPE_HB]=='R' && pkt[PKT_TYPE_LB]=='T' && pkt[DATA_LEN]){
      result =1;
  }
  return result;
}

/**********sendInstructions

  This packet creates and sends an instruction packet to a peripheral.
  
  parameter: id - the id of the peripheral the instructions are for
             dataLen - the length of the data
             data - a pointer to the data to be sent
  
*/

void sendInstructions(uint8_t id, uint8_t dataLen, uint8_t* data ){
  uint8_t* pkt;
  pkt = createPkt('P', id, nexusID, seqNum[id][LAST_TX]+1, (uint8_t *)"IN", dataLen, data);
  sendPacket(pkt, HEADER_LEN+dataLen); 
}

/*********** checkSerial
  This function checks the serail port and handles any packets received from the base station 
  computer. 
  
*/
  
void checkSerial(){
 uint8_t header[NEXUS_HEADER_LEN];
 uint8_t*data;
 
 
 if (Serial.available())
 {
   for(int i=0; i<NEXUS_HEADER_LEN; i++){
     while(!Serial.available());
     header[i]=Serial.read(); 
   }
   
   int dataLength = header[DATA_LEN_N];
   
   data = (uint8_t *)malloc(dataLength);
   
   
   for(int i=0; i<dataLength; i++){
    while(!Serial.available());
    data[i]=Serial.read(); 
   }
 
   if(header[PKT_TYPE]=='N' && header[PERIPH_ID]==0xFF){ //add new peripheral command
     addNewPeripheral();
   }
     
   else if(header[PKT_TYPE]=='T') //transmit data command
     sendInstructions(header[PERIPH_ID], header[DATA_LEN_N], data); 
   
   else if( header[PKT_TYPE] =='R') //remove peripheral command
     removePeriph(header[PERIPH_ID]);
    
   else if(searching && header[PKT_TYPE]=='S') //stop searching command
   {
     searching=0;
   }
 }
}

/************ removePeriph

  This function removes a peripheral from its data base and resets variables so that the
  ID number can be used by another peripheral. It does not however inform the peripheral that 
  it has been removed. This is under the assumption that a user who removes a peripheral will 
  also be unplugging or reseting the peripheral.
  
  parameter: the id of the peripheral to be removed

*/

void removePeriph(uint8_t id){
  //reset everything so it can be used by another peripheral later
  seqNum[id][LAST_TX]=0; 
  seqNum[id][LAST_RX]=0;
  free(lastSentPkt[id]);
  ackCounter[id]=0;
  commEst[id]=0;
  idTaken[id]=0;
}
