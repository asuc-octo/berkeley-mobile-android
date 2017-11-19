package com.asuc.asucmobile.main;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.adapters.RouteAdapter;
import com.asuc.asucmobile.models.Journey;
import com.asuc.asucmobile.models.Stop;
import com.asuc.asucmobile.models.Trip;
import com.asuc.asucmobile.utilities.MapHttpConnection;
import com.asuc.asucmobile.utilities.PathJSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alexthomas on 5/24/17.
 */

public class RouteSelected extends BaseActivity {

    private static final int REQUEST_CODE_PLAY_SERVICES = 1;

    private MapFragment mapFragment;
    private GoogleMap mMap;
    private Journey route;
    private ArrayList<Polyline> polyLines = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_route);
        if (exitIfNoData()) return;

        setupToolbar("Route", true);

        route = (Journey) getIntent().getSerializableExtra("list_of_times");

        // Get references to views.
        ListView routeList = (ListView) findViewById(R.id.stop_list);
        View header = getLayoutInflater().inflate(R.layout.header_map, routeList, false);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        // Add the header to the list.
        routeList.addHeaderView(header);

        // Populate route list.
        RouteAdapter adapter = new RouteAdapter(this, route);
        routeList.setAdapter(adapter);
        setUpMap();

    }

    public boolean exitIfNoData() {
        return getIntent().getSerializableExtra("list_of_times") == null;
    }

    @SuppressWarnings("deprecation")
    private void setUpMap() {
        // Checking if Google Play Services are available to set up the map.
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                GooglePlayServicesUtil
                        .getErrorDialog(status, this, REQUEST_CODE_PLAY_SERVICES).show();
            } else {
                Toast.makeText(
                        this,
                        "Unable to display map. Make sure you have Google Play Services.",
                        Toast.LENGTH_LONG
                ).show();
            }

            return;
        }

        if (mMap == null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        mMap = googleMap;
                        Trip firstTrip = route.getTrips().get(0);
                        Stop firstStop = firstTrip.getStops().get(0);
                        Trip lastTrip = route.getTrips().get(route.getTrips().size() - 1);
                        Stop lastStop = lastTrip.getStops().get(lastTrip.getStops().size() - 1);
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        BitmapDescriptor pin = BitmapDescriptorFactory.fromResource(R.drawable.markernew);

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lastStop.getLatitude(), lastStop.getLongitude()))
                                .icon(pin)
                        );
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(firstStop.getLatitude(), firstStop.getLongitude()))
                                .icon(pin)
                        );

                        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mMap.setMyLocationEnabled(true);
                        }
                        final LatLngBounds.Builder builder = LatLngBounds.builder();
                        builder.include(new LatLng(firstStop.getLatitude(), firstStop.getLongitude()));
                        builder.include(new LatLng(lastStop.getLatitude(), lastStop.getLongitude()));

                        LatLng prevLocation = null;
                        for (Trip trip : route.getTrips()) {
                            for (Stop stop : trip.getStops()) {
                                if (prevLocation == null) {
                                    prevLocation = new LatLng(stop.getLatitude(), stop.getLongitude());


                                } else {
                                    LatLng currentLocation = new LatLng(stop.getLatitude(), stop.getLongitude());
                                    String url = getMapsApiDirectionsUrl(prevLocation, currentLocation);
                                    RouteSelected.ReadTask downloadTask = new RouteSelected.ReadTask();
                                    downloadTask.execute(url);
                                }
                            }
                        }


                        try {
                            Location myLocation = locationManager.getLastKnownLocation(bestProvider);
                            if (myLocation != null) {
                                builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                            }
                        } catch (SecurityException e) {
                            // Don't do anything
                        }
                        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                            @Override
                            public void onCameraChange(CameraPosition cameraPosition) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 240));
                            }
                        });
                    }
                }
            });
        }


    }


    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String key = "&key=" + "AIzaSyDcbLej4zbLofExtmWBHFUNHtI15_8BTYo";

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + key;


        return url;

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;
            // traversing through routes
            if (routes.size() == 0) {
                return;
            }
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(20);
                polyLineOptions.color(Color.BLUE);
            }
            polyLines.add(mMap.addPolyline(polyLineOptions));

        }


    }

    private class ReadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);


            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new RouteSelected.ParserTask().execute(result);
        }

    }

    public void removeAllPolylines() {
        for (Polyline polyline : polyLines) {
            polyline.remove();
        }
        polyLines.clear();
    }
}
