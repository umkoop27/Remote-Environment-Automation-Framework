
#ifndef GEN
#define GEN


const unsigned char MAX_FUNCTIONS = 255;
/* Send data to the base station. assign something like Serial.write to this. */
void sendResult(const uint8_t * data, uint8_t len, uint8_t cmd);


/* FUNCTION IDs */

const unsigned char STARTBLINKING = 1;

const unsigned char STOPBLINKING = 3;

const unsigned char DIDSTOPBLINKING = 2;


/** CALLBACKS AVAILABLE ON NEXUS **/

void didStopBlinkingNexus(int16_t blinkTimes);



/** FUNCTIONS AVAILABLE ON PERIPHERAL **/

extern void startBlinking(int16_t timesToBlink, int16_t delayTime);

extern void stopBlinking();


/** OTHER FUNCTIONS **/
void doFunction(unsigned char rpc_cmd, char *packet, int16_t packetLen);

#endif /* GEN */
    
