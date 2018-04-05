package com.asuc.asucmobile.utilities;

/**
 * Created by rustie on 3/26/18.
 */

public class ServerUtils {

    private static final String BASE_URL = "http://asuc-mobile-dev.herokuapp.com/api/";
    private static final String DEBUG_URL = "https://berkeleymobile-sandbox.herokuapp.com/";

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getDebugUrl() {
        return DEBUG_URL;
    }


}
