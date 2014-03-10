
#include "PeripheralGen.h"
#include "msgpackalt.h"


void switch1ChangedToNexus(int8_t switchState)
{
  const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  msgpack_pack_int8( p, switchState );
  
  
  msgpack_get_buffer( p, &buffer, &len );
  sendResult(buffer, len, 1);
  msgpack_pack_free( p );
}

void switch2ChangedToNexus(int8_t switchState)
{
  const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  msgpack_pack_int8( p, switchState );
  
  
  msgpack_get_buffer( p, &buffer, &len );
  sendResult(buffer, len, 2);
  msgpack_pack_free( p );
}

void switch3ChangedToNexus(int8_t switchState)
{
  const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  msgpack_pack_int8( p, switchState );
  
  
  msgpack_get_buffer( p, &buffer, &len );
  sendResult(buffer, len, 3);
  msgpack_pack_free( p );
}

void dialChanged2Nexus(int8_t dialState)
{
  const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  msgpack_pack_int8( p, dialState );
  
  
  msgpack_get_buffer( p, &buffer, &len );
  sendResult(buffer, len, 4);
  msgpack_pack_free( p );
}


void doFunction(unsigned char rpc_cmd, char *packet, int packetLen)
{
  if (rpc_cmd <= MAX_FUNCTIONS)
  {
  
    if (SETENABLED == rpc_cmd)
    {
      msgpack_u *u;
      u = msgpack_unpack_init( packet, packetLen, 1 ); // could set to 0
      int8_t arg0;
      msgpack_unpack_int8( u, &arg0 );
      setEnabled(arg0);
      msgpack_unpack_free( u );
  	}
    
  }
}
    