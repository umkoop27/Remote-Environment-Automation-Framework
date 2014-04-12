
#ifndef GEN
#define GEN


const unsigned char MAX_FUNCTIONS = 255;
/* Send data to the base station. assign something like Serial.write to this. */
void sendResult(const uint8_t * data, uint8_t len, uint8_t cmd);


/* FUNCTION IDs */

const unsigned char SETENABLED = 0;

const unsigned char COLORCHASESTART = 1;

const unsigned char COLORCHASESTOP = 2;





/** FUNCTIONS AVAILABLE ON PERIPHERAL **/

extern void colChaseStart();
extern void colChaseStop();


/** OTHER FUNCTIONS **/
void doFunction(unsigned char rpc_cmd, char *packet, int16_t packetLen);

#endif /* GEN */
    
