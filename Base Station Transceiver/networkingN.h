/******
  This file contains any definitions required by the base station transceiver.
  
  Author: Valerie Beynon
  Date: February 2014
  
  */

#ifndef BASE_STATION_H
#define BASE_STATION_H

#define MAX_PKT_SIZE 16     // limited due to encryption method
#define HEADER_LEN 7        // wireless packet header length
#define NEXUS_HEADER_LEN 3  // base station trnasceiver/computer communications header length
#define RETRY_MAX 4         // maximum number of retransmissions
#define T_CODE_TIMEOUT 2000 //every 2 seconds re-broadcast searching for new devices
#define LAST_TX 0           // last transmitted column
#define LAST_RX 1           // last received column
#define MAX_ID 65           //allows for 64 peripherals because ID 0 is not assigned
#define ACK_TIMEOUT 200     //wait for an ack for .2 sec
#define RF_TIMER 1000       //check the RF for 1 second
#define CHECK_FOR_STOP_TIMER 1000  //check for a stop searching packet while waiting for a reply every 1 sec
#define BLOCK_SIZE 16       // encryption block size

void newPeriphBroadcast();
uint8_t * createPkt (uint8_t destType, uint8_t targetID, uint8_t sourceID,
					uint8_t pktNum, uint8_t * data, uint16_t dataLen);		
uint32_t testIfTypeCode (uint8_t * pkt);				
void waitForReply();
void sendAuthQuery();
void sendPacket(uint8_t * pkt, uint16_t length);
int testIfAuth (uint8_t * pkt);		
int waitForAuth();
int addNewPeripheral();
uint8_t * createPktForNexus(uint8_t pktType, uint8_t periphId, uint8_t fcnCode, 
                            uint8_t dataLen, uint8_t * data);
					
#endif


