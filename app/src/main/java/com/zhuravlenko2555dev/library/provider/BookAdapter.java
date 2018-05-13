package com.zhuravlenko2555dev.library.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuravlenko2555dev.library.R;
import com.zhuravlenko2555dev.library.activity.BookActivity;
import com.zhuravlenko2555dev.library.provider.BookItem;
import com.zhuravlenko2555dev.library.util.APIAsyncTask;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<BookItem> {
    private Context context;
    private LayoutInflater inflater;
    private int listRowItem;
    private ArrayList<BookItem> listData;

    public BookAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BookItem> objects) {
        super(context, resource, objects);

        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listRowItem = resource;
        this.listData = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(listRowItem, parent, false);
            holder = new ViewHolder();
            holder.imageViewBookSmall = convertView.findViewById(R.id.imageViewBookSmall);
            holder.textViewBookName = convertView.findViewById(R.id.textViewBookName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String bookName = listData.get(position).getName();

        holder.textViewBookName.setText(bookName);
        /*holder.imageViewBookSmall.setImageDrawable(getContext().getResources().getDrawable(listData.get(position).getImageSmall()));*/
        // Finally load the image asynchronously into the ImageView, this also takes care of
        // setting a placeholder image while the background thread runs
        ((BookActivity) context).imageFetcher.loadImage(APIAsyncTask.serverAddress + APIAsyncTask.apiPath + listData.get(position).getImageSmall(), holder.imageViewBookSmall);
        Log.d("path", APIAsyncTask.serverAddress + APIAsyncTask.apiPath + listData.get(position).getImageSmall());

        return convertView;
    }

    class ViewHolder {
        ImageView imageViewBookSmall;
        TextView textViewBookName;
    }
}
