package com.dinis.whatsnext;

import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AcceptFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AcceptFriendsFragment extends Fragment implements RecyclerViewInterface{

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
    RecyclerView recyclerView;
    AcceptedFriendsAdapter friendAdapter;

    public AcceptFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AcceptFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AcceptFriendsFragment newInstance(String param1, String param2) {
        AcceptFriendsFragment fragment = new AcceptFriendsFragment();
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
        root = inflater.inflate(R.layout.fragment_accept_friends, container, false);

        //Initiate DB
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        List<FriendRequestModelClass> everyoneList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("friends_list");
        recyclerView = root.findViewById(R.id.recyclerViewAccept);

        //Username
        assert user != null;
        String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
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
                        String[] users = result.split("\\}, ");
                        int size = users.length;
                        int count = 1;
                        for(String usr: users){
                            if (count != size){
                                usr = usr + "}";
                            }
                            count++;
                            String[] data = usr.split("=\\{");
                            String name = data[0];
                            data = Arrays.copyOfRange(data, 1, data.length);
                            if(name.equals(username)){
                                for(String st : data){
                                    st = st.substring(0, st.length() - 1);
                                    for(String st2 : st.split(", ")){
                                        String usernameReq = st2.split("=")[0];
                                        String status = st2.split("=")[1];
                                        if(!status.equals("true"))
                                            everyoneList.add(new FriendRequestModelClass(usernameReq,false));
                                    }
                                }
                            }
                        }
                        PutDataIntoRecyclerViewFriends(everyoneList);
                    }
                }
            }
        });



        return root;
    }

    @Override
    public void onItemClick(int position) {

    }

    public void PutDataIntoRecyclerViewFriends(List<FriendRequestModelClass> everyoneList){
        friendAdapter = new AcceptedFriendsAdapter(getActivity(), everyoneList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(friendAdapter);
    }
}