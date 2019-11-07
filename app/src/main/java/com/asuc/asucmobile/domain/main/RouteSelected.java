package com.asuc.asucmobile.domain.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.asuc.asucmobile.R;
import com.asuc.asucmobile.domain.adapters.RouteAdapter;
import com.asuc.asucmobile.domain.models.Journey;
import com.asuc.asucmobile.domain.models.Stop;
import com.asuc.asucmobile.domain.models.Trip;
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
                        BitmapDescriptor dest = BitmapDescriptorFactory.fromResource(R.drawable.markernew);
                        BitmapDescriptor origin = BitmapDescriptorFactory.fromResource(R.drawable.markerclicked);

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lastStop.getLatitude(), lastStop.getLongitude()))
                                .icon(origin)
                        );
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(firstStop.getLatitude(), firstStop.getLongitude()))
                                .icon(dest)
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
                        String previd = "";
                        for (Trip trip : route.getTrips()) {
                            for (Stop stop : trip.getStops()) {
                                if (prevLocation == null) {
                                    prevLocation = new LatLng(stop.getLatitude(), stop.getLongitude());
                                    previd = stop.getName();


                                } else {
                                    if (previd.equals("Li Ka Shing: West Crescent Side") && stop.getName().equals("Life Science Addition West Circle Side") ) {
                                        PolylineOptions polyLineOptions = new PolylineOptions();
                                        polyLineOptions.add(prevLocation);
                                        polyLineOptions.add(new LatLng(37.871715, -122.264942));
                                        polyLineOptions.add(new LatLng(37.871819, -122.264640));
                                        polyLineOptions.add(new LatLng(37.872024, -122.263815));

                                        polyLineOptions.add(new LatLng(37.872039, -122.263737));
                                        polyLineOptions.add(new LatLng(37.871981, -122.263620));
                                        polyLineOptions.add(new LatLng(37.871981, -122.263620));
                                        polyLineOptions.add(new LatLng(stop.getLatitude(), stop.getLongitude()));

                                        polyLineOptions.width(10);
                                        polyLineOptions.color(Color.BLUE);
                                        mMap.addPolyline(polyLineOptions);
                                        previd = stop.getName();
                                        prevLocation = new LatLng(stop.getLatitude(), stop.getLongitude());
                                    }
                                    else if (previd.equals("Life Science Addition West Circle Side") && stop.getName().equals("Evans Hall Hearst Mining Circle Side")) {
                                        PolylineOptions polyLineOptions = new PolylineOptions();
                                        polyLineOptions.add(prevLocation);

                                        polyLineOptions.add(new LatLng(stop.getLatitude(), stop.getLongitude()));

                                        polyLineOptions.width(10);
                                        polyLineOptions.color(Color.BLUE);

                                        mMap.addPolyline(polyLineOptions);

                                        previd = stop.getName();
                                        prevLocation = new LatLng(stop.getLatitude(), stop.getLongitude());
                                    }
                                    else if (previd.equals("Life Science Addition West Circle Side") && stop.getName().equals("Li Ka Shing: West Crescent Side")) {
                                        PolylineOptions polyLineOptions = new PolylineOptions();
                                        polyLineOptions.add(prevLocation);
                                        //Custom points since Google Maps doesn't recognize that you can go through this route.
                                        polyLineOptions.add(new LatLng(37.872160, -122.263362));
                                        polyLineOptions.add(new LatLng(37.872271, -122.263487));
                                        polyLineOptions.add(new LatLng(37.872267, -122.263726));
                                        polyLineOptions.add(new LatLng(37.872131, -122.263779));
                                        polyLineOptions.add(new LatLng(37.871968, -122.264609));
                                        polyLineOptions.add(new LatLng(stop.getLatitude(), stop.getLongitude()));
                                        polyLineOptions.width(10);
                                        polyLineOptions.color(Color.BLUE);

                                        mMap.addPolyline(polyLineOptions);

                                        previd = stop.getName();
                                        prevLocation = new LatLng(stop.getLatitude(), stop.getLongitude());
                                    }   else if (previd.equals("Moffitt Library; Memorial Glade Side") && stop.getName().equals("Life Science Addition West Circle Side")) {
                                        PolylineOptions polyLineOptions = new PolylineOptions();
                                        polyLineOptions.add(prevLocation);
                                        polyLineOptions.add(new LatLng(37.873268, -122.260197));
                                        polyLineOptions.add(new LatLng(37.873209, -122.260573));
                                        polyLineOptions.add(new LatLng(37.873167, -122.260959));
                                        polyLineOptions.add(new LatLng(37.872688, -122.261614));
                                        polyLineOptions.add(new LatLng(37.872807, -122.262085));
                                        polyLineOptions.add(new LatLng(37.872786, -122.262530));
                                        polyLineOptions.add(new LatLng(37.872689, -122.262895));
                                        polyLineOptions.add(new LatLng( 37.872333, -122.263426));
                                        polyLineOptions.add(new LatLng(37.872295, -122.263464));
                                        polyLineOptions.add(new LatLng(stop.getLatitude(), stop.getLongitude()));

                                        polyLineOptions.width(10);
                                        polyLineOptions.color(Color.BLUE);

                                        mMap.addPolyline(polyLineOptions);

                                        previd = stop.getName();
                                        prevLocation = new LatLng(stop.getLatitude(), stop.getLongitude());
                                    }
                                    else {
                                        LatLng currentLocation = new LatLng(stop.getLatitude(), stop.getLongitude());
                                        String url = getMapsApiDirectionsUrl(prevLocation, currentLocation);
                                        RouteSelected.ReadTask downloadTask = new RouteSelected.ReadTask();
                                        downloadTask.execute(url);
                                        prevLocation = currentLocation;
                                        previd = stop.getName();
                                    }
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
        String key = "&key=" + getResources().getString(R.string.google_api_key_android);

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        //Mode
        String mode = "&driving";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + key + mode;


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
            ArrayList<LatLng> points;
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
                polyLineOptions.width(10);
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