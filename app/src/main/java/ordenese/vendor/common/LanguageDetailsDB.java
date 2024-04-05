package ordenese.vendor.common;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LanguageDetailsDB extends SQLiteOpenHelper {

    final static String name = "language_details_db";
    final static int version = 1;
    final static String TABLE_NAME_LANGUAGE_DETAILS = "language_details_table";

    final static String LANGUAGE_ID = "language_id";

    final static String DROP_TABLE_LANGUAGE = "DROP TABLE IF EXISTS " + TABLE_NAME_LANGUAGE_DETAILS;
    final static String DELETE_TABLE_LANGUAGE = "DELETE FROM " + TABLE_NAME_LANGUAGE_DETAILS;
    final static String SELECT_VALUE_SELECT = "select ";
    final static String SELECT_VALUE_FROM = "from ";
    Cursor cursor;

    final static String CREATE_ACCOUNT_NEW = "create table " + TABLE_NAME_LANGUAGE_DETAILS + "(" + LANGUAGE_ID + " integer primary key);";

    private static LanguageDetailsDB sInstance;

    public static synchronized LanguageDetailsDB getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LanguageDetailsDB(context.getApplicationContext());
        }
        return sInstance;
    }

    private LanguageDetailsDB(Context context) {
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNT_NEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_LANGUAGE);
        onCreate(db);
    }

    public Boolean insert_language_detail(String languageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LANGUAGE_ID, languageId);
        db.insert(TABLE_NAME_LANGUAGE_DETAILS, null, contentValues);
        return true;
    }


    public boolean check_language_selected() {
        String query = SELECT_VALUE_SELECT + LANGUAGE_ID + " " + SELECT_VALUE_FROM + TABLE_NAME_LANGUAGE_DETAILS;
        String result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        cursor = db.rawQuery(query, null);
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex(LANGUAGE_ID));
            }
            cursor.close();
        }
        return (result != null);
    }


    public String get_language_id() {
        String select = SELECT_VALUE_SELECT + "* " + SELECT_VALUE_FROM + TABLE_NAME_LANGUAGE_DETAILS;
        String result = "";
        SQLiteDatabase db = this.getReadableDatabase();
        cursor = db.rawQuery(select, null);
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex(LANGUAGE_ID));
            }
            cursor.close();
        }
        return result;
    }

    public void delete_language_detail() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_TABLE_LANGUAGE);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}