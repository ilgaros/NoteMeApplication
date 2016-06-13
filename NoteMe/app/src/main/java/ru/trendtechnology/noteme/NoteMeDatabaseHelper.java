package ru.trendtechnology.noteme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteMeDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "note_me.db"; // Имя БД
    private static final int DB_VERSION = 1; // Версия БД
    // Названия таблицы и столбцов
    public static final String DB_TABLE = "Note"; // Название таблицы
    public static final String NOTE_ID = "_id";
    public static final String NOTE_HEAD = "Head";
    public static final String NOTE_BODY = "NoteBody";
    public static final String NOTE_IMAGE_URI = "ImageUri";
    public static final String NOTE_CREATED = "NoteCreated";
    public static final String NOTE_CHANGED = "NoteChanged";
    public static final String[] ALL_COLUMNS =
            {NOTE_ID, NOTE_HEAD, NOTE_BODY, NOTE_IMAGE_URI, NOTE_CREATED, NOTE_CHANGED};
    // Команда SQL для создания таблицы
    private static final String TABLE_CREATE = "CREATE TABLE "
            + DB_TABLE + " ("
            + NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NOTE_HEAD + " TEXT, "
            + NOTE_BODY + " TEXT, "
            + NOTE_IMAGE_URI + " TEXT, "
            + NOTE_CREATED + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, "
            + NOTE_CHANGED + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
            + ")";

    public NoteMeDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + DB_TABLE);
        onCreate(db);
    }
}
