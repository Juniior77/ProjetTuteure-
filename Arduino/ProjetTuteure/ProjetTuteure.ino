/*
* Bluetooh Vehicule
* Coder - VILAIRE Guillaume
* Download the App : https://github.com/Juniior77/ProjetTuteure-/blob/master/Arduino/ProjetTuteure/ProjetTuteure.ino
* This program controls a car by bluetooth reception
*/

#include <Servo.h>
#include <FastLED.h>

Servo myservo;//create a object of servo,named as myservo

#define ENA_PIN   5 // ENA of DC motor driver module attach to pin5 of sunfounder uno board
#define ENB_PIN   6 // ENB of DC motor driver moduleattach to pin6 of sunfounder uno board
#define MOTOR_L_1 8 // left MOTOR_L_1 attach to pin8 
#define MOTOR_L_2 9 // left MOTOR_L_2 attach to pin9
#define MOTOR_R_1 10 //right  MOTOR_R_1 attach to pin10
#define MOTOR_R_2 11 //right MOTOR_R_2 attach to pin11
#define LED_PIN   13
CRGB leds[4];

#define LIGHT_LEFT_1_PIN  A0 //attach the left first Tracking module pinA0 to A0
#define LIGHT_LEFT_2_PIN  A1 //attach the left second Tracking module pinA0 to A1
#define LIGHT_MIDDLE_PIN  A2 //attach the module Tracking module pinA0 to A2
#define LIGHT_RIGHT_1_PIN A3 //attach the right  second Tracking module pinA0 to A3
#define LIGHT_RIGHT_2_PIN A4 //attach the right first Tracking module pinA0 to A4

<<<<<<< HEAD
byte dataIn[16];            //Variable for storing received data
=======
byte sensorValue[5];

byte data[4];            //Variable for storing received data
>>>>>>> origin/Totoss-Path
int dataAvAr;
int dataMotor1;
int dataMotor2;
int dataServoDir;
void setup()
{
    pinMode(LED_BUILTIN, OUTPUT);
  
    Serial.begin(115200);   //Sets the baud for serial data transmission    

    pinMode(ENA_PIN, OUTPUT);
    pinMode(ENB_PIN, OUTPUT);
    pinMode(MOTOR_L_1, OUTPUT);
    pinMode(MOTOR_L_2, OUTPUT);
    pinMode(MOTOR_R_1, OUTPUT);
    pinMode(MOTOR_R_2, OUTPUT);
    
    myservo.attach(2);//servo attach to pin2

    FastLED.addLeds<WS2811, LED_PIN, RGB>(leds, 4);
    for(int i = 0; i < 4; i++)
    {
      leds[i].r = 0;
      leds[i].g = 0;
      leds[i].b = 0;
    }
    FastLED.show();
    Serial.print("Module prêt ! \n");
}
void CAR_move(int direction, int speed_left, int speed_right, int direct)
{
    switch(direction)
    {
        //car move forward with speed 180
        case 0: digitalWrite(MOTOR_L_1,HIGH);digitalWrite(MOTOR_L_2,LOW);//left motor clockwise rotation
          digitalWrite(MOTOR_R_1,HIGH);digitalWrite(MOTOR_R_2,LOW);
          break;//right motor clockwise rotation
        //car move back with speed 180
        case 1: digitalWrite(MOTOR_L_1,LOW);digitalWrite(MOTOR_L_2,HIGH);
          digitalWrite(MOTOR_R_1,LOW);digitalWrite(MOTOR_R_2,HIGH);
          break;
        default: break;
    }
    analogWrite(ENA_PIN,speed_left);//write speed_left to ENA_PIN,if speed_left is high,allow left motor rotate
    analogWrite(ENB_PIN,speed_right);//write speed_right to ENB_PIN,if speed_right is high,allow right motor rotate
    myservo.write(direct);
}
void followline(){
  sensorValue[0] = A0;
  sensorValue[1] = A1;
  sensorValue[2] = A2;
  sensorValue[3] = A3;
  sensorValue[4] = A4;
}
void sendsensorsvalue(){
      for(int i=0; i<5; i++){ 
      Serial.print(sensorValue[i]);
      //Serial.print('+');
      }
      Serial.print('~');
      delay(10);
}

void loop()
{
  Serial.flush();
   if(Serial.available())      // Send data only when you receive data:
   {
<<<<<<< HEAD
      Serial.readBytes(dataIn, 16);        //Read the incoming data & store into data

      for(int i = 0; i < 16; i++)
=======
    digitalWrite(LED_BUILTIN, HIGH);
    sendsensorsvalue();
      Serial.readBytes(data, 4);        //Read the incoming data & store into data
      dataAvAr = data[0];
      dataMotor1 = data[1];
      dataMotor2 = data[2];
      dataServoDir = data[3];
      
      if(dataAvAr <= 255 && dataAvAr >127)
      {
        dataAvAr -= 128;
      }
      else
      {
        dataAvAr += 128;
      }
      
      if(dataMotor1 <= 255 && dataMotor1 >127)
      {
        dataMotor1 -= 128;
      }
      else
      {
        dataMotor1 += 128;
      }
/*      
Serial.print("Moteur1: ");
Serial.print(dataMotor1);
Serial.print("\n");
*/      
      
      if(dataMotor2 <= 255 && dataMotor2 >127)
      {
        dataMotor2 -= 128;
      }
      else
>>>>>>> origin/Totoss-Path
      {
          if(dataIn[i] <= 255 && dataIn[i] >127)
          {
            dataIn[i] -= 128;
          }
          else
          {
            dataIn[i] += 128;
          }
      }

      
      dataAvAr = dataIn[0];
      dataMotor1 = dataIn[1];
      dataMotor2 = dataIn[2];
      dataServoDir = dataIn[3];

      leds[0].r = dataIn[4];
      leds[1].r = dataIn[5];
      leds[2].r = dataIn[6];
      leds[3].r = dataIn[7];

      leds[0].g = dataIn[8];
      leds[1].g = dataIn[9];
      leds[2].g = dataIn[10];
      leds[3].g = dataIn[11];

      leds[0].b = dataIn[12];
      leds[1].b = dataIn[13];
      leds[2].b = dataIn[14];
      leds[3].b = dataIn[15];
     
      CAR_move(dataAvAr, dataMotor1, dataMotor2, dataServoDir);
      FastLED.show();
   }
}


