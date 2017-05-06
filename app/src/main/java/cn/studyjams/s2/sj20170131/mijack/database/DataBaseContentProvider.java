package cn.studyjams.s2.sj20170131.mijack.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DataBaseContentProvider extends ContentProvider {
    SQLiteOpenHelper sqLiteOpenHelper;
    public static final String AUTHORITIES = "cn.studyjams.s2.sj20170131.mijack.DataProvider";
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int TABLE_DATA = 1;

    static {
        uriMatcher.addURI(AUTHORITIES, DatabaseSQLiteOpenHelper.TABLE_NAME, TABLE_DATA);
    }

    public DataBaseContentProvider() {
    }

    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = new DatabaseSQLiteOpenHelper(getContext());
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = getTableNameIfExist(uri);
        if (tableName == null) {
            return -1;
        }
        return getWritableDatabase().delete(tableName, selection, selectionArgs);
    }

    private String getTableName(Uri uri) {
        return getType(uri);
    }

    private String getTableNameIfExist(Uri uri) {
        String tableName = getTableName(uri);
        if (tableName != null) {
            Cursor cursor = getWritableDatabase().rawQuery("SELECT name FROM sqlite_master WHERE type = \"table\"", null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    if (tableName.equals(name)) {
                        return tableName;
                    }
                } while (cursor.moveToNext());
            }
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        if (match == TABLE_DATA) {
            return DatabaseSQLiteOpenHelper.TABLE_NAME;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = getTableNameIfExist(uri);
        if (tableName == null) {
            return null;
        }
        getWritableDatabase().insert(tableName, null, values);
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String tableName = getTableNameIfExist(uri);
        if (tableName == null) {
            return null;
        }
        return getWritableDatabase()
                .query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private SQLiteDatabase getWritableDatabase() {
        return sqLiteOpenHelper.getWritableDatabase();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String tableName = getTableNameIfExist(uri);
        if (tableName == null) {
            return -1;
        }
        return getWritableDatabase()
                .update(tableName, values, selection, selectionArgs);
    }
}
