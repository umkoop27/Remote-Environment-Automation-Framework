


void periphInit(){
	typeCode = 19;
  pinMode(RED, OUTPUT);
  digitalWrite(RED, HIGH);
  pinMode(WHITE, OUTPUT);
  digitalWrite(WHITE, HIGH);
  pinMode(BLUE , OUTPUT);
  digitalWrite(BLUE, HIGH);
  pinMode(GREEN, OUTPUT);
  digitalWrite(GREEN, HIGH);
  pinMode(YELLOW, OUTPUT);
  digitalWrite(YELLOW, HIGH);
  delay(500);
  digitalWrite(RED, LOW);
  digitalWrite(WHITE, LOW);
  digitalWrite(BLUE, LOW);
  digitalWrite(GREEN, LOW);
  digitalWrite(YELLOW, LOW);
}

uint8_t numSoFar =0;
uint8_t blinking =0;
uint8_t total;

void resumeTask(){
  if(blinking)
    blinkAll();
}

void redLightOn(){
  if(blinking)
    stopBlinking();
    
  pinMode(RED, OUTPUT);
  digitalWrite(RED, HIGH);
}

void redLightOff(){
  if(blinking)
    stopBlinking();
    
  pinMode(RED, OUTPUT);
  digitalWrite(RED, LOW);
}

void whiteLightOn(){
  if(blinking)
    stopBlinking();
    
  pinMode(WHITE, OUTPUT);
  digitalWrite(WHITE, HIGH);
}

void whiteLightOff(){
  if(blinking)
    stopBlinking();
    
  pinMode(WHITE, OUTPUT);
  digitalWrite(WHITE, LOW);
}

void yellowLightOn(){
  if(blinking)
    stopBlinking();
  pinMode(YELLOW, OUTPUT);
  digitalWrite(YELLOW, HIGH);
}

void yellowLightOff(){
  if(blinking)
    stopBlinking();
    
  pinMode(YELLOW, OUTPUT);
  digitalWrite(YELLOW, LOW);
}

void greenLightOn(){
  if(blinking)
    stopBlinking();
    
  pinMode(GREEN, OUTPUT);
  digitalWrite(GREEN, HIGH);
}

void greenLightOff(){
  if(blinking)
    stopBlinking();
  pwm=0; //if the green was using pwm
  pinMode(GREEN, OUTPUT);
  digitalWrite(GREEN, LOW);
}

void blueLightOn(){
  if(blinking)
    stopBlinking();
  pinMode(BLUE, OUTPUT);
  digitalWrite(BLUE, HIGH);
}

void blueLightOff(){
  if(blinking)
    stopBlinking();
    
  pinMode(BLUE, OUTPUT);
  digitalWrite(BLUE, LOW);
}

void setGreenBrightness(int16_t value){
  if(blinking)
    stopBlinking();
    
        pwm=1; //set pwm flag
	pinMode (GREEN, OUTPUT);
	analogWrite(GREEN, (uint8_t)value);
}

void turnAllOn(){
  if(blinking)
    stopBlinking();
	redLightOn();
	whiteLightOn();
	greenLightOn();
	yellowLightOn();
	blueLightOn();
}

void turnAllOff(){
	redLightOff();
	greenLightOff();
	whiteLightOff();
	blueLightOff();
	yellowLightOff();
}

void blinkLights(int8_t num){
  task =1;
  blinking=1;
  total = num;
  numSoFar=0;
  pinMode(BLUE, OUTPUT);
  pinMode(GREEN, OUTPUT);
  pinMode(YELLOW, OUTPUT);
  pinMode(RED, OUTPUT);
  pinMode(WHITE, OUTPUT);
  //blinkAll();
  
}

void blinkAll(){
 
  for (int i=0; i<5&& numSoFar<total; i++){ //return to the main program after every 5 blinks
    
    digitalWrite(BLUE, HIGH);
    digitalWrite(GREEN, HIGH);
    digitalWrite(YELLOW, HIGH);
    digitalWrite(RED, HIGH);
    digitalWrite(WHITE, HIGH);
    
    safeDelay(500); //allow interrupts to check the RF
    
    
    digitalWrite(BLUE, LOW);
    digitalWrite(GREEN, LOW);
    digitalWrite(YELLOW, LOW);
    digitalWrite(RED, LOW);
    digitalWrite(WHITE, LOW);
    
    numSoFar++;
    
    safeDelay(500); //allowed to be interrupted to check the RF
  }
  
  if(numSoFar==total)
    stopBlinking();
  
}
void stopBlinking(){
  numSoFar =0;
  blinking =0;
  total=0;
  task=0;
}

