import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import jdk.swing.interop.SwingInterOpUtils;

public class ParameterProcessor {

  private static final int MAX_THREADS = 1000;
  private static final int MAX_SKIERS = 100000;
  private static final int MIN_LIFTS = 5;
  private static final int MAX_LIFTS = 60;
  private static final int MAX_NUM_RUNS = 20;
  private static String FILE_NAME = "config.properties";

  public static CommandLineParameter processParameters() {
    InputStream inputStream;
    Properties properties = new Properties();
    try {
      ClassLoader classLoader = SkiersClient.class.getClassLoader();
      inputStream = classLoader.getResourceAsStream(FILE_NAME);
      if (inputStream != null) {
        properties.load(inputStream);
      } else {
        throw new FileNotFoundException("Property File : " + FILE_NAME + " not found");
      }
      if (!validInput(properties)) {
        throw new IllegalArgumentException(
            "Input Parameters are not correct. Please check the file: " +
                FILE_NAME);
      }
      return loadParameters(properties);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new CommandLineParameter();
  }

  private static CommandLineParameter loadParameters(Properties properties) {
    CommandLineParameter parameters = new CommandLineParameter();
    parameters.setNumThreads(Integer.valueOf(properties.getProperty("numThreads")));
    parameters.setNumLifts(Integer.valueOf(properties.getProperty("numLifts")));
    parameters.setNumRuns(Integer.valueOf(properties.getProperty("numRuns")));
    parameters.setNumSkiers(Integer.valueOf(properties.getProperty("numSkiers")));
    parameters.setPort(Integer.valueOf(properties.getProperty("ipAddress")));
    return parameters;
  }

  private static boolean validInput(Properties properties) {
    if (properties == null || properties.size() == 0) {
      System.out.println("Invalid parameters. Please check the file: " + FILE_NAME);
      return false;
    }
    if (Integer.valueOf(properties.getProperty("numLifts")) < MIN_LIFTS ||
        Integer.valueOf(properties.getProperty("numLifts")) > MAX_LIFTS) {
      System.out.println(
          "numLifts is not in the range of [5, 60]. Please check the file: " + FILE_NAME);
      return false;
    }
    if (Integer.valueOf(properties.getProperty("numThreads")) > MAX_THREADS) {
      System.out.println(
          "Too many threads. Please add numThreads in the range [1, " + MAX_THREADS +" in the file: "
              + FILE_NAME);
      return false;
    }
    if (Integer.valueOf(properties.getProperty("numSkiers")) > MAX_SKIERS) {
      System.out.println(
          "Too many Skiers. Please add numSkiers in the range [1, " + MAX_SKIERS + "] in the file "
              + FILE_NAME);
      return false;
    }
    if (Integer.valueOf(properties.getProperty("numRuns")) > MAX_NUM_RUNS) {
      System.out.println("Too many numRuns. Please add numRuns in the range [1, " + MAX_NUM_RUNS
          + "] in the file " + FILE_NAME);
      return false;
    }
    return true;
  }
}
