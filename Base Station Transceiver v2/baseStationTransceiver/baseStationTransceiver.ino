/**************
  This is the main file for the base station transceiver, 
  which is an ArduIMU connected to the base station
  computer via the serial port. It is responsible for performing
  tasks given to it by the base station computer, transmitting
  packets from the computer to the peripherals wirelessly, and
  receiving and forwarding messages from peripherals to the 
  computer.
  
  Author: Valerie Beynon
  Date: February 2014
  
 */


#include <stdarg.h>
#include <stdint.h>
#include <stdlib.h>
#include <asf.h>

#include <Wire.h>
#include <EEPROM.h>
#include <ZigduinoRadio.h>
#include "networkingN.h"



#define SRADIO 0
#define SSERIAL 1
#define SERIAL_PORT_SPEED 19200


#define RF_DEVICE_ID 0x11;

int rfChannel = 11;

uint8_t on=0;
boolean sendRF = false;

enum { //packet structure for wireless packets
  DEST_TYPE,
  DEST_ID,
  SOURCE_ID,
  PKT_NUM,
  PKT_TYPE_HB,
  PKT_TYPE_LB,
  DATA_LEN,
  DATA
} packetPos;

enum{ //packet structure for Serial packets to/from base station comp
  PKT_TYPE,
  PERIPH_ID,
  DATA_LEN_N,
  DATA_N
} nexusPkt;


uint32_t newPeriphTCode;  // the type code of a new peripheral that is being added
uint8_t nextAvailID = 0;  // the next awailable ID
uint8_t nexusID = 1;      // the base station's ID
uint8_t expAck;           // the number of the ACK that the bases station is waiting on
uint8_t expAckID;         // the ID of the peripheral it is waiting on an ACK from
uint8_t waitingOnAck=0;   // indicates if the base station is currently waiting on an ACK
uint8_t waitingOnTCode=0; // indicates if the base station is currently waiting on a type code message
uint8_t searching=0;      // indicates if the base station is currently searching for new peripherals

uint8_t seqNum[MAX_ID][2]; //2 columns for last sent and last received for each peripheral
long timer;                //the acknowledgement timer
uint8_t * lastSentPkt[MAX_ID]; //stores the last sent packet in case of needing retransmission
uint8_t ackCounter[MAX_ID];    //counts the number of retransmissions
uint8_t commEst[MAX_ID];  
uint8_t idTaken[MAX_ID];  //indicates whether or not an ID is taken
long timerRF;             //timer used to switch between checking the RF and checking the Serial


enum{    //the states when adding a new peripheral 
  NEW_PERIPH_BROADCAST,
  WAIT_FOR_REPLY,
  SEND_TO_JAVA,
  SEND_PERIPH_ID,
  EXIT
} joinState = NEW_PERIPH_BROADCAST;  
  

void setup()
{
  Serial.begin(SERIAL_PORT_SPEED);
  Wire.begin();  
  
  RF.begin(rfChannel);
  RF.setParam(RP_TXPWR(15));
  RF.setParam(RP_DATARATE(MOD_OQPSK_1000));
  RF.attachError(errHandle);
  DDRD = 0x40;
  
  calcDecryptionKey();

}
/******** loop

  This loop switches between checking the serial and transceiver for incoming messages.
  However, if waiting on an ACK it just checks the transceiver until it receives it
  or reaches its resend limit.

*/
void loop() 
{  
  if(!waitingOnAck)
    checkSerial();

  while(waitingOnAck) {

    if((millis()-timer) > ACK_TIMEOUT){
      resend(expAckID);
    }
    receivePacket();
  }
  
  timerRF=millis();
  while((millis()-timerRF)<RF_TIMER)
    receivePacket();  
}



void errHandle(radio_error_t err)
{
}
  




