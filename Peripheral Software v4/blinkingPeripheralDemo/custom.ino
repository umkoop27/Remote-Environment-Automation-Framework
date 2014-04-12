/*
	custom.ino
	
	This file is a template that can be used for creating custom peripherals. The three
	functions already started in this file are to be filled in by the peripheral
	developer to meet there needs. This is also the file in which the custom remote
	functions by the base station are to be written as well as any helper functions.
	
	This is the only file in which source code should be written by a developer when
	creating a custom peripheral and goes with the corresponding header file "custom.h".
	
	Author: Valerie Beynon
	Date: February 2014

*/

int32_t blinksRemaining = 0;
int32_t delayMSRemaining = 0;


/******** periphInit

	This function is called in the setup function of all peripherals and can be used to
	perform any peripheral specific initialization tasks, such as turning off all lights
	or reading an initial temperature. The only required task is in this function is to 
	set the peripheral's type code to match the corresponding descriptor's type code
	located on the base station.
*/

void periphInit(){
	typeCode = 12; //set the type code of the custom peripheral
Serial.println("init");
        pinMode(LED_PIN, OUTPUT);
}


#ifdef TIME_SLEEP

/*********** monitor
	
	This function called periodically by the main loop if peripherals are designed for 
	monitoring applications which is indicated by defining TIME_SLEEP. In this function
	users may perform any monitoring tasks they wish through reading various inputs and
	outputs.
	
	Note: When writing this function ensure that only one packet is transmitted to the 
	base station at a time, that it the corresponding ACK must have been received before
	the next packet is transmitted. This may require that a call back function be written
	to transmit multiple sensors' data back in a single packet.
	
*/

void monitor(){
    TIMSK2 = 0<<TOIE2; // disable TC2 overflow interrupt
    
    // insert monitoring function or make calls to functions gather data 
    
    TIMSK2=1<<TOIE2;
}
#endif



#ifdef TASK

/******** resumeTask

	This function is called in the main loop if TASK is defined. This function is used
	by peripherals that require extensive use of the cpu, such as peripherals that must
	constantly perform tasks until they are told to stop. Custom peripheral functions 
	must return to the main loop periodically to allow for the peripheral to check its 
	transceiver buffer. This function should then be written to be able to call the
	correct function to resume any tasks that are in process.
	
	For example if peripheral is required to blink lights until told to stop this function
	needs to know that it is in the process of blinking lights and call the correct
	function to continue. The function it calls is responsible for returning after some
	time, for example every 5 blinks.
	
	Use of this method may require variables to be defined outside of the functions to 
	indicate current tasks and the variables it may need.
	
*/

void resumeTask()
{
  Serial.println("Resume");
  if (delayMSRemaining > 0)
  {
    if (delayMSRemaining > 1000)
    {
      Serial.print("Delay: ");
      Serial.println(delayMSRemaining);
      safeDelay(1000);
      delayMSRemaining -= 1000;
    } else
    {
      Serial.print("Delay: ");
      Serial.println(delayMSRemaining);
      safeDelay(delayMSRemaining);
      delayMSRemaining = 0;
    }
  } else if(blinksRemaining > 0)
  {
    Serial.print("Blink: ");
    Serial.println(blinksRemaining);
    digitalWrite(LED_PIN, HIGH);
    safeDelay(1000);
    digitalWrite(LED_PIN, LOW);
    safeDelay(1000);
    blinksRemaining --;
  }
}

#endif

/*

Custom peripheral functions can be entered here

*/



void startBlinking(int16_t timesToBlink, int16_t delayTime)
{
  Serial.print("sdelay: ");
  Serial.println(delayTime);
  Serial.print("blink: ");
  Serial.println(timesToBlink);
  
  task=1;
  blinksRemaining = timesToBlink;
  delayMSRemaining = delayTime;
}

void stopBlinking()
{
  task=0;
  blinksRemaining = 0;
  delayMSRemaining = 0;
}
