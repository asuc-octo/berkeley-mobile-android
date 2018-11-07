package com.asuc.asucmobile.models.transformers;

import com.asuc.asucmobile.models.Journey;
import com.asuc.asucmobile.models.Line;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.models.TripBeforeTransform;
import com.asuc.asucmobile.models.responses.TripResponse;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class TripListToJourneyTransformer {

    private static final java.text.SimpleDateFormat DATE_FORMAT =
            new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final java.util.TimeZone PST = java.util.TimeZone.getTimeZone("America/Los_Angeles");

    public static Journey tripListToJourney(TripResponse tripResponse) throws ParseException {
//        StopListToLineTransformer transformer = new StopListToLineTransformer();
        for (TripBeforeTransform tripElem : tripResponse.getTripList()) {
            Long tmpTime = DATE_FORMAT.parse(tripElem.getTmpStartTime()).getTime();
            Date startTime = new Date(tmpTime + PST.getOffset(tmpTime));
            tmpTime = DATE_FORMAT.parse(tripElem.getTmpEndTime()).getTime();
            Date endTime = new Date(tmpTime + PST.getOffset(tmpTime));
//            Stop startStop = transformer.getStop();
        }
    }
}
