package com.example.allergendetector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {
    private ArrayList<FoodAllergy> allergylist;

    public recyclerAdapter(ArrayList<FoodAllergy> allergylist) {
        this.allergylist = allergylist;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvAllergen;
        private TextView txtvAllergy;

        public MyViewHolder(final View view) {
            super(view);

            txtvAllergen = view.findViewById(R.id.txtvAllergen);
            txtvAllergy = view.findViewById(R.id.txtvAllergy);
        }
    }


    @NonNull
    @Override
    public recyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerAdapter.MyViewHolder holder, int position) {
        String allergen = allergylist.get(position).getFood();
        String allergy = allergylist.get(position).getAllergy();

        holder.txtvAllergen.setText(allergen);
        holder.txtvAllergy.setText(allergy);

    }

    @Override
    public int getItemCount() {
        return allergylist.size();
    }
}
