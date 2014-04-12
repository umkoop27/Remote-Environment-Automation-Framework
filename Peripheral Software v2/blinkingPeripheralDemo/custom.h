/*
	custom.h	
	This file is used by developers for definitions required by their custom peripheral. 
	
	Author: YOUR_NAME_HERE
	Date: DATE_HERE
*/

#ifndef CUSTOM_H
#define CUSTOM_H

/* choose which option to define below */

//#define TIME_SLEEP 	// for monitoring peripherals
#define AUTO_JOIN 	// used for peripherals that join automatically without user input
					// not recomended for security reasons
#define TASK 		// used for peripherals that require extened use of the cpu
					// will allow the cpu to be interrupted to check for RF messages

/*

Available I/O ports

label on board : definition in software

Ain 	: A1
PWM 	: 10
MOSI 	: MOSI
MISO	: MISO
SCK 	: SCK

Use to define meaningful names.

Example:

#define LAMP 10
#define TEMP A1
#define SWITCH MOSI

*/
#define LED_PIN 13

#endif

