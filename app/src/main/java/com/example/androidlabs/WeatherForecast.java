package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.xmlpull.v1.XmlPullParser.START_TAG;


public class WeatherForecast extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "Weather Forecast";
    private TextView currentTemperature, minTemperature, maxTemperature, uvRating;
    private ImageView weatherImage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        weatherImage = findViewById(R.id.weatherImage);
        currentTemperature = findViewById(R.id.currentTemperature);
        minTemperature = findViewById(R.id.minWeather);
        maxTemperature = findViewById(R.id.maxWeather);
        uvRating = findViewById(R.id.uvRating);
        progressBar = findViewById(R.id.progressBar);

        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
        String uvUrl = "http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";

        ForecastQuery forecastQuery = new ForecastQuery();
        forecastQuery.execute(weatherUrl, uvUrl);


        progressBar.setVisibility(View.VISIBLE);
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        private String currentTemp, minTemp, maxTemp, iconName;
        private Bitmap image;
        private String uvValue;

        @Override
        protected String doInBackground(String... args) {

            String returnString = null;
            try {
                //create a URL object of what server to contact:
                URL url = new URL(args[0]);
                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //create parser for XML
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == START_TAG){
                        String tagName = xpp.getName();
                        switch(tagName){
                            case "temperature" :
                            currentTemp = xpp.getAttributeValue(null, "value");
                            publishProgress(25);

                            minTemp = xpp.getAttributeValue(null, "min");
                            publishProgress(50);

                            maxTemp = xpp.getAttributeValue(null, "max");
                            publishProgress(75);

                            case "weather" :
                            iconName = xpp.getAttributeValue(null, "icon");
                        }
                    }
                    eventType = xpp.next();//move to the next xml event and store it in a variable
                }

                String fileName = iconName + ".png";

                if (fileExistance(fileName)){
                    Log.e(ACTIVITY_NAME, "Looking for file" + iconName + ".png");
                    Log.e(ACTIVITY_NAME, "Weather image exists, found locally");

                    FileInputStream fis = null;
                    try {
                        fis = openFileInput(iconName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    image = BitmapFactory.decodeStream(fis);

                } else {
                    Log.i(ACTIVITY_NAME, "Looking for file" + fileName);
                    Log.i(ACTIVITY_NAME, "Weather image does not exist, need to download");
                    image = null;
                    URL imageUrl = new URL("http://openweathermap.org/img/w/" + fileName);
                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        image = BitmapFactory.decodeStream(connection.getInputStream());
                    }
                    FileOutputStream outputStream  = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }

                publishProgress(100);

                URL uvUrl = new URL(args[1]);
                HttpURLConnection uvUrlConnection = (HttpURLConnection) uvUrl.openConnection();
                InputStream uvResponse = uvUrlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(uvResponse, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder(100);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                String result = sb.toString();

                //convert string to JSON
                JSONObject jObject = new JSONObject(result);
                //get the double associated with "value"
                uvValue = String.valueOf(jObject.getDouble("value"));
                //uvRatingValue = uvValue + "";
                Log.i("UV is:", "" + uvValue);
            } catch (Exception e) {
                returnString = "error";
            }

            return returnString;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i("AsyncTaskExample", "update:" + values[0]);
            progressBar.setProgress(values[0]);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String fromDoInBackground) {
            super.onPostExecute(fromDoInBackground);
            char celsiusSymbol = 0x2103;
            weatherImage.setImageBitmap(image);
            currentTemperature.setText(String.format("Current Temp: %s%c", currentTemp, celsiusSymbol));
            minTemperature.setText(String.format("Min: %s%c", minTemp, celsiusSymbol));
            maxTemperature.setText(String.format("Max: %s%c", maxTemp, celsiusSymbol));
            uvRating.setText(String.format("UV Index: %s", uvValue));
            progressBar.setVisibility(View.INVISIBLE);
        }

        public boolean fileExistance(String fname) {
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

    }
}