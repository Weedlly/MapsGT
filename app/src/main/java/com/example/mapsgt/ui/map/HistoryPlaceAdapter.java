package com.example.mapsgt.ui.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;

import java.util.List;

public class HistoryPlaceAdapter extends RecyclerView.Adapter<HistoryPlaceAdapter.ViewHolder> implements View.OnClickListener {

    private final List<HistoryPlace> historyPlaces;
    private OnItemClickListener listener;

    public HistoryPlaceAdapter(List<HistoryPlace> historyPlaces) {
        this.historyPlaces = historyPlaces;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = (int) view.getTag();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryPlace historyPlace = historyPlaces.get(position);

        holder.nameTextView.setText(historyPlace.getName());
        holder.addressTextView.setText(historyPlace.getAddress());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return historyPlaces.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public TextView addressTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_name);
            addressTextView = itemView.findViewById(R.id.tv_address);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}



