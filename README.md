# RC Car Developement

Disigning and implementing a remote controlled rover from zero to hero!

# Results

![final1](docs/final1.jpg)
![final2](docs/final2.jpg)

# Known bugs:
  1. When powered on, one of the wheels rotate for few seconds until uC reach init sequence. This is because of motors driver (DRV8835). EN pins works on opposite logic level. E.g. to rotate boot wheels you need to do something like this: EN1=HIGH, EN2=LOW. When you power on circuit, both pins are connected to GND this mean that one of the motors will start rotating.
  **Solve?** - Use one or two transistors to sync logical levels. 
  
  2. Power ON/OFF Button - This is not a bug but such a button is a MUST because some powerbanks may not have such a button and is not ok to connect/disconnect battery when you want car to work or not.