package com.example.manuelalejandro.organizer;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.manuelalejandro.organizer.holders.BaseHolder;
import com.example.manuelalejandro.organizer.tasks.ImageDownloaderTask;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by manuelalejandro on 2016-10-04.
 */
public class CursorArrayAdapter extends SimpleCursorAdapter implements SectionIndexer {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_NORMAL = 0;

    private static final int TYPE_COUNT = 2;
    AlphabetIndexer mAlphabetIndexer;
    private int[] usedSectionNumbers;
    Cursor cursor;
    private Map<Integer, Integer> sectionToOffset;
    private Map<Integer, Integer> sectionToPosition;
    MainActivity context;

    public CursorArrayAdapter(Context context, int simpleListItem1,
                              Cursor cursor, String[] strings, int[] is) {
        super(context, simpleListItem1, cursor, strings, is);
        this.cursor = cursor;
        this.context = (MainActivity) context;
        mAlphabetIndexer = new AlphabetIndexer(cursor,
                cursor.getColumnIndex(DatabaseAccessor.TAG_ARTIST),
                " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        mAlphabetIndexer.setCursor(cursor);//Sets a new cursor as the data set and resets the cache of indices.
        sectionToPosition = new TreeMap<Integer, Integer>(); //use a TreeMap because we are going to iterate over its keys in sorted order
        sectionToOffset = new HashMap<Integer, Integer>();
        final int count = super.getCount();

        int i;
        //temporarily have a map alphabet section to first index it appears
        //(this map is going to be doing somethine else later)
        for (i = count - 1; i >= 0; i--) {
            sectionToPosition.put(mAlphabetIndexer.getSectionForPosition(i), i);
        }

        i = 0;
        usedSectionNumbers = new int[sectionToPosition.keySet().size()];

        //note that for each section that appears before a position, we must offset our
        //indices by 1, to make room for an alphabetical header in our list
        for (Integer section : sectionToPosition.keySet()) {
            sectionToOffset.put(section, i);
            usedSectionNumbers[i] = section;
            i++;
        }

        //use offset to map the alphabet sections to their actual indicies in the list
        for (Integer section : sectionToPosition.keySet()) {
            sectionToPosition.put(section, sectionToPosition.get(section) + sectionToOffset.get(section));
        }

    }

    @Override
    public int getCount() {
        if (super.getCount() != 0) {
            //sometimes your data set gets invalidated. In this case getCount()
            //should return 0 and not our adjusted count for the headers.
            //The only way to know if data is invalidated is to check if
            //super.getCount() is 0.
            return super.getCount() + usedSectionNumbers.length;
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {//we define this function in the full code later
            //if the list item is not a header, then we fetch the data set item with the same position
            //off-setted by the number of headers that appear before the item in the list
            return super.getItem(position - sectionToOffset.get(getSectionForPosition(position)) - 1);
        }

        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        if (!sectionToOffset.containsKey(section)) {
            //This is only the case when the FastScroller is scrolling,
            //and so this section doesn't appear in our data set. The implementation
            //of Fastscroller requires that missing sections have the same index as the
            //beginning of the next non-missing section (or the end of the the list if
            //if the rest of the sections are missing).
            //So, in pictorial example, the sections D and E would appear at position 9
            //and G to Z appear in position 11.
            int i = 0;
            int maxLength = usedSectionNumbers.length;

            //linear scan over the sections (constant number of these) that appear in the
            //data set to find the first used section that is greater than the given section, so in the
            //example D and E correspond to F
            while (i < maxLength && section > usedSectionNumbers[i]) {
                i++;
            }
            if (i == maxLength) return getCount(); //the given section is past all our data

            return mAlphabetIndexer.getPositionForSection(usedSectionNumbers[i]) + sectionToOffset.get(usedSectionNumbers[i]);
        }

        return mAlphabetIndexer.getPositionForSection(section) + sectionToOffset.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        int i = 0;
        int maxLength = usedSectionNumbers.length;

        //linear scan over the used alphabetical sections' positions
        //to find where the given section fits in
        while (i < maxLength && position >= sectionToPosition.get(usedSectionNumbers[i])) {
            i++;
        }
        return usedSectionNumbers[i - 1];
    }

    @Override
    public Object[] getSections() {
        return mAlphabetIndexer.getSections();
    }

    //nothing much to this: headers have positions that the sectionIndexer manages.
    @Override
    public int getItemViewType(int position) {
        if (position == getPositionForSection(getSectionForPosition(position))) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    //return the header view, if it's in a section header position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        cursor.moveToFirst();
        final int type = getItemViewType(position);

        if (type == TYPE_HEADER) {
            if (convertView == null) {
                convertView = this.context.getLayoutInflater().inflate(R.layout.header, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.header = ((TextView) convertView.findViewById(R.id.header));

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }
            viewHolder.header.setText((String) getSections()[getSectionForPosition(position)]);
            return convertView;
        } else {
            if (convertView == null) {
                convertView = this.context.getLayoutInflater().inflate(R.layout.lstt_layout, parent, false);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                viewHolder.getImageView().setImageBitmap(null);
            }
        }
        if(viewHolder == null){
            viewHolder = new ViewHolder();
            viewHolder.tvName =
                    (TextView) convertView.findViewById(R.id.tvArtist);
            viewHolder.tvId =
                    (TextView) convertView.findViewById(R.id.tvId);
            viewHolder.tvTitle =
                    (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.setImageView(
                    (ImageView) convertView.findViewById(R.id.smallImage));
            viewHolder.getImageView().setImageBitmap(null);
            convertView.setTag(viewHolder);
        }
        final int pos = position - sectionToOffset.get(getSectionForPosition(position)) - 1;
        cursor.moveToPosition(pos);
        String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        Log.i("id",viewHolder.getImageView().getDrawable() + "");
        viewHolder.setID(Integer.parseInt(id));

        String caption = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAccessor.TAG_ARTIST));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAccessor.TAG_TITLE));
        String image_url = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAccessor.TAG_IMAGE));
        viewHolder.tvName.setText("Artista: " + caption);
        viewHolder.tvTitle.setText("Titulo: " + title);
            //Log.i("here", caption + " is " + viewHolder.smallImage);
        ImageDownloaderTask.getInstance(context, viewHolder, Integer.parseInt(id)).execute(image_url);
        Log.i("CursorArrayAdapter.View", caption);
        return convertView;
    }


    //these two methods just disable the headers
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return false;
        }
        return true;
    }

    /**
     * Cache of the children views for a list item.
     */
    public static class ViewHolder extends BaseHolder {
        public TextView tvName;
        public TextView tvId;
        public TextView tvTitle;
        public TextView header;

        public ViewHolder(){

        }
        public ViewHolder(View view, boolean isHeader) {
            if (!isHeader) {
                tvName =
                        (TextView) view.findViewById(R.id.tvArtist);
                tvId =
                        (TextView) view.findViewById(R.id.tvId);
                tvTitle =
                        (TextView) view.findViewById(R.id.tvTitle);
                super.setImageView(
                        (ImageView) view.findViewById(R.id.smallImage));
            } else {
                header = ((TextView) view.findViewById(R.id.header));
            }

        }

    }

//    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
//        private final WeakReference<ImageView> imageViewReference;
//        public ImageDownloaderTask(ImageView imageView) {
//            imageViewReference = new WeakReference<ImageView>(imageView);
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            if (Settings.isLoadingImages()) {
//                return ImageHelper.getImage(new File(params[0]), REQ_WIDTH, REQ_HEIGHT, CursorArrayAdapter.this.context);
//            } else {
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            if (isCancelled()) {
//                bitmap = null;
//            }
//
//
//            if (imageViewReference != null) {
//                // Log.i("there", cap + " is " + imageViewReference.get().smallImage);
//                ImageView imageView = imageViewReference.get();
//                if (imageView != null) {
//                    if (bitmap != null) {
//                        imageView.setImageBitmap(bitmap);
//                    } else {
//                        Log.i("asde", "aseeeeee");
//                        Drawable placeholder = context.getResources().getDrawable(R.mipmap.ic_launcher);
//                        imageView.setImageDrawable(placeholder);
//                    }
//                }
//            }
//
//        }
//    }
}


