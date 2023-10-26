package com.example.myapplication2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.CompetitionItem;
import com.example.myapplication2.R;

import java.util.List;

public class CompetitionAdapter extends RecyclerView.Adapter<CompetitionAdapter.CompetitionViewHolder> {

    private List<CompetitionItem> competitionItems;

    public CompetitionAdapter(List<CompetitionItem> competitionItems) {
        this.competitionItems = competitionItems;
    }

    @NonNull
    @Override
    public CompetitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_item, parent, false);
        return new CompetitionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompetitionViewHolder holder, int position) {
        CompetitionItem item = competitionItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return competitionItems.size();
    }

    static class CompetitionViewHolder extends RecyclerView.ViewHolder {

        private final TextView rankTextView;
        private final TextView usernameTextView;
        private final TextView waterUsedTextView;

        public CompetitionViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.competition_rankingPosition);
            usernameTextView = itemView.findViewById(R.id.competition_userName);
            waterUsedTextView = itemView.findViewById(R.id.competition_averageWaterUsed);
        }
        public void bind(CompetitionItem item) {
            rankTextView.setText(String.valueOf(item.getRank()));
            usernameTextView.setText(item.getUsername());
            waterUsedTextView.setText(String.valueOf(item.getAverageWaterUsed()));
        }
    }
}

//    @Override
//    public void onBindViewHolder(@NonNull CompetitionViewHolder holder, int position) {
//        CompetitionItem currentItem = rankings.get(position);
//
//        holder.rankTextView.setText(String.valueOf(currentItem.getRank()));
//        holder.usernameTextView.setText(currentItem.getUsername());
//        holder.waterUsedTextView.setText(String.valueOf(currentItem.getAverageWaterUsed()));
//    }

