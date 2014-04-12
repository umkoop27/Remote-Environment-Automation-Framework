
#include "SPI.h"
#include <LPD8806.h>

// Number of RGB LEDs in strand:
int nLEDs = 32;

// Chose 2 pins for output; can be any valid output pins:
int dataPin  = MOSI;
int clockPin = SCK;

LPD8806 strip = LPD8806(nLEDs);
uint8_t rain=0;
uint8_t rainCycle=0;
uint8_t colWipe=0;
uint8_t colChase=0;
uint8_t tChase=0;
uint8_t tChaseRain=0;
//uint8_t colour;
//uint8_t wait;



void periphInit(){
    typeCode = 47; //set the type code of the custom peripheral
    strip.begin();
    //strip.show();
}

void resumeTask(){

   if(colChase==1){

       colorChase(strip.Color(127,   0, 127), 300); // Violet   
   }

}



void colChaseStart(){
  //Serial.println("colour chase start");
  task=1;
  rain=0;
  rainCycle=0;
  colWipe=0;
  colChase=1;
  tChase=0;
  tChaseRain=0;
}

void colChaseStop(){
  colChase=0;
  task=0;
}


/* Helper functions */

//Input a value 0 to 384 to get a color value.
//The colours are a transition r - g -b - back to r

uint32_t Wheel(uint16_t WheelPos)
{
  byte r, g, b;
  switch(WheelPos / 128)
  {
    case 0:
      r = 127 - WheelPos % 128;   //Red down
      g = WheelPos % 128;      // Green up
      b = 0;                  //blue off
      break; 
    case 1:
      g = 127 - WheelPos % 128;  //green down
      b = WheelPos % 128;      //blue up
      r = 0;                  //red off
      break; 
    case 2:
      b = 127 - WheelPos % 128;  //blue down 
      r = WheelPos % 128;      //red up
      g = 0;                  //green off
      break; 
  }
  return(strip.Color(r,g,b));
}

void colorChase(uint32_t c, uint8_t wait) {
  int i;

  // Start by turning all pixels off:
  for(i=0; i<strip.numPixels(); i++) strip.setPixelColor(i, 0);

  // Then display one pixel at a time:
  for(i=0; i<strip.numPixels(); i++) {
    strip.setPixelColor(i, c); // Set new pixel 'on'
    strip.show();              // Refresh LED states
    strip.setPixelColor(i, 0); // Erase pixel, but don't refresh!
   
    safeDelay(wait);
    
  }

  strip.show(); // Refresh to turn off last pixel
}

