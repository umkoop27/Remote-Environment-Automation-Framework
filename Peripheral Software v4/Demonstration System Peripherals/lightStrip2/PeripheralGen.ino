
#include "PeripheralGen.h"
#include "msgpackalt.h"



void doFunction(unsigned char rpc_cmd, char *packet, int packetLen)
{
  if (rpc_cmd <= MAX_FUNCTIONS)
  {
  
    if (COLORCHASESTART == rpc_cmd)
    {
      msgpack_u *u;
      u = msgpack_unpack_init( packet, packetLen, 1 ); // could set to 0
      int16_t arg0;
      msgpack_unpack_int16( u, &arg0 );
      colorChaseStart(arg0);
      msgpack_unpack_free( u );
  	}
    else
    if (COLORCHASESTOP == rpc_cmd)
    {
      colorChaseStop();
      
  	}
    else
    if (RAINCYCLESTART == rpc_cmd)
    {
      rainCycleStart();
      
  	}
    else
    if (RAINCYCLESTOP == rpc_cmd)
    {
      rainCycleStop();
      
  	}
    
  }
}
    