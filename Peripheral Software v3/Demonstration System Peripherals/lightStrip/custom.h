#ifndef CUSTOM_H
#define CUSTOM_H

//#define TIME_SLEEP //for monitoring peripherals that won't necessarily be woken up by incomimg messages
#define AUTO_JOIN //used for peripherals that join automatically -> not recomended for security reasons
#define TASK //used for peripherals that require extened use of the cpu -> will allow the cpu to be interrupted to check for RF messages

#define DIAL A1
#define SW_L MOSI
#define SW_M MISO
#define SW_R SCK


#endif
