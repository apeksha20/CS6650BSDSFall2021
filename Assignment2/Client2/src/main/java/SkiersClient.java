import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkiersClient {

  private static final float PHASE_1_MULTIPLYING_FACTOR = 0.2f;
  private static final float PHASE_2_MULTIPLYING_FACTOR = 0.6f;
  private static final float PHASE_3_MULTIPLYING_FACTOR = 0.1f;
  private static final int RESORT_ID = 20;
  private static final String SEASON_ID = "2021";
  private static final String DAY_ID = "21";
  private static final String FILE_NAME = "Results.csv";

  public static void main(String args[]) {
    //get the input parameters from the properties file
    CommandLineParameter parameters = ParameterProcessor.processParameters();
    //Print the parameters
    System.out.println("numThreads: " + parameters.getNumThreads());
    System.out.println("numRuns: " + parameters.getNumRuns());
    System.out.println("numLifts: " + parameters.getNumLifts());
    System.out.println("numSkiers: " + parameters.getNumSkiers());
    System.out.println("ipAddress: " + parameters.getIpAddress());

    //calculating thread and request count for different phases
    int newNumThreads = (int) Math.round(parameters.getNumThreads() / 4.0);
    int totalThreads = newNumThreads * 2
        + parameters.getNumThreads();// 1 and 3 phase creates newNumThreads and phase 2 creates numThreads
    //creating master latch so that all threads i.e. total threads complete
    CountDownLatch masterLatch = new CountDownLatch(totalThreads);
    //new results object for storing the result
    Results results = new Results();
    //creating phase 1, phase2, phase 3

    int phase1Requests = (int) Math.round(
        parameters.getNumRuns() * PHASE_1_MULTIPLYING_FACTOR * (parameters.getNumSkiers() / (
            newNumThreads * 1.0)));
    Phase phase1 = new Phase(RESORT_ID, SEASON_ID, DAY_ID, newNumThreads, parameters.getNumSkiers(),
        parameters.getNumLifts(), phase1Requests, (int) (Math.ceil(newNumThreads / 10.0)),
        parameters.getIpAddress(), 1, 90,
        results, masterLatch);

    int phase2Requests = (int) Math.round(
        parameters.getNumRuns() * PHASE_2_MULTIPLYING_FACTOR * (parameters.getNumSkiers() / (
            parameters.getNumThreads() * 1.0)));
    Phase phase2 = new Phase(RESORT_ID, SEASON_ID, DAY_ID, parameters.getNumThreads(),
        parameters.getNumSkiers(),
        parameters.getNumLifts(), phase2Requests,
        (int) (Math.ceil(parameters.getNumThreads() / 10.0)), parameters.getIpAddress(), 91, 360,
        results, masterLatch);

    int phase3Requests = Math.round(parameters.getNumRuns() * PHASE_3_MULTIPLYING_FACTOR);
    Phase phase3 = new Phase(RESORT_ID, SEASON_ID, DAY_ID, newNumThreads, parameters.getNumSkiers(),
        parameters.getNumLifts(), phase3Requests, (int) (Math.ceil(newNumThreads / 10.0)),
        parameters.getIpAddress(), 361, 420,
        results, masterLatch);

    System.out.println("Est requests: " + (newNumThreads * phase1Requests
        + parameters.getNumThreads() * phase2Requests + newNumThreads * phase3Requests));

    //calculating wall time by running the three phases
    try {
      Long wallTime = calculateWallTime(phase1, phase2, phase3, masterLatch);
      //writing  data to csv file
      writeDataToCSV(results.getResultData());
      //printing the results
      printResults(results, wallTime);

    } catch (InterruptedException | IOException e) {
      System.err.println("Unable to calculate the wallTime as error in running the phases");
      e.printStackTrace();
    }
  }

  private static void writeDataToCSV(List<String[]> results) throws IOException {
    File resultFile = new File(FILE_NAME);
    try (PrintWriter pw = new PrintWriter(resultFile)) {
      results.stream()
          .map(SkiersClient::convertToCSV)
          .forEach(pw::println);
    }

  }
  public static String escapeSpecialCharacters(String data) {
    String escapedData = data.replaceAll("\\R", " ");
    if (data.contains(",") || data.contains("\"") || data.contains("'")) {
      data = data.replace("\"", "\"\"");
      escapedData = "\"" + data + "\"";
    }
    return escapedData;
  }
  public static String convertToCSV(String[] data) {
    return Stream.of(data)
        .map(SkiersClient::escapeSpecialCharacters)
        .collect(Collectors.joining(","));
  }

  private static void printResults(Results results, Long wallTime) throws FileNotFoundException {
    System.out.println("Results:");
    System.out.println("Number of Successful requests sent: " + results.getSuccessfulRequests());
    System.out.println("Number of Failed requests sent: " + results.getFailedRequests());
    System.out.println("Total runtime(Wall Time): " + wallTime + " seconds");
    System.out.println("Total Throughput: "
        + (float)(results.getSuccessfulRequests() + results.getFailedRequests()) / wallTime);
    System.out.println("Mean response time: " + results.getMeanResponse() + " milliseconds");
    System.out.println("Median response time: " + results.getMedianResponseTime() + " milliseconds");
    System.out.println("Max response time: " + Collections.max(results.getResponseTime()) + " milliseconds");
    System.out.println("p99 response time: " + results.getPercentileResponse(99) + " milliseconds");
  }

  private static Long calculateWallTime(Phase phase1, Phase phase2, Phase phase3,
      CountDownLatch masterLatch)
      throws InterruptedException {
    long start = System.currentTimeMillis();

    phase1.run();
    phase2.run();
    phase3.run();
    //System.out.println("All 3 phases running");
    masterLatch.await();
    long end = System.currentTimeMillis();
    return (end - start)/1000;
  }
}
