

#ifndef GEN
#define GEN
#include <stdint.h>
const unsigned char MAX_FUNCTIONS = 255;
/* Send data to the base station. assign something like Serial.write to this. */
void sendResult(const uint8_t * data, uint8_t len, uint8_t cmd);


/* FUNCTION IDs */




const unsigned char SETAPPLIANCE1ON = 6;

const unsigned char SETAPPLIANCE1OFF = 7;




/** CALLBACKS AVAILABLE ON NEXUS **/


void receiveAppliance1StateNexus(int8_t state);




/** FUNCTIONS AVAILABLE ON PERIPHERAL **/



extern void setAppliance1On();

extern void setAppliance1Off();




/** OTHER FUNCTIONS **/
void doFunction(unsigned char rpc_cmd, char *packet, int16_t packetLen);

#endif /* GEN */
    
