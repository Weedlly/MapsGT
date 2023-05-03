package com.example.mapsgt.ui.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mapsgt.R;

import java.util.List;
public class FavouritePlaceAdapter extends BaseAdapter {
    private List<FavouritePlace> mFavouritePlaces;
    private LayoutInflater mInflater;

    public FavouritePlaceAdapter(Context context, List<FavouritePlace> favouritePlaces) {
        mInflater = LayoutInflater.from(context);
        mFavouritePlaces = favouritePlaces;
    }

    @Override
    public int getCount() {
        return mFavouritePlaces.size();
    }

    @Override
    public FavouritePlace getItem(int position) {
        return mFavouritePlaces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.favourite_place_item, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.place_name_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FavouritePlace favouritePlace = getItem(position);

        holder.nameTextView.setText(favouritePlace.getName());

        return convertView;
    }

    private static class ViewHolder {
        TextView nameTextView;
    }
}
