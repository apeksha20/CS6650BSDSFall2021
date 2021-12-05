import com.google.gson.annotations.SerializedName;

public class SkierInfo {
  private int skierId;
  private int time;
  @SerializedName("liftID")
  private int liftId;
  private int resortId;
  private int dayId;
  private int seasonId;

  public SkierInfo(int skierId, int time, int liftId, int resortId, int dayId, int seasonId) {
    this.skierId = skierId;
    this.time = time;
    this.liftId = liftId;
    this.resortId = resortId;
    this.dayId = dayId;
    this.seasonId = seasonId;
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

  public int getResortId() {
    return resortId;
  }

  public void setResortId(int resortId) {
    this.resortId = resortId;
  }

  public int getDayId() {
    return dayId;
  }

  public void setDayId(int dayId) {
    this.dayId = dayId;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public void setSeasonId(int seasonId) {
    this.seasonId = seasonId;
  }
}
