package com.asuc.asucmobile.infrastructure.models;

import com.google.gson.annotations.SerializedName;

public class TripBeforeTransform {
    @SerializedName("departure_time")
    private String tmpStartTime;

    @SerializedName("arrival_time")
    private String tmpEndTime;

    @SerializedName("line_name")
    private String lineName;

    @SerializedName("starting_stop")
    private StartingStop startingStop;

    @SerializedName("destination_stop")
    private DestinationStop destinationStop;

    @SerializedName("line_id")
    private int lineId;

    public String getTmpStartTime() {
        return tmpStartTime;
    }

    public String getTmpEndTime() {
        return tmpEndTime;
    }

    public String getLineName() {
        return lineName;
    }

    public StartingStop getStartingStop() {
        return startingStop;
    }

    public DestinationStop getDestinationStop() {
        return destinationStop;
    }

    public int getLineId() {
        return lineId;
    }

    public class StartingStop {
        @SerializedName("id")
        private int startId;

        @SerializedName("name")
        private String startName;

        public int getStartId() {
            return startId;
        }

        public String getStartName() {
            return startName;
        }
    }

    public class DestinationStop {
        @SerializedName("id")
        private int endId;

        @SerializedName("name")
        private String endName;

        public int getEndId() {
            return endId;
        }

        public String getEndName() {
            return endName;
        }
    }

}
