package ir.alizeyn.neshanmock.database;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author alizeyn
 * Created at 4/5/19
 */

@Entity(tableName = "mock")
public class MockEntity implements Serializable {

    @PrimaryKey()
    private long id;

    @ColumnInfo(name = "mock_type")
    private String mockType;

    @ColumnInfo(name = "description")
    private String desc;


    public MockEntity(long id, String mockType) {
        this.id = id;
        this.mockType = mockType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMockType() {
        return mockType;
    }

    public void setMockType(String mockType) {
        this.mockType = mockType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
