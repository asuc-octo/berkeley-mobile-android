package com.asuc.asucmobile.models;

import java.util.Date;

public class Library {

    private String id;
    private String name;
    private String location;
    private String phone;
    private Date opening;
    private Date closing;
    private boolean byAppointment;

    public Library(String id, String name, String location, String phone, Date opening, Date closing, boolean byAppointment) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.phone = phone;
        this.opening = opening;
        this.closing = closing;
        this.byAppointment = byAppointment;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPhone() {
        return phone;
    }

    public Date getOpening() {
        return opening;
    }

    public Date getClosing() {
        return closing;
    }

    public boolean isByAppointment() {
        return byAppointment;
    }

    /**
     * isOpen() returns whether or not the facility is open.
     *
     * @return Boolean indicating if the library is open or not.
     */
    public boolean isOpen() {
        if (opening == null || closing == null) {
            return false;
        }

        Date currentTime = new Date();
        return currentTime.after(opening) && currentTime.before(closing);
    }

    public static void testIsOpen() {
        Date currentTime = new Date();

        Date opening1 = new Date();
        opening1.setTime(currentTime.getTime() - 1000000);
        Date closing1 = new Date();
        closing1.setTime(currentTime.getTime() + 1000000);
        Library testLibrary1 =
                new Library("0", "Library", "Location", "000-000-0000", opening1, closing1, false);
        if (testLibrary1.isOpen()) {
            System.out.println("Test Case 1 passed.");
        } else {
            System.out.println("Test Case 1 failed: Library that should have been open is not.");
        }

        Date opening2 = new Date();
        opening2.setTime(currentTime.getTime() + 1000000);
        Date closing2 = new Date();
        closing2.setTime(currentTime.getTime() - 1000000);
        Library testLibrary2 =
                new Library("0", "Library", "Location", "000-000-0000", opening2, closing2, false);
        if (!testLibrary2.isOpen()) {
            System.out.println("Test Case 2 passed.");
        } else {
            System.out.println("Test Case 2 failed: Library that should have been closed is not.");
        }

        Library testLibrary3 =
                new Library("0", "Library", "Location", "000-000-0000", null, null, false);
        if (!testLibrary3.isOpen()) {
            System.out.println("Test Case 3 passed.");
        } else {
            System.out.println("Test Case 3 failed: Library that's closed all day is displaying open.");
        }
    }

}
