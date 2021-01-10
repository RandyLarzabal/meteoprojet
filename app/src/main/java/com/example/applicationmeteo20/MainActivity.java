package com.example.applicationmeteo20;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;


import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;







public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String OPEN_WEATHER_MAP_URL = "https://www.prevision-meteo.ch/services/json/";
    private static Context context;

    TabLayout tabLayout;

    static String latitude;
    static String longitude;
    protected LocationManager locationManager;
    TextView txtLat;
    Bundle bundle = new Bundle();

    @Override
    protected void onResume() {
        super.onResume();

        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        if(btnSearch != null)
        {
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getJSONResponse("ville","","");
             }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //requestPermissions();

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        Button btnPosition = (Button) findViewById(R.id.btnPosition);
        btnPosition.setOnClickListener(v -> navController.navigate(R.id.action_meteoVilles_to_meteoPosition, bundle));
        Button btnVille = (Button) findViewById(R.id.btnVille);
        btnVille.setOnClickListener(v -> navController.navigate(R.id.action_meteoPosition_to_meteoVilles, bundle));




        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

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
                    EditText editText = findViewById(R.id.SearchText);
                    getWeatherJsonVille(editText.getText().toString());
                    break;
            }

        } catch (Exception e) {
            Log.d("Error", "Cannot process Json Result", e);
        }
    }

    @SuppressLint("SetTextI18n")
    public void JsonRenderPosition(JSONObject meteoValue) {
        try {

            String temperature = meteoValue.getString("tmp");
            String condition = meteoValue.getString("condition");
            String icon = meteoValue.getString("icon");

            TextView currentConditionField = findViewById(R.id.PositionCondition);
            TextView currentTemperatureField = findViewById(R.id.PositionTemperature);
            currentTemperatureField.setText(temperature + "°");
            currentConditionField.setText(condition);
            ImageView imageView = findViewById(R.id.PositionImage);
            Picasso.get().load(icon).into(imageView);

        } catch (Exception e) {
            Log.d("Error", "Error");
        }
    }

    @SuppressLint("SetTextI18n")
    public void JsonRenderVille(JSONObject meteoValue) {
        try {

            String temperature = meteoValue.getString("tmp");
            String condition = meteoValue.getString("condition");
            String icon = meteoValue.getString("icon");

            TextView currentTemperatureField = findViewById(R.id.VilleTemperature);
            TextView currentConditionField = findViewById(R.id.VilleCondition1);
            currentTemperatureField.setText(temperature + "°");
            currentConditionField.setText(condition);
            ImageView imageView = findViewById(R.id.VilleImage);
            Picasso.get().load(icon).into(imageView);

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
                            jObjCurrent.put("condition", jObj.getJSONObject("current_condition").getString("condition"));
                            jObjCurrent.put("icon", jObj.getJSONObject("current_condition").getString("icon"));
                            jObjCurrent.put("humidity", jObj.getJSONObject("current_condition").getString("humidity"));
                            JsonRenderVille(jObjCurrent);

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