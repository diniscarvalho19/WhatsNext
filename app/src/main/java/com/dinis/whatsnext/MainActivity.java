package com.dinis.whatsnext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{

    //popular movies
    private static String JSON_URL = "https://utelly-tv-shows-and-movies-availability-v1.p.rapidapi.com/lookup?term=lost&country=uk";

    List<MovieModelClass>  movieList;
    RecyclerView recyclerView;
    MovieAdapter adapter;
    SearchView searchView;
    Fragment movieFrag = new MovieFragment();
    Fragment profileFrag = new ProfileFragment();
    Fragment watchlistFrag = new WatchlistFragment();
    Fragment groupFrag = new GroupsFragment();
    BottomNavigationView bottomNavigationView;

    public void getMovieFrag(String id, String title, String cover, String locations){
        FragmentManager fragmentManager = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("title", title);
        bundle.putString("cover", cover);
        bundle.putString("locations", locations);
        movieFrag.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainActivity, movieFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getWatchlistFrag(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainActivity, watchlistFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getProfileFrag(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainActivity, profileFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getGroupsFrag(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainActivity, groupFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                movieList = new ArrayList<>();
                recyclerView = findViewById(R.id.recyclerView);
                JSON_URL = "https://utelly-tv-shows-and-movies-availability-v1.p.rapidapi.com/lookup?term=" + query + "&country=uk";
                GetData getData = new GetData();
                getData.execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.page_1:
                    getFragmentManager().popBackStack();
                    break;
                case R.id.page_2:
                    getWatchlistFrag();
                    break;
                case R.id.page_3:
                    getProfileFrag();
                    break;
                case R.id.page_4:
                    getGroupsFrag();
                    break;
            }
            return true;
        });
        movieList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        GetData getData = new GetData();
        getData.execute();
    }



    //On movie click
    @Override
    public void onItemClick(int position) {
        getMovieFrag(adapter.getItem(position).getId(),adapter.getItem(position).getName(),adapter.getItem(position).getImg(),adapter.getItem(position).getLocations());

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
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++){
                    StringBuilder allLocations = new StringBuilder();
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    MovieModelClass model = new MovieModelClass();
                    JSONArray locations = jsonObject1.getJSONArray("locations");
                    String id = jsonObject1.getJSONObject("external_ids").getJSONObject("imdb").getString("id");
                    for (int j = 0; j < locations.length(); j++) {
                        String l = locations.getJSONObject(j).getString("display_name");
                        allLocations.append(l).append("\n");
                    }
                    model.setLocations(allLocations.toString());
                    model.setId(id);
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
       adapter = new MovieAdapter(this, movieList, this);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
       recyclerView.setAdapter(adapter);
    }


}