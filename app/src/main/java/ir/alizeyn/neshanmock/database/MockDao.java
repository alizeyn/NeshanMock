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

    @Insert
    long insert(MockEntity mockEntity);

    @Delete
    void delete(MockEntity mockEntity);

    @Update
    void update(MockEntity mockEntity);
}
