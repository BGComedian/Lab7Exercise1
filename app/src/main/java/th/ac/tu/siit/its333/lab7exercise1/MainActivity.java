package th.ac.tu.siit.its333.lab7exercise1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {

    int oldId = -1;
    long oldvalMins=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WeatherTask w = new WeatherTask();
        w.execute("http://ict.siit.tu.ac.th/~cholwich/bangkok.json", "Bangkok Weather");
    }

    public void buttonClicked(View v) {
        int id = v.getId();

        WeatherTask w = new WeatherTask();
        long newval=System.currentTimeMillis();

        long currentTime;
        long currentTimeMins;

        currentTime=System.currentTimeMillis();
        currentTimeMins= TimeUnit.MILLISECONDS.toMinutes(currentTime);
        switch (id) {
            case R.id.btBangkok:


                if(oldId != id || currentTimeMins-oldvalMins >= 1){
                    w.execute("http://ict.siit.tu.ac.th/~cholwich/bangkok.json", "Bangkok Weather");
                    oldId = id;
                    oldvalMins = TimeUnit.MILLISECONDS.toMinutes(newval);
                }else{
                    Toast t = Toast.makeText(this,"1 min please.",Toast.LENGTH_SHORT);
                    t.show();
                }

                break;
            case R.id.btNon:

                if(oldId != id || currentTimeMins-oldvalMins >= 1){
                    w.execute("http://ict.siit.tu.ac.th/~cholwich/nonthaburi.json", "Nonthaburi Weather");
                    oldId = id;
                    oldvalMins = TimeUnit.MILLISECONDS.toMinutes(newval);
                }else{
                    Toast t = Toast.makeText(this,"1 min please.",Toast.LENGTH_SHORT);
                    t.show();
                }

                break;
            case R.id.btPathum:

                if(oldId != id || currentTimeMins-oldvalMins >= 1){
                    w.execute("http://ict.siit.tu.ac.th/~cholwich/pathumthani.json", "Pathumthani Weather");
                    oldId = id;
                    oldvalMins = TimeUnit.MILLISECONDS.toMinutes(newval);
                }else{
                    Toast t = Toast.makeText(this,"1 min please.",Toast.LENGTH_SHORT);
                    t.show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class WeatherTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        ProgressDialog pDialog;
        String title;

        double windSpeed,temp,tempMax,tempMin,humid;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading weather data ...");
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            try {
                title = params[1];
                URL u = new URL(params[0]);
                HttpURLConnection h = (HttpURLConnection)u.openConnection();
                h.setRequestMethod("GET");
                h.setDoInput(true);
                h.connect();

                int response = h.getResponseCode();
                if (response == 200) {
                    reader = new BufferedReader(new InputStreamReader(h.getInputStream()));
                    while((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    //Start parsing JSON
                    JSONObject jWeather = new JSONObject(buffer.toString());
                    JSONObject jWind = jWeather.getJSONObject("wind");
                    windSpeed = jWind.getDouble("speed");
                    errorMsg = "";

                    JSONObject jTemp = jWeather.getJSONObject("main");
                    temp = jTemp.getDouble("temp");
                    tempMax = jTemp.getDouble("temp_max");
                    tempMin = jTemp.getDouble("temp_min");
                    humid = jTemp.getDouble("humidity");
                    errorMsg = "";
                    return true;


                }
                else {
                    errorMsg = "HTTP Error";
                }
            } catch (MalformedURLException e) {
                Log.e("WeatherTask", "URL Error");
                errorMsg = "URL Error";
            } catch (IOException e) {
                Log.e("WeatherTask", "I/O Error");
                errorMsg = "I/O Error";
            } catch (JSONException e) {
                Log.e("WeatherTask", "JSON Error");
                errorMsg = "JSON Error";
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            TextView tvTitle, tvWeather, tvWind, tvTemp,tvHumid;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            tvTitle = (TextView)findViewById(R.id.tvTitle);
            tvWeather = (TextView)findViewById(R.id.tvWeather);
            tvWind = (TextView)findViewById(R.id.tvWind);
            tvTemp = (TextView)findViewById(R.id.tvTemp);

            tvHumid = (TextView)findViewById(R.id.tvHumid);

            if (result) {
                tvTitle.setText(title);

                tvTemp.setText(String.format("%.2f (max = %.2f, min= %.2f)", temp,tempMax,tempMin));
                tvHumid.setText(String.format("%.0f%%", humid));
                tvWind.setText(String.format("%.1f", windSpeed));
                //long currentTime=System.currentTimeMillis();
                //long currentTimeMins=TimeUnit.MILLISECONDS.toMinutes(currentTime);

            }
            else {
                tvTitle.setText(errorMsg);
                tvWeather.setText("");
                tvWind.setText("");
            }
        }
    }
}
