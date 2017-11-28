package com.example.manuelalejandro.organizer.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.manuelalejandro.organizer.Adapters.OrganizerSimpleAdapter;
import com.example.manuelalejandro.organizer.FileAccessor.FilesIntoCollection;
import com.example.manuelalejandro.organizer.R;

public class UsedImagesActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_used_images);
        FilesIntoCollection fc = new  FilesIntoCollection();
        setListAdapter(new OrganizerSimpleAdapter(this, android.R.layout.activity_list_item,fc.getImagesIntoList()));
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, UsedImagesActivity.class);
        return intent;
    }
}
