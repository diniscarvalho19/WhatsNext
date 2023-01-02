package com.dinis.whatsnext;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class PopupMovieRecommedation implements RecyclerViewInterface {

    public PopupMovieRecommedation(Context context, ArrayList allMovies) {
        this.context = context;
        this.allMovies = allMovies;

    }

    Context context;

    //PopupWindow display method
    TextView movieTitle;
    ImageView movieCover;
    ImageButton refreshRecommendation;
    FirebaseAuth auth;
    FirebaseUser user;
    ArrayList<String> allMovies;
    ArrayList<ArrayList<String>> res = new ArrayList<>();





    public void showPopupWindow(final View view) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_movie_recommendation, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        movieTitle = (TextView) popupView.findViewById(R.id.movie_title_popup);
        movieCover = (ImageView) popupView.findViewById(R.id.movie_cover_popup);
        refreshRecommendation = (ImageButton) popupView.findViewById(R.id.refresh_recommendation);
        ArrayList<String> recMovies = new ArrayList<>();


        if(allMovies.isEmpty()){
            String noWL = "Watchlist empty";
            movieTitle.setText(noWL);
        }else{
            for(String movie : allMovies){
                if(Collections.frequency(allMovies, movie) > 1){
                    recMovies.add(movie);
                }
            }

            if(recMovies.isEmpty()){
                String noCommon = "No movies in common. Picking a random movie from all watchlists:";
                //holder.recommendation_status.setText(noCommon);
                ArrayList<String> allMoviesDistinct = new ArrayList<>(new HashSet<>(allMovies));
                res = parseArrayListMovies(allMoviesDistinct);
                //res = removeDuplicate(res);
                movieTitle.setText(res.get(0).get(0));
                Glide.with(popupView)
                        .load(res.get(0).get(0))
                        .into(movieCover);

                //holder.recommendation.setText(allMoviesDistinct.get(new Random().nextInt(allMoviesDistinct.size())));
            }else {
                String common = "Movies in common: ";
                //holder.recommendation_status.setText(common);
                ArrayList<String> recMoviesDistinct = new ArrayList<>(new HashSet<>(recMovies));
                res = parseArrayListMovies(recMoviesDistinct);
               // res = removeDuplicate(res);
                movieTitle.setText(res.get(0).get(0));
                Glide.with(popupView)
                      .load(res.get(0).get(0))
                        .into(movieCover);

            }

        }
        if(res.size()>0){
            res.remove(0);
        }
        if(res.size()==0 || res==null){
            refreshRecommendation.setAlpha(0.5f);
            refreshRecommendation.setClickable(false);
        }


        refreshRecommendation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(res.size()>0){
                movieTitle.setText(res.get(0).get(0));
                Glide.with(popupView)
                        .load(res.get(0).get(0))
                        .into(movieCover);
                res.remove(0);}
                if(res.size()==0 || res==null){
                    refreshRecommendation.setAlpha(0.5f);
                    refreshRecommendation.setClickable(false);
                }
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });



    }

    @Override
    public void onItemClick(int position) {

    }

    public ArrayList<ArrayList<String>> parseArrayListMovies(ArrayList<String> recMovies){
       Hashtable<String, ArrayList<String>> resMovies = new Hashtable<String, ArrayList<String>>();

        for(String input: recMovies){
            String[] parts = input.split("[{,]");
            ArrayList<String> currentMovie = new ArrayList<>();
// Initialize the img and name variables
            String img = "";
            String name = "";
            String id ="";
// Loop through the parts array
            for (String part : parts) {
                // If the part starts with "img="
                System.out.println(part);
                if(part.startsWith("tt")){
                    id = part.substring(0, part.length()-1);
                }
                if (part.startsWith("img=")) {
                    // Extract the value of the img parameter
                    img = part.substring(4);
                } else if (part.startsWith(" name=")) {
                    // Extract the value of the name parameter
                    name = part.substring(6);
                    name = name.substring(0, name.length() - 1);
                }
            }
            currentMovie.add(name);
            currentMovie.add(img);
            resMovies.put(id, currentMovie);
        }


        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
        Enumeration<ArrayList<String>> elements = resMovies.elements();
        while (elements.hasMoreElements()) {
            values.add(elements.nextElement());
        }

        return values;
    }

}