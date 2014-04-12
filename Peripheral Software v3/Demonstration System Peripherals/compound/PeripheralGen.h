

#ifndef GEN
#define GEN
#include <stdint.h>
const unsigned char MAX_FUNCTIONS = 255;
/* Send data to the base station. assign something like Serial.write to this. */
void sendResult(const uint8_t * data, uint8_t len, uint8_t cmd);


/* FUNCTION IDs */

const unsigned char REQUESTMAGNETICCONTACTSTATE = 1;

const unsigned char REQUESTBUZZERSTATE = 2;

const unsigned char SETBUZZERON = 3;

const unsigned char SETBUZZEROFF = 4;

const unsigned char REQUESTAPPLIANCE1STATE = 5;

const unsigned char SETAPPLIANCE1ON = 6;

const unsigned char SETAPPLIANCE1OFF = 7;

const unsigned char REQUESTAPPLIANCE2STATE = 8;

const unsigned char SETAPPLIANCE2ON = 9;

const unsigned char SETAPPLIANCE2OFF = 10;

const unsigned char REQUESTAPPLIANCE3STATE = 11;

const unsigned char SETAPPLIANCE3ON = 12;

const unsigned char SETAPPLIANCE3OFF = 13;

const unsigned char RECEIVEMAGNETICCONTACTSTATE = 14;

const unsigned char RECEIVEBUZZERSTATE = 15;

const unsigned char RECEIVEAPPLIANCE1STATE = 16;

const unsigned char RECEIVEAPPLIANCE2STATE = 17;

const unsigned char RECEIVEAPPLIANCE3STATE = 18;


/** CALLBACKS AVAILABLE ON NEXUS **/

void receiveMagneticContactStateNexus(int8_t state);

void receiveBuzzerStateNexus(int8_t state);

void receiveAppliance1StateNexus(int8_t state);

void receiveAppliance2StateNexus(int8_t state);

void receiveAppliance3StateNexus(int8_t state);



/** FUNCTIONS AVAILABLE ON PERIPHERAL **/

extern void requestMagneticContactState();

extern void requestBuzzerState();

extern void setBuzzerOn();

extern void setBuzzerOff();

extern void requestAppliance1State();

extern void setAppliance1On();

extern void setAppliance1Off();

extern void requestAppliance2State();

extern void setAppliance2On();

extern void setAppliance2Off();

extern void requestAppliance3State();

extern void setAppliance3On();

extern void setAppliance3Off();


/** OTHER FUNCTIONS **/
void doFunction(unsigned char rpc_cmd, char *packet, int16_t packetLen);

#endif /* GEN */
    
