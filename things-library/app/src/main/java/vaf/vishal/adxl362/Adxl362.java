package vaf.vishal.adxl362;

import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Vishal Dubey (vishal-android-freak)
 * @link https://github.com/vishal-android-freak/ADXL362-Interfacing-Library
 * Android Things library for interfacing ADXL362 3 Axis accelerometer with Raspberry Pi 3 (Core)
 * X, Y and Z axis values are obtained as integers.
 * Temperature values are obtained as integers.
 * TODO: Handle interrupt based interfacing
 */

public class Adxl362 implements AutoCloseable {

    private static final String TAG = "Adxl362";
    private SpiDevice device;
    private Handler mHandler = new Handler();


    /**
     * Creates a new SpiDevice instance
     * @param spiPort - can be either SPI0.0 or SPI0.1 (for Raspberry Pi)
     * @param frequencyInHz - This will vary according to the peripheral to be interfaced, default should be 1MHz.
     *                      ADXL362 has a range of 1MHz to 8MHz
     * @param mode - Modes are MODE_0, MODE_1, MODE_2, MODE_3.
     *             ADXL362 works on MODE_0
     * @throws IOException
     */
    public Adxl362(String spiPort, int frequencyInHz, int mode) throws IOException {
        PeripheralManagerService service = new PeripheralManagerService();
        device = service.openSpiDevice(spiPort);
        connfigureSpi(device, frequencyInHz, mode);
    }

    /**
     * Configures SPI settings based on peripheral to be interfaced
     * @param device SpiDevice instance
     * @param frequencyInHz frequency in Hz. Minimum value should be 1MHz
     * @param mode MODE_0, MODE_1, MODE_2, MODE_3
     * @throws IOException
     */
    private void connfigureSpi(SpiDevice device, int frequencyInHz, int mode) throws IOException {
        device.setMode(mode);
        device.setBitsPerWord(8);
        device.setFrequency(frequencyInHz);
        device.setBitJustification(false);

        mHandler.postDelayed(softRest, 1000);
    }

    /**
     * Puts the accelerometer in measurement mode.
     * This method is mandatory to be called before starting
     * with the readings of X, Y, Z and Temp values.
     * @throws IOException
     * @throws InterruptedException
     */
    private void beginMeasurement() throws IOException, InterruptedException {
        device.write(new byte[]{(byte) 0x0A, (byte) 0x2D, (byte) 0x02}, 3);
        Thread.sleep(10);
    }

    /**
     * Read X axis data changes
     * @return X axis values as integer.
     * @throws IOException
     */
    public int readXData() throws IOException {
        return readRegisterValues(0x0E);
    }

    /**
     * Read Y axis data changes
     * @return Y axis values as integer.
     * @throws IOException
     */
    public int readYData() throws IOException {
        return readRegisterValues(0x10);
    }

    /**
     * Read Z axis data changes
     * @return Z axis values as integer.
     * @throws IOException
     */
    public int readZData() throws IOException {
        return readRegisterValues(0x12);
    }

    /**
     * Read Temperature sensor data
     * @return internal system temperature as integer.
     * @throws IOException
     */
    public int readTempData() throws IOException {
        return readRegisterValues(0x14);
    }

    /**
     * Read X, Y, Z axis and temperature values simultaneously.
     * A burst read of all the three axis is required for all measurements
     * corresponding to same sample rate.
     * @return array of integer values of X, Y, Z axis and temperature.
     * @throws IOException
     */
    public float[] readXYZTempData() throws IOException {
        float[] result = new float[4];

        byte[] data = new byte[10];
        byte[] output = new byte[10];

        data[0] = 0x0B;
        data[1] = 0x0E;
        data[2] = 0x00;             // XAXIS_L register
        data[3] = 0x00;             // XAXIS_H register
        data[4] = 0x00;             // YAXIS_L register
        data[5] = 0x00;             // YAXIS_H register
        data[6] = 0x00;             // ZAXIS_L register
        data[7] = 0x00;             // ZAXIS_H register
        data[8] = 0x00;             // TEMPERATURE_L register
        data[9] = 0x00;             // TEMPERATURE_H register

        device.transfer(data, output, data.length);

        result[0] = (output[2] & 0xFF) + (output[3] << 8); // 16 bit XAXIS values
        result[1] = (output[4] & 0xFF) + (output[5] << 8); // 16 bit YAXIS values
        result[2] = (output[6] & 0xFF) + (output[7] << 8); // 16 bit ZAXIS values
        result[3] = (output[8] & 0xFF) + (output[9] << 8); // 16 bit TEMP values

        return result;
    }

    /**
     * Soft reset the device before beginning the measurement.
     * This requires a small delay for the settlement of the sensor after reset.
     */
    private Runnable softRest = new Runnable() {
        @Override
        public void run() {
            try {
                device.write(new byte[]{(byte) 0x0A, (byte) 0x1F, (byte) 0x52}, 3);
                Thread.sleep(1000);
                beginMeasurement();
            } catch (IOException | InterruptedException e) {
                Log.d(TAG, "Couldn't write over SPI");
            }
        }
    };

    /**
     * Read register values.
     * @param regAddress register address to be read.
     * @return 16 bit integer values combining both LSB and MSB registers.
     * @throws IOException
     */
    private int readRegisterValues(int regAddress) throws IOException {
        byte[] data = new byte[4];
        byte[] output = new byte[4];

        data[0] = 0x0B;             // read instruction
        data[1] = (byte) regAddress;
        data[2] = 0x00;             // the byte to be read
        data[3] = 0x00;             // next byte to read

        try {
            device.transfer(data, output, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ((output[2] & 0xFF) + (output[3] << 8));
    }

    @Override
    public void close() throws Exception {

        if (device != null) {
            try {
                device.close();
                device = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close SPI device", e);
            }
        }

    }
}
