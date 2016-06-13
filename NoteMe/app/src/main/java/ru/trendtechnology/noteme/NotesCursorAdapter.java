package ru.trendtechnology.noteme;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NotesCursorAdapter extends CursorAdapter {
    private String[] captions;
    private int[] imageIds;

    public NotesCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_note, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView itemHead = (TextView) view.findViewById(R.id.item_head);
        ImageView itemImage = (ImageView) view.findViewById(R.id.item_image);
        TextView creationDate = (TextView) view.findViewById(R.id.item_creation_date);
        TextView changeDate = (TextView) view.findViewById(R.id.item_modified_date);
        String head = cursor.getString(cursor.getColumnIndex(NoteMeDatabaseHelper.NOTE_HEAD));
        String imageURI = cursor.getString(cursor.getColumnIndex(NoteMeDatabaseHelper.NOTE_IMAGE_URI));
        String crDate = cursor.getString(cursor.getColumnIndex(NoteMeDatabaseHelper.NOTE_CREATED));
        String chDate = cursor.getString(cursor.getColumnIndex(NoteMeDatabaseHelper.NOTE_CHANGED));
        itemHead.setText(head);
        if (imageURI != null) {
            itemImage.setImageURI(Uri.parse(imageURI));
        }
        creationDate.setText(crDate);
        changeDate.setText(chDate);

    }
}