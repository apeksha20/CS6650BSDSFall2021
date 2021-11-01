import com.google.gson.annotations.SerializedName;

public class SkierInfo {
  private int skierId;
  private int time;
  @SerializedName("liftID")
  private int liftId;

  public SkierInfo(int skierId, int time, int liftId) {
    this.skierId = skierId;
    this.time = time;
    this.liftId = liftId;
  }

  public int getSkierId() {
    return skierId;
  }

  public void setSkierId(int skierId) {
    this.skierId = skierId;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public int getLiftId() {
    return liftId;
  }

  public void setLiftId(int liftId) {
    this.liftId = liftId;
  }
}
