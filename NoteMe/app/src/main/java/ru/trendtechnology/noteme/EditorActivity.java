package ru.trendtechnology.noteme;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 1;
    private Uri imageUri = null;
    private String filter;
    private String oldHead;
    private String oldImageUri;
    private String oldBody;
    private String action;
    private EditText editorHead;
    private ImageView editorImage;
    private EditText editorBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editorHead = (EditText) findViewById(R.id.editor_head);
        editorImage = (ImageView) findViewById(R.id.editor_image);
        Button buttonAddImage = (Button) findViewById(R.id.editor_btn_add_photo);
        editorBody = (EditText) findViewById(R.id.editor_body);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(NoteMeProvider.CONTENT_ITEM_TYPE);
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            filter = NoteMeDatabaseHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri,
                    NoteMeDatabaseHelper.ALL_COLUMNS, filter, null, null);
            cursor.moveToFirst();
            oldHead = cursor.getString(cursor.getColumnIndex(NoteMeDatabaseHelper.NOTE_HEAD));
            oldImageUri = cursor.getString(cursor.getColumnIndex(NoteMeDatabaseHelper.NOTE_IMAGE_URI));
            oldBody = cursor.getString(cursor.getColumnIndex(NoteMeDatabaseHelper.NOTE_BODY));
            cursor.close();
            editorHead.setText(oldHead);
            if (oldImageUri != null) {
                editorImage.setImageURI(Uri.parse(oldImageUri));
            }
            editorBody.setText(oldBody);
            editorBody.requestFocus();
        }
        if (buttonAddImage != null) {
            buttonAddImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    editorImage.setImageBitmap(bitmap);
                    editorImage.setMaxWidth(30);
                    editorImage.setMaxHeight(30);
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete_note:
                deleteNote();
                break;
        }
        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(NoteMeProvider.CONTENT_URI, filter, null);
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
        String newHead = editorHead.getText().toString().trim();
        String newBody = editorBody.getText().toString().trim();
        String imageURI = null;
        if (imageUri != null) {
            imageURI = imageUri.toString();
        }
        switch (action) {
            case Intent.ACTION_INSERT:
                if (newBody.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertData(newHead, newBody, imageURI);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newHead.length() == 0 && newBody.length() == 0 && imageUri == null) {
                    deleteNote();
                } else if (oldHead.equals(newHead) && oldBody.equals(newBody)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newHead, newBody, imageURI);
                }
        }
        finish();
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void updateNote(String noteHead, String noteBody, String imageURI) {
        ContentValues values = new ContentValues();
        values.put(NoteMeDatabaseHelper.NOTE_HEAD, noteHead);
        values.put(NoteMeDatabaseHelper.NOTE_IMAGE_URI, imageURI);
        imageUri = null;
        values.put(NoteMeDatabaseHelper.NOTE_BODY, noteBody);
        values.put(NoteMeDatabaseHelper.NOTE_CHANGED, getDateTime());
        getContentResolver().update(NoteMeProvider.CONTENT_URI, values, filter, null);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertData(String noteHead, String noteBody, String imageURI) {
        ContentValues values = new ContentValues();
        values.put(NoteMeDatabaseHelper.NOTE_HEAD, noteHead);
        values.put(NoteMeDatabaseHelper.NOTE_IMAGE_URI, imageURI);
        imageUri = null;
        values.put(NoteMeDatabaseHelper.NOTE_BODY, noteBody);
        getContentResolver().insert(NoteMeProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

}
