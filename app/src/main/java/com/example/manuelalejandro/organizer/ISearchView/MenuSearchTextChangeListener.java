package com.example.manuelalejandro.organizer.ISearchView;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.widget.SearchView;

import com.example.manuelalejandro.organizer.CursorArrayAdapter;
import com.example.manuelalejandro.organizer.DatabaseAccessor;

/**
 * Created by mfreites on 2017-07-02.
 */

public class MenuSearchTextChangeListener implements SearchView.OnQueryTextListener {

    private SearchView searchView;
    private Activity activity;
    public MenuSearchTextChangeListener(Activity activity, SearchView searchView){
        this.activity = activity;
        this.searchView = searchView;
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Cursor c = DatabaseAccessor.readDbLike(s);
        Log.i("OnQueryTextChange", s);
            searchView.setSuggestionsAdapter(new CursorArrayAdapter(activity, android.R.layout.simple_list_item_1,
                    c, new String[]{DatabaseAccessor.TAG_ARTIST}, new int[]{android.R.id.text1}));
            return true;



    }
}
