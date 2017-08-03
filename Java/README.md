# JAVA Source code

Not updated as requirements for this code was not to be published!
However, there is an password protected archive with Java source code: **controldeveloper.zip**

## Major to original project

PS: Old code is just commented.

#### A. Under **src/hsrt/mec/controldeveloper/core/com**:
  1. Added new class: **WiFiCard.java**
  2. Added new class: **WiFiCardHandler.java**
  3. Changed **BackgroundControl.java**
      * Added function *private void waitForResponse(int timeout)*
      * Changed line 59;
      * Adeed lines 65-82;
  4. Changed **ComHandler.java**
      * Changed function **private MotorCommand computeDirectionCommand(IDirection command)**;
      *	Changed function **private MotorCommand computeGearCommand(IGear command)**
  5. Changed **MotorCommand.java**
      * Rewritten **String getCommandString()**

#### B. Under **src/hsrt/mec/controldeveloper/core/com/test**:
  1. Added new class: **WiFiCardHandlerTest.java** -> this class is used to test Wi-Fi connection with the car. The model is the same like the other as the other tests.

#### c. Under **src/hsrt/mec/controldeveloper/io**
  1. Added new class **WiFi.java**
  
#### D. Under **src/hsrt/mec/controldeveloper/util**
  1. Changed **Globals.java**
      * Appended some more global variables.
      
