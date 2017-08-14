package com.dar.mymal.adapters.dataEntry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.R;
import com.dar.mymal.entries.inspector.Characters;
import com.dar.mymal.downloader.DownloadImage;
import com.dar.mymal.global.Settings;
import com.dar.mymal.utils.MALUtils;

import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {
    public void setContext(Context context) {
        this.context = context;
    }
    Context context;
    List<String>files;
    List<Characters.Character> mData;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout right,left;
        boolean isSet=false;
        public ViewHolder(View v) {
            super(v);
            left= (LinearLayout)v.findViewById(R.id.staff_left );
            right=(LinearLayout)v.findViewById(R.id.staff_right);
            //right.addView(princ);
        }

        public boolean isSet() {
            return isSet;
        }

        public void setSet(boolean set) {
            isSet = set;
        }
    }

    public StaffAdapter(Context context, List<Characters.Character> mData) {
        this.context = context;
        this.files= MALUtils.getCacheFile();
        this.mData = mData;
    }
    public StaffAdapter(List<Characters.Character> mData) {
        this.files= MALUtils.getCacheFile();
        this.mData = mData;
    }
    @Override
    public StaffAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_staff, parent, false));
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(!holder.isSet) {
            populateView(inflater.inflate(R.layout.simple_staff_left, holder.left, true), mData.get(position).getCharacter());
            if (mData.get(position).getVoices().size() != 0) {
                for (int a = 0; a < mData.get(position).getVoices().size(); a++) {
                    View s = inflater.inflate(R.layout.simple_staff_right, holder.right, false);
                    holder.right.addView(s);
                    populateView(s, mData.get(position).getVoices().get(a));
                }
            } else {
                holder.right.setVisibility(View.GONE);
            }
            holder.isSet = true;
        }
    }
    void populateView(View v, Characters.People p){
        if(!Settings.isUsingLessData()){
            ((ImageView)v.findViewById(R.id.staff_image)).setScaleType(ImageView.ScaleType.FIT_START);
            new DownloadImage((ImageView)v.findViewById(R.id.staff_image)).execute(p.getImage());
        }
        ((TextView)v.findViewById(R.id.staff_name)).setText(p.getName());
        ((TextView)v.findViewById(R.id.staff_type)).setText(p.getDesc());
    }

    @Override
    public int getItemCount() {
       return mData.size();
    }

}
