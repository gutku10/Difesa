package com.tika.app2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tika.app2.Medical.bcgActivity;
import com.tika.app2.R;
import com.tika.app2.Selections.InfantDetails;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<VaccineViewHolder>{

    private Context mContext;
    private List<InfantDetails> myVaccineList;

    public MyAdapter(Context mContext, List<InfantDetails> myVaccineList) {
        this.mContext = mContext;
        this.myVaccineList = myVaccineList;
    }


    @Override
    public VaccineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_item,parent,false);


        return new VaccineViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final VaccineViewHolder holder, int position) {

        holder.imageView.setImageResource(myVaccineList.get(position).getVaccineImage());
        holder.mTitle.setText(myVaccineList.get(position).getVaccineName());
        holder.mDescription.setText(myVaccineList.get(position).getVaccineDet());
        holder.mDose.setText(myVaccineList.get(position).getVaccineDose());

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Intent intent = new Intent(mContext,bcgActivity.class);
                Intent intent = new Intent(mContext,bcgActivity.class);

                intent.putExtra("Title",myVaccineList.get(holder.getAdapterPosition()).getVaccineName());
                intent.putExtra("Image",myVaccineList.get(holder.getAdapterPosition()).getVaccineImage());
                intent.putExtra("Description",myVaccineList.get(holder.getAdapterPosition()).getVideodesc());
                intent.putExtra("VideoString",myVaccineList.get(holder.getAdapterPosition()).getVideoCode());


                mContext.startActivity(intent);
/*
                Bundle bundle= new Bundle();

                bundle.putString("Title",myVaccineList.get(holder.getAdapterPosition()).getVaccineName());
                bundle.putInt("Image",myVaccineList.get(holder.getAdapterPosition()).getVaccineImage());
                bundle.putString("Description",myVaccineList.get(holder.getAdapterPosition()).getVideodesc());
                bundle.putString("VideoString",myVaccineList.get(holder.getAdapterPosition()).getVideoCode());

                fragment.setArguments(bundle);*/






            }
        });



    }

    @Override
    public int getItemCount() {
        return myVaccineList.size();
    }
}

class VaccineViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView mTitle,mDescription,mDose;
    CardView mCardView;

    public VaccineViewHolder(View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.ivImage);
        mTitle = itemView.findViewById(R.id.tvTitle);
        mDescription = itemView.findViewById(R.id.tvDescription);
        mDose = itemView.findViewById(R.id.dose);

        mCardView = itemView.findViewById(R.id.myCardView);
    }
}
