package com.dinis.whatsnext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {


    private Context mContext;
    private List<MovieModelClass> mData;

    public MovieAdapter(Context mContext, List<MovieModelClass> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.movie_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.id.setText(mData.get(position).getId());
        holder.name.setText(mData.get(position).getName());

        // Using Glide Library to display the image
        GlideApp.with(mContext)
                .load("https://image.tmdb.org/t/p/w500" + mData.get(position).getImg())
                .into(holder.image);



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView id, name;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id_txt);
            name = itemView.findViewById(R.id.name_txt);
            image = itemView.findViewById(R.id.imageView);


        }
    }



}
