# ADXL362 Java and AndroidThings Library
ADXL362B Accelerometer Java and Android Things Library

## Java Documentation 

This is a pure Java interfacing of Accelerometer ADXL362 with Raspberry Pi using pi4j on Raspbian.
This should work on any other OS for Raspberry Pi (or any other development platform, may be), which have Oracle JDK and pi4j support.
X, Y, Z axis values are obtained as integers.
Temperature values are obtained as integers.

## Requirements
 
 1. Raspberry Pi with raspbian
 2. Orcale JDK (latest is recommended)
 3. pi4j. Installation instructions [here](http://pi4j.com/install.html)

## Sample usage

```java
try {
     Adxl362 adxl362 = new Adxl362(SpiChannel.CS0, 5000000, SpiMode.MODE_0);
     adxl362.doSoftReset();
     adxl362.beginMeasurement();
     while(true) {
                 System.out.println("X is: " + adxl362.readXData() + ", Y is: " + adxl362.readYData() + ", Z is: " + adxl362.readZData() + ", TEMP is: " + adxl362.readTempData());
                Thread.sleep(1000);
     }
    } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
```



TODO: Handle interrupt based interfacing.

TODO: Android Things library. Coming soon.
