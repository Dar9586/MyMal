package com.dar.mymal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dar.mymal.adapters.RecommendationAdapter;
import com.dar.mymal.adapters.ReviewAdapter;
import com.dar.mymal.entries.recommendation.Recommendations;
import com.dar.mymal.entries.review.Reviews;

public class RecommendationsActivity extends AppCompatActivity {
    String title;
    int id,sub;
    boolean anime,isRew;
    RecyclerView mRecyclerView;
    boolean useLessData=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        sub=getIntent().getIntExtra("ENTRY_SUBMENU",-1);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if(sub!=-1){
            mRecyclerView.setAdapter(new RecommendationAdapter(this,useLessData,ListLoader.getCacheFile(),sub));
        }else{
        title=getIntent().getStringExtra("ENTRY_TITLE");
        id=getIntent().getIntExtra("ENTRY_ID",-1);
        anime=getIntent().getBooleanExtra("ENTRY_ISANIME",true);
        isRew=getIntent().getBooleanExtra("ENTRY_ISREW",true);
        mRecyclerView.setAdapter(
                isRew?
                        new ReviewAdapter(this,new Reviews(id,anime),useLessData,ListLoader.getCacheFile()):
                        new RecommendationAdapter(this,new Recommendations(id,anime),useLessData,ListLoader.getCacheFile())
        );
        }
    }
}
