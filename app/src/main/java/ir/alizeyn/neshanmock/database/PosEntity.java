package ir.alizeyn.neshanmock.database;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * @author alizeyn
 * Created at 4/5/19
 */
@Entity(tableName = "pos",
        foreignKeys = @ForeignKey(entity = MockEntity.class,
        parentColumns = "id",
        childColumns = "mock_id",
        onDelete = CASCADE))

public class PosEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long mock_id;

    private double lng;

    private double lat;

    private double speed;

    private long time;

    private double accuracy;

    private double bearing;

    private String provider;

    public PosEntity(long mock_id, double lng, double lat, double speed, long time, double accuracy, double bearing, String provider) {
        this.mock_id = mock_id;
        this.lng = lng;
        this.lat = lat;
        this.speed = speed;
        this.time = time;
        this.accuracy = accuracy;
        this.bearing = bearing;
        this.provider = provider;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMock_id() {
        return mock_id;
    }

    public void setMock_id(long mock_id) {
        this.mock_id = mock_id;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
