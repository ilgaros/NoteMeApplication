package ru.trendtechnology.noteme;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NoteMeProvider extends ContentProvider {
    private static final String AUTHORITY = "ru.trendtechnology.noteme.notemeprovider";
    private static final String BASE_PATH = "Notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    // Константы для определения запрашиваемых операций
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    // Константа для идентификации элемента ListView
    public static final String CONTENT_ITEM_TYPE = "Note";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTES_ID);
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        NoteMeDatabaseHelper helper = new NoteMeDatabaseHelper(getContext());
        db = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == NOTES_ID) {
            selection = NoteMeDatabaseHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }
        return db.query(NoteMeDatabaseHelper.DB_TABLE, NoteMeDatabaseHelper.ALL_COLUMNS,
                selection, null, null, null,
                NoteMeDatabaseHelper.NOTE_CHANGED + " DESC");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = db.insert(NoteMeDatabaseHelper.DB_TABLE,
                null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return db.delete(NoteMeDatabaseHelper.DB_TABLE, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return db.update(NoteMeDatabaseHelper.DB_TABLE, values, selection, selectionArgs);
    }
}
