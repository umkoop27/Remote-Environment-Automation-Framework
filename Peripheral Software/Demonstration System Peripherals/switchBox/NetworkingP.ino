/************
This file contains the functions used by the peripheral core
for sending wireless transmissions. This file should not be
changed when creating custom peripherals.

Author: Valerie Beynon
Date: February 2014


*/


  uint8_t aesKey[16] = {0x99,0xCA,0x56,0xBD,0xF6,0x26,0x38,0x56,0x7F,0x45,0x69,0x41,0x65,0x02,0x37,0xAB};
  uint8_t decryptKey[BLOCK_SIZE];
  
  
/************resend
  
  This method resends the previous packet that was not an ACK.

*/


void resend(){
  uint16_t length = prevPkt[DATA_LEN]+HEADER_LEN;
  sendPacket(prevPkt, length);
  startTimer();

}

/************ sendACK
  This function sends an acknowledgement for the last recieved packet. The packet is 
  created and then encrypted before it is sent wirelessly.
*/

void sendACK(){ //acknowledges the last received pkt
  uint8_t* origPkt = createPkt('N', nexusID, ntwkID, lastRcvd, (uint8_t *)"AK", 0, (uint8_t*)"");
  uint8_t* pkt;
  
  pkt = encrypt(origPkt, HEADER_LEN+origPkt[DATA_LEN]);
  RF.beginTransmission();
   for (uint16_t i=0; i<BLOCK_SIZE; i++){
      RF.write(pkt[i]);
   }
   RF.endTransmission();
   free(pkt);
   free(origPkt);

}

/**************** readPktRF
  This function reads a packet from the transceiver's receive buffer.
  It reads in a 16 byte block and then decrypts it before returning it.
  
  Return: a pointer to the next decrypted packet that was received by 
          the transceiver or NULL if no packets are available
*/

uint8_t * readPktRF(){
  uint8_t pkt[BLOCK_SIZE]; 
  uint8_t * decoded;
  
  if(RF.available()){
    for(int i=0; i<BLOCK_SIZE; i++){
      while(!RF.available());
      pkt[i]=RF.read();
    }
    decoded = decode(pkt); //returns the decrypted packet
   }
   else
     decoded=NULL;
   
  
  return decoded;
}

/***** stopTimer
  Stops the ACK timer.
*/

void stopTimer(){
 lastSentTimer=0; 
}

void startTimer(){
  lastSentTimer=millis();
}

/**********sendPacket
    Sends a wireless packet by first encrypting it and then transmitting it.
    

      parameters: origPkt, a pointer to the packet to transmit
                  length, the length of the packet in bytes
      
      
*/

void sendPacket(uint8_t * origPkt, uint16_t length){
  uint8_t * pkt = encrypt(origPkt, length);

   RF.beginTransmission();
   for (uint16_t i=0; i<BLOCK_SIZE; i++){
      RF.write(pkt[i]);
   }
   RF.endTransmission();
   lastSent++;
   prevPkt=origPkt;
   startTimer();
   
   free(pkt);

}

/**************** createPkt
    This function creates the wireless packet to be sent.
    
    Parameters: destType, the destination type, should always be 'N' for nexus (aka base station)
                targetID, the base station's ID
                sourceID, the peripheral's ID
                pktNum, the next packet number
                dataType, a String containing 2 chars indicating the type of packet
                dataLen, the length of the data field
                data, the data to send
                
    Return: A pointer to the properly structured wireless packet to be sent
*/

uint8_t * createPkt (uint8_t destType, uint8_t targetID, uint8_t sourceID,
					uint8_t pktNum, uint8_t* dataType, uint8_t dataLen, uint8_t * data){
	
	uint16_t length = HEADER_LEN + dataLen;
	uint8_t * packet = (uint8_t *)malloc(length);
        uint8_t* pkt;
	
	packet[DEST_TYPE] = destType;
	packet[DEST_ID] = targetID;
	packet[SOURCE_ID] = sourceID;
	packet[PKT_NUM] = pktNum;
        packet[PKT_TYPE_HB] = dataType[0];
        packet[PKT_TYPE_LB] = dataType[1];
        packet[DATA_LEN] = dataLen;
        	
	for (uint8_t i=0; i<dataLen; i++)
		packet[DATA + i] = data[i];
	
	pkt = packet;
	
  return pkt;
	
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
  
   for (int i=0; i<BLOCK_SIZE; i++)
    AES_STATE = pkt[i]; 
   
  
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
  
  AES_CTRL &=~( 1<<AES_MODE); //ECB mode
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
