package cn.studyjams.s220170131.mijack.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DataBaseContentProvider extends ContentProvider {
    SQLiteOpenHelper sqLiteOpenHelper;
    public static final String AUTHORITIES = "cn.studyjams.s220170131.mijack.DataProvider";
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
        return sqLiteOpenHelper.getWritableDatabase().delete(getTableName(uri), selection, selectionArgs);
    }

    private String getTableName(Uri uri) {
        return getType(uri);
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
        sqLiteOpenHelper.getWritableDatabase().insert(getTableName(uri), null, values);
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return sqLiteOpenHelper.getWritableDatabase()
                .query(getTableName(uri), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return sqLiteOpenHelper.getWritableDatabase()
                .update(getTableName(uri), values, selection, selectionArgs);
    }
}
