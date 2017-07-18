package com.dar.mymal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dar.mymal.adapters.RecommendationMainAdapter;
import com.dar.mymal.entries.recommendation.Recommendations;

public class RecommendationsActivity extends AppCompatActivity {
    String title;
    int id;
    boolean anime;
    RecyclerView mRecyclerView;
    boolean useLessData=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        title=getIntent().getStringExtra("ENTRY_TITLE");
        id=getIntent().getIntExtra("ENTRY_ID",-1);
        anime=getIntent().getBooleanExtra("ENTRY_ISANIME",true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new RecommendationMainAdapter(this,new Recommendations(id,anime),useLessData,ListLoader.getCacheFile()));
    }
}
