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

import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class RecommendationMainAdapter extends RecyclerView.Adapter<RecommendationMainAdapter.ViewHolder> {
    private Recommendations mDataset;
    private boolean useLessData;
    Context context;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public Button btn;
        public TextView title,id,author,rec;
        public LinearLayout master;
        public ViewHolder(View v) {
            super(v);
            imgView=(ImageView)v.findViewById(R.id.image);
            title=(TextView)v.findViewById(R.id.title);
            author=(TextView)v.findViewById(R.id.author);
            rec=(TextView)v.findViewById(R.id.text);
            id=(TextView)v.findViewById(R.id.id);
            btn=(Button)v.findViewById(R.id.view_all);
            master=(LinearLayout)v.findViewById(R.id.master_layout);
        }
    }

    public RecommendationMainAdapter(Context cont, Recommendations myDataset, boolean useLessData, List<String> files) {
        this.context=cont;
        this.mDataset = myDataset;
        this.useLessData=useLessData;
        this.files=files;
    }

    @Override
    public RecommendationMainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_layout_anime, parent, false);
        //ViewHolder vh = new ViewHolder(myView);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_recommendations, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Recommendation ent=mDataset.getRec().get(position);

        if(files.contains((mDataset.isAnime()?"A":"M")+ent.getId()+".jpg")){
            holder.imgView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/"+(mDataset.isAnime()?"A":"M")+ent.getId()+".jpg")));
        }else if(!useLessData){
            new DownloadImage(holder.imgView).execute(ent.getImageURL());
        }
        holder.title.setText(Html.fromHtml(ent.getTitle()).toString());
        holder.author.setText("Recommended by "+ent.getRec().get(0).getUser());
        holder.rec.setText(Html.fromHtml(ent.getRec().get(0).getText()));
        holder.id.setText(""+ent.getId());
        holder.btn.setVisibility(ent.getHowMany()==0?View.GONE:View.VISIBLE);
        holder.btn.setText("View all "+ent.getHowMany()+" recommendations");
        holder.master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), EntryActivity.class);
                i.putExtra("ENTRY_ID",ent.getId());
                i.putExtra("ENTRY_ISANIME",mDataset.isAnime());
                i.putExtra("ENTRY_TITLE",ent.getTitle());
                v.getContext().startActivity(i);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mDataset.getRec().size();
    }

}
