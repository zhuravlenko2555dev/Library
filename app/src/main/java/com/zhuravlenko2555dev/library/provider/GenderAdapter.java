package com.zhuravlenko2555dev.library.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuravlenko2555dev.library.R;

import java.util.ArrayList;

/**
 * Created by zhura on 20.03.2018.
 */

public class GenderAdapter extends ArrayAdapter<GenderItem> {
    private LayoutInflater inflater;
    private int listRowItem;
    private ArrayList<GenderItem> listData;

    public GenderAdapter(@NonNull Context context, int resource, @NonNull ArrayList<GenderItem> objects) {
        super(context, resource, objects);

        this.inflater = LayoutInflater.from(context);
        this.listRowItem = resource;
        this.listData = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    private View customView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(listRowItem, parent, false);
            holder = new ViewHolder();
            holder.imageViewGenderIcon = convertView.findViewById(R.id.imageViewGenderIcon);
            holder.textViewGenderName = convertView.findViewById(R.id.textViewGenderName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String genderName = listData.get(position).getName();

        holder.textViewGenderName.setText(genderName);
        holder.imageViewGenderIcon.setImageDrawable(getContext().getResources().getDrawable(listData.get(position).getIcon()));

        /*if (genderName.equals("male")) {
            holder.imageViewGenderIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.male));
            Log.d("female", "female");
        } else {
            holder.imageViewGenderIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.female));
            Log.d("male", "male");
        }*/

        return convertView;
    }

    class ViewHolder {
        ImageView imageViewGenderIcon;
        TextView textViewGenderName;
    }
}
