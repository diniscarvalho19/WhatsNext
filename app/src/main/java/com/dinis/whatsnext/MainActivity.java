package com.dinis.whatsnext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    //popular movies
    //private static final String JSON_URL = "https://api.themoviedb.org/3/movie/popular?api_key=3c56b344ede62a30660b01ccd5f8c655&page=1";
    //private static final String JSON_URL = "https://run.mocky.io/v3/99f38e18-199f-4f11-affb-69a1b9c36d1a";
    private static final String JSON_URL = "https://utelly-tv-shows-and-movies-availability-v1.p.rapidapi.com/lookup?term=lost&country=uk";

    List<MovieModelClass>  movieList;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        movieList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        //async task
        GetData getData = new GetData();
        getData.execute();

        //MyTask task = new MyTask();
        //task.execute();
    }

    private class MyTask extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Do something like display a progress bar

        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            int StatusCode = 0;
            try {
                URL url = new URL(JSON_URL);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty ("X-RapidAPI-Key", "1d4c45ee65msh3f3913364221f69p113272jsnb9979b2b3f40");
                urlConnection.setRequestProperty("X-RapidAPI-Host", "utelly-tv-shows-and-movies-availability-v1.p.rapidapi.com");
                StatusCode = urlConnection.getResponseCode();

            } catch (Exception e) {
                //Toast.makeText(Act_Details.this, ""+e, Toast.LENGTH_SHORT).show();
            }

            return String.valueOf(StatusCode);
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Do things like update the progress bar

        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Do things like hide the progress bar or change a TextView
            Log.d("MOVIES", "RESULTADO");
            Log.d("MOVIES", result);


        }
    }

    public class GetData extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {

            String current = "";

            try {
                URL url;
                HttpsURLConnection urlConnection = null;

                try {
                    url = new URL(JSON_URL);
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty ("X-RapidAPI-Key", "1d4c45ee65msh3f3913364221f69p113272jsnb9979b2b3f40");
                    urlConnection.setRequestProperty("X-RapidAPI-Host", "utelly-tv-shows-and-movies-availability-v1.p.rapidapi.com");


                    InputStream is = urlConnection.getInputStream();


                    InputStreamReader isr = new InputStreamReader(is);


                    int data = isr.read();
                    while( data != -1 ){
                        current += (char) data;
                        data = isr.read();
                    }
                    return current;

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(urlConnection != null){
                        urlConnection.disconnect();;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return current;
        }


        @Override
        protected void onPostExecute(String s) {
            Log.d("MOVIES","POST");
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++){
                    StringBuilder allLocations = new StringBuilder();
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    MovieModelClass model = new MovieModelClass();
                    JSONArray locations = jsonObject1.getJSONArray("locations");

                    for (int j = 0; j < locations.length(); j++) {
                        String l = locations.getJSONObject(j).getString("display_name");
                        allLocations.append(l).append("\n");

                    }

                    //model.setId(jsonObject1.getString("vote_average"));
                    model.setId(allLocations.toString().toString());
                    model.setName(jsonObject1.getString("name"));
                    model.setImg(jsonObject1.getString("picture"));


                    movieList.add(model);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            PutDataIntoRecyclerView(movieList);

        }
    }


    private void PutDataIntoRecyclerView(List<MovieModelClass> movieList){

        MovieAdapter adapter = new MovieAdapter(this, movieList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

    }


}