
int magnetState;
int buzzerState = 0;
int appliance1State = 0;
int appliance2State = 0;
int appliance3State = 0;



#ifdef TIME_SLEEP
void monitor(){

}
#endif

#ifdef TASK
void resumeTask(){
  
}
#endif


void periphInit(){
    typeCode = 69; //set the type code of the custom peripheral

     
     pinMode(APPLIANCE1, OUTPUT);
    digitalWrite(APPLIANCE1, LOW);
    
}


void setAppliance1On()
{
  appliance1State = 1;
  pinMode(APPLIANCE1, OUTPUT);
  digitalWrite(APPLIANCE1, HIGH);
}
void setAppliance1Off()
{
  appliance1State = 0;
  pinMode(APPLIANCE1, OUTPUT);
  digitalWrite(APPLIANCE1, LOW);
}

