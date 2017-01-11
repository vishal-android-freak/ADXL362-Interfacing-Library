# ADXL362 Java and AndroidThings Library
ADXL362B Accelerometer Java and Android Things Library

# Java Documentation 

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



# Android Things Documentation

##How to use the library

1. Clone the repository `git clone https://github.com/vishal-android-freak/ADXL362-Interfacing-Library.git`
2. Clone the sample project template `https://github.com/androidthings/new-project-template.git` for android things or create your own project in Android studio following https://developer.android.com/things.
3. In Android Studio, go to File > New > Import Module.
4. Browse for the **ADXL362-Interfacing-Library** cloned folder.
5. In the 'Module name' section type `:adxl`.
6. Right click on the **app** folder and select 'Open Module Settings'.
7. Go to the 'Dependencies' section and click on the '+' and select 'Module dependency'.
8. Select ':adxl' and click Ok. Done!

##Sample Usage

```java

import vaf.vishal.Adxl362;

.....

// Access the environmental sensor directly:

Adxl362 adxl;

try {
    aadxl = new Adxl362("SPI0.0", 5000000, SpiDevice.MODE0);
} catch(IOException e) {
  // couldn't configure the device...
}

//Read X, Y, Z axis and Temperature sensor values values in centimeters.

try {
    float[] values = adxl.readXYZTempData();
    Log.d("ADXL", "X is: " + values[0] + ", Y is: " + values[1] + ", Z is: " + values[2] + ", TEMP is: " + values[3]);
} catch(IOException e) {
  //error reading values.
}

//close the accelerometer after finished.
try {
    adxl.close();
} catch(IOException e) {
  //error closing sensor.
}

```

If you need to read sensor values continuously, you can register the Hcsr04 with the system and listen for sensor values using the [Sensor APIs](https://developer.android.com/guide/topics/sensors/sensors_overview.html)

```java

SensorManager mSensorManager = getSystemService(Context.SENSOR_SERVICE);
SensorEventListener mListener = ...;
Adxl362AccelerometerDriver mSensorDriver;

mSensorManager.registerDynamicSensorCallback(new SensorManager.DynamicSensorCallback() {
    @Override
    public void onDynamicSensorConnected(Sensor sensor) {
          mSensorManager.registerListener(mListener, sensor,
                  SensorManager.SENSOR_DELAY_NORMAL);
    }
});

try {
    mSensorDriver = new Adxl362AccelerometerDriver("SPI0.0", 5000000, SpiDevice.MODE0);
    mSensorDriver.register();
} catch (IOException e) {
    // Error configuring sensor
}

// Unregister and close the driver when finished:

mSensorManager.unregisterListener(mListener);
mSensorDriver.unregister();
try {
    mSensorDriver.close();
} catch (IOException e) {
    // error closing sensor
}

```

TODO: Handle interrupt based interfacing.

#License

MIT License

Copyright (c) 2017 Vishal Dubey

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
