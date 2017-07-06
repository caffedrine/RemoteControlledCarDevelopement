# RC Car Developement

Designing and implementing a remote controlled rover from zero to hero!

# List of components
Before proceeding, make sure you have the possibility to print the frame of car using a 3D Printer! It is in folder 3D_Design. Also don't forget to print the wheels!
Also make sure you have the possibility to create Printed Circuit Board (PCB).

### Main components required:
  1. **2x QRE1113** - SparkFun RedBot Sensor - Line Follower
     https://eckstein-shop.de/SparkFun-RedBot-Sensor-Line-Follower-EN
  2. **2x DC Motors + GearBox** - Recommended gear ratio: 120:1
     http://www.exp-tech.de/dagu-dc-gear-motor-paar-90-degree-shaft-120-1
  3. **1x DRV8835 Dual Motor Driver** - In case you don't want to make your own H bridges.
     http://www.exp-tech.de/drv8835-dual-motor-driver-carrier 
  4. **1x Espressif ESP-WROOM-32** - This is the WiFi 2.4 GHz module used to control car.
     https://eckstein-shop.de/Espressif-ESP-WROOM-32-EN
  5. **1x Wifi Module Adapter** - This will make life easier
     https://eckstein-shop.de/Adapter-Breakout-Board-for-ESP-32S-ESP32-ESP-WROOM-32-Wireless-Bluetooth-Module
  6. **1x Plastic Wheel Ball**
     http://www.exp-tech.de/pololu-ball-caster-with-1-2-plastic-ball?gclid=CKmr-tb2h9QCFUklgQode0oNgg
  7. **1x Powerbank** - You can also find this on Amazon at better prices.
     https://www.tln-werbemittel.de/powerbanks/powerbank-akku-tower-38-2600.html?force_sid=856833aa6188c21b70c41b708ace8f3c&br=1&gclid=CMzsyZ-ZiNQCFRmBswodprYAWQ
  8. **2xRubber O-Rings** - In order to get some aderency on wheels.
     https://www.hug-technik.com/shop/product_info.php?info=p20325_praezisions-o-ring-30-00-x-2-60-mm-nbr70.html
  9. **1xUSB Micro B Connector** - http://uk.farnell.com/amphenol-fci/10104110-0001lf/micro-usb-2-0-type-b-receptacle/dp/2293753
	 
### Electrical components:
  1. 1x LM317 Voltage regulator
  2. 2x Reasonable leds
  3. 10x Pinheaders Male
  4. 25x Pinheader Female
  5. 2x 20 Double Pinheader Female
  6. 2x Reasonable leds
  7. 2x 10K Resistors
  8. 1x 12k Resistors
  9. *1x 390 Ohm Resistor*
  10. *1x240 Ohm Resistor*
  11. Electrolithic capacitors: 1x100uF, 2x220uF or lower.
  12. Ceramic capacitors: 1x10uF  

### Components/Tools required to build prototype
  1. **1x FTDI1232** - This is an adapter which allows you to write program to WiFi Module. You can make your own it's not hard but be aware that you need to convert logical levels from USB from [0-5V] to [0-3.3V]
     https://www.amazon.de/AZDelivery-Adapter-FT232RL-Serial-Arduino/dp/B01N9RZK6I/ref=sr_1_1?ie=UTF8&qid=1499328221&sr=8-1-spons&keywords=ftdi&psc=1
  2. **Optional:** If you want to test and debug code easily, you may want to pick up an Sparkfun ESP32 Thing - Developement board. Build code on that board and upload to ESP32-WROOM when it is finished.
     http://www.exp-tech.de/en/sparkfun-esp32-thing?___from_store=de

#### Other components
Screws, wires, zipties and stuff like this :)
	 
**PS: If the links expired, let those guys know: http://web.archive.org **
	 
# Results

![final1](docs/final1.jpg)
![final2](docs/final2.jpg)

# Known bugs:
  1. When powered on, one of the wheels rotate for few seconds until uC reach init sequence. This is because of motors driver (DRV8835). There are two EN (enable) pins which works on opposite logic levels. E.g. to rotate both wheels you need to do something like this: EN1=HIGH, EN2=LOW. However when you power on circuit, both pins are connected to GND this mean that one of the motors will start rotating.
  **Solve?** - Use transistors to sync logical levels.
  
  2. Power ON/OFF Button - This is not a bug but such a button is a MUST because some powerbanks may not have such a button and is not ok to connect/disconnect battery whenever you want to start/stop the car..