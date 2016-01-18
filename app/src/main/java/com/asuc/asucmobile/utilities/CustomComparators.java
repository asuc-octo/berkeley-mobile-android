package com.asuc.asucmobile.utilities;

import android.widget.Toast;

import com.asuc.asucmobile.main.ListOfFavorites;
import com.asuc.asucmobile.models.FoodItem;
import com.asuc.asucmobile.models.Library;

import java.util.Comparator;

/**
 * Created by Ankit on 10/17/2015.
 */
public class CustomComparators {
    public static class FacilityComparators {

        public static Comparator<Library> getSortByAZ() {
            return sortByAZ;
        }

        public static Comparator<Library> getSortByOpenness() {
            return sortByOpenness;
        }

        public static Comparator<Library> getSortByFavoriteLibrary() {
            return sortByFavoriteLibrary;
        }

        public static Comparator<FoodItem> getFoodSortByVegetarian() {
            return foodSortByVegetarian;
        }

        public static Comparator<FoodItem> getFoodSortByAZ() {
            return foodSortByAZ;
        }

        public static Comparator<FoodItem> getFoodSortByVegan() {
            return foodSortByVegan;
        }

        public static Comparator<FoodItem> getFoodSortByFavorite() {
            return foodSortByFavorite;
        }

        private static Comparator<Library> sortByAZ = new Comparator<Library>() {
            public int compare(Library arg0, Library arg1) {
                return arg0.getName().compareTo(arg1.getName());
            }
        };
        private static Comparator<Library> sortByOpenness = new Comparator<Library>() {
            public int compare(Library arg0, Library arg1) {
                if (arg0.isOpen() && arg1.isOpen()) return 0;
                if (arg0.isOpen()) return -1;
                if (arg1.isOpen()) return 1;
                return 0;
            }
        };
        private static Comparator<Library> sortByFavoriteLibrary = new Comparator<Library>() {
            @Override
            public int compare(Library lhs, Library rhs) {
                ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject("libraries");
                if (listOfFavorites.contains(lhs.getName()) && listOfFavorites.contains(rhs.getName())) return 0;
                if (listOfFavorites.contains(lhs.getName())) return -1;
                if (listOfFavorites.contains(rhs.getName())) return 1;
                return 0;
            }
        };
        private static Comparator<FoodItem> foodSortByAZ = new Comparator<FoodItem>() {
            public int compare(FoodItem arg0, FoodItem arg1) {
                return arg0.getName().compareTo(arg1.getName());
            }
        };

        private static Comparator<FoodItem> foodSortByVegetarian = new Comparator<FoodItem>() {
            public int compare(FoodItem arg0, FoodItem arg1) {
                if (!arg0.getFoodType().equals("None") && arg0.getFoodType().toUpperCase().equals("VEGETARIAN")) return -1;
                if (!arg1.getFoodType().equals("None") && arg1.getFoodType().toUpperCase().equals("VEGETARIAN")) return 1;
                return 0;
            }
        };

        private static Comparator<FoodItem> foodSortByVegan = new Comparator<FoodItem>() {
            public int compare(FoodItem arg0, FoodItem arg1) {
                if (!arg0.getFoodType().equals("None") && arg0.getFoodType().toUpperCase().equals("VEGAN")) return -1;
                if (!arg1.getFoodType().equals("None") && arg1.getFoodType().toUpperCase().equals("VEGAN")) return 1;
                return 0;
            }
        };

        private static Comparator<FoodItem> foodSortByFavorite = new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem arg0, FoodItem arg1) {
                ListOfFavorites listOfFavorites = (ListOfFavorites) SerializableUtilities.loadSerializedObject("dining_halls");
                if (listOfFavorites.contains(arg0.getName()) && listOfFavorites.contains(arg1.getName())) return 0;
                if (listOfFavorites.contains(arg0.getName())) return -1;
                if (listOfFavorites.contains(arg1.getName())) return 1;
                return 0;
            }
        };

    }
}
