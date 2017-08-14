package com.dar.mymal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dar.mymal.entries.top.Top;
import com.dar.mymal.global.Settings;

public class TopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        boolean anime=getIntent().getBooleanExtra("IS_ANIME",true);
        int id=getIntent().getIntExtra("TYPE",0);
        new Top(anime,0, id,findViewById(R.id.master_layout),this);
    }
}
