package cn.studyjams.s2.sj20170131.mijack.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * @author Mr.Yuan
 * @date 2017/5/3
 */
public class DatabaseSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME="data";
    public static final String CREATE_SQL =
            "CREATE TABLE "+TABLE_NAME+" (" +
                    Database.COLUMNS_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Database.COLUMNS_NAME + " VARCHAR(400)," +
                    Database.COLUMNS_PATH + " VARCHAR(400)," +
                    Database.COLUMNS_FILE_EXTENSION_NAME + " VARCHAR(400)," +
                    Database.COLUMNS_SIZE + " INTEGER ," +
                    Database.COLUMNS_WIDTH + " INTEGER ," +
                    Database.COLUMNS_HEIGHT + " INTEGER ," +
                    Database.COLUMNS_GS_CLOUD_FILE_NAME + " VARCHAR(400)," +
                    Database.COLUMNS_URL + " VARCHAR(400)," +
                    Database.COLUMNS_STATUS + " VARCHAR(20)" +
                    ")";

    public DatabaseSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public interface Database extends BaseColumns {
        String COLUMNS_ID = "id";
        String COLUMNS_NAME = "name";
        String COLUMNS_PATH = "path";
        String COLUMNS_SIZE = "size";
        String COLUMNS_WIDTH = "width";
        String COLUMNS_HEIGHT = "height";
        String COLUMNS_GS_CLOUD_FILE_NAME = "cloudFileName";
        String COLUMNS_URL = "url";
        String COLUMNS_STATUS = "status";
        String COLUMNS_FILE_EXTENSION_NAME = "file_extension_name";
    }

}
