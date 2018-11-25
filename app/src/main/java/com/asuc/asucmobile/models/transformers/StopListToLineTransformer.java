package com.asuc.asucmobile.models.transformers;

import android.util.SparseArray;

import com.asuc.asucmobile.models.Line;
import com.asuc.asucmobile.models.LineRespModel;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.models.StopBeforeTransform;

import java.util.ArrayList;

public class StopListToLineTransformer {

    private SparseArray<Stop> stops = new SparseArray<>();
    private SparseArray<Line> lines = new SparseArray<>();

    public Line stopListToLine(LineRespModel lineResponse) {
        ArrayList<Stop> lineStops = new ArrayList<>();
        for (StopBeforeTransform s : lineResponse.getLineStops()) {
            if(stops.get(s.getStopId()) == null) {
                Stop stop = new Stop(s.getStopId(), s.getStopName(), s.getLatitude(), s.getLongitude());
                stops.put(s.getStopId(), stop);
                lineStops.add(stop);
            } else {
                lineStops.add(stops.get(s.getStopId()));
            }
        }
        Line currLine = new Line(lineResponse.getId(), lineResponse.getName(), lineStops);
        lines.put(lineResponse.getId(), currLine);
        return currLine;
    }

    Line getLine(int id, String name) {
        Line line = lines.get(id);
        if (line == null) {
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).getName().equals(name)) {
                    return lines.get(i);
                }
            }
        }
        return line;
    }

    Stop getStop(int id, String name) {
        Stop stop = stops.get(id);
        if (stop == null) {
            for (int i = 0; i < stops.size(); i++) {
                if (stops.valueAt(i).getName().equals(name)) {
                    return stops.valueAt(i);
                }
            }
        }
        return stop;
    }
}
