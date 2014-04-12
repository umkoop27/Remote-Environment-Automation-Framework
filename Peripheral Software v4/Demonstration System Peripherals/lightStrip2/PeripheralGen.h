
#ifndef GEN
#define GEN


const unsigned char MAX_FUNCTIONS = 255;
/* Send data to the base station. assign something like Serial.write to this. */
void sendResult(const uint8_t * data, uint8_t len, uint8_t cmd);


/* FUNCTION IDs */

const unsigned char COLORCHASESTART = 1;

const unsigned char COLORCHASESTOP = 2;

const unsigned char RAINCYCLESTART = 3;

const unsigned char RAINCYCLESTOP = 4;


/** CALLBACKS AVAILABLE ON NEXUS **/



/** FUNCTIONS AVAILABLE ON PERIPHERAL **/

extern void colorChaseStart(int16_t color);

extern void colorChaseStop();

extern void rainCycleStart();

extern void rainCycleStop();


/** OTHER FUNCTIONS **/
void doFunction(unsigned char rpc_cmd, char *packet, int16_t packetLen);

#endif /* GEN */
    