public class CommandLineParameter {

  private int numThreads;
  private int numSkiers;
  private int numLifts;
  private int numRuns;
  private int port;

  public CommandLineParameter() {
  }
  public int getNumThreads() {
    return numThreads;
  }

  public void setNumThreads(int numThreads) {
    this.numThreads = numThreads;
  }

  public int getNumSkiers() {
    return numSkiers;
  }

  public void setNumSkiers(int numSkiers) {
    this.numSkiers = numSkiers;
  }

  public int getNumLifts() {
    return numLifts;
  }

  public void setNumLifts(int numLifts) {
    this.numLifts = numLifts;
  }

  public int getNumRuns() {
    return numRuns;
  }

  public void setNumRuns(int numRuns) {
    this.numRuns = numRuns;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
