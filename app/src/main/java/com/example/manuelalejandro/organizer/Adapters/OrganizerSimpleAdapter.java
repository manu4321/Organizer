package com.example.manuelalejandro.organizer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manuelalejandro.organizer.MainActivity;
import com.example.manuelalejandro.organizer.R;
import com.example.manuelalejandro.organizer.holders.BaseHolder;
import com.example.manuelalejandro.organizer.tasks.ImageDownloaderTask;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Created by mfreites on 2017-07-29.
 */
public class OrganizerSimpleAdapter extends ArrayAdapter<File>{
    public Context context;
    List<File> list;
    public OrganizerSimpleAdapter(@NonNull Context context, @LayoutRes int resource, List<File> list) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = ((Activity)this.context).getLayoutInflater().inflate(R.layout.list_for_images, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.setID(position);
        ImageDownloaderTask.getInstance(context, viewHolder, position).execute(list.get(position));
        return convertView;
    }

    public static class ViewHolder extends BaseHolder {

        public ViewHolder(View view) {
             super.setImageView((ImageView)view.findViewById(R.id.imageInDir));

        }

    }
}
