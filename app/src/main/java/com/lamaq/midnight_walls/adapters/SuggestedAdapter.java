package com.lamaq.midnight_walls.adapters;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lamaq.midnight_walls.R;
import com.lamaq.midnight_walls.interfaces.RecyclerViewClickListner;
import com.lamaq.midnight_walls.models.SuggestedModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestedAdapter extends RecyclerView.Adapter<SuggestedAdapter.SuggestedViewHolder> {

    ArrayList<SuggestedModel> suggestedModels;
    final private RecyclerViewClickListner clickListner;

    public SuggestedAdapter(ArrayList<SuggestedModel> suggestedModels, RecyclerViewClickListner clickListner) {
        this.suggestedModels = suggestedModels;
        this.clickListner = clickListner;
    }

    @NonNull
    @Override
    public SuggestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_item, parent, false);
        final SuggestedViewHolder suggestedViewHolder = new SuggestedViewHolder(view);
        return suggestedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedViewHolder holder, int position) {
        SuggestedModel suggestedModel = suggestedModels.get(position);
        holder.image.setImageResource(suggestedModel.getImage());
        holder.title.setText(suggestedModel.getTitle());
    }

    @Override
    public int getItemCount() {
        return suggestedModels.size();
    }

    public class SuggestedViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView title;
        public SuggestedViewHolder(@NonNull View itemView){
            super(itemView);

            image = itemView.findViewById(R.id.suggestedImage);
            title = itemView.findViewById(R.id.suggestedTitle);

            itemView.setOnClickListener(view -> {
                clickListner.onItemClick(getBindingAdapterPosition());
            });
        }
    }
}
