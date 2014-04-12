
#include "PeripheralGen.h"
#include <msgpackalt.h>



void doFunction(unsigned char rpc_cmd, char *packet, int packetLen)
{
  if (rpc_cmd <= MAX_FUNCTIONS)
  {
  
    if (REDLIGHTON == rpc_cmd)
    {
      redLightOn();
      
  	}
    else
    if (REDLIGHTOFF == rpc_cmd)
    {
      redLightOff();
      
  	}
    else
    if (WHITELIGHTON == rpc_cmd)
    {
      whiteLightOn();
      
  	}
    else
    if (WHITELIGHTOFF == rpc_cmd)
    {
      whiteLightOff();
      
  	}
    else
    if (YELLOWLIGHTON == rpc_cmd)
    {
      yellowLightOn();
      
  	}
    else
    if (YELLOWLIGHTOFF == rpc_cmd)
    {
      yellowLightOff();
      
  	}
    else
    if (GREENLIGHTON == rpc_cmd)
    {
      greenLightOn();
      
  	}
    else
    if (GREENLIGHTOFF == rpc_cmd)
    {
      greenLightOff();
      
  	}
    else
    if (BLUELIGHTON == rpc_cmd)
    {
      blueLightOn();
      
  	}
    else
    if (BLUELIGHTOFF == rpc_cmd)
    {
      blueLightOff();
      
  	}
    else
    if (BLINKLIGHTS == rpc_cmd)
    {
      msgpack_u *u;
      u = msgpack_unpack_init( packet, packetLen, 1 ); // could set to 0
      int8_t arg0;
      msgpack_unpack_int8( u, &arg0 );
      blinkLights(arg0);
      msgpack_unpack_free( u );
  	}
    else
    if (TURNALLON == rpc_cmd)
    {
      turnAllOn();
      
  	}
    else
    if (TURNALLOFF == rpc_cmd)
    {
      turnAllOff();
      
  	}
    else
    if (SETGREENBRIGHTNESS == rpc_cmd)
    {
      msgpack_u *u;
      u = msgpack_unpack_init( packet, packetLen, 1 ); // could set to 0
      int16_t arg0;
      msgpack_unpack_int16( u, &arg0 );
      setGreenBrightness(arg0);
      msgpack_unpack_free( u );
  	}
    
  }
}
    
