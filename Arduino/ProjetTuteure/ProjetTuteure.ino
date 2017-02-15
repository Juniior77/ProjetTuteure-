/*
* Bluetooh Basic: LED ON OFF - Avishkar
* Coder - Mayoogh Girish
* Website - http://bit.do/Avishkar
* Download the App : https://github.com/Mayoogh/Arduino-Bluetooth-Basic
* This program lets you to control a LED on pin 13 of arduino using a bluetooth module
*/

#include <Servo.h>

Servo myservo;//create a object of servo,named as myservo

#define ENA_PIN   5 // ENA of DC motor driver module attach to pin5 of sunfounder uno board
#define ENB_PIN   6 // ENB of DC motor driver moduleattach to pin6 of sunfounder uno board
#define MOTOR_L_1 8 // left MOTOR_L_1 attach to pin8 
#define MOTOR_L_2 9 // left MOTOR_L_2 attach to pin9
#define MOTOR_R_1 10 //right  MOTOR_R_1 attach to pin10
#define MOTOR_R_2 11 //right MOTOR_R_2 attach to pin11



byte data[3];            //Variable for storing received data
int dataConv;
int dataMotor1;
int dataMotor2;
int dataServoDir;
void setup()
{
    Serial.begin(9600);   //Sets the baud for serial data transmission    

    pinMode(ENA_PIN, OUTPUT);
    pinMode(ENB_PIN, OUTPUT);
    pinMode(MOTOR_L_1, OUTPUT);
    pinMode(MOTOR_L_2, OUTPUT);
    pinMode(MOTOR_R_1, OUTPUT);
    pinMode(MOTOR_R_2, OUTPUT);
    myservo.attach(2);//servo attach to pin2
}
void loop()
{
   if(Serial.available() > 0)      // Send data only when you receive data:
   {
    
      Serial.readBytes(data, 3);        //Read the incoming data & store into data
      dataMotor1 = data[0];
      dataMotor2 = data[1];
      dataServoDir = data[2];
      if(dataMotor1 <= 255 && data >127)
      {
        dataMotor1 -= 128;
      }
      else
      {
        dataMotor1 += 128;
      }
      
Serial.print("Moteur1: ");
Serial.print(dataMotor1);
Serial.print("\n");
      
      
      if(dataMotor2 <= 255 && dataMotor2 >127)
      {
        dataMotor2 -= 128;
      }
      else
      {
        dataMotor2 += 128;
      }

Serial.print("Moteur2: ");
Serial.print(dataMotor2);
Serial.print("\n");
      
      
      if(dataServoDir <= 255 && dataServoDir >127)
      {
        dataServoDir -= 128;
      }
      else
      {
        dataServoDir += 128;
      }

Serial.print("Servo: ");
Serial.print(dataServoDir);
Serial.print("\n");
              
      CAR_move(1,dataMotor1,dataMotor2,dataServoDir);
   }
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
