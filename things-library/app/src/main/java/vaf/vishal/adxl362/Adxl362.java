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
 * Created by vishal on 20/12/16.
 */

public class Adxl362 implements AutoCloseable {

    private static final String TAG = "Adxl362";
    private SpiDevice device;
    private Handler mHandler = new Handler();

    public Adxl362(String spiPort) throws IOException {
        PeripheralManagerService service = new PeripheralManagerService();
        device = service.openSpiDevice(spiPort);
        connfigureSpi(device);
    }

    private void connfigureSpi(SpiDevice device) throws IOException {
        device.setMode(SpiDevice.MODE0);
        device.setBitsPerWord(8);
        device.setFrequency(5000000);
        device.setBitJustification(false);

        mHandler.postDelayed(softRest, 1000);
    }

    private void beginMeasurement() throws IOException, InterruptedException {
        device.write(new byte[]{(byte) 0x0A, (byte) 0x2D, (byte) 0x02}, 3);
        Thread.sleep(10);
    }

    public int readXData() throws IOException {
        return readRegisterValues(0x0E);
    }

    public int readYData() throws IOException {
        return readRegisterValues(0x10);
    }

    public int readZData() throws IOException {
        return readRegisterValues(0x12);
    }

    public int readTempData() throws IOException {
        return readRegisterValues(0x14);
    }

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
