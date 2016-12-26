package adxl362;

/*
  Example showing interfacing of ADXL362 with Raspberry Pi.
*/
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Vishal Dubey (vishal-android-freak)
 * @link https://github.com/vishal-android-freak/ADXL362-Interfacing-Library
 * ADXL362 interfacing with Raspberry Pi.
 * This is an example of pure Java interfacing of Accelerometer ADXL362 with Raspberry Pi using pi4j on Raspbian.
 * This should work on any other OS for Raspberry Pi (or any other development platform, may be), which have Oracle JDK and pi4j support.
 * X, Y, Z axis values are obtained as integers.
 * Temperature values are obtained as integers.
 * TODO: Handle interrupt based interfacing.
 */

public class Adxl {
    
    public static SpiDevice spiDevice;
    public static byte [] write = new byte[10];
    public static byte [] readPwr = new byte[10];
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
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
    }
}
