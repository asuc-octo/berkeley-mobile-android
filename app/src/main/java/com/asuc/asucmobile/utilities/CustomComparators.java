package com.asuc.asucmobile.utilities;

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

        public static Comparator<Library> getSortByZA() {
            return sortByZA;
        }

        public static Comparator<Library> getSortByOpenness() {
            return sortByOpenness;
        }

        private static Comparator<Library> sortByAZ = new Comparator<Library>() {
            public int compare(Library arg0, Library arg1) {
                return arg0.getName().compareTo(arg1.getName());
            }
        };
        private static Comparator<Library> sortByZA = new Comparator<Library>() {
            public int compare(Library arg0, Library arg1) {
                return -1 * arg0.getName().compareTo(arg1.getName());
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
    }
}
