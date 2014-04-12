
#ifndef GEN
#define GEN


const unsigned char MAX_FUNCTIONS = 255;
/* Send data to the base station. assign something like Serial.write to this. */
//void (*sendPacket)(const unsigned char * data, unsigned char len, unsigned char cmd);


/* FUNCTION IDs */

const unsigned char REDLIGHTON = 1;

const unsigned char REDLIGHTOFF = 2;

const unsigned char WHITELIGHTON = 3;

const unsigned char WHITELIGHTOFF = 4;

const unsigned char YELLOWLIGHTON = 5;

const unsigned char YELLOWLIGHTOFF = 6;

const unsigned char GREENLIGHTON = 7;

const unsigned char GREENLIGHTOFF = 8;

const unsigned char BLUELIGHTON = 9;

const unsigned char BLUELIGHTOFF = 10;

const unsigned char BLINKLIGHTS = 11;

const unsigned char TURNALLON = 12;

const unsigned char TURNALLOFF = 13;

const unsigned char SETGREENBRIGHTNESS = 14;


/** CALLBACKS AVAILABLE ON NEXUS **/



/** FUNCTIONS AVAILABLE ON PERIPHERAL **/

extern void redLightOn();

extern void redLightOff();

extern void whiteLightOn();

extern void whiteLightOff();

extern void yellowLightOn();

extern void yellowLightOff();

extern void greenLightOn();

extern void greenLightOff();

extern void blueLightOn();

extern void blueLightOff();

extern void blinkLights(unsigned char num);

extern void turnAllOn();

extern void turnAllOff();

extern void setGreenBrightness(int16_t value);


/** OTHER FUNCTIONS **/
void doFunction(unsigned char rpc_cmd, char *packet, int16_t packetLen);

#endif /* GEN */
    
