import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class LiftRide {

  @SerializedName("liftID")
  private int liftId;
  private int time;

  public LiftRide(int liftId, int time) {
    this.liftId = liftId;
    this.time = time;
  }
  public int getLiftId() {
    return liftId;
  }

  public void setLiftId(int liftId) {
    this.liftId = liftId;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LiftRide liftRide = (LiftRide) o;
    return liftId == liftRide.liftId && time == liftRide.time;
  }

  @Override
  public int hashCode() {
    return Objects.hash(liftId, time);
  }
}
