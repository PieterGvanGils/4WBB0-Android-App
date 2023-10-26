package com.example.myapplication2;

import androidx.recyclerview.widget.AsyncDifferConfig;

public class CompetitionItem {

    private int rank;
    private String username;
    private double averageWaterUsed;

    public CompetitionItem(int rank, String username, double averageWaterUsed) {
        this.rank = rank;
        this.username = username;
        this.averageWaterUsed = averageWaterUsed;
    }

    public int getRank() {
        return rank;
    }

    public String getUsername() {
        return username;
    }

    public double getAverageWaterUsed() {
        return averageWaterUsed;
    }
}
