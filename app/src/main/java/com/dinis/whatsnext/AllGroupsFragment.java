package com.dinis.whatsnext;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.dinis.whatsnext.TaskManager.TaskManager;


public class AllGroupsFragment extends Fragment implements RecyclerViewInterface{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    TaskManager taskManager = new TaskManager();



    View root;
    Button addGroup;
    RecyclerView recyclerView;
    RecyclerViewInterface rvi;


    public AllGroupsFragment() {
        // Required empty public constructor
    }

    public static AllGroupsFragment newInstance(String param1, String param2) {
        AllGroupsFragment fragment = new AllGroupsFragment();
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
        AllGroupsFragment allGroupsFragment = this;
        root = inflater.inflate(R.layout.fragment_groups, container, false);
        recyclerView = root.findViewById(R.id.group_recycler);
        rvi = this;





        taskManager.executeGetAllGroups(allGroupsFragment, rvi, recyclerView);

        addGroup = root.findViewById(R.id.btn_create_group);
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupCreateGroup popupClass = new PopupCreateGroup((MainActivity) getActivity());
                popupClass.showPopupWindow(view, allGroupsFragment);

            }
        });
        recyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    taskManager.executeGetAllGroups(allGroupsFragment, rvi, recyclerView);
                }
            }
        });

        return root;
    }

    @Override
    public void onItemClick(int position) {
    }

}