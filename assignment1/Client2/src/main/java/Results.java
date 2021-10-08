import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Results {

  private int successfulRequests;
  private int failedRequests;
  private List<String[]> resultData;
  private List<Long> responseTime;
  private double meanResponse;

  public Results() {
    this.meanResponse = 0.0;
    this.resultData = new ArrayList<>();
    this.successfulRequests = 0;
    this.failedRequests = 0;
    this.responseTime = new ArrayList<Long>();
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

  public List<String[]> getResultData() {
    return resultData;
  }

  public List<Long> getResponseTime() {
    return responseTime;
  }

  public synchronized void addNewData(List<String[]> resultData) {
    this.resultData.addAll(resultData);
  }

  public void addResponseTime(long latency) {
    this.responseTime.add(latency);
  }

  public double getMeanResponse() {
    double responseSum = 0;
    for (long i : responseTime) {
      responseSum += i;
    }
    return responseSum / responseTime.size();
  }

  public double getMedianResponseTime() {

    Collections.sort(responseTime);
    if(responseTime.size()%2 == 1){
      return responseTime.get(responseTime.size()/2);
    }
    else{
      long a = responseTime.get(responseTime.size()/2);
      long b = responseTime.get(responseTime.size()/2-1);
      return (a+b)/2.0;
    }
  }

  public double getPercentileResponse(int percentile){
    Collections.sort(this.responseTime);
    return this.responseTime.get((int)(Math.ceil(percentile/100.0*responseTime.size())-1));
  }
}
