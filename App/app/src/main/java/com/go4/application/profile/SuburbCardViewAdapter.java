package com.go4.application.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go4.application.R;
import com.go4.application.profile.SuburbCard;

import java.util.ArrayList;

public class SuburbCardViewAdapter extends RecyclerView.Adapter<SuburbCardViewAdapter.RecyclerViewHolder> {

    // creating a variable for our array list and context.
    private ArrayList<SuburbCard> suburbDataArrayList;
    private Context mcontext;

    // creating a constructor class.
    public SuburbCardViewAdapter(ArrayList<SuburbCard> recyclerDataArrayList, Context mcontext) {
        this.suburbDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_profile_card, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview from our modal class.
        SuburbCard recyclerData = suburbDataArrayList.get(position);
        holder.label.setText(recyclerData.getLabel());
        holder.suburb.setText(recyclerData.getSuburb());
        holder.quality.setText(recyclerData.getQuality());
        holder.pm10Number.setText(recyclerData.getPm10Number());
    }

    @Override
    public int getItemCount() {
        // this method returns
        // the size of recyclerview
        return suburbDataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        // creating a variable for our text view.
        private TextView label;
        private TextView suburb;
        private TextView quality;
        private TextView pm10Number;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            label = itemView.findViewById(R.id.pa_card_label);
            suburb = itemView.findViewById(R.id.pa_card_suburb);
            quality = itemView.findViewById(R.id.pa_card_quality);
            pm10Number = itemView.findViewById(R.id.pa_card_number);

        }
    }
}
