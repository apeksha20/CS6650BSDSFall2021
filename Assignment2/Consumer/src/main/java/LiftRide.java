import com.google.gson.annotations.SerializedName;

public class LiftRide {

  private int time;
  @SerializedName("liftID")
  private int liftId;

  public LiftRide(int time, int liftId) {
    this.time = time;
    this.liftId = liftId;
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
