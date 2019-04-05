package ir.alizeyn.neshanmock.database;

import android.content.Context;

import androidx.room.Room;

/**
 * @author alizeyn
 * Created at 4/5/19
 */
public class DatabaseClient {

    private static DatabaseClient databaseClient;
    private Context context;
    private MockDatabase mockDatabase;


    private DatabaseClient(Context context) {
        this.context = context;
        this.mockDatabase = Room.databaseBuilder(context, MockDatabase.class, "mock_db").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (databaseClient == null) {
            databaseClient = new DatabaseClient(context);
        }
        return databaseClient;
    }

    public MockDatabase getMockDatabase() {
        return mockDatabase;
    }
}
