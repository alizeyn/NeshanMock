package ir.alizeyn.neshanmock.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

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
    private float speed;
    private long time;
    private long elapsedRealTime;
    private float accuracy;
    private float bearing;
    private String provider;

    public PosEntity(long mock_id, double lng, double lat, float speed, long time, long elapsedRealtime, float accuracy, float bearing, String provider) {
        this.mock_id = mock_id;
        this.lng = lng;
        this.lat = lat;
        this.speed = speed;
        this.time = time;
        this.elapsedRealTime = elapsedRealtime;
        this.accuracy = accuracy;
        this.bearing = bearing;
        this.provider = provider;
    }

    public PosEntity() {
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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getElapsedRealTime() {
        return elapsedRealTime;
    }

    public void setElapsedRealTime(long elapsedRealTime) {
        this.elapsedRealTime = elapsedRealTime;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
