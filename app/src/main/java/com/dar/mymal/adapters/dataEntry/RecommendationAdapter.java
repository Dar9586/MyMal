package com.dar.mymal.adapters.dataEntry;

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
import com.dar.mymal.RecommendationsActivity;
import com.dar.mymal.entries.inspector.Recommendations;
import com.dar.mymal.downloader.DownloadImage;
import com.dar.mymal.global.Settings;
import com.dar.mymal.utils.MALUtils;

import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {
    public void setContext(Context context) {
        this.context = context;
    }

    private static Recommendations mDataset;
    int subMenu=-1;
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
    public RecommendationAdapter(int subMenu) {
        this.files= MALUtils.getCacheFile();
        this.subMenu=subMenu;
    }
    public RecommendationAdapter(Recommendations myDataset) {
        this.mDataset = myDataset;
        this.files= MALUtils.getCacheFile();
    }
    public RecommendationAdapter(Context cont, int subMenu) {
        this.context=cont;
        this.files= MALUtils.getCacheFile();
        this.subMenu=subMenu;
    }
    public RecommendationAdapter(Context cont, Recommendations myDataset) {
        this.context=cont;
        this.mDataset = myDataset;
        this.files= MALUtils.getCacheFile();
    }

    @Override
    public RecommendationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_layout_anime, parent, false);
        //ViewHolder vh = new ViewHolder(myView);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_recommendations, parent, false));
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int posit) {
        int pos=subMenu==-1?posit:subMenu;
        final Recommendations.Recommendation ent=mDataset.getRec().get(pos);
        if(subMenu==-1||posit==0) {
            if (files.contains((mDataset.isAnime() ? "A" : "M") + ent.getId() + ".jpg")) {
                holder.imgView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myMalCache/" + (mDataset.isAnime() ? "A" : "M") + ent.getId() + ".jpg")));
            } else if (!Settings.isUsingLessData()) {
                new DownloadImage(holder.imgView).execute(ent.getImageURL());
            }
            holder.title.setText(Html.fromHtml(mDataset.getRec().get(pos).getTitle()).toString());
            holder.id.setText(""+mDataset.getRec().get(pos).getTitle());
            holder.btn.setVisibility(ent.getHowMany()<2||subMenu!=-1?View.GONE:View.VISIBLE);
            holder.btn.setText("View all "+ent.getHowMany()+" recommendations");
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(view.getContext(), RecommendationsActivity.class);
                    i.putExtra("ENTRY_SUBMENU",posit);
                    view.getContext().startActivity(i);
                }
            });
        }else{
            holder.imgView.setVisibility(View.GONE);
            holder.title.setVisibility(View.GONE);
            holder.id.setVisibility(View.GONE);
            holder.btn.setVisibility(View.GONE);
        }
        //holder.title.setText(Html.fromHtml(ent.getTitle()).toString());
        //holder.id.setText(""+ent.getId());
        holder.author.setText("Recommended by "+ent.getRec().get(subMenu==-1?0:posit).getUser());
        holder.rec.setText(Html.fromHtml(ent.getRec().get(subMenu==-1?0:posit).getText()));

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
        holder.rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.rec.setMaxLines(holder.rec.getMaxLines()==10?9999:10);
            }
        });

    }
    @Override
    public int getItemCount() {
        if(subMenu==-1)
            return mDataset.getRec().size();
        return mDataset.getRec().get(subMenu).getRec().size();
    }

}
