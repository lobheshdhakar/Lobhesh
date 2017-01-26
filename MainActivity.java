package com.example.android.lobhesh;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button bt;
    private EditText input;
    private TextView temp,lo,ln;
    private TextView city;
    private TextView pressure;
    private TextView humidity;
    private TextView windspeed;
    private TextView min_temp;
    private TextView max_temp;
    private TextView weathertype;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt=(Button)findViewById(R.id.btn);
        input=(EditText)findViewById(R.id.input_location);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location=input.getText().toString();
                new JsonTask().execute("http://api.openweathermap.org/data/2.5/weather?APPID=924811d4d4cb3f3219d7a95c2f0fc61a&q=" + location + "&mode=json");
                setContentView(R.layout.activity_home);
                temp = (TextView) findViewById(R.id.temp);
                city = (TextView) findViewById(R.id.location);
                pressure = (TextView) findViewById(R.id.pressure);
                humidity = (TextView) findViewById(R.id.humidity);
                min_temp = (TextView) findViewById(R.id.min_temp);
                max_temp = (TextView) findViewById(R.id.max_temp);
                windspeed = (TextView) findViewById(R.id.windspeed);
                lo=(TextView)findViewById(R.id.lon);
                ln=(TextView)findViewById(R.id.lan);
                weathertype = (TextView) findViewById(R.id.weatherType);

            }
        });
    }


    public class JsonTask extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader=null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                ///// extraction data from JSON Format Text /////


                JSONObject parent = new JSONObject(finalJson);
               JSONArray array = parent.getJSONArray("weather");
                JSONObject description = array.getJSONObject(0);

                JSONObject system = parent.getJSONObject("sys");
                JSONObject tempdata = parent.getJSONObject("main");
                JSONObject cod=parent.getJSONObject("coord");

                JSONObject wind = parent.getJSONObject("wind");

                String[] finalData = new String[10];
                finalData[0] = parent.getString("name")+", "+system.getString("country");
                finalData[1] = ""+(tempdata.getInt("temp")-273);
                finalData[2] = ""+tempdata.getInt("pressure");
                finalData[3] = ""+tempdata.getInt("humidity");
                finalData[4] = ""+(tempdata.getInt("temp_min")-275);
                finalData[5] = ""+(tempdata.getInt("temp_max")-268);
                finalData[6] = ""+wind.getInt("speed");
                finalData[7] = description.getString("description");
                finalData[8]=""+(cod.getInt("lon"));
                finalData[9]=""+(cod.getInt("lan"));

                //connection.disconnect();
                return finalData;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);

            city.setText(result[0]);
            temp.setText(result[1]);
            pressure.setText("P "+result[2]+" hPa");
            humidity.setText(""+result[3]+"%");
            min_temp.setText("MIN  "+result[4] + "°C");
            max_temp.setText("MAX "+result[5] + "°C");
            windspeed.setText(""+result[6]+" mph");
            weathertype.setText(result[7]);
            lo.setText(result[8]);
            ln.setText(result[9]);


        }
    }

    @Override
    public void onBackPressed() {
        setContentView(R.layout.activity_main);
        input = (EditText) findViewById(R.id.input_location);
        bt=(Button)findViewById(R.id.btn);
    }

}
