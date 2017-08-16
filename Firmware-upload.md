# Firmware

# 1. Connection

Debugging and firmware uploading share the same connection. No matter if you want to debug or to upload a new version of firmware, connection instructions are the same, always!

The connection to car is via Serial port with a baud rate of **115200**. The other settings are default and should not be changed.

Usually the computer already comes with serial ports **BUT** it's logic level is 0-5V which makes it incompatible with the designed car as it equire 0-3.3V as operable logical levels.

In order to do this, it's recommended to use a serial adapter such as the following one: 

![serial adapter](docs/usb-ttl-ft232rl-pinout.png)

WARNING: Don't forget to set juper on 3.3V, as in picture:
![jumper](docs/serial_jumper.jpg)

Don't forget also about mini USB cable used to connect adapter to computer:
![miniusb cable](docs/miniusb_cable.jpg)

And the witing should looks like this:
![serial connection](docs/serial_connection_pins.jpg)

