String Incoming_value = "0";
void setup() 
{
  Serial.begin(9600);         
  pinMode(13, OUTPUT);        
  pinMode(12, OUTPUT);        
  pinMode(11, OUTPUT);        
  pinMode(10, OUTPUT);        
  pinMode(9, OUTPUT);        
  pinMode(8, OUTPUT);        
  pinMode(7, OUTPUT);        
  pinMode(6, OUTPUT);        
}
void loop()
{
  if(Serial.available() > 0)  
  {
    Incoming_value = Serial.readString();     
    Serial.print(Incoming_value);   
    Serial.print("\n");        //New line 
  if(Incoming_value == "a1")
  {  
    digitalWrite(13, HIGH);
    Serial.print("One ");
  }
  else
    digitalWrite(13, LOW); 
  
  if(Incoming_value == "b1")
    digitalWrite(12, HIGH);
  else
    digitalWrite(12, LOW); 
  
  if(Incoming_value == "c1")
    digitalWrite(11, HIGH); 
  else
    digitalWrite(11, LOW); 
  
  if(Incoming_value == "d1")
    digitalWrite(10, HIGH);
  else
    digitalWrite(10, LOW); 
  
  if(Incoming_value == "e1")
    digitalWrite(9, HIGH); 
  else
    digitalWrite(9, LOW); 
  
  if(Incoming_value == "f1")
    digitalWrite(8, HIGH);
  else
    digitalWrite(8, LOW);

  if(Incoming_value == "g1")
    digitalWrite(7, HIGH); 
  else
    digitalWrite(7, LOW); 
  
  if(Incoming_value == "h1")
    digitalWrite(6, HIGH);
  else
    digitalWrite(6, LOW); 
  if(Incoming_value=="y")
    { 
    digitalWrite(13, HIGH); 
    digitalWrite(12, HIGH); 
    digitalWrite(11, HIGH); 
    digitalWrite(10, HIGH); 
    digitalWrite(9, HIGH); 
    digitalWrite(8, HIGH); 
    digitalWrite(7, HIGH); 
    digitalWrite(6, HIGH); 
    }
  if(Incoming_value=="x")
  {
    digitalWrite(13, LOW); 
    digitalWrite(12, LOW); 
    digitalWrite(11, LOW); 
    digitalWrite(10, LOW); 
    digitalWrite(9, LOW); 
    digitalWrite(8, LOW); 
    digitalWrite(7, LOW); 
    digitalWrite(6, LOW);
  }  
  }                            
} 