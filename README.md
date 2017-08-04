## RC Car Developement

Designing and implementing a remote controlled rover from zero to hero!

## Details:
  1. Project information: [RC_Car_WordDoc.odt](docs/RC_Car_Developement.odt)
  2. Required list of components: [here](Components.md)
  3. How to build: [here](How-to-build.md)

*Costs:* ~60EUR

## Results

![final1](docs/final1.jpg)
![final2](docs/final2.jpg)

## Connections 

![connections](docs/connections.jpg)

#### 1. Connection indicator LED
This is a LED whose meant is to indicate whether there is a connection established with car or not.
It have three states:
  1. Turned off - this mean that a connection is established with car and no further incoming connections will be accepted;
  2. Blinking - No clients connected to car AP nor Sockets are oppened;
  3. Blinking faster - Clients connected to AP but no sockets oppened. If use Java developed app, possibly application is not running.

#### 2. Traffic indicator LED
This is a LED whose meant is to act like any regular ethernet traffic indicator. Whether there is a data exchange between client and car this led will blink. Efficient feedback to confirm if your data is being received by car and vice-versa.

#### 3. Left QRE1113 IR Sensor
Connector for left wheel sensor.

#### 4. RESET Button

## How to control the car

![car](carinfo.jpg)
	
