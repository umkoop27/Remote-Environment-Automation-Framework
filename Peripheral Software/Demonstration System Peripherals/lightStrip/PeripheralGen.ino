
#include "PeripheralGen.h"
#include "msgpackalt.h"



void doFunction(unsigned char rpc_cmd, char *packet, int packetLen)
{
  if (rpc_cmd <= MAX_FUNCTIONS)
  {
  
    if (COLORCHASESTART == rpc_cmd)
    {
     // Serial.println("rcvd col start");
      msgpack_u *u;
      u = msgpack_unpack_init( packet, packetLen, 1 ); // could set to 0
      colChaseStart();
      msgpack_unpack_free( u );
    }
    
    if (COLORCHASESTOP == rpc_cmd)
    {
      msgpack_u *u;
      u = msgpack_unpack_init( packet, packetLen, 1 ); // could set to 0
      colChaseStop();
      msgpack_unpack_free( u );
    }
    
  }
}
    
