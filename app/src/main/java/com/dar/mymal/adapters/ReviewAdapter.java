package com.dar.mymal.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.EntryActivity;
import com.dar.mymal.R;
import com.dar.mymal.entries.recommendation.*;
import com.dar.mymal.downloader.DownloadImage;
import com.dar.mymal.entries.review.Review;
import com.dar.mymal.entries.review.Reviews;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private Reviews mDataset;
    private boolean useLessData;
    Context context;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView author,rew,helpful,date,progress,scores,other;
        public LinearLayout master;
        public ViewHolder(View v) {
            super(v);
            imgView=(ImageView)v.findViewById(R.id.image);
            author=(TextView)v.findViewById(R.id.name);
            rew=(TextView)v.findViewById(R.id.review);
            helpful=(TextView)v.findViewById(R.id.helpful);
            date=(TextView)v.findViewById(R.id.date);
            progress=(TextView)v.findViewById(R.id.progress);
            scores=(TextView)v.findViewById(R.id.scores);
            other=(TextView)v.findViewById(R.id.other_scores);
            master=(LinearLayout)v.findViewById(R.id.master_layout);
        }
    }

    public ReviewAdapter(Context cont, Reviews myDataset, boolean useLessData, List<String> files) {
        this.context=cont;
        this.mDataset = myDataset;
        this.useLessData=useLessData;
        this.files=files;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_review, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Review ent=mDataset.getReviews().get(position);
        if(!useLessData){
            new DownloadImage(holder.imgView).execute(ent.getImageURL());
        }
        holder.author.setText(ent.getUser());
        holder.rew.setText(Html.fromHtml(ent.getReview()));
        holder.helpful.setText(ent.getHelpful()+" people found this review helpful");
        holder.date.setText(new SimpleDateFormat("dd/MM/yyyy").format(ent.getDate()));
        holder.progress.setText(ent.getEpisodeSeen()+" episodes seen");
        holder.scores.setText("Overall: "+ent.getScores().get(0));
        holder.other.setText(
                "Story: "+ent.getScores().get(1)+"\n"+
                "Animation: "+ent.getScores().get(2)+"\n"+
                "Sound: "+ent.getScores().get(3)+"\n"+
                "Character: "+ent.getScores().get(4)+"\n"+
                "Enjoyment: "+ent.getScores().get(5)
        );
        holder.master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.other.setVisibility(holder.other.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
            }
        });
        holder.rew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.rew.setMaxLines(holder.rew.getMaxLines()==10?9999:10);


            }
        });
    }
    @Override
    public int getItemCount() {
        return mDataset.getReviews().size();
    }

}
