
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
    magnetState = digitalRead(MAGSENSOR);
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
}
void setAppliance2On()
{
}
void setAppliance2Off()
{
}
void requestAppliance3State()
{
}
void setAppliance3On()
{
}
void setAppliance3Off()
{
}
