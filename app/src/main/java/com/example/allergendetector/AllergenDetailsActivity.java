package com.example.allergendetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AllergenDetailsActivity extends AppCompatActivity {

    List<FoodAllergy> foodAllergyList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergen_details);

        foodAllergyList = ScannerActivity.FOOD_ALLERGY_LIST;
        recyclerView = findViewById(R.id.recyclerView);
        setAdapter();
    }

    private ArrayList<FoodAllergy> displayAllergen() {
        Intent intent = getIntent();
        ArrayList<String> detectedAllergens = intent.getStringArrayListExtra("allergen");

        ArrayList<FoodAllergy> allergenInfo = new ArrayList<>();

        for (int i = 0; i < foodAllergyList.size(); i++) {
            if (detectedAllergens.contains(foodAllergyList.get(i).getFood().toLowerCase(Locale.ROOT))) {
                allergenInfo.add(foodAllergyList.get(i));
            }
        }
        return allergenInfo;
    }

    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(displayAllergen());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}