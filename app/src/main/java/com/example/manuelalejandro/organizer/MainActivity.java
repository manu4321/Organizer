package com.example.manuelalejandro.organizer;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.manuelalejandro.organizer.AdminSwitch.AdminSwitch;
import com.example.manuelalejandro.organizer.FileAccessor.FileToDb;
import com.example.manuelalejandro.organizer.ISearchView.MenuOnSuggestionListener;
import com.example.manuelalejandro.organizer.ISearchView.MenuSearchTextChangeListener;
import com.example.manuelalejandro.organizer.Permission.PermissionUtil;

import java.util.HashMap;

public class MainActivity extends ListActivity {

    HashMap<String, String> headerOn = new HashMap<String, String>();
    Cursor currentCursor;
    private ProgressDialog pDialog;
    public AdminSwitch adminSwitch;
    Toast adminSwitchToast = null;
    Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo);
        //this.deleteDatabase("organizer_db");
        setContentView(R.layout.activity_main);
        Settings.setIsLoadingImages(true);
        PermissionUtil.hasPermissionOrRequest(this, PermissionUtil.REQUEST_EXTERNAL_STORAGE_PHOTO);
        DatabaseAccessor.dbHelper = new DatabaseOpenHelper(this);
        DatabaseAccessor.db = DatabaseAccessor.dbHelper.getWritableDatabase();
        currentCursor = DatabaseAccessor.readDb(0);
        if(currentCursor.getCount() == 0){
            loadInitialValue();
        }else{
            setListAdapter(new CursorArrayAdapter(this, android.R.layout.simple_list_item_1,
                    currentCursor, new String[]{DatabaseAccessor.TAG_ARTIST}, new int[]{android.R.id.text1}));
        }

        getListView().setFastScrollEnabled(true);

        Intent intent = getIntent();
        handleIntent(getIntent());
        handleTitleClick();


    }

    private void handleTitleClick() {
        final int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(adminSwitch == null){
                    adminSwitch = new AdminSwitch();
                }else{
                    adminSwitch.setNextTime(System.nanoTime());
                }
                if(!adminSwitch.isAdmin()){
                    long prev = adminSwitch.getPreviousTime();
                    long next = adminSwitch.getNextTime();
                    double seconds = (double)(next - prev) / 1000000000.0;
                    if(seconds > 3){
                        adminSwitch = new AdminSwitch();
                        adminSwitch.setTimesClicked(1);
                    }else{
                        adminSwitch.setPreviousTime(next);
                        adminSwitch.setTimesClicked((adminSwitch.getTimesClicked() + 1));
                    }
                    if(adminSwitchToast != null){
                        adminSwitchToast.cancel();
                    }
                    if(adminSwitch.getTimesClicked() == 7){
                        change_mode();
                    }
                    else if(adminSwitch.getTimesClicked() > 3){
                        adminSwitchToast = Toast.makeText(MainActivity.this,"Admin mode will open on: " + (7 - adminSwitch.getTimesClicked()) + " clicks", Toast.LENGTH_SHORT);
                        adminSwitchToast.show();
                    }

                }
            }
        });
    }

    public void loadInitialValue(){
        FileToDb s = new FileToDb(this);
        s.initialRecordInsertion();
        refresh();
    }
    @Override
    public boolean onSearchRequested() {
        Log.i("doMySearch", "asd");
        return super.onSearchRequested();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.i("HandleIntent", "Starting");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    public void doMySearch(String query){
        Log.i("doMySearch", query);
        Cursor c = DatabaseAccessor.readDbLike(query);
        setListAdapter(new CursorArrayAdapter(this, android.R.layout.simple_list_item_1,
                c, new String[]{DatabaseAccessor.TAG_ARTIST}, new int[]{android.R.id.text1}));
    }

    @Override
    public void onPause() {

        super.onPause();
        Settings.setIsLoadingImages(false);
        if (pDialog != null)
            pDialog.dismiss();
    }

    @Override
    protected void onResume() {
        Settings.setIsLoadingImages(true);
        super.onResume();
    }

    public HashMap<String, String> getHeaderOn() {
        return headerOn;
    }

    public void setHeaderOn(HashMap<String, String> headerOn) {
        this.headerOn = headerOn;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        AutoCompleteTextView search_text = (AutoCompleteTextView) searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        search_text.setThreshold(1);
//        Cursor c = DatabaseAccessor.readDbLike(searchView.getQuery().toString());
//        searchView.setSuggestionsAdapter (new CursorArrayAdapter(this, android.R.layout.simple_list_item_1,
//                c, new String[]{DatabaseAccessor.TAG_ARTIST}, new int[]{android.R.id.text1}));
        searchView.setOnQueryTextListener(new MenuSearchTextChangeListener(this, searchView));
        searchView.setOnSuggestionListener(new MenuOnSuggestionListener(this, searchView));
        menu.setGroupVisible(R.id.overFlowItemsToHide, false);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public void goToSecond() {
        goToSecond(null);
    }

    public void goToSecond(Long id) {
        Intent intent;
        if (id != null) {
            intent = Main2Activity.newIntent(this, id);
        } else {
            intent = Main2Activity.newIntent(this);
        }
        startActivityForResult(intent, 2);
    }

    public void addItem(MenuItem item) {
        goToSecond();
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = (Cursor) l.getItemAtPosition(position);
        id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));

        goToSecond(id);
    }


    @Override
    // step 9 - implement the handler to execute upon picture being taken
    // get picture to display in image view.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Cursor c = DatabaseAccessor.readDb(0);
        setListAdapter(new CursorArrayAdapter(this, android.R.layout.simple_list_item_1,
                c, new String[]{DatabaseAccessor.TAG_ARTIST}, new int[]{android.R.id.text1}));
        handleIntent(getIntent());

    }

    public void openImageBig(View view) {
        Log.i("Aseeee", "seoo");
        Dialog builder = new Dialog(this);
        ViewGroup.LayoutParams params = builder.getWindow().getAttributes();
        params.width = ActionBar.LayoutParams.MATCH_PARENT;
        params.height = ActionBar.LayoutParams.MATCH_PARENT;
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });
        ImageView tempImageView = (ImageView) view;
        ImageView nImage = new ImageView(this);
        nImage.setImageDrawable(tempImageView.getDrawable());
        builder.addContentView(nImage, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        builder.show();
    }

    public void delete_db(MenuItem item) {
        DatabaseAccessor.deleteAll();
        refresh();
    }

    private void refresh(){
        currentCursor = DatabaseAccessor.readDb(0);
        setListAdapter(new CursorArrayAdapter(this, android.R.layout.simple_list_item_1,
                currentCursor, new String[]{DatabaseAccessor.TAG_ARTIST}, new int[]{android.R.id.text1}));
    }

    public void insert_initial(MenuItem item) {
        if(currentCursor != null && currentCursor.getCount() == 0){
            loadInitialValue();
        }
    }

    public void change_mode(){
        if(adminSwitch != null){
            if(adminSwitch.isAdmin()){
                adminSwitchToast = Toast.makeText(MainActivity.this,"User mode on", Toast.LENGTH_SHORT);
                adminSwitchToast.show();
                adminSwitch.setAdmin(false);
                menu.setGroupVisible(R.id.overFlowItemsToHide, false);
            }else{
                adminSwitchToast = Toast.makeText(MainActivity.this,"Admin mode on", Toast.LENGTH_SHORT);
                adminSwitchToast.show();
                adminSwitch.setAdmin(true);
                menu.setGroupVisible(R.id.overFlowItemsToHide, true);
            }
        }
    }
    public void change_mode(MenuItem item) {
        change_mode();
    }

    public void importDb(MenuItem item) {
        DatabaseAccessor.importDatabase();
    }
}



