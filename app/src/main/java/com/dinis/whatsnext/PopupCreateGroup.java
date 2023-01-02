package com.dinis.whatsnext;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.dinis.whatsnext.TaskManager.TaskManager;

public class PopupCreateGroup implements RecyclerViewInterface {

    public PopupCreateGroup(Context context) {
        this.context = context;
    }

    Context context;
    TaskManager taskManager = new TaskManager();





    public void showPopupWindow(final View view, AllGroupsFragment allGroupsFragment) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_group_create, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);





        RecyclerViewInterface rvi = this;
        taskManager.executeCreateGroup(context, rvi, popupView, popupWindow);

    }

    @Override
    public void onItemClick(int position) {

    }
}