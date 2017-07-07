# Arduino Programs used

  1. **ControlDeveloper_ESP32Thing** - Program used to test and develop final program. It is compatible with ESP32Thing from Sparkfun and is almost the same with ESP32WROOM. Only few pins are changed as pinmap on WROOM is different.
  
  2. **ControlDeveloper_ESP32Wroom** - Program uploaded on the car board (ESP32WROOM).
  
  3. **ControlDeveloper_PID_Tests** - Program used to test stability loop for the car. Basicaly this is the algoritm used in order to get stable car movement
  
  4. **docs** - Pinmaps for all boards and other things which will help you to reproduce the project.
  
### Access Point credentials
In case you want to test or change connection credentials: ***connection.h***
```
const char *AP_Name   = "LAB_ROVER_1";
const char *AP_Passwd = "LabRover@Passwd";
```