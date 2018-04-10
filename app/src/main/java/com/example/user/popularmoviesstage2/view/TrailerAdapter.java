package com.example.user.popularmoviesstage2.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.user.popularmoviesstage2.R;
import com.example.user.popularmoviesstage2.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Andris on 027 27.03.18.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>{
    private List<Video> videos;
    private Context context;

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Video item = (Video) view.getTag();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + item.getKey()));
            intent.setPackage("com.google.android.youtube");
            context.startActivity(intent);
        }
    };

    public TrailerAdapter(List<Video> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @NonNull
    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.ViewHolder holder, int position) {

        Video video = videos.get(position);

        Picasso.with(context)
                .load("http://img.youtube.com/vi/" + video.getKey() + "/0.jpg")
                .into(holder.trailerImage);
        holder.playButton.setTag(videos.get(position));
        holder.playButton.setOnClickListener(onClickListener);

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView trailerImage;
        private ImageView playButton;

        public ViewHolder(View itemView) {
            super(itemView);
            trailerImage = itemView.findViewById(R.id.trailerImageView);
            playButton = itemView.findViewById(R.id.playImageView);
        }

        @Override
        public void onClick(View v) {


        }
    }
}
