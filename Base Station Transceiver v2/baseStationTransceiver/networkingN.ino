/*********
  This file contains the functions used for communicating wirelessly with the peripherals.
  
  Author: Valerie Beynon
  Date: February 2014
  
 */


  uint8_t aesKey[16] = {0x99,0xCA,0x56,0xBD,0xF6,0x26,0x38,0x56,0x7F,0x45,0x69,0x41,0x65,0x02,0x37,0xAB};
  uint8_t decryptKey[BLOCK_SIZE];


/**************** createPkt
  This fucntion creates a properly formatted packet for sending data wirelessly to a peripheral.
  
  parameters: destType - destination type, should always be 'P' for peripheral
              targetID - the ID of the peripheral the packet is for
              sourceID - the base station's ID
              pktNum - the next packet number in the communication from the base station to this peripheral
              dataType - a 2 byte string containing the type of data being sent
              dataLen - the number of bytes in the data field
              data - a pointer to the data to be sent
              
  return: a pointer to the created packet

*/

uint8_t * createPkt (uint8_t destType, uint8_t targetID, uint8_t sourceID,
					uint8_t pktNum, uint8_t* dataType, uint8_t dataLen, uint8_t * data){
	
	uint16_t length = HEADER_LEN + dataLen;
	uint8_t * packet = (uint8_t *)malloc(length);
	
	packet[DEST_TYPE] = destType;
	packet[DEST_ID] = targetID;
	packet[SOURCE_ID] = sourceID;
	packet[PKT_NUM] = pktNum;
        packet[PKT_TYPE_HB] = dataType[0];
        packet[PKT_TYPE_LB] = dataType[1];
        packet[DATA_LEN] = dataLen;
        	
	for (uint16_t i=0; i<dataLen; i++)
		packet[DATA + i] = data[i];
		
  return packet;
}  

/************** sendPacket
  
  This function transmits a wireless packet.The packet is encrypted, transmitted, and 
  networking variables are updated and the retransmisson timer is started. This function
  should only be called if the base station is not waiting on an ACK.
  
  parameters: pkt - a pointer to the packet to transmit
              length - the length of the packet

*/

void sendPacket(uint8_t * pkt, uint16_t length){ //only called if the previous pkt sent has been ack'd
   
   uint8_t * toSend = encrypt(pkt, length);
  
   RF.beginTransmission();
   for (uint8_t i=0; i<BLOCK_SIZE; i++){
      RF.write(toSend[i]);
   }
   RF.endTransmission();
   free(toSend);
   toSend = NULL;
   
   startTimer(); //starts the retransmission timer
   
   lastSentPkt[pkt[DEST_ID]] = pkt;
   seqNum[pkt[DEST_ID]][LAST_TX] = pkt[PKT_NUM];
   
   waitingOnAck=1;
   expAck = pkt[PKT_NUM];
   expAckID = pkt[DEST_ID];
 
}

/***************** sendACK

  This function sends an acknowledgement packet. If the packet number is correct then the packet is ack'd,
  but if the packet number is not the number that is expected for that id the perviously received packet from
  that ID is ack'd.
  
  parameter: a pointer to a results packet received from a peripheral
  return: 1 if the packet was the next expected packet, 0 if the packet did not have the correct number
*/

uint8_t sendACK(uint8_t* pkt){
  uint8_t pktNum;
  uint8_t id = pkt[SOURCE_ID];
  uint8_t* AckPkt;
  uint8_t result = 0;
  uint8_t* toSend;
  
  if(pkt[PKT_NUM]==(uint8_t)(seqNum[id][LAST_RX]+1)){
    pktNum = pkt[PKT_NUM];
    result=1;
  }
  else
    pktNum = seqNum[id][LAST_RX];
  
   AckPkt= createPkt('P', id, nexusID, pktNum, (uint8_t *)"AK", 0, (uint8_t*)"");
   
   toSend = encrypt(AckPkt, HEADER_LEN+AckPkt[DATA_LEN]);
   RF.beginTransmission();
     for (uint16_t i=0; i<BLOCK_SIZE; i++){
        RF.write(toSend[i]);
   }
   RF.endTransmission();
   free(toSend);
   toSend = NULL;

  seqNum[id][LAST_RX] = pktNum;
  
  free(AckPkt);
  AckPkt = NULL;
  
  return result;
}

/************** checkIfACK
  This function checks if a packet is an acknowledgement as indicated by a packet type "AK"
  
  parameter: a pointer to the packet to check
  result: 1 if the packet is an ACK, else 0 is returned
*/

int checkIfACK(uint8_t *pkt){
  int result=0;
  if(pkt[PKT_TYPE_HB]=='A' && pkt[PKT_TYPE_LB]=='K')
    result=1;
  
  return result;
}

/*****startTimer
  Starts the resend timer.
*/
void startTimer(){
 timer = millis(); 
  
}

/*****stopTimer
  Stops the resend timer.
*/

void stopTimer(){
  timer=0;
  
}

/*************resend
  This function retransmits the previous packet sent to the given peripheral.
  
  parameter: the id of the peripheral who is not ACKing and whose packet needs to be retransmitted
  */
  

void resend(uint8_t id){
  
 uint8_t* toSend; 
 if (ackCounter[id]<RETRY_MAX){ 
   ackCounter[id]++;
   sendPacket(lastSentPkt[id], lastSentPkt[id][DATA_LEN] + HEADER_LEN);
   timer=millis();
 }
 
 else{

   toSend=createPktForNexus('X', id, 0,NULL);

   Serial.write(toSend, NEXUS_HEADER_LEN+toSend[DATA_LEN_N]);

   free(toSend);
   toSend = NULL;
   timer=0;
   waitingOnAck=0;
   seqNum[id][LAST_TX]--; //the last one was unsuccessfully but it may respond again later
   ackCounter[id]=0;
 }
}

/*********** readPktRF

  This function reads a packet from the tranceiver's Rx buffer and decodes it.
  
  return: a pointer to the next decoded packet, or NULL if there are no packets.

*/

uint8_t * readPktRF(){
  uint8_t pkt [BLOCK_SIZE];
  uint8_t * decoded;
  
  if(RF.available()){
    for(int i=0; i<BLOCK_SIZE; i++){
      while(!RF.available());
      pkt[i]=RF.read();
    }
    decoded=decode(pkt);
 }
   
   else
     decoded=NULL;
   
  return decoded;
}

/******** calcDecryptionKey
  This function calculates the decryption key by encrypting a dummy block of
  zeros and reading the resulting 128 bit value from the AES_KEY register.
  This value is stored in the global variable decryptKey.

*/

void calcDecryptionKey(){
  
  uint8_t pkt[16] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
  uint8_t cipher[BLOCK_SIZE];
  
  for(int i=0; i<BLOCK_SIZE; i++)
    AES_KEY = aesKey[i];
  
  AES_CTRL &=~(1<<AES_MODE); //ECB mode
  AES_CTRL&=~(1 << AES_DIR); //encryption mode
   for (int i=0; i<BLOCK_SIZE; i++){
    AES_STATE = pkt[i]; 
   }
  
  AES_CTRL|=(1<<AES_REQUEST);
  delay(1);
  
  for(int i=0; i<BLOCK_SIZE; i++)
    cipher[i] = AES_STATE;
  for(int i=0; i<BLOCK_SIZE; i++){
     decryptKey[i] = AES_KEY;
  }
}

/******** encrypt

  This function encrypts a given packet using the ECB mode of the security module on
  the ATmegaRF128a1 microcontroller.In the current version of this code the packet
  size is limited to the block size of 16 bytes. All packets shorter than 16 bytes are
  padded on the end with zeros.
  
  Parameters: pkt, the packet to encrypt
              length, the length of the packet
              
  Return: a pointer to the 16 byte encrypted packet
*/

uint8_t * encrypt (uint8_t * pkt, uint16_t length){
  uint8_t * cipher = (uint8_t * )malloc(16);
   
   
  for(int i=0; i<BLOCK_SIZE; i++)
    AES_KEY = aesKey[i];
  
  AES_CTRL &=~( 1<<AES_MODE);
  AES_CTRL &= ~(1 << AES_DIR); //encryption
  
   for (int i=0; i<length; i++){
    AES_STATE = pkt[i]; 
   }
  
  for(int i=0; i<BLOCK_SIZE-length; i++) //need to write 16 bytes
   AES_STATE = 0x00; //zero padding
  
  AES_CTRL|=(1<<AES_REQUEST);
  delay(1);
  for(int i=0; i<BLOCK_SIZE; i++){
    cipher[i] = AES_STATE;
  }
  return cipher;
}

/*************** decode

  This function decodes a 16 byte packet using the decryptKey previously calculated.
  
  Parameter: A 16 byte packet to decode
  Return: A pointer to the decoded packet
  
*/

uint8_t * decode (uint8_t * pkt){
  uint8_t * msg = (uint8_t * )malloc(16);
   
  for(int i=0; i<BLOCK_SIZE; i++)
    AES_KEY = decryptKey[i];
  
  AES_CTRL &=~( 1<<AES_MODE);
  AES_CTRL |= (1 << AES_DIR); //decryption
  
   for (int i=0; i<BLOCK_SIZE; i++){
    AES_STATE = pkt[i]; 
   }
  
  AES_CTRL|=(1<<AES_REQUEST);
  delay(1);
  
  for(int i=0; i<BLOCK_SIZE; i++){
    msg[i] = AES_STATE;
  }
  return msg;
}
