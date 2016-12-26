package vaf.vishal.adxl362;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.google.android.things.userdriver.UserDriverManager;
import com.google.android.things.userdriver.UserSensor;
import com.google.android.things.userdriver.UserSensorDriver;
import com.google.android.things.userdriver.UserSensorReading;

import java.io.IOException;

/**
 * Created by vishal on 20/12/16.
 */

public class Adxl362AccelerometerDriver implements AutoCloseable {

    private static final String TAG = Adxl362AccelerometerDriver.class.getSimpleName();
    private static final String DRIVER_NAME = "ADXL362Accelerometer";
    private static final String DRIVER_VENDOR = "Sparkfun";
    private static final int DRIVER_VERSION = 1;

    private Adxl362 mDevice;
    private UserSensor mUserSensor;

    public Adxl362AccelerometerDriver(String spiPort) throws IOException {
        mDevice = new Adxl362(spiPort);
    }

    public void beginMeasurement() throws IOException, InterruptedException {
        mDevice.beginMeasurement();
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

    private static UserSensor build(final Adxl362 adxl362) {
        return UserSensor.builder()
                .setType(Sensor.TYPE_ACCELEROMETER)
                .setName(DRIVER_NAME)
                .setVendor(DRIVER_VENDOR)
                .setVersion(DRIVER_VERSION)
                .setDriver(new UserSensorDriver() {
                    @Override
                    public UserSensorReading read() throws IOException {
                        float[] values = adxl362.readXYZTempData();
                        return new UserSensorReading(values, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
                    }
                })
                .build();
    }
}
