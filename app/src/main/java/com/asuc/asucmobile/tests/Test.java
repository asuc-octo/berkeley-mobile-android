package com.asuc.asucmobile.tests;

import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.models.Library;
import com.asuc.asucmobile.models.Stop;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Test {

    @SuppressWarnings("all")
    public static void runTests() {

        // Library/Gym Test
        Date currentTime = new Date();

        Date opening1 = new Date();
        opening1.setTime(currentTime.getTime() - 1000000);
        Date closing1 = new Date();
        closing1.setTime(currentTime.getTime() + 1000000);
        Library testLibrary1 =
                new Library(0, "Library", "Location", "000-000-0000", opening1, closing1, null,
                        null, 0, 0, false, null, 0);

        Date opening2 = new Date();
        opening2.setTime(currentTime.getTime() + 1000000);
        Date closing2 = new Date();
        closing2.setTime(currentTime.getTime() - 1000000);
        Gym testGym1 =
                new Gym(0, "Library", "Location", opening2, closing2, "");

        Library testLibrary2 =
                new Library(1, "Library", "Location", "000-000-0000", null, null, null, null, 0, 0,
                        false, null, 0);

        if (!testLibrary1.isOpen()) {
            System.out.println("Open library reported as closed");
        }

        if (testGym1.isOpen()) {
            System.out.println("Closed gym reported as open");
        }

        if (testLibrary2.isOpen()) {
            System.out.println("Library with null hours reported as open");
        }

        // Name Abbreviation Test
     /*   Stop stop1 = new Stop(0, "Moffitt Library; Memorial Glade Side", new LatLng(0, 0));
        Stop stop2 = new Stop(0, "Li Ka Shing: West Crescent Side", new LatLng(0, 0));
        Stop stop3 = new Stop(0, "ASUC: Bancroft Way; Telegraph Avenue", new LatLng(0, 0));
        Stop stop4 = new Stop(0, "ASUC; Bancroft Way: Telegraph Avenue", new LatLng(0, 0));

        if (!stop1.getAbbreviatedName().equals("Moffitt Library")) {
            System.out.println(
                    "EXPECTED \"Moffitt Library\", but got: " + stop1.getAbbreviatedName());
        }

        if (!stop2.getAbbreviatedName().equals("Li Ka Shing")) {
            System.out.println("EXPECTED \"Li Ka Shing\", but got: " + stop2.getAbbreviatedName());
        }

        if (!stop3.getAbbreviatedName().equals("ASUC")) {
            System.out.println("EXPECTED \"ASUC\", but got: " + stop3.getAbbreviatedName());
        }

        if (!stop4.getAbbreviatedName().equals("ASUC")) {
            System.out.println("EXPECTED \"ASUC\", but got: " + stop4.getAbbreviatedName());
        }
*/
        System.out.println("Tests completed. Check above for any errors.");
    }

}
