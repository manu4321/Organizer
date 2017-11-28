package com.example.manuelalejandro.organizer.ISearchView;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.widget.SearchView;

import com.example.manuelalejandro.organizer.DatabaseAccessor;
import com.example.manuelalejandro.organizer.Main2Activity;

/**
 * Created by mfreites on 2017-07-02.
 */

public class MenuOnSuggestionListener implements SearchView.OnSuggestionListener{

    private Activity activity1;
    private SearchView searchView;
    public MenuOnSuggestionListener(Activity activity1, SearchView searchView){
        this.activity1 = activity1;
        this.searchView = searchView;
    }
    @Override
    public boolean onSuggestionSelect(int i) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int i) {
        SQLiteCursor cursor = (SQLiteCursor) searchView.getSuggestionsAdapter().getItem(i);
        long index = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseAccessor.TAG_ID));


        Intent intent =  Main2Activity.newIntent(activity1, index);
        activity1.startActivityForResult(intent, 2);

        return true;
    }
}
