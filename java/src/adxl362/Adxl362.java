package adxl362;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;
import java.io.IOException;

/**
 *
 * @author Vishal Dubey (vishal-android-freak)
 * @link https://github.com/vishal-android-freak/ADXL362-Interfacing-Library
 * ADXL362 interfacing with Raspberry Pi.
 * This is a pure Java interfacing of Accelerometer ADXL362 with Raspberry Pi using pi4j on Raspbian.
 * This should work on any other OS for Raspberry Pi (or any other development platform, may be), which have Oracle JDK and pi4j support.
 * X, Y, Z axis values are obtained as integers.
 * Temperature values are obtained as integers.
 * TODO: Handle interrupt based interfacing.
 */
public class Adxl362 {
    
    private SpiDevice spiDevice;
    
    /**
     * Creates a new SpiDevice instance
     * @param channel - SPI channels are CHANNEL_0 (CS0), CHANNEL_1 (CS1)
     * @param frequencyInHz - This will vary according to the peripheral to be interfaced, default should be 1MHz
     * @param mode - Modes are MODE_0, MODE_1, MODE_2, MODE_3
     * @throws IOException
     */
    
    public Adxl362(SpiChannel channel, int frequencyInHz, SpiMode mode) throws IOException {
        spiDevice = SpiFactory.getInstance(channel, frequencyInHz, mode);
    }
    
    /**
     * Soft reset the device before beginning the measurement.
     * This requires a small delay for the settlement of the sensor after reset.
     * @throws IOException
     * @throws InterruptedException
     */
    
    public void doSoftReset() throws IOException, InterruptedException {
        byte[] data = new byte[3];
        data[0] = 0x0A;             // write instruction
        data[1] = 0x1F;
        data[2] = 0x52;
        spiDevice.write(data);
        Thread.sleep(1000);
    }
    
    /**
     * Puts the accelerometer in measurement mode.
     * This method is mandatory to be called before starting
     * with the readings of X, Y, Z and Temp values.
     * This method enables the measurement mode on ADXL362.
     * @throws IOException
     * @throws InterruptedException
     */
    
    public void beginMeasurement() throws IOException, InterruptedException {
        byte[] data = new byte[3];
        data[0] = 0x0A;            // write instruction
        data[1] = 0x2D;
        data[2] = 0x02;
        spiDevice.write(data);
        Thread.sleep(10);
    }
    
    /**
     * Read X axis data changes
     * @return X axis values as integer.
     * @throws IOException 
     */
    public int readXData() throws IOException {
        return readRegisterValue(0x0E);
    }
    
    /**
     * Read Y axis data changes
     * @return Y axis values as integer.
     * @throws IOException 
     */
    public int readYData() throws IOException {
        return readRegisterValue(0x10);
    }
    
    /**
     * Read Z axis data changes
     * @return Z axis values as integer.
     * @throws IOException 
     */
    public int readZData() throws IOException {
        return readRegisterValue(0x12);
    }
    
    /**
     * Read Temperature sensor data
     * @return internal system temperature as integer.
     * @throws IOException 
     */
    public int readTempData() throws IOException {
        return readRegisterValue(0x14);
    }
    
    /**
     * Read X, Y, Z axis and temperature values simultaneously.
     * A burst read of all the three axis is required for all measurements 
     * corresponding to same sample rate.
     * @return array of integer values of X, Y, Z axis and temperature.
     * @throws IOException 
     */
    public int [] readXYZTempData() throws IOException {
        int[] result = new int[4];
        
        byte[] data = new byte[10];
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
        
        byte[] output = spiDevice.write(data);
        
        result[0] = (output[2] & 0xFF) + (output[3] << 8); // 16 bit XAXIS values
        result[1] = (output[4] & 0xFF) + (output[5] << 8); // 16 bit YAXIS values
        result[2] = (output[6] & 0xFF) + (output[7] << 8); // 16 bit ZAXIS values
        result[3] = (output[8] & 0xFF) + (output[9] << 8); // 16 bit TEMP values
        
        return result;
    }
    
    /**
     * Read register values.
     * @param regAddress
     * @return 16 bit integer values combining both LSB and MSB registers.
     * @throws IOException 
     */
    private int readRegisterValue(int regAddress) throws IOException {
        byte[] data = new byte[4];
        data[0] = 0x0B;             // read instruction
        data[1] = (byte)regAddress;
        data[2] = 0x00;             // the byte to be read
        data[3] = 0x00;             // next byte to read
        
        byte[] output = spiDevice.write(data);
        int result = (output[2] & 0xFF) + (output[3] << 8); // 16 bit feteched value
        
        return result;
    }
    
}
