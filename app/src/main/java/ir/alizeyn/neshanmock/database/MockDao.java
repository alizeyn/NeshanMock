package ir.alizeyn.neshanmock.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * @author alizeyn
 * Created at 4/5/19
 */
@Dao
public interface MockDao {
    @Query("SELECT * FROM mock")
    List<MockEntity> getAll();

    @Query("SELECT * FROM mock WHERE mock_type == 'TRACK' ")
    List<MockEntity> getTracks();

    @Query("SELECT * FROM mock WHERE mock_type == 'CUSTOM'")
    List<MockEntity> getCustoms();

    @Insert
    long insert(MockEntity mockEntity);

    @Delete
    void delete(MockEntity mockEntity);

    @Update
    void update(MockEntity mockEntity);
}
