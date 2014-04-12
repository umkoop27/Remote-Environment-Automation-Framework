/***************

This file is the main file for the Peripheral Core and contains all necessary include
statements, global variable definitions, as well as the setup() and loop() functions.
This file should not be changed when creating custom peripherals.

Author: Valerie Beynon
Date: February 2014
*/

#include <stdarg.h>
#include <stdlib.h>
#include <asf.h>

#include <Wire.h>
#include <EEPROM.h>
#include <ZigduinoRadio.h>
#include "customPeripheral.h"
#include "PeripheralGen.h" //provided by developer
#include "custom.h"

//output stream identifiers
#define SRADIO 0
#define SSERIAL 1
#define SERIAL_PORT_SPEED 19200
#define BLUE_LED 24
#define GREEN_LED 28

#define RF_DEVICE_ID 0x11
int rfChannel = 11;
int result;
boolean sendRF = false;

uint8_t ntwkID=0; //the peripherals unique network ID, changed once it joins a network

enum { //the packet structure of a wireless data packet
  DEST_TYPE,
  DEST_ID,
  SOURCE_ID,
  PKT_NUM,
  PKT_TYPE_HB,
  PKT_TYPE_LB,
  DATA_LEN,
  DATA
} packetPos;

 enum { //the states for joining a network
	WAIT_FOR_NEXUS,
        GET_EXT_AUTH,
	SEND_TYPE_CODE,
	WAIT_FOR_ID_FROM_NEXUS,
	EXIT
} joinNtwkState = WAIT_FOR_NEXUS;

uint8_t nexusID;      // the id of the base station of the network the peripheral joins
uint8_t lastSent = 0; // last sent packet number
uint8_t lastRcvd = 0; // last received packet number
uint8_t * prevPkt;    // the last sent packet
long lastSentTimer;   // resend timer
uint8_t * buffer[MAX_BUFFERED]; // circular buffer used when waiting for an ACK and instruction pkts are rcvd
uint8_t buffStart=0;  // points to first pkt in buffer
uint8_t buffEnd=0;    // points to next open space in the buffer
uint32_t typeCode;    // the type code of the peripheral
uint8_t* instructionPkt; // an instruction packet that has been received and is to be handled
uint8_t pwm=0;        // a variable to indicate if PWM is currently being used
uint8_t task=0;       // a variable to indicate is a task needs to be resumed, used when TASK is defined
uint8_t safe =0;      // a variable to indicate if the function smartDelay() is currently being used

extern void resumeTask();

void setup()
{
  Serial.begin(SERIAL_PORT_SPEED);
  Wire.begin();  
    Serial.println("restarting..");

  RF.begin(rfChannel);
  RF.setParam(RP_TXPWR(15));
  RF.setParam(RP_DATARATE(MOD_OQPSK_1000));
  RF.attachError(errHandle);
  RF.beginTransmission();
  
  #ifdef TASK //set up timeout interrupt
  ASSR = 1<<AS2; // turn on 32kHz crystal oscillator
  TCCR2B = 0x04; // .5 s interrupts
  #endif
  
  #ifdef TIME_SLEEP //set up timeout interrupt
  ASSR = 1<<AS2; // turn on 32kHz crystal oscillator
  TCCR2B = 0x04; // .5 s interrupts
  #endif
  
  periphInit();
  calcDecryptionKey();
}

void loop() 
{  
  while (!ntwkID){
    Serial.println("calling join network...");
    result = joinNetwork();
    if (result){
      Serial.print("Joined Network with ID: ");
      Serial.println(ntwkID);
      delay(500);
    }
   }
   
  if(!pwm){ //cannot sleep if pwm is in use because pwm requires the clock 
            //and any analog outputs will be converted to digital
            
   #ifdef TIME_SLEEP //used for monitoring applications
   timerSleep();
   #endif
   
   #ifndef TIME_SLEEP
     sleep();
   #endif
   
   }

 do{
   do{
     if (instructionPkt!=NULL){ //instruction packet may have been read in an interrupt
       doFunction(instructionPkt[DATA], (char*)&instructionPkt[DATA+1], instructionPkt[DATA_LEN]-1);
       free(instructionPkt);  
       instructionPkt = NULL;
     }
     receivePacket();
   }while(instructionPkt!=NULL); //continue until there are no more packets
    
    while(lastSentTimer!=0) //if the function performed requires an ack then it needs to arrive before it sends anything else
      receivePacket();

   #ifdef TASK
   if(task){
     resumeTask();  
   }
   #endif
   
    #ifdef TIME_SLEEP //monitoring application
    monitor();
    while(lastSentTimer!=0) //wait for any ACKs that are required
      receivePacket();
    #endif
    
  }while(task); //don't sleep if currently in the middle of a task
}
  

 void errHandle(radio_error_t err)
{
  Serial.println();
  Serial.print("Error: ");
  Serial.print((uint8_t)err, 10);
  Serial.println();
}
