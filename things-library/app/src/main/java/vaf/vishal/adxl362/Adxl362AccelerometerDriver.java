package vaf.vishal.adxl362;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.google.android.things.pio.SpiDevice;
import com.google.android.things.userdriver.UserDriverManager;
import com.google.android.things.userdriver.UserSensor;
import com.google.android.things.userdriver.UserSensorDriver;
import com.google.android.things.userdriver.UserSensorReading;

import java.io.IOException;

/**
 * @author Vishal Dubey (vishal-android-freak)
 * @link https://github.com/vishal-android-freak/ADXL362-Interfacing-Library
 * Android Things library for interfacing ADXL362 3 Axis accelerometer with Raspberry Pi 3 (Driver)
 * X, Y and Z axis values are obtained as integers.
 * Temperature values are obtained as integers.
 * TODO: Handle interrupt based interfacing
 */


public class Adxl362AccelerometerDriver implements AutoCloseable {

    private static final String TAG = Adxl362AccelerometerDriver.class.getSimpleName();
    private static final String DRIVER_NAME = "ADXL362Accelerometer";
    private static final String DRIVER_VENDOR = "Sparkfun";
    private static final int DRIVER_VERSION = 1;

    private Adxl362 mDevice;
    private UserSensor mUserSensor;

    /**
     * Creates a new ADXL362 driver instance
     * @param spiPort - can be either SPI0.0 or SPI0.1 (for Raspberry Pi)
     * @param frequencyInHz - This will vary according to the peripheral to be interfaced, default should be 1MHz.
     *                      ADXL362 has a range of 1MHz to 8MHz. Tested on 5MHz
     * @param mode - Modes are MODE_0, MODE_1, MODE_2, MODE_3.
     *             ADXL362 works on MODE_0
     * @throws IOException
     */
    public Adxl362AccelerometerDriver(String spiPort, int frequencyInHz, int mode) throws IOException {
        mDevice = new Adxl362(spiPort, frequencyInHz, mode);
    }

    @Override
    public void close() throws Exception {
        unregister();
        if (mDevice != null) {
            try {
                mDevice.close();
            } finally {
                mDevice = null;
            }
        }
    }

    /**
     * Register the driver in the framework.
     * @see #unregister()
     */
    public void register() {
        if (mDevice == null) {
            throw new IllegalStateException("cannot registered closed driver");
        }
        if (mUserSensor == null) {
            mUserSensor = build(mDevice);
            UserDriverManager.getManager().registerSensor(mUserSensor);
        }
    }

    /**
     * Unregister the driver from the framework.
     */
    public void unregister() {
        if (mUserSensor != null) {
            UserDriverManager.getManager().unregisterSensor(mUserSensor);
            mUserSensor = null;
        }
    }

    /**
     * Build User sesnor to be registered with the Android Things framework
     * @param adxl362 instance of core ADXL362.java
     */
    private static UserSensor build(final Adxl362 adxl362) {
        return UserSensor.builder()
                .setName(DRIVER_NAME)
                .setVendor(DRIVER_VENDOR)
                .setVersion(DRIVER_VERSION)
                .setCustomType(Sensor.TYPE_DEVICE_PRIVATE_BASE,
                        "vaf.vishal.adxl", Sensor.REPORTING_MODE_CONTINUOUS)
                .setDriver(new UserSensorDriver() {
                    @Override
                    public UserSensorReading read() throws IOException {
                        float[] values = adxl362.readXYZTempData();
                        return new UserSensorReading(values);
                    }
                })
                .build();
    }
}
