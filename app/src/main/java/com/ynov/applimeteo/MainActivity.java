package com.ynov.applimeteo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String OPEN_WEATHER_MAP_URL = "https://www.prevision-meteo.ch/services/json/";
    private static Context context;

    TextView cityField, detailsField, currentTemperatureField, humidtyField, pressureField, weatherIcon, updatedField;
    static String latitude;
    static String longitude;
    protected LocationManager locationManager;
    TextView txtLat;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //requestPermissions();


        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        txtLat = (TextView) findViewById(R.id.city_field);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        getJSONResponse("location", latitude, longitude);
    }

    public void getJSONResponse(String type, String lat,String lng) {
        try {
            switch (type) {
                case "location":
                    getWeatherJsonPosition(lat, lng);
                    break;
                case "ville":
                    getWeatherJsonVille("Toulouse");
                    break;
            }

        } catch (Exception e) {
            Log.d("Error", "Cannot process Json Result", e);
        }
    }

    public void JsonRenderPosition(JSONObject meteoValue) {
        try {

            String temperature = meteoValue.getString("tmp");
            String condition = meteoValue.getString("condition");
            String icon = meteoValue.getString("icon");
            String humidity = meteoValue.getString("humidity");

            currentTemperatureField = findViewById(R.id.current_temperature);
            currentTemperatureField.setText(temperature);

        } catch (Exception e) {
            Log.d("Error", "Error");
        }
    }

    public void getWeatherJsonPosition(String lat, String lon) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = OPEN_WEATHER_MAP_URL + "lat=" + lat + "lng=" + lon;
        Log.v("url", url);
        final JSONObject jObjCurrent = new JSONObject();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            Log.v("object", jObj.toString());
                            jObjCurrent.put("tmp", jObj.getJSONObject("current_condition").getString("tmp"));
                            jObjCurrent.put("condition", jObj.getJSONObject("current_condition").getString("condition"));
                            jObjCurrent.put("icon", jObj.getJSONObject("current_condition").getString("icon"));
                            jObjCurrent.put("humidity", jObj.getJSONObject("current_condition").getString("humidity"));
                            JsonRenderPosition(jObjCurrent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        queue.add(stringRequest);
    }

    public void getWeatherJsonVille(String ville) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = OPEN_WEATHER_MAP_URL + ville;
        final JSONObject jObjCurrent = new JSONObject();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            jObjCurrent.put("tmp", jObj.getJSONObject("current_condition").getString("tmp"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        queue.add(stringRequest);
    }
}