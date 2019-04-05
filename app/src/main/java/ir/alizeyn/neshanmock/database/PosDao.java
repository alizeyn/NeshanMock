package ir.alizeyn.neshanmock.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * @author alizeyn
 * Created at 4/5/19
 */
@Dao
public interface PosDao {

    @Query("SELECT * FROM pos WHERE mock_id=:mockId")
    List<PosEntity> getMockPos(final long mockId);

    @Insert
    long insertPos(PosEntity posEntity);

}
