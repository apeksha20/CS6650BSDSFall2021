import java.util.concurrent.CountDownLatch;

public class Phase {

  private int resortId; //id of the resort like 127
  private String seasonId; // Year like 2021
  private String dayId;    // dau number like 1 - 365
  private int numThreads;
  private int numSkiers;
  private int numLifts;
  private int numPostRequests; // number of post requests to be made to the server
  private int numWaitingThreads;
  private int startTime;
  private int endTime;
  private Results results;
  private CountDownLatch masterLatch;


  public Phase(int resortId, String seasonId, String dayId, int numThreads, int numSkiers,
      int numLifts,
      int numPostRequests, int numWaitingThreads, int startTime, int endTime, Results results,
      CountDownLatch masterLatch) {
    this.resortId = resortId;
    this.seasonId = seasonId;
    this.dayId = dayId;
    this.numThreads = numThreads;
    this.numSkiers = numSkiers;
    this.numLifts = numLifts;
    this.numPostRequests = numPostRequests;
    this.numWaitingThreads = numWaitingThreads;
    this.startTime = startTime;
    this.endTime = endTime;
    this.results = results;
    this.masterLatch = masterLatch;
  }

  public void run() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(this.numWaitingThreads);
    for (int i = 0; i < this.numThreads; ++i) {
      int startingSkierId = (i * (this.numSkiers / this.numThreads)) + 1;
      int endingSkierId = (i + 1) * (this.numSkiers / this.numThreads);
      //creating the thread that will send numPostRequests post requests
      Thread skierThread = new SkierThread(resortId, seasonId, dayId, startTime, endTime,
          startingSkierId,
          endingSkierId, numPostRequests, numLifts, countDownLatch, masterLatch, results);
      skierThread.start();
    }
    countDownLatch.await();
  }
}
