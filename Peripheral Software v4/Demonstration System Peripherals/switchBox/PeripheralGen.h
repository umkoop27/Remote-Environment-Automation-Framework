
#ifndef GEN
#define GEN


const unsigned char MAX_FUNCTIONS = 255;
/* Send data to the base station. assign something like Serial.write to this. */
void sendResult(const uint8_t * data, uint8_t len, uint8_t cmd);


/* FUNCTION IDs */

const unsigned char SETENABLED = 1;

const unsigned char SWITCH1CHANGEDTO = 2;

const unsigned char SWITCH2CHANGEDTO = 3;

const unsigned char SWITCH3CHANGEDTO = 4;

const unsigned char DIALCHANGED2 = 5;


/** CALLBACKS AVAILABLE ON NEXUS **/

void switch1ChangedToNexus(int8_t switchState);

void switch2ChangedToNexus(int8_t switchState);

void switch3ChangedToNexus(int8_t switchState);

void dialChanged2Nexus(int8_t dialState);



/** FUNCTIONS AVAILABLE ON PERIPHERAL **/

extern void setEnabled(int8_t set);


/** OTHER FUNCTIONS **/
void doFunction(unsigned char rpc_cmd, char *packet, int16_t packetLen);

#endif /* GEN */
    
