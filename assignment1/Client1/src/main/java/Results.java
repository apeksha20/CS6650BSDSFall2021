public class Results {

  private int successfulRequests;
  private int failedRequests;

  public Results() {
    this.successfulRequests = 0;
    this.failedRequests = 0;
  }

  public synchronized void addSuccessfulRequests(int val) {
    this.successfulRequests += val;
  }

  public synchronized void addFailedRequests(int val) {
    this.failedRequests += val;
  }

  public int getSuccessfulRequests() {
    return successfulRequests;
  }

  public int getFailedRequests() {
    return failedRequests;
  }
}
