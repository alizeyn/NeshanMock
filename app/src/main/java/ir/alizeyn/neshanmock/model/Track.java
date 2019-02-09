package ir.alizeyn.neshanmock.model;

import android.location.Location;

import java.util.List;
import java.util.Locale;

/**
 * @author alizeyn
 * Created at 2/9/19
 */
public class Track {
    private long id;
    private long datetime;
    private String description;
    private List<Location> locations;

    public long getId() {
        return id;
    }

    public Track setId(long id) {
        this.id = id;
        return this;
    }

    public long getDatetime() {
        return datetime;
    }

    public Track setDatetime(long datetime) {
        this.datetime = datetime;
        return this;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public Track setLocations(List<Location> locations) {
        this.locations = locations;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Track setDescription(String description) {
        this.description = description;
        return this;
    }
}
