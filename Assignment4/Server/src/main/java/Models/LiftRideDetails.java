package Models;

import Database.LiftRideDAO;
import com.google.gson.annotations.SerializedName;

public class LiftRideDetails {

  private int time;
  @SerializedName("liftID")
  private int liftId;
  private int skierId;
  private int resortId;
  private int seasonId;
  private int dayId;

  public LiftRideDetails(int time, int liftId, int skierId, int resortId, int seasonId, int dayId) {
    this.time = time;
    this.liftId = liftId;
    this.skierId = skierId;
    this.resortId = resortId;
    this.seasonId = seasonId;
    this.dayId = dayId;
  }

  public LiftRideDetails(int time, int liftId) {
    this.time = time;
    this.liftId = liftId;
  }

  public LiftRideDetails(int skierId, int resortId, int seasonId, int dayId) {
    this.skierId = skierId;
    this.resortId = resortId;
    this.seasonId = seasonId;
    this.dayId = dayId;
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

  public int getSkierId() {
    return skierId;
  }

  public void setSkierId(int skierId) {
    this.skierId = skierId;
  }

  public int getResortId() {
    return resortId;
  }

  public void setResortId(int resortId) {
    this.resortId = resortId;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public void setSeasonId(int seasonId) {
    this.seasonId = seasonId;
  }

  public int getDayId() {
    return dayId;
  }

  public void setDayId(int dayId) {
    this.dayId = dayId;
  }
}
