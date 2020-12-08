package com.ynov.applimeteo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
public class MainActivity extends AppCompatActivity {

    private static final String OPEN_WEATHER_MAP_URL = "";
    private static final String OPEN_WEATHER_MAP_API = "";

    TextView cityField, detailsField, currentTemperatureField, humidtyField, pressureField, weatherIcon, updatedField;
    Typeface weatherFont;
    static String latitude;
    static String longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        requestPermissions();

        FusedLocationProviderClient mFusedLocationProviderClient;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MainActivity.this,ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());

                    weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/weathericons-regular-webfont.ttf");

                    cityField= findViewById(R.id.city_field);
                    currentTemperatureField= findViewById(R.id.current_temperature);
                    updatedField= findViewById(R.id.updated_field);
                    detailsField= findViewById(R.id.details_field);
                    humidtyField= findViewById(R.id.humidity_field);
                    pressureField= findViewById(R.id.pressure_field);
                    weatherIcon= findViewById(R.id.weather_icon);
                    weatherIcon.setTypeface(weatherFont);

                    String[] jsonData = getJSONResponse();
                }
            }
        });
    }
public String[] getJSONResponse(){
        String[] jsonData = new String[7];
        JSONObject jsonWeather = null;
                try{
                    jsonWeather = getWeatherJson(latitude,longitude);
                }catch (Exception e){
                    Log.d("Error","Cannot process Json Result", e);
                }
                try {
                    if (jsonWeather != null){
                        JSONObject details = jsonWeather.getJSONArray("weather").getJSONObject(0);
                        JSONObject main = jsonWeather.getJSONObject("main");
                        DateFormat df = DateFormat.getDateInstance();

                        String city = jsonWeather.getString("name");
                    }
                } catch (Exception e) {
                    Log.d("Error","Error");
                }
    }

    public static JSONObject getWeatherJson(String lat, String lon){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_URL,lat,lon));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("x-ap-key",OPEN_WEATHER_MAP_API);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null){
                json.append(tmp).append("\n");
            }
            reader.close();
            JSONObject data = new JSONObject(json.toString());
            if (data.getInt("cod") != 200){
                return null;
            }
            return data;
        } catch (Exception e) {
return null;
        }
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }
}