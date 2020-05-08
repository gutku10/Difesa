package com.tika.app2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.BiMap;
import com.tika.app2.Front.Emergency.EmergencyMapActivity;
import com.tika.app2.Front.Emergency.EmergencySelectionActivity;
import com.tika.app2.R;

import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.MyViewHolder> {

    private Map<String, LatLng> locations;
    private Map<String, String> names, near;
    private Map<String, Integer> ages;
    private BiMap<String, Integer> UIdIndex;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView emergencyConstTextView, nearConstTextView, nearTextView, imageTextView, apprxTimeTextView, nameTextView, ageTextView;

        public MyViewHolder(View view) {
            super(view);

            emergencyConstTextView = view.findViewById(R.id.emergencyConstText);
            nearConstTextView = view.findViewById(R.id.nearConstText);
            nearTextView = view.findViewById(R.id.emergencyNear);
            imageTextView = view.findViewById(R.id.emergencySelectionMap);
            ageTextView = view.findViewById(R.id.emergency_age);
            apprxTimeTextView = view.findViewById(R.id.emergencyApprxTimeByDriving);
            nameTextView = view.findViewById(R.id.emergency_name);

        }

    }

    public EmergencyAdapter(Map<String, LatLng> locations, Map<String, String> names, Map<String, Integer> ages, Map<String, String> near, BiMap<String, Integer> UIdIndex,  Context context){

        this.locations = locations;
        this.names = names;
        this.ages = ages;
        this.near = near;
        this.UIdIndex = UIdIndex;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emergency_item_layout, parent, false);

        MyViewHolder vh = new MyViewHolder(view);

        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(locations.size()==0){

        }

        else{
            holder.nearConstTextView.setText("Near");
            holder.emergencyConstTextView.setText("Emergency");
            holder.imageTextView.setText("View on map");
            holder.imageTextView.setCompoundDrawables(null, context.getDrawable(R.drawable.ic_map), null, null);


            holder.apprxTimeTextView.setText("5 min");
            holder.nearTextView.setText("Plymouth2");

            BiMap<Integer, String> indexUId = UIdIndex.inverse();
            Log.i(TAG, "onDataChange:$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ " + UIdIndex);


            Log.i(TAG, "onBindViewHolder: $$$$$$$$$$$$$$$$ Binding view for UID" + indexUId.get(position));

            holder.nameTextView.setText(names.get(indexUId.get(position)));
//            holder.ageTextView.setText(ages.get(indexUId.get(position)));


            holder.imageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, EmergencyMapActivity.class);

                    BiMap<Integer, String> indexUId = UIdIndex.inverse();

                    intent.putExtra("Latitude", locations.get(indexUId.get(position)).latitude);
                    intent.putExtra("Longitude", locations.get(indexUId.get(position)).longitude);
                    intent.putExtra("UId", indexUId.get(position));
                    intent.putExtra("Name", names.get(indexUId.get(position)));

                    context.startActivity(intent);
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        if(locations.size()==0)
            return 0;
        return locations.size();
    }


}
