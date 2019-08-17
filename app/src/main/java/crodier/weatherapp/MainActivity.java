package crodier.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LocationListener{

    ImageView ivIcon;


    TextView tvTemp, tvLocation, tvWeather, tvHumidity, tvWindSpeed;

    Button btnWeather;

    private LocationManager locationManager;

    private String provider;

    Location location;

    double longitude, latitude;

    // TODO get own API at https://openweathermap.org/
    final String API_KEY = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivIcon = findViewById(R.id.ivIcon);

        tvTemp = findViewById(R.id.tvTemp);
        tvLocation = findViewById(R.id.tvLocation);
        tvWeather = findViewById(R.id.tvWeather);
        tvHumidity = findViewById(R.id.tvHumdity);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);

        btnWeather = findViewById(R.id.btnWeather);

        ivIcon.setVisibility(View.GONE);
        tvLocation.setVisibility(View.GONE);
        tvWeather.setVisibility(View.GONE);
        tvHumidity.setVisibility(View.GONE);
        tvWindSpeed.setVisibility(View.GONE);



        getPermission();



        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoordinates();

            }
        });


    }

    private void getPermission() {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            //if permissions not granted then request them and put in request code of 0
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        }
    }

    public void getCoordinates()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        provider = locationManager.getBestProvider(criteria, false);

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            //if permissions not granted then request them and put in request code of 0
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 0);


            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                getCoordinates();

            }

        }
        else {

            //get lat location
            location = locationManager.getLastKnownLocation(provider);

            //if we have a last known location
            if (location != null){
                //call on location changed method and pass in last location



                onLocationChanged(location);
            }

        }

    }


    @Override
    public void onLocationChanged(Location location) {

        longitude = location.getLongitude();

        latitude = location.getLatitude();

        Log.e("lat ", +location.getLatitude()+"");
        Log.e("long ", +location.getLongitude()+"");

        getCurrentWeather();

    }



    private void getCurrentWeather() {

        String urlForTodaysWeather = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;

        Log.e("url today weather", urlForTodaysWeather);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                urlForTodaysWeather,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {


                            //temp
                            double tempKel = response.getJSONObject("main").getDouble("temp");

                            double tempFer = Math.round(9/5 * (tempKel - 273) + 32);


                            tvTemp.setText(tempFer + "°F");


                            //location
                            String city = response.getString("name");

                            String country = response.getJSONObject("sys").getString("country");

                            tvLocation.setVisibility(View.VISIBLE);

                            tvLocation.setText(city + ", " + country);

                            //weather description
                            JSONArray weather = response.getJSONArray("weather");

                            JSONObject main = weather.getJSONObject(0);

                            String mainDescription = main.getString("main");

                            tvWeather.setVisibility(View.VISIBLE);

                            tvWeather.setText(mainDescription);


                            //humidity
                            String humidity = response.getJSONObject("main").getString("humidity");

                            tvHumidity.setVisibility(View.VISIBLE);

                            tvHumidity.setText("Humidity " + humidity+"%");


                            //wind speed
                            String windSpeed = response.getJSONObject("wind").getString("speed");

                            tvWindSpeed.setVisibility(View.VISIBLE);

                            tvWindSpeed.setText("Wind Speed " + windSpeed);


                            //icon
                            String icon = main.getString("icon");

                            switch (icon)
                            {
                                case "01d":
                                   ivIcon.setImageResource(R.mipmap.clear_sky_day);
                                    break;

                                case "01n":
                                    ivIcon.setImageResource(R.mipmap.clear_sky_night);
                                    break;

                                case "03d":
                                    ivIcon.setImageResource(R.mipmap.scattered_clouds);
                                    break;

                                case "03n":
                                    ivIcon.setImageResource(R.mipmap.scattered_clouds);
                                    break;

                                case "04d":
                                    ivIcon.setImageResource(R.mipmap.broken_clouds);
                                    break;

                                case "04n":
                                    ivIcon.setImageResource(R.mipmap.broken_clouds);
                                    break;

                                case "09d":
                                    ivIcon.setImageResource(R.mipmap.shower_rain);
                                    break;

                                case "09n":
                                    ivIcon.setImageResource(R.mipmap.shower_rain);
                                    break;

                                case "10d":
                                    ivIcon.setImageResource(R.mipmap.rain_day);
                                    break;

                                case "10n":
                                    ivIcon.setImageResource(R.mipmap.rain_night);
                                    break;

                                case "11d":
                                    ivIcon.setImageResource(R.mipmap.thunderstorm);
                                    break;

                                case "11n":
                                    ivIcon.setImageResource(R.mipmap.thunderstorm);
                                    break;


                                case "13d":
                                    ivIcon.setImageResource(R.mipmap.snow);
                                    break;

                                case "13n":
                                    ivIcon.setImageResource(R.mipmap.snow);
                                    break;


                                case "50d":
                                    ivIcon.setImageResource(R.mipmap.mist);
                                    break;


                                case "50n":
                                    ivIcon.setImageResource(R.mipmap.mist);
                                    break;
                            }

                            ivIcon.setVisibility(View.VISIBLE);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Error Response ", error.toString());

                    }
                }
        );


        requestQueue.add(objectRequest);


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
}
