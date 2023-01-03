package com.dinis.whatsnext;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinis.whatsnext.TaskManager.TaskManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment implements RecyclerViewInterface, TaskManager.Callback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View root;
    SearchView searchView;
    RecyclerView recyclerView;
    CommunityAdapter friendAdapter;
    TaskManager.Callback callback;
    TaskManager taskManager = new TaskManager();


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

        recyclerView = root.findViewById(R.id.recyclerView);
        searchView = root.findViewById(R.id.searchView);
        callback = this;

        taskManager.executeCommunityNoFilter(this);


        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskManager.executeCommunityWithFilter(callback, newText);
                return true;
            }
        });

        return root;
    }

    @Override
    public void PutDataIntoRecyclerViewFriendsCommunity(List<FriendModelClass> everyoneList){
        friendAdapter = new CommunityAdapter(getActivity(), everyoneList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(friendAdapter);
    }


    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void PutDataIntoRecyclerView(List<MovieModelClass> movieList) {

    }

    @Override
    public void PutDataIntoRecyclerViewFriends(List<FriendRequestModelClass> everyoneList) {

    }

    @Override
    public void removeAt(int pos) {

    }

    @Override
    public void recHelper(ArrayList<String> recMovies, ArrayList<String> allMovies, @NonNull GroupAdapter.Viewholder holder) {

    }
}