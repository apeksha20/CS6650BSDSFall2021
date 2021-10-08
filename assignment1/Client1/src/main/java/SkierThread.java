import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class SkierThread extends Thread {

//  private final String BASE_PATH = "http://localhost:8080/BSDSAssignment1Server_war_exploded/";
  private final String basePath;

  private int resortId; //id of the resort like 127
  private String seasonId; // Year like 2021
  private String dayId;
  private int StartTime;
  private int endTime;
  private int startingSkierId;
  private int endingSkierId;
  private int numPostRequests;
  private int liftId;
  private CountDownLatch countDownLatch;
  private CountDownLatch masterLatch;
  private Results results;

  public SkierThread(int resortId, String seasonId, String dayId, int startTime, int endTime,
      int startingSkierId, int endingSkierId, int numPostRequests, int liftId, final String ipAddress,
      CountDownLatch countDownLatch, CountDownLatch masterLatch, Results results) {
    this.resortId = resortId;
    this.seasonId = seasonId;
    this.dayId = dayId;
    StartTime = startTime;
    this.endTime = endTime;
    this.startingSkierId = startingSkierId;
    this.endingSkierId = endingSkierId;
    this.numPostRequests = numPostRequests;
    this.liftId = liftId;
    this.countDownLatch = countDownLatch;
    this.masterLatch = masterLatch;
    this.results = results;
    this.basePath = ipAddress;
  }

  @Override
  public void run() {
    int successfulRequests = 0;
    int failedRequests = 0;
    SkiersApi skiersApi = new SkiersApi();
    ApiClient apiClient = skiersApi.getApiClient();

    apiClient.setBasePath(this.basePath);
    //apiClient.setBasePath("http://localhost:8080/BSDSAssignment1Server_war_exploded");

    //making post request to the server
    for (int i = 0; i < numPostRequests; ++i) {
      LiftRide liftRide = new LiftRide();
      liftRide.time(ThreadLocalRandom.current().nextInt(StartTime, endTime + 1));
      liftRide.liftID(ThreadLocalRandom.current().nextInt(this.liftId) + 1);
      int skierId = ThreadLocalRandom.current()
          .nextInt(this.endingSkierId - this.startingSkierId) + this.startingSkierId;

      int numTries = 5;
      do {
        try {
          skiersApi.writeNewLiftRideWithHttpInfo(liftRide,
              this.resortId, this.seasonId, this.dayId, skierId);
          ++successfulRequests;
          break;
        } catch (ApiException e) {
          System.err.println(
              "Api Exception while trying to call SkiersApi:writeNewLiftRide e.getCode() = "
                  + e.getCode() +
                  " e.getMessage() = " + e.getMessage() + " e.getCause() =  " + e.getCause());
          e.printStackTrace();
          if (e.getCode() / 100 == 4 | e.getCode() / 100 == 5) {
            --numTries;
          }
        }
      }while (numTries > 0);
        if(numTries == 0)
          ++failedRequests;

    }
    this.results.addFailedRequests(failedRequests);
    this.results.addSuccessfulRequests(successfulRequests);
    try {
      this.countDownLatch.countDown();
      this.masterLatch.countDown();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

