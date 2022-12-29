package com.dinis.whatsnext;

import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class MovieFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    DB db;
    FirebaseAuth auth;
    FirebaseUser user;

    View root;
    Button addMovie;
    public MovieFragment() {
        // Required empty public constructor
    }

    public static MovieFragment newInstance(String param1, String param2) {
        MovieFragment fragment = new MovieFragment();
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
        root =inflater.inflate(R.layout.fragment_movie, container, false);
        Bundle bundle = this.getArguments();
        String image, title, id, loc;
        image = bundle.getString("cover");
        title = bundle.getString("title");
        id = bundle.getString("id");
        loc = bundle.getString("loc");
        TextView textView = (TextView) root.findViewById(R.id.movieTitle);
        textView.setText(title);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        ImageView imageView = (ImageView) root.findViewById(R.id.movieCover);
        Glide.with(root)
                .load(image)
                .into(imageView);
        db = DB.getInstance(getActivity());
        addMovie = (Button)root.findViewById(R.id.addMovie);
        addMovie.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                db.dao().insert(new MovieModelClass(id,title,image,loc));
                for(MovieModelClass movie: db.dao().getAll()){
                    System.out.println(movie.getName());
                }


                String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                myRef.child(username).child("watchlist").child(id).child("name").setValue(title);
                myRef.child(username).child("watchlist").child(id).child("img").setValue(image);




            }
        });
        return root;
    }
}