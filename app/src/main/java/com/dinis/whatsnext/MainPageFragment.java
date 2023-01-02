package com.dinis.whatsnext;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinis.whatsnext.TaskManager.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPageFragment extends Fragment implements RecyclerViewInterface{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //popular movies
    private static String JSON_URL = "https://utelly-tv-shows-and-movies-availability-v1.p.rapidapi.com/lookup?term=lost&country=uk";

    List<MovieModelClass>  movieList;
    RecyclerView recyclerView;
    MovieAdapter movieAdapter;
    SearchView searchView;
    TaskManager taskManager = new TaskManager();
    View root;
    Fragment movieFrag = new MovieFragment();

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



    public MainPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainPageFragment newInstance(String param1, String param2) {
        MainPageFragment fragment = new MainPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_main_page, container, false);

        taskManager.executeUpdateLogin();

        searchView = root.findViewById(R.id.searchView);
        searchView.clearFocus();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                movieList = new ArrayList<>();
                recyclerView = root.findViewById(R.id.recyclerView);
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


        movieList = new ArrayList<>();
        recyclerView = root.findViewById(R.id.recyclerView);
        GetData getData = new GetData();
        getData.execute();
        return root;
    }



    //On movie click
    @Override
    public void onItemClick(int position) {
        getMovieFrag(movieAdapter.getItem(position).getId(), movieAdapter.getItem(position).getName(), movieAdapter.getItem(position).getImg(), movieAdapter.getItem(position).getLocations());

    }


    public class GetData extends AsyncTask<String, String, String> {

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
        movieAdapter = new MovieAdapter(getContext(), movieList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(movieAdapter);
    }

}