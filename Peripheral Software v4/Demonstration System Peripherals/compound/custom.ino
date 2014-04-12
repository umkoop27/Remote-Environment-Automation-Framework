
int magnetState;
int buzzerState = 0;
int appliance1State = 0;
int appliance2State = 0;
int appliance3State = 0;



#ifdef TIME_SLEEP
void monitor(){
  int newState = digitalRead(MAGSENSOR);
  
  if (newState != magnetState)
  {
    magnetState = newState;
    receiveMagneticContactStateNexus(5*magnetState);
  }
}
#endif

#ifdef TASK
void resumeTask(){
  
}
#endif


void periphInit(){
    typeCode = 49; //set the type code of the custom peripheral
    pinMode(MAGSENSOR, INPUT_PULLUP);
    magnetState = digitalRead(MAGSENSOR);
    
     pinMode(BUZZEROUT, OUTPUT);
     digitalWrite(BUZZEROUT, LOW);
     
     pinMode(APPLIANCE1, OUTPUT);
    digitalWrite(APPLIANCE1, LOW);
    
     pinMode(APPLIANCE2, OUTPUT);
    digitalWrite(APPLIANCE2, LOW);
}

void requestMagneticContactState()
{
  receiveMagneticContactStateNexus(5*magnetState);
}

void requestBuzzerState()
{
  if (buzzerState)
  {
    receiveBuzzerStateNexus( 5 );
  } else
  {
    receiveBuzzerStateNexus( 0 );
  }
}

void setBuzzerOn()
{
  buzzerState = 1;
  pinMode(BUZZEROUT, OUTPUT);
  digitalWrite(BUZZEROUT, HIGH);
}
void setBuzzerOff()
{
  buzzerState = 0;
  pinMode(BUZZEROUT, OUTPUT);
  digitalWrite(BUZZEROUT, LOW);
}
void requestAppliance1State()
{
  if (appliance1State)
  {
    receiveAppliance1StateNexus(5);
  } else
  {
    receiveAppliance1StateNexus(0);
  }
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
void requestAppliance2State()
{
    if (appliance2State)
  {
    receiveAppliance2StateNexus(5);
  } else
  {
    receiveAppliance2StateNexus(0);
  }
}
void setAppliance2On()
{
  
  appliance1State = 1;
  pinMode(APPLIANCE2, OUTPUT);
  digitalWrite(APPLIANCE2, HIGH);
  
}
void setAppliance2Off()
{
  appliance2State = 0;
  pinMode(APPLIANCE2, OUTPUT);
  digitalWrite(APPLIANCE2, LOW);
}
void requestAppliance3State()
{
  if (appliance3State)
  {
    receiveAppliance3StateNexus(5);
  } else
  {
    receiveAppliance3StateNexus(0);
  }
}
void setAppliance3On()
{
  appliance3State = 1;
  pinMode(APPLIANCE3, OUTPUT);
  digitalWrite(APPLIANCE3, HIGH);
}
void setAppliance3Off()
{
  appliance3State = 0;
  pinMode(APPLIANCE3, OUTPUT);
  digitalWrite(APPLIANCE3, LOW);
}
