/***********************************************************
BLE CC41A Bluetooth Master setup sketch
This is a clone of the HM10 BLE board
In the Serial Monitor ensure that 'Both NL and CR' is selected
Select a Baud Rate of 9600
enter the following commands into the MASTER unit
AT - should return OK
AT+RENEW - restores to factory settings
AT+RESET - software reset
AT+ROLE1 - sets to Master
AT+INQ - searches for nearby Slave units
AT+CONN1 - connects to Slave Unit 1
************************************************************/
#include <SoftwareSerial.h>
SoftwareSerial btModuleSerial(2, 3); // RX, TX
byte PIN_RELAY_ENABLE = 7;
int PIN_EN_OUT = 4;
int PIN_STATE_IN = 5;
int PIN_LM35 = A0;

float SINGLE_PROBE_TIME_MS = 1000, TOTAL_PROBE_TIME_MS = 3000;  
float desiredTemperature = 28, TEMPERATURE_NUDGE = 0.1;

void setup() {
  // initialize digital pin LED_BUILTIN as an output.
  // REPRESENTS HEATING CIRCUIT OPERATION
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(PIN_RELAY_ENABLE, OUTPUT);

  //set aref as 5V
  analogReference(DEFAULT);
  
  // put your setup code here, to run once:
  btModuleSerial.begin(9600);
  Serial.begin(9600);

  sendCommand("AT");
  sendCommand("AT+ROLE0");
  sendCommand("AT+UUID0xFFE0");
  sendCommand("AT+CHAR0xFFE1");
  sendCommand("AT+NAMEbluino");
}

void sendCommand(const char * command){
  Serial.print("Command send :");
  Serial.println(command);
  btModuleSerial.println(command);
  //wait some time
  delay(2000);
  
  char reply[100];
  int i = 0;
  while (btModuleSerial.available()) {
    reply[i] = btModuleSerial.read();
    i += 1;
  }
  //end the string
  reply[i] = '\0';
  Serial.print(reply);
  Serial.println("Reply end");
}

void readSerial(){
  char reply[50];
  int i = 0;
  while (btModuleSerial.available()) {
    reply[i] = btModuleSerial.read();
    i += 1;
  }
  //end the string
  reply[i] = '\0';
  if(strlen(reply) > 0){
    Serial.print("New temperature is ");
    Serial.println(reply);
    String replyStr = String(reply);
    desiredTemperature = replyStr.toFloat();
  }
}

void printDouble( double val, unsigned int precision){
  // prints val with number of decimal places determine by precision
  // NOTE: precision is 1 followed by the number of zeros for the desired number of decimial places
  // example: printDouble( 3.1415, 100); // prints 3.14 (two decimal places)

   Serial.print (int(val));  //prints the int part
   Serial.print("."); // print the decimal point
   unsigned int frac;
   if(val >= 0)
       frac = (val - int(val)) * precision;
   else
       frac = (int(val)- val ) * precision;
   Serial.print(frac,DEC) ;
}  

float getTemperature() {
  int reading = analogRead(PIN_LM35);
  float millivolts = reading * (5000/1024.0);
  float celsius = millivolts / 10; //10mV por grau 
  /*float celsius = reading/9.31;*/

  char string[50];
  char tempString[6], desiredTempString[6];
  dtostrf(celsius, 4 /*min width*/, 2/*precision*/, tempString);
  dtostrf(desiredTemperature, 4 /*min width*/, 2/*precision*/, desiredTempString);
  sprintf(string, "Temperature (celsius) : (%s/%s)", tempString, desiredTempString);
  Serial.println(string);
  
  return celsius;
}

void heat(float milliSeconds) {
    // turn the LED on (HEATING CIRCUIT ENABLED)
    digitalWrite(LED_BUILTIN, HIGH);   
    digitalWrite(PIN_RELAY_ENABLE, HIGH);   
    // wait some time to heat
    delay(milliSeconds);
}

void cool(float milliSeconds) {
    // turn the LED off (HEATING CIRCUIT DISABLED)
    digitalWrite(LED_BUILTIN, LOW);   
    digitalWrite(PIN_RELAY_ENABLE, LOW);
    // wait some time to cool
    delay(milliSeconds);
}

void loop() {
  Serial.println("New temperature?");
  readSerial();

  // heat or cool resistor until desired temp reached
  // if it cant reach it in TOTAL_PROBE_TIME_MS, reset loop()
  int elapsed;
  for (elapsed = 0 ; elapsed < TOTAL_PROBE_TIME_MS  ; elapsed += SINGLE_PROBE_TIME_MS) {

    float temperature = getTemperature();
    if (temperature < desiredTemperature - TEMPERATURE_NUDGE) {// too cold
      Serial.println("Heating ...");
      heat(SINGLE_PROBE_TIME_MS);
    } else if (temperature > desiredTemperature + TEMPERATURE_NUDGE) { // too hot
      Serial.println("Cooling ...");
      cool(SINGLE_PROBE_TIME_MS);
    } else { //desired temperature reached 
      Serial.println("Mantaining ...");
      //maintain temperature
      heat(SINGLE_PROBE_TIME_MS/2);
      cool(SINGLE_PROBE_TIME_MS/2);
      break;
    }
  }
}
