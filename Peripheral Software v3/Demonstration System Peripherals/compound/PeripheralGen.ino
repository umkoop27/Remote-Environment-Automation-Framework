
#include "PeripheralGen.h"
#include "msgpackalt.h"


void receiveMagneticContactStateNexus(int8_t state)
{
  const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  msgpack_pack_int8( p, state );
  
  
  msgpack_get_buffer( p, &buffer, &len );
  sendResult(buffer, len, 14);
  msgpack_pack_free( p );
}

void receiveBuzzerStateNexus(int8_t state)
{
  const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  msgpack_pack_int8( p, state );
  
  
  msgpack_get_buffer( p, &buffer, &len );
  sendResult(buffer, len, 15);
  msgpack_pack_free( p );
}

void receiveAppliance1StateNexus(int8_t state)
{
  const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  msgpack_pack_int8( p, state );
  
  
  msgpack_get_buffer( p, &buffer, &len );
  sendResult(buffer, len, 16);
  msgpack_pack_free( p );
}

void receiveAppliance2StateNexus(int8_t state)
{
  const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  msgpack_pack_int8( p, state );
  
  
  msgpack_get_buffer( p, &buffer, &len );
  sendResult(buffer, len, 17);
  msgpack_pack_free( p );
}

void receiveAppliance3StateNexus(int8_t state)
{
  const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  msgpack_pack_int8( p, state );
  
  
  msgpack_get_buffer( p, &buffer, &len );
  sendResult(buffer, len, 18);
  msgpack_pack_free( p );
}


void doFunction(unsigned char rpc_cmd, char *packet, int16_t packetLen)
{
  if (rpc_cmd <= MAX_FUNCTIONS)
  {
  
    if (REQUESTMAGNETICCONTACTSTATE == rpc_cmd)
    {
      requestMagneticContactState();
      
  	}
    else
    if (REQUESTBUZZERSTATE == rpc_cmd)
    {
      requestBuzzerState();
      
  	}
    else
    if (SETBUZZERON == rpc_cmd)
    {
      setBuzzerOn();
      
  	}
    else
    if (SETBUZZEROFF == rpc_cmd)
    {
      setBuzzerOff();
      
  	}
    else
    if (REQUESTAPPLIANCE1STATE == rpc_cmd)
    {
      requestAppliance1State();
      
  	}
    else
    if (SETAPPLIANCE1ON == rpc_cmd)
    {
      setAppliance1On();
      
  	}
    else
    if (SETAPPLIANCE1OFF == rpc_cmd)
    {
      setAppliance1Off();
      
  	}
    else
    if (REQUESTAPPLIANCE2STATE == rpc_cmd)
    {
      requestAppliance2State();
      
  	}
    else
    if (SETAPPLIANCE2ON == rpc_cmd)
    {
      setAppliance2On();
      
  	}
    else
    if (SETAPPLIANCE2OFF == rpc_cmd)
    {
      setAppliance2Off();
      
  	}
    else
    if (REQUESTAPPLIANCE3STATE == rpc_cmd)
    {
      requestAppliance3State();
      
  	}
    else
    if (SETAPPLIANCE3ON == rpc_cmd)
    {
      setAppliance3On();
      
  	}
    else
    if (SETAPPLIANCE3OFF == rpc_cmd)
    {
      setAppliance3Off();
      
  	}
    
  }
}
    
