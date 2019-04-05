package ir.alizeyn.neshanmock.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * @author alizeyn
 * Created at 4/5/19
 */

@Database(entities = {MockEntity.class, PosEntity.class},
        version = 1)

public abstract class MockDatabase extends RoomDatabase {

    public abstract MockDao getMockDao();

    public abstract PosDao getPosDao();
}
