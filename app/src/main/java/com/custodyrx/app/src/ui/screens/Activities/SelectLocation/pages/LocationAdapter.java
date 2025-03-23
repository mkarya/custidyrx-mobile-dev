package com.custodyrx.app.src.ui.screens.Activities.SelectLocation.pages;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.custodyrx.app.R;

import com.custodyrx.app.src.ui.screens.Activities.SelectLocation.models.GetAllLocationResponseModel.LocationData;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private Context context;
    private List<LocationData> locationList;
    private OnItemClickListener onItemClickListener;

    public LocationAdapter(Context context, List<LocationData> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    public interface OnItemClickListener {
        void onItemClick(LocationData location);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_select_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationData location = locationList.get(position);
        holder.tvLocationName.setText(location.getName());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(location);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocationName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tv_location_name);
        }
    }
}

