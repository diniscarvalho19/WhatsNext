package com.dinis.whatsnext;

import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment implements RecyclerViewInterface{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View root;
    FirebaseAuth auth;
    FirebaseUser user;
    SearchView searchView;
    RecyclerView recyclerView;
    CommunityAdapter friendAdapter;


    public CommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityFragment newInstance(String param1, String param2) {
        CommunityFragment fragment = new CommunityFragment();
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
        root = inflater.inflate(R.layout.fragment_community, container, false);

        //Initiate DB
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        List<FriendModelClass> everyoneList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("users");

        recyclerView = root.findViewById(R.id.recyclerView);
        searchView = root.findViewById(R.id.searchView);




        //Username
        assert user != null;
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    if (task.getResult().getValue() != null){
                        Log.d("Com",task.getResult().getValue().toString());
                        Log.d("Com",task.getResult().getValue().toString().split("=\\{.*\\}\\}\\},")[0]);
                        Log.d("Com",task.getResult().getValue().toString().split("=\\{.*\\}\\}\\},")[1]);
                        int count = 0;
                        String[] users = Objects.requireNonNull(task.getResult().getValue()).toString().split("=\\{friend_request|\\}\\}\\}, ");
                        for(String usr: users){
                            if(count % 2 == 0){
                                String name = usr.replaceAll("\\{","");
                                everyoneList.add(new FriendModelClass(name,"https://cdn-icons-png.flaticon.com/512/16/16363.png"));
                            }
                            count++;
                        }
                        PutDataIntoRecyclerViewFriends(everyoneList);
                    }
                }

            }
        });

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                everyoneList.clear();
                mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            if (task.getResult().getValue() != null){
                                int count = 0;
                                String[] users = Objects.requireNonNull(task.getResult().getValue()).toString().split("=\\{friend_request|\\}\\}\\}, ");
                                for(String usr: users){
                                    if(count % 2 == 0){
                                        String name = usr.replaceAll("\\{","");
                                        if(name.contains(query))
                                            everyoneList.add(new FriendModelClass(name,"https://cdn-icons-png.flaticon.com/512/16/16363.png"));
                                    }
                                    count++;
                                }
                                PutDataIntoRecyclerViewFriends(everyoneList);
                            }
                        }

                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return root;
    }

    public void PutDataIntoRecyclerViewFriends(List<FriendModelClass> everyoneList){
        friendAdapter = new CommunityAdapter(getActivity(), everyoneList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(friendAdapter);
    }


    @Override
    public void onItemClick(int position) {

    }
}