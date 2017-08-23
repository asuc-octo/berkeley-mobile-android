package com.asuc.asucmobile.controllers;

import android.content.Context;

import com.asuc.asucmobile.models.Libraries;
import com.asuc.asucmobile.models.Libraries.Library;
import com.asuc.asucmobile.utilities.CustomComparators;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class LibraryController {

    public static Library currentLibrary;

    public interface cService {

        String PATH = "weekly_libraries/";

        @GET(PATH)
        Call<Libraries> getData();

        @GET(PATH + "{id}/")
        Call<Libraries> getDatum(@Path("id") int id);
    }

    public static List<Library> parse(Libraries libraries, Context context) {
        for (Library library : libraries.data) {
            parseDatum(library);
        }
        Collections.sort(libraries.data, CustomComparators.FacilityComparators.getSortByFavoriteLibrary(context));
        return libraries.data;
    }

    public static Library parseDatum(Library library) {
        library.generateLatLng();
        library.setDayOfWeek();
        return library;
    }


    public static void setCurrentLibrary(Library library) {
        currentLibrary = library;
    }

    public static Library getCurrentLibrary() {
        return currentLibrary;
    }

}

