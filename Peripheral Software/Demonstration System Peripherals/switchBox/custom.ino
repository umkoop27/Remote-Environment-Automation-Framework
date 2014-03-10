


uint8_t monitorE;
uint16_t dialValue;
uint8_t switchLeftValue;
uint8_t switchRightValue;
uint8_t switchMidValue;



void periphInit(){
	typeCode = 6; //set the type code of the custom peripheral
	pinMode(DIAL, INPUT);
	pinMode(SW_L, INPUT_PULLUP);
	pinMode(SW_M, INPUT_PULLUP);
	pinMode(SW_R, INPUT_PULLUP);
        monitorE=1; //enable the switch box
}


#ifdef TIME_SLEEP

void monitor(){
    TIMSK2 = 0<<TOIE2; // disable TC2 overflow interrupt
    if(monitorE&&lastSentTimer==0){ //only checks the next switch/dial if the current one has not changed
      if(!checkSwitchLeft()){
        if(!checkSwitchRight()){
          if(!checkSwitchMid()){
            checkDial();
          }
        }
      }
    }
    
    TIMSK2=1<<TOIE2;
}
#endif

/***************
  Reads the analog dial value
  
  Return: an int between 0 and ~ 951


*/
int checkDial(){
 int val = analogRead(DIAL); 
 
 int result=0;
 int diff = val-dialValue;
   delay(100);
 
 if((diff>=0 && diff>5)|| (diff<0 && diff<-5)) {  
   dialValue = val;
   //sendResult(MON_DIAL, 1, data);
   dialChanged2Nexus(dialValue);
   //Serial.println(dialValue);
   //delay(100);
   result=1;
  }
  return result;
}

/****** checkSwitchLeft
  Reads the value of the left switch and compares it to the previous reading.
  
  Return: 1 if the switch's state has changed, else returns 0
 */


int checkSwitchLeft(){
 uint8_t val = !digitalRead(SW_L);  
 int result=0;
 if(val!=switchLeftValue) {  
   switchLeftValue = val;
   switch1ChangedToNexus(val);
   result=1;
  }
  return result;
}

/****** checkSwitchMid
  Reads the value of the middle switch and compares it to the previous reading.
  
  Return: 1 if the switch's state has changed, else returns 0
 */

int checkSwitchMid(){
 uint8_t val = !digitalRead(SW_M); 
 int result=0;
 if(val!=switchMidValue) {  
   switchMidValue = val;
   switch2ChangedToNexus(val);
   result=1;
  }
  return result;
}

/****** checkSwitchRight
  Reads the value of the right switch and compares it to the previous reading.
  
  Return: 1 if the switch's state has changed, else returns 0
 */

int checkSwitchRight(){
 uint8_t val = !digitalRead(SW_R); 
 int result=0;
 if(val!=switchRightValue) {  
   switchRightValue = val;
   switch3ChangedToNexus(val);
   result=1;
  }
  return result;
}

/********** setEnabled
  This function allows this peripheral's monitoring functions to be
  enabled and disabled.
  
  parameter: an int8_t, 1 enables the device, and 0 disables it

*/

void setEnabled(int8_t set){
  
  if(set){
    Serial.println("enable");
    delay(100);
    monitorE =1;
    switchMidValue = !digitalRead(SW_M);
    switchLeftValue = !digitalRead(SW_L);
    switchRightValue = !digitalRead(SW_R);
    dialValue = analogRead(DIAL); 

  }
    
  else
    monitorE =0;
  
}

