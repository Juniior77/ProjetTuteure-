/*
* Bluetooh Vehicule
* Coder - VILAIRE Guillaume
* Download the App : https://github.com/Juniior77/ProjetTuteure-/blob/master/Arduino/ProjetTuteure/ProjetTuteure.ino
* This program controls a car by bluetooth reception
*/

#include <Servo.h>
#include <FastLED.h>
#include <Ultrasonic.h>

void readDistance();
void startParking();
void manoeuvre();
void avanceManoeuvre();
//void CAR_move(int direction, int speed_left, int speed_right, int direct);

Servo myservo;//create a object of servo,named as myservo

#define ENA_PIN   5 // ENA of DC motor driver module attach to pin5 of sunfounder uno board
#define ENB_PIN   6 // ENB of DC motor driver moduleattach to pin6 of sunfounder uno board
#define MOTOR_L_1 8 // left MOTOR_L_1 attach to pin8 
#define MOTOR_L_2 9 // left MOTOR_L_2 attach to pin9
#define MOTOR_R_1 10 //right  MOTOR_R_1 attach to pin10
#define MOTOR_R_2 11 //right MOTOR_R_2 attach to pin11
#define LED_PIN   13

#define LIGHT_LEFT_1_PIN  A0 //attach the left first Tracking module pinA0 to A0
#define LIGHT_LEFT_2_PIN  A1 //attach the left second Tracking module pinA0 to A1
#define LIGHT_MIDDLE_PIN  A2 //attach the module Tracking module pinA0 to A2
#define LIGHT_RIGHT_1_PIN A3 //attach the right  second Tracking module pinA0 to A3
#define LIGHT_RIGHT_2_PIN A4 //attach the right first Tracking module pinA0 to A4

#define repPlaceIdeal 450
#define repPlaceManoeuvre 90

#define kp 8
#define ki 0
#define kd 0


CRGB leds[4];
Ultrasonic ultraAV(7, 12);    //Capteur AV
Ultrasonic ultraAVD(4, 3);

byte dataIn[17];            //Variable for storing received data
int capt[5];
int dataAvAr;
int dataMotor1;
int dataMotor2;
int dataServoDir;
int dstAV;
int dstAVD;
int trajectoire;
int oldTrajectoire = 0;
int P;
int I;
int D;
int PID;

int PlaceManoeuvre = repPlaceManoeuvre;
int PlaceIdeal = repPlaceIdeal; //Pour une vitesse de 150 sur chaque moteur

void setup()
{
    pinMode(LED_BUILTIN, OUTPUT);
  
//    Serial.begin(115200);   //Sets the baud for serial data transmission    
      Serial.begin(9600);

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
  if(direct < 60){
    direct = 60;
  }
  if(direct > 120){
    direct = 120;
  }
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
    myservo.write(direct+6);
}

void readDistance(){
  dstAV = ultraAV.distanceRead();
  dstAVD = ultraAVD.distanceRead();
  delay(10);
}
void startParking(){
  
  int dstBefore = ultraAVD.distanceRead();  //Largeur avant de trouver place
  int timeVoiture = 0;    //Temps que met la voiture pour trouver la longueur ideal de la place
  int dstEnPlus = 0;
  
    //Serial.print("Distance Before: ");
    //Serial.print(dstBefore);
    //Serial.print("\n");
    readDistance();
    
  //Tant que la distance du mur est pareil
  while(dstAVD > (dstBefore - 3) && dstAVD < (dstBefore + 3))
  { 
    timeVoiture++;
    readDistance();
    //La voiture avance doucement tant que la distance n'augmente pas ou diminue pas
    CAR_move(1, 150, 150, 90);
  }
  
  //On stoppe la voiture
  CAR_move(1, 0, 0, 90);
  delay(200);

  //On lie la distance AVD pour tester lecartement de la place de parking dstDroit2
  int largeurPlace = ultraAVD.distanceRead();
    //Serial.print("Largeur Place: ");
    //Serial.print(largeurPlace);
    //Serial.print("\n");
    delay(500);
    
  //Si dstDroit2 est supérieur a dstDroit1 alors on engage la procédure
  if(largeurPlace > dstBefore)
  {
    int dstPlaceLibre = 0;    // Incrémenter pour tester si la voiture passe dans la place
    //Il y à peut etre une place
    //La voiture avance tant que la distance est plus ou moin pareil
    while(dstPlaceLibre <= PlaceIdeal)   //On garde une marge d'erreur de -1 valeur +1
    {
      //La voiture avance jusqu'a ce quon soit sur qu'il y est la place de se garer
      dstPlaceLibre++;
      dstAVD = ultraAVD.distanceRead();
      CAR_move(1, 170, 170, 90);
    }
    CAR_move(1, 0, 0, 90);
    //Serial.print("dstPlaceLibre: ");
    //Serial.print(dstPlaceLibre);
    //Serial.print("\n");
    delay(250);
    
    //Serial.print("dstEnPlus: ");
    //Serial.print(dstEnPlus);
    //Serial.print("\n");
    delay(250);
    
    
    while(dstEnPlus <= PlaceManoeuvre){
      CAR_move(1, 170, 170, 90);
      delay(10);
      dstEnPlus++;
    }
   
    CAR_move(1, 0, 0, 90);

    //Serial.print("dstEnPlus: ");
    //Serial.print(dstEnPlus);
    //Serial.print("\n");
    delay(250);
   
    //Ici la voiture est positionner pour manoeuvré
    manoeuvre();
  }
  else
  {
    //Il n'y a pas de place, la distance est plus court que l'initial
    PlaceManoeuvre = repPlaceManoeuvre;
    PlaceIdeal = repPlaceIdeal;
  }
}

void manoeuvre(){
  
  int marcheAR1 = 95;
  int marcheAR2 = 0;
  int marcheAR3 = 95;
  
  CAR_move(0, 0, 0, 120);
  delay(250);
  while(marcheAR1 != 0)
  {
    marcheAR1--;
    //dstAVD = ultraAVD.distanceRead();
    delay(10);
    CAR_move(0,170,170,120);
  }
  CAR_move(0, 0, 0, 90);
  delay(250);

    while(marcheAR2 != 0)
  {
    marcheAR2--;
    //dstAVD = ultraAVD.distanceRead();
    delay(10);
    CAR_move(0,170,170,90);
  }

  CAR_move(0, 0, 0, 60);
  delay(250);

    while(marcheAR3 != 0)
  {
    marcheAR3--;
    //dstAVD = ultraAVD.distanceRead();
    delay(10);
    CAR_move(0,170,170,60);
  }
  CAR_move(0, 0, 0, 90);
  return loop;
}
void calculPID(){
  P = trajectoire;
  I += trajectoire;
  D = trajectoire - oldTrajectoire;
  PID = (kp * P)+(ki * I) + (kd * D);
  oldTrajectoire = trajectoire;
}

void loop()
{
  
capt[4] = analogRead(LIGHT_LEFT_1_PIN);
capt[3] = analogRead(LIGHT_LEFT_2_PIN);
capt[2] = analogRead(LIGHT_MIDDLE_PIN); 
capt[0] = analogRead(LIGHT_RIGHT_2_PIN);
capt[1] = analogRead(LIGHT_RIGHT_1_PIN);

//0 0 0 0 1
if(capt[0] < 150 && capt[1] < 980 && capt[2] < 150 && capt[3] < 150 && capt[4] > 150)
{
  trajectoire = -4;
}
//0 0 0 1 1 
else if(capt[0] < 150 && capt[1] < 980 && capt[2] < 150 && capt[3] > 150 && capt[4] > 150)
{
  trajectoire = -3;
}
//0 0 0 1 0
else if(capt[0] < 150 && capt[1] < 980 && capt[2] < 150 && capt[3] > 150 && capt[4] < 150)
{
  trajectoire = -2;
}
//0 0 1 1 0
else if(capt[0] < 150 && capt[1] < 980 && capt[2] > 150 && capt[3] > 150 && capt[4] < 150)
{
  trajectoire = -1;
}
//0 0 1 0 0
else if(capt[0] < 150 && capt[1] < 980 && capt[2] > 150 && capt[3] < 150 && capt[4] < 150)
{
  trajectoire = 0;
}
//0 1 1 0 0
else if(capt[0] < 150 && capt[1] > 980 && capt[2] > 150 && capt[3] < 150 && capt[4] < 150)
{
  trajectoire = 1;
}
//0 1 0 0 0
else if(capt[0] < 150 && capt[1] > 980 && capt[2] < 150 && capt[3] < 150 && capt[4] < 150)
{
  trajectoire = 2;
}
//1 1 0 0 0
else if(capt[0] > 150 && capt[1] > 980 && capt[2] < 150 && capt[3] < 150 && capt[4] < 150)
{
  trajectoire = 3;
}
//1 0 0 0 0
else if(capt[0] > 150 && capt[1] < 980 && capt[2] < 150 && capt[3] < 150 && capt[4] < 150)
{
  trajectoire = 4;
}
else{
  trajectoire = 15;
}
calculPID();
/*
Serial.print("Capt1: ");
Serial.print(capt[0]);
Serial.print("\n");

Serial.print("Capt2: ");
Serial.print(capt[1]);
Serial.print("\n");

Serial.print("Capt3: ");
Serial.print(capt[2]);
Serial.print("\n");

Serial.print("Capt4: ");
Serial.print(capt[3]);
Serial.print("\n");

Serial.print("Capt5: ");
Serial.print(capt[4]);
Serial.print("\n");
Serial.print("\n");
Serial.print("Trajectoire: ");
Serial.print(trajectoire);
Serial.print("\n");
Serial.print("PID: ");
Serial.print(PID);
Serial.print("\n");*/
if(trajectoire != 15)
  CAR_move(1, 190, 190, 90 + PID);
else
  CAR_move(1, 0, 0, 90);

/*  Serial.flush();
   if(Serial.available())      // Send data only when you receive data:
   {
      Serial.readBytes(dataIn, 17);        //Read the incoming data & store into data

      for(int i = 0; i < 17; i++)
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

      if(dataIn[16] == 0){
      
      dataAvAr = dataIn[0];
      dataMotor1 = dataIn[1];
      dataMotor2 = dataIn[2];
      dataServoDir = dataIn[3];

      leds[0].r = dataIn[4];
      leds[1].r = dataIn[5];
      leds[3].r = dataIn[6];
      leds[2].r = dataIn[7];

      leds[0].g = dataIn[8];
      leds[1].g = dataIn[9];
      leds[3].g = dataIn[10];
      leds[2].g = dataIn[11];

      leds[0].b = dataIn[12];
      leds[1].b = dataIn[13];
      leds[3].b = dataIn[14];
      leds[2].b = dataIn[15];
     
      CAR_move(dataAvAr, dataMotor1, dataMotor2, dataServoDir);
      FastLED.show();
      }
      else if(dataIn[16] == 1)
      {
        startParking();
      }
   }
   */
}
