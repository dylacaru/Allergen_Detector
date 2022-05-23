package com.example.allergendetector;

public class FoodAllergy {
    private String food;
    private String allergy;

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    @Override
    public String toString() {
        return "FoodAllergy{" +
                "food='" + food + '\'' +
                ", allergy='" + allergy + '\'' +
                '}';
    }
}

