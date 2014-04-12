#ifndef CUSTOM_H
#define CUSTOM_H

//#define TIME_SLEEP //for monitoring/continuous output peripherals
#define AUTO_JOIN //used for peripherals that join automatically -> not recomended for security reasons
//#define TASK //used for peripherals that require extened use of the cpu -> will allow the cpu to be interrupted to check for RF messages

#define APPLIANCE1 MOSI
#define APPLIANCE2 A1
#define APPLIANCE3 10 //PWM pin
#define MAGSENSOR MISO
#define BUZZEROUT SCK


#endif
