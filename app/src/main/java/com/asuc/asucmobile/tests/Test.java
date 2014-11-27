package com.asuc.asucmobile.tests;

import com.asuc.asucmobile.models.Gym;
import com.asuc.asucmobile.models.Library;

import java.util.Date;

public class Test {

    public static void runTests() {

        // Library/Gym Test
        Date currentTime = new Date();

        Date opening1 = new Date();
        opening1.setTime(currentTime.getTime() - 1000000);
        Date closing1 = new Date();
        closing1.setTime(currentTime.getTime() + 1000000);
        Library testLibrary1 =
                new Library("0", "Library", "Location", "000-000-0000", opening1, closing1, "", 0, 0, false);

        System.out.print("Testing Library that should be open: ");
        if (testLibrary1.isOpen()) {
            System.out.println("PASS\n");
        } else {
            System.out.println("FAIL\n");
        }

        Date opening2 = new Date();
        opening2.setTime(currentTime.getTime() + 1000000);
        Date closing2 = new Date();
        closing2.setTime(currentTime.getTime() - 1000000);
        Gym testGym2 =
                new Gym("0", "Library", "Location", opening2, closing2, "");

        System.out.print("Testing Gym that should be closed: ");
        if (!testGym2.isOpen()) {
            System.out.println("PASS\n");
        } else {
            System.out.println("FAIL\n");
        }

        Library testLibrary3 =
                new Library("0", "Library", "Location", "000-000-0000", null, null, "", 0, 0, false);

        System.out.print("Testing Library that should be closed when given null Dates: ");
        if (!testLibrary3.isOpen()) {
            System.out.println("PASS\n");
        } else {
            System.out.println("FAIL\n");
        }

    }

}
