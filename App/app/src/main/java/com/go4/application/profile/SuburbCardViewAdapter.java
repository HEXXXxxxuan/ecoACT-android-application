package com.go4.application.profile;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go4.application.R;

import java.util.ArrayList;

/**
 * This class is used to transform each SuburbCard Object into a view on screen.
 *
 * <p>With reference to <a href="https://www.geeksforgeeks.org/swipe-to-delete-and-undo-in-android-recyclerview/">this website</a></p></p>
 * @author u8003980 Chan Cheng Leong
 */
public class SuburbCardViewAdapter extends RecyclerView.Adapter<SuburbCardViewAdapter.RecyclerViewHolder> {
    private ArrayList<SuburbCard> suburbDataArrayList;

    public SuburbCardViewAdapter(ArrayList<SuburbCard> recyclerDataArrayList) {
        this.suburbDataArrayList = recyclerDataArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_profile_card, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        SuburbCard recyclerData = suburbDataArrayList.get(position);
        holder.label.setText(recyclerData.getLabel());
        holder.suburb.setText(recyclerData.getSuburb());
        holder.quality.setText(recyclerData.getQuality());
        holder.pm10Number.setText(recyclerData.getPm10Number());

        holder.label.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recyclerData.setLabel(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if (recyclerData.getQuality() == "Good") {
            holder.background.setBackgroundResource(R.drawable.rounded_bg_good);
            holder.imageView.setImageResource(R.drawable.quality_good);
        } else if (recyclerData.getQuality() == "Moderate") {
            holder.background.setBackgroundResource(R.drawable.rounded_bg_moderate);
        } else if (recyclerData.getQuality() == "Bad") {
            holder.background.setBackgroundResource(R.drawable.rounded_bg_bad);
        }
    }

    @Override
    public int getItemCount() {
        return suburbDataArrayList.size();
    }

    /**
     * This class extends {@link RecyclerView.ViewHolder} and is used to handle {@link RecyclerView}.
     *
     * <p>It links each component in the suburb card to a variable.</p>
     * @author u8003980 Chan Cheng Leong
     */
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView label;
        private TextView suburb;
        private TextView quality;
        private TextView pm10Number;
        private ImageView imageView;
        private LinearLayout background;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.pa_card_label);
            suburb = itemView.findViewById(R.id.pa_card_suburb);
            quality = itemView.findViewById(R.id.pa_card_quality);
            pm10Number = itemView.findViewById(R.id.pa_card_number);
            imageView = itemView.findViewById(R.id.pa_card_image);
            background = itemView.findViewById(R.id.pa_card);
        }
    }
}
