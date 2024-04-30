package ordenese.vendor.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DriverAssignAlarmHandleDB extends SQLiteOpenHelper {

    private final static String name = "DriverAssignAlarmHandleDataBase";
    private final static int version = 1;
    private final static String TABLE_NAME = "driver_assign_alarm_handle_table";
    private final static String Is_Alarm_Running = "is_alarm_running";

    private final static String CREATE = "create table " + TABLE_NAME + " (" + Is_Alarm_Running +" text);";
    private final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private final static String DELETE_TABLE = "DELETE FROM " + TABLE_NAME;
    private final static String SELECT = "select ";
    private final static String FROM = "from ";


    private static DriverAssignAlarmHandleDB sInstance;

    public static synchronized DriverAssignAlarmHandleDB getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DriverAssignAlarmHandleDB(context.getApplicationContext());
        }
        return sInstance;
    }

    private DriverAssignAlarmHandleDB(Context context) {
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDb) {
        sqLiteDb.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDb, int oldVersion, int newVersion) {
        sqLiteDb.execSQL(DROP_TABLE);
        onCreate(sqLiteDb);
    }


    public void add(String status) {
        SQLiteDatabase mSqLiteDb = this.getWritableDatabase();
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(Is_Alarm_Running, status);
        mSqLiteDb.insert(TABLE_NAME, null, mContentValues);
    }





    public void deleteDB() {

        SQLiteDatabase mSqLiteDb = this.getWritableDatabase();
        mSqLiteDb.execSQL(DELETE_TABLE);
        // Log.d("***UserAreaDB***","Deleted");

    }

    public int getSizeOfList() {
        SQLiteDatabase mSqLiteDb = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(mSqLiteDb, TABLE_NAME);
    }


    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}

