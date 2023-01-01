package com.dinis.whatsnext;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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


public class GroupsFragment extends Fragment implements RecyclerViewInterface{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    DB db;
    FirebaseAuth auth;
    FirebaseUser user;

    View root;
    Button addGroup;
    RecyclerView recyclerView;


    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
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
        root =inflater.inflate(R.layout.fragment_groups, container, false);

        recyclerView = root.findViewById(R.id.group_recycler);
        RecyclerViewInterface rvi = this;

        ArrayList<GroupModel> groupModelArrayList = new ArrayList<>();

        //Initiate DB
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        List<FriendRequestModelClass> everyoneList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert user != null;
        String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
        DatabaseReference mDatabase = database.getReference("groups").child(username);
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    if (task.getResult().getValue() != null){
                        String result = task.getResult().getValue().toString().substring(1);
                        result = result.substring(0, result.length() - 1);
                        String[] groups = result.split("\\}, ");
                        for(String g: groups){
                            String g_data = g.split("=\\{")[1];
                            String tittle = g_data.split("=")[0];
                            String userList = g_data.split("=")[1].replace("}","").replace("[","").replace("]","");
                            groupModelArrayList.add(new GroupModel(tittle, userList));
                        }

                        GroupAdapter groupAdapter = new GroupAdapter(getActivity(), groupModelArrayList, rvi);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(groupAdapter);



                        addGroup = root.findViewById(R.id.btn_create_group);
                        addGroup.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                PopupCreateGroup popupClass = new PopupCreateGroup((MainActivity)getActivity());
                                popupClass.showPopupWindow(view);
                            }
                        });
                    }
                }
            }
        });







        return root;
    }

    @Override
    public void onItemClick(int position) {

    }
}