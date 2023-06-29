package frc.team670.mustanglib.dataCollection.sensors;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import frc.team670.mustanglib.dataCollection.sensors.PicoColorSensor.RawColor;

import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;

public class PicoColorMatcher {

  /**
   * A Rev Color Sensor V3 object is constructed with an I2C port as a parameter.
   * The device will be automatically initialized with default parameters.
   */
  private final PicoColorSensor m_colorSensor = new PicoColorSensor();

  /**
   * A Rev Color Match object is used to register and detect known colors. This
   * can be calibrated ahead of time or during operation.
   * 
   * This object uses a simple euclidian distance to estimate the closest match
   * with given confidence range.
   */
  private final ColorMatch m_colorMatcher = new ColorMatch();

  public enum colors {

    BLUE(0, new Color(0.136, 0.412, 0.450)), // 2022 blue game piece
    RED(1, new Color(0.475, 0.371, 0.153));  // 2022 red game piece

    private int colorNumber;
    private Color color;

    private colors(int colorNumber, Color color) {
      this.colorNumber = colorNumber;
      this.color = color;
    }

    /**
     * 
     * @return the corresponding integer code for each color on the wheel
     */
    public int getColorNumber() {
      return colorNumber;
    }

    private Color getTargetColor() {
      return color;
    }
  }

  public static final int UNKNOWN_COLOR_NUMBER = -1;

  private final double CONFIDENCE_THRESHOLD = 0.85;

  public PicoColorMatcher() {
    init();
  }

  public void init() {
    m_colorMatcher.addColorMatch(colors.BLUE.getTargetColor());;
    m_colorMatcher.addColorMatch(colors.RED.getTargetColor());

    m_colorMatcher.setConfidenceThreshold(CONFIDENCE_THRESHOLD);
  }

  public int detectColor() {
    /**
     * The method GetColor() returns a normalized color value from the sensor and
     * can be useful if outputting the color to an RGB LED or similar. To read the
     * raw color, use GetRawColor().
     * 
     * The color sensor works best when within a few inches from an object in well
     * lit conditions (the built in LED is a big help here!). The farther an object
     * is the more light from the surroundings will bleed into the measurements and
     * make it difficult to accurately determine its color.
     */
    RawColor detectedColor = m_colorSensor.getRawColor0();

    /**
     * Run the color match algorithm on our detected color
     */
    // String colorString;
    int colorNumber;

    ColorMatchResult match = m_colorMatcher.matchClosestColor(convertRawToColor(detectedColor));
    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    SmartDashboard.putNumber("Confidence", match.confidence);
    SmartDashboard.putBoolean("CS: sensor is connected", m_colorSensor.isSensor0Connected());
    if(match.confidence >= CONFIDENCE_THRESHOLD) {
        if (match.color == colors.BLUE.getTargetColor()) {
        //   coaalorString = "Blue";
            colorNumber = colors.BLUE.getColorNumber();
        } else if (match.color == colors.RED.getTargetColor()) {
            // colorString = "Red";
            colorNumber = colors.RED.getColorNumber();
        } else {
            // colorString = "Unknown";
            colorNumber = UNKNOWN_COLOR_NUMBER;
        }
        return colorNumber;
    }
    
    return -1;  
  }

  public Color convertRawToColor(RawColor rawColor) {
    var red = rawColor.red;
    var green = rawColor.green;
    var blue = rawColor.blue;
    double sum = red + green + blue;
    return new Color(red/sum,green/sum,blue/sum);
  }
}