/*
	custom.h
	
	This file is to be used by peripheral developers for definitions required by their
	custom peripheral. 
	
	Author: Valerie Beynon
	Date: February 2014

*/

#ifndef CUSTOM_H
#define CUSTOM_H

/* choose which option to define below */

#define TIME_SLEEP 	// for monitoring peripherals
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

#endif

