/*********

This file is the header file for customPeripheral.ino containing the definitions
used by the peripheral core.

Author: Valerie Beynon
Date: February 2014

*/

#ifndef NETWORKINGP_H
#define NETWORKINGP_H

#define NUM_READ 10


#define MAX_NTWKS 16 //the maximum number of different networks that a peripheral can join at a time
#define MAX_PKT_SIZE 16 //limited due to the encryption method
#define HEADER_LEN 7 //wireless packet header length
#define RESPONSE_LIMIT 1000
#define RESEND_LIMIT 4 //the maximum number of resend attempts
#define MAX_BUFFERED 20 // can store up to 20 other pkts while waiting on an ack
#define RESEND_TIMER 500 //wait for .5s for an ack before resending a packet
#define PWM 10 //PWM pin
#define PUSH_BUTTON 26 // push button pin number
#define BLOCK_SIZE 16 //used for 128 bit AES encryption



void safeDelay(unsigned long wait);

#endif

  
