
/*

*/

#include <SoftwareSerial.h>
int bluetoothRX = 0;
int bluetoothTX = 1;
int pinMotorA1 = 2;
int pinMotorA2 = 13;
int pinMotorA = 5;
int pinMotorB1 = A2;
int pinMotorB2 = A1;
int pinMotorB = 6;
int pinMotorC1 = 7;
int pinMotorC2 = 8;
int pinMotorC = 10;
int pinMotorD1 = 12;
int pinMotorD2 = 9;
int pinMotorD = 11;
int trigPin = 3;
int echoPin = 4;

int ledPin = 13;

long t = millis();


SoftwareSerial bluetooth(bluetoothRX, bluetoothTX);

void setup(){
  Serial.begin(9600);  // can't run both Serial and SoftwareSerial
  //Serial.println("F");
  //pinMode(ledPin, OUTPUT);
  pinMode(bluetoothRX, INPUT);
  pinMode(bluetoothTX, OUTPUT);
  pinMode(pinMotorA1, OUTPUT);
  pinMode(pinMotorA2, OUTPUT);
  pinMode(pinMotorA, OUTPUT);
  pinMode(pinMotorB1, OUTPUT);
  pinMode(pinMotorB2, OUTPUT);
  pinMode(pinMotorB, OUTPUT);
  pinMode(pinMotorC1, OUTPUT);
  pinMode(pinMotorC2, OUTPUT);
  pinMode(pinMotorC, OUTPUT);
  pinMode(pinMotorD1, OUTPUT);
  pinMode(pinMotorD2, OUTPUT);
  pinMode(pinMotorD, OUTPUT); 
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  bluetooth.begin(9600); 
  
}

void loop(){
  long time, cm,x;
  pinMode(trigPin, OUTPUT);
  digitalWrite(trigPin,LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  pinMode(echoPin, INPUT);
  time= pulseIn(echoPin, HIGH);
  
  //inches= time/74/2;
  
  cm= time/29/2;
  
  x=millis()-t;
  
  if(x>100){
    Serial.print(cm);
    Serial.println();
    t=millis();
  }
  
  if(cm <5){
      digitalWrite(pinMotorA1, LOW);
      digitalWrite(pinMotorA2, LOW);
      digitalWrite(pinMotorA, 0);
      digitalWrite(pinMotorB1, LOW);
      digitalWrite(pinMotorB2, LOW);
      digitalWrite(pinMotorB, 0);
      digitalWrite(pinMotorC1, LOW);
      digitalWrite(pinMotorC2, LOW);
      digitalWrite(pinMotorC, 0);
      digitalWrite(pinMotorD1, LOW);
      digitalWrite(pinMotorD2, LOW);
      digitalWrite(pinMotorD, 0);
      
      delay(100);
  }
  else //go straight
  
      digitalWrite(pinMotorA1, LOW);
      digitalWrite(pinMotorA2, HIGH);
      digitalWrite(pinMotorA, 255);
      digitalWrite(pinMotorB1, LOW);
      digitalWrite(pinMotorB2, HIGH);
      digitalWrite(pinMotorB, 255);
      digitalWrite(pinMotorC1, LOW);
      digitalWrite(pinMotorC2, HIGH);
      digitalWrite(pinMotorC, 255);
      digitalWrite(pinMotorD1, LOW);
      digitalWrite(pinMotorD2, HIGH);
      digitalWrite(pinMotorD, 255);
      
}
  
  
  
