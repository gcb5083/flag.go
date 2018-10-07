package com.ssg.maptest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


public class MainMap extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap lmap;
    private String url = "http://127.0.0.1";

    private double elementlength = 100 / 364567.2;
    private int iter = 13;

    private static final int STROKE_COLOR = 0x999f87af;
    private static final int FILL_COLOR = 0x4488527f;
    private static final int ACTIVE_COLOR = 0x66fcff6c;
    private static final int STROKE_WIDTH = 3;
    private double[] coordinates = new double[2];
    private HashMap<LatLng, Polygon> hexagons;
    private LocationManager locationManager;
    private RequestQueue queue;
    private String hints = "No Hints Provided.";
    private long playerid;
    private String provider;
    double latitude; // latitude
    double longitude; // longitude
    private boolean canGetLocation;
    private Location glocation = null;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    // The minimum time between updates in milliseconds
    //private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 60 minute
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
            return;
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, listener);

        playerid = (long) (Math.random() * 1000000000) + 1000000000;

        queue = Volley.newRequestQueue(this);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "/hints", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hints = response.toString();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
        };

        queue.add(getRequest);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, hints, Snackbar.LENGTH_LONG)
                        .setAction("Captured", null).show();

            }
        });
        fab.setSize(fab.SIZE_NORMAL);
        fab.show();
        fab.bringToFront();
    }


    private final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Location glocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            JSONObject request = new JSONObject();
            try {
                request.put("location", Long.toString(playerid) + "\t" + Double.toString(glocation.getLatitude()) + "\t" + Double.toString(glocation.getLongitude()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        lmap = googleMap;
        // Set the long click listener as a way to exit the map.
        //lmap.setOnMapLongClickListener(this);

        LatLng List = new LatLng(40.803845, -77.865218);
        lmap.addMarker(new MarkerOptions().position(List).title("LIST"));
        lmap.moveCamera(CameraUpdateFactory.newLatLng(List));
        lmap.moveCamera(CameraUpdateFactory.zoomTo(8.0f));

        LocationProvider provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        // Add a marker in Sydney and move the camera
        /*LatLng stateCollege = new LatLng(40.8, -77.86);
        lmap.addMarker(new MarkerOptions().position(stateCollege).title("State College, PA"));
        lmap.moveCamera(CameraUpdateFactory.newLatLng(stateCollege));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        lmap.setMyLocationEnabled(true);*/

        coordinates[0] = 40.8;
        coordinates[1] = -77.86;
        int offset = (int) (iter * elementlength * 1.2);
        Log.d("LAT", Double.toString(coordinates[0]));
        Log.d("LONG", Double.toString(coordinates[1]));
        LatLng location = new LatLng(coordinates[0], coordinates[1]);
        hexagons = plotHexMesh(coordinates, 10);
        lmap.moveCamera(CameraUpdateFactory.newLatLng(location));
        lmap.setMaxZoomPreference(18);
        lmap.setMinZoomPreference(14);

    }

    private LatLng getCenter(List<LatLng> points){
        float sumLat = 0;
        float sumLong = 0;
        for ( int i =0; i< points.size(); i++){
            sumLat += points.get(i).latitude;
            sumLong += points.get(i).longitude;
        }

        return new LatLng( sumLat/points.size(), sumLong/points.size());
    }

    private HashMap<LatLng, Polygon> plotHexMesh(double[] city, int iterationnumber) {
//            Log.d("A","Ran plotHexMesh");
        int scalefactor = 1000;
        hexagons = new HashMap<LatLng, Polygon>();
        double[] center;
        Polygon centerhex = lmap.addPolygon(new PolygonOptions().clickable(true).fillColor(ACTIVE_COLOR).
                strokeWidth(STROKE_WIDTH).strokeColor(STROKE_COLOR).add (
                new LatLng(city[0] + elementlength * Math.pow(3, 0.5), city[1] + elementlength),
                new LatLng(city[0], city[1] + elementlength * 2),
                new LatLng(city[0] - elementlength * Math.pow(3, 0.5), city[1] + elementlength),
                new LatLng(city[0] - elementlength * Math.pow(3, 0.5), city[1] - elementlength),
                new LatLng(city[0], city[1] - elementlength * 2),
                new LatLng(city[0] + elementlength * Math.pow(3, 0.5), city[1] - elementlength)));

        hexagons.put(getCenter(centerhex.getPoints()),centerhex);
        for (double shell = 1; shell < iterationnumber; shell ++) {
            for (int side = 0; side < 6; side ++) {
                for (double hexagon = 0; hexagon < shell - 1; hexagon ++) {
                    center = findCenter(city, elementlength * Math.pow(3, 0.5) / 2, shell, side, hexagon);
                    Polygon cityhex = lmap.addPolygon(new PolygonOptions().clickable(true).fillColor(FILL_COLOR).
                            strokeWidth(STROKE_WIDTH).strokeColor(STROKE_COLOR).add(
                            new LatLng(center[0] + elementlength * Math.pow(3, 0.5), center[1] + elementlength),
                            new LatLng(center[0], center[1] + elementlength * 2),
                            new LatLng(center[0] - elementlength * Math.pow(3, 0.5), center[1] + elementlength),
                            new LatLng(center[0] - elementlength * Math.pow(3, 0.5), center[1] - elementlength),
                            new LatLng(center[0], center[1] - elementlength * 2),
                            new LatLng(center[0] + elementlength * Math.pow(3, 0.5), center[1] - elementlength)));
                    hexagons.put(getCenter(cityhex.getPoints()),cityhex);
                }

            }
        }
        return hexagons;

    }

    private double[] findCenter(double[] city, double elementlength, double shell, int side, double hexagon) {
        double[] center = new double[2];
        elementlength = elementlength * 2;
        shell --;
        if (side == 0) {
            center[0] = city[0] + (shell * 2 - hexagon) * elementlength;
            center[1] = city[1] + hexagon * elementlength * Math.pow(3, 0.5);
        }
        else if (side == 1) {
            center[0] = city[0] + (shell - hexagon * 2) * elementlength;
            center[1] = city[1] + shell * elementlength * Math.pow(3, 0.5);
        }
        else if (side == 2) {
            center[0] = city[0] - (shell + hexagon) * elementlength;
            center[1] = city[1] + (shell - hexagon) * elementlength * Math.pow(3, 0.5);
        }
        else if (side == 3) {
            center[0] = city[0] + (hexagon - shell * 2) * elementlength;
            center[1] = city[1] - hexagon * elementlength * Math.pow(3, 0.5);
        }
        else if (side == 4) {
            center[0] = city[0] + (hexagon * 2 - shell) * elementlength;
            center[1] =  city[1] - shell * elementlength * Math.pow(3, 0.5);
        }
        else {
            center[0] = city[0] + (shell + hexagon) * elementlength;
            center[1] = city[1] + (hexagon - shell) * elementlength * Math.pow(3, 0.5);
        }
        return center;
    }
}
