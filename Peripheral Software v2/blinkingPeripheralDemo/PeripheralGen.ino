
#include "PeripheralGen.h"
#include "msgpackalt.h"


void didStopBlinkingNexus(int16_t blinkTimes)
{
  const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  msgpack_pack_int16( p, blinkTimes );
  
  
  msgpack_get_buffer( p, &buffer, &len );
  sendResult(buffer, len, 2);
  msgpack_pack_free( p );
}


void doFunction(unsigned char rpc_cmd, char *packet, int packetLen)
{
  if (rpc_cmd <= MAX_FUNCTIONS)
  {
  
    if (STARTBLINKING == rpc_cmd)
    {
      msgpack_u *u;
      u = msgpack_unpack_init( packet, packetLen, 1 ); // could set to 0
      int16_t arg0;
      msgpack_unpack_int16( u, &arg0 );
      int16_t arg1;
      msgpack_unpack_int16( u, &arg1 );
      startBlinking(arg0, arg1);
      msgpack_unpack_free( u );
  	}
    else
    if (STOPBLINKING == rpc_cmd)
    {
      stopBlinking();
      
  	}
    
  }
}
    
