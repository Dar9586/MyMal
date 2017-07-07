package com.dar.mymal;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dar.mymal.entries.Anime;
import com.dar.mymal.entries.IDInspector;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.utils.MALUtils;
import com.dar.mymal.utils.MalAPI;
import com.dar.mymal.utils.downloader.DownloadImage;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EntryActivity extends AppCompatActivity {
    int id;
    boolean anime,tageditshown=false;
    String url;
    IDInspector entry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        id=getIntent().getIntExtra("ENTRY_ID",-1);
        anime=getIntent().getBooleanExtra("ENTRY_ISANIME",true);
        url="http://www.myanimelist.net/"+(anime?"anime":"manga")+'/'+id;
        ((Button)findViewById(R.id.entry_view_mal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        entry=new IDInspector(id,anime);
        ((TextView)findViewById(R.id.entry_synopsis)).setText(entry.getDescription());


        List<String>files=ListLoader.getCacheFile();
        if(files.contains("A"+id+".jpg")){
            ((ImageView)findViewById(R.id.entry_image)).setBackground(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/A"+id+".jpg")));
        }else/* if(!useLessData)*/{
            new DownloadImage(((ImageView)findViewById(R.id.entry_image)),Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache","A"+Integer.toString(id)).execute(entry.getImageURL());
        }
        Tuple2<String, List<Tuple2<String, String>>>[] infos=entry.getInfo();
        LinearLayout lny=(LinearLayout)findViewById(R.id.entry_infos);
        LinearLayout lny1=(LinearLayout)findViewById(R.id.entry_titles);
        for(int a=0;a<infos.length;a++){
            View v=getLayoutInflater().inflate(R.layout.entry_info_title,null);
            ((TextView)v.findViewById(R.id.entry_info_header)).setText(infos[a].getA());
            (a==0?lny1:lny).addView(v);
            for(int b=0;b<infos[a].getB().size();b++){
                View v1=getLayoutInflater().inflate(R.layout.entry_info,null);
                ((TextView)v1.findViewById(R.id.name)) .setText(infos[a].getB().get(b).getA());
                ((TextView)v1.findViewById(R.id.value)).setText(infos[a].getB().get(b).getB());
                (a==0?lny1:lny).addView(v1);
            }
        }
        final Tuple2<Integer,Integer>where;
        if( (where=MALUtils.getIdIndex(ListLoader.ownList[0],id))!=null){
            final Anime r=(Anime)ListLoader.ownList[0][where.getA()].get(where.getB());
            ((Spinner)findViewById(R.id.entry_score)).setSelection(r.getScore());
            ((Spinner)findViewById(R.id.entry_status)).setSelection(r.getMyStatus()-1);
            ((TextView)findViewById(R.id.entry_tags)).setText(r.getTags());
            ((TextView)findViewById(R.id.entry_tags_edit)).setText(r.getTags());
            ((TextView)findViewById(R.id.entry_episode_static)).setText(r.getMyEpisodes()+"/"+r.getEpisodes());
            ((TextView)findViewById(R.id.entry_episode_total)).setText(Integer.toString(r.getEpisodes()) );
            ((EditText)findViewById(R.id.entry_episode)).setText(Integer.toString(r.getMyEpisodes()));
            ((Button)findViewById(R.id.open_episodes_menu)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean f=((LinearLayout)findViewById(R.id.entry_episode_changer)).getVisibility()==View.VISIBLE;
                    ((LinearLayout)findViewById(R.id.entry_episode_changer)).setVisibility(f?View.GONE:View.VISIBLE);
                    ((Button)findViewById(R.id.open_episodes_menu)).setText(f?"▼":"▲");
                }
            });
            ((Spinner)findViewById(R.id.entry_status)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((Anime) ListLoader.ownList[0][where.getA()].get(where.getB())).setMyStatus(position);
                    MalAPI.update(view.getContext(),ListLoader.ownList[0][where.getA()].get(where.getB()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ((Spinner)findViewById(R.id.entry_score)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((Anime) ListLoader.ownList[0][where.getA()].get(where.getB())).setScore(position);
                    MalAPI.update(view.getContext(),ListLoader.ownList[0][where.getA()].get(where.getB()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            ((Button)findViewById(R.id.entry_adder)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Anime) ListLoader.ownList[0][where.getA()].get(where.getB())).setMyEpisodes((((Anime) ListLoader.ownList[0][where.getA()].get(where.getB())).getMyEpisodes()+1));
                    MalAPI.update(view.getContext(),ListLoader.ownList[0][where.getA()].get(where.getB()));
                    ((TextView)findViewById(R.id.entry_episode_static)).setText(((Anime) ListLoader.ownList[0][where.getA()].get(where.getB())).getMyEpisodes()+"/"+r.getEpisodes());
                    ((EditText)findViewById(R.id.entry_episode)).setText(Integer.toString (((Anime) ListLoader.ownList[0][where.getA()].get(where.getB())).getMyEpisodes()));
                }
            });
            ((Button)findViewById(R.id.entry_tags_changer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tageditshown){
                        ((EditText)findViewById(R.id.entry_tags_edit)).setVisibility(View.GONE);
                        ((TextView)findViewById(R.id.entry_tags)).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.entry_tags)).setText(((EditText)findViewById(R.id.entry_tags_edit)).getText().toString());
                        tageditshown=false;
                        ListLoader.ownList[0][where.getA()].get(where.getB()).setTags(((EditText)findViewById(R.id.entry_tags_edit)).getText().toString());
                        MalAPI.update(view.getContext(),ListLoader.ownList[0][where.getA()].get(where.getB()));
                    }else{
                        ((EditText)findViewById(R.id.entry_tags_edit)).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.entry_tags)).setVisibility(View.GONE);
                        tageditshown=true;

                    }
                }
            });
            ((Button)findViewById(R.id.entry_episodes_changer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Anime) ListLoader.ownList[0][where.getA()].get(where.getB())).setMyEpisodes(Integer.parseInt(((EditText)findViewById(R.id.entry_episode)).getText().toString()));
                    MalAPI.update(view.getContext(),ListLoader.ownList[0][where.getA()].get(where.getB()));
                }
            });

            ((TextView)findViewById(R.id.entry_tags)).setText(r.getTags());

        }
    }
}
