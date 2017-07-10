package com.dar.mymal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dar.mymal.entries.Anime;
import com.dar.mymal.entries.Entry;
import com.dar.mymal.entries.IDInspector;
import com.dar.mymal.entries.Manga;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.tuple.Tuple3;
import com.dar.mymal.utils.EntryList;
import com.dar.mymal.utils.LoginData;
import com.dar.mymal.utils.MALUtils;
import com.dar.mymal.utils.MalAPI;
import com.dar.mymal.utils.downloader.DownloadImage;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntryActivity extends AppCompatActivity {
    int id;
    boolean anime,tageditshown=false;
    String url,title;
    IDInspector entry;
    Tuple2<Integer,Integer>where;
    Anime r1;Manga r2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        title=getIntent().getStringExtra("ENTRY_TITLE");
        id=getIntent().getIntExtra("ENTRY_ID",-1);
        anime=getIntent().getBooleanExtra("ENTRY_ISANIME",true);
        url="http://www.myanimelist.net/"+(anime?"anime":"manga")+'/'+id;
        entry=new IDInspector(id,anime);
        where=MALUtils.getIdIndex(EntryList.getOwnlist()[anime?0:1],id);
        List<String>files=ListLoader.getCacheFile();
        Tuple2<String, List<Tuple2<String, String>>>[] infos=entry.getInfo();
        final List<Tuple2<String,List<Tuple3<Integer,Boolean,String>>>> rela=entry.getRel();

        LinearLayout lny=(LinearLayout)findViewById(R.id.entry_infos);
        LinearLayout lny1=(LinearLayout)findViewById(R.id.entry_titles);
        LinearLayout sss=((LinearLayout)findViewById(R.id.entry_relative));
        ((TextView)findViewById(R.id.entry_synopsis)).setText(entry.getDescription());
        ((TextView)findViewById(R.id.entry_title)).setText(title);


        if(files.contains("A"+id+".jpg")){
            ((ImageView)findViewById(R.id.entry_image)).setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/A"+id+".jpg")));
        }else/* if(!useLessData)*/{
            new DownloadImage(((ImageView)findViewById(R.id.entry_image)),Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache","A"+Integer.toString(id)).execute(entry.getImageURL());
        }


        //apri su MAL
        findViewById(R.id.entry_view_mal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        findViewById(R.id.add_to_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MalAPI.add(view.getContext(),id,anime);
                findViewById(R.id.add_to_list).setVisibility(View.GONE);
                findViewById(R.id.is_in_list).setVisibility(View.VISIBLE);
                findViewById(R.id.entry_tags_layout).setVisibility(View.VISIBLE);
                loadListVariables();

            }
        });
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
        for(int a=0;a<rela.size();a++){
            View v=getLayoutInflater().inflate(R.layout.entry_info_title,null);
            ((TextView)v.findViewById(R.id.entry_info_header)).setText(rela.get(a).getA());
            sss.addView(v);
            for(int b=0;b<rela.get(a).getB().size();b++){
                View v1=getLayoutInflater().inflate(R.layout.basic_relation,null);
                ((TextView)v1.findViewById(R.id.basic_relation2)).setText(rela.get(a).getB().get(b).getC());
                final int c=a,d=b;
                v1.findViewById(R.id.basic_relation2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(v.getContext(), EntryActivity.class);
                        i.putExtra("ENTRY_ID",rela.get(c).getB().get(d).getA());
                        i.putExtra("ENTRY_TITLE",rela.get(c).getB().get(d).getC());
                        i.putExtra("ENTRY_ISANIME",rela.get(c).getB().get(d).getB());
                        v.getContext().startActivity(i);
                    }
                });
                sss.addView(v1);
            }
        }

        if(where!=null)loadListVariables();
            else loadListNull();
    }
    void loadListNull(){
        findViewById(R.id.is_in_list).setVisibility(View.GONE);
        findViewById(R.id.entry_tags_layout).setVisibility(View.GONE);
        findViewById(R.id.add_to_list).setVisibility(View.VISIBLE);
    }
    void loadListVariables(){
        where=MALUtils.getIdIndex(EntryList.getOwnlist()[anime?0:1],id);
            Entry r=EntryList.getOwnlist()[anime?0:1][where.getA()].get(where.getB());
            if(anime)r1=(Anime)r;else r2=(Manga)r;
            ((Spinner)findViewById(R.id.entry_score)).setSelection(r.getScore());
            ((Spinner)findViewById(R.id.entry_status)).setSelection(r.getMyStatus()-1);
            List<String>arl=new ArrayList<>(Arrays.asList(Entry.getMyStatusList(anime)));
            ((Spinner)findViewById(R.id.entry_status)).setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,arl));
            ((TextView)findViewById(R.id.entry_tags)).setText(r.getTags());
            ((TextView)findViewById(R.id.entry_tags_edit)).setText(r.getTags());
            ((TextView)findViewById(R.id.entry_tags)).setText(r.getTags());

            ((Spinner)findViewById(R.id.entry_status)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    (anime?r1:r2).setMyStatus(position==4?6:(position+1));
                    executeUpdate(view);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ((Spinner)findViewById(R.id.entry_score)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    (anime?r1:r2).setScore(position);
                    executeUpdate(view);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            findViewById(R.id.entry_tags_changer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tageditshown){
                        findViewById(R.id.entry_tags_edit).setVisibility(View.GONE);
                        findViewById(R.id.entry_tags).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.entry_tags)).setText(((EditText)findViewById(R.id.entry_tags_edit)).getText().toString());
                        tageditshown=false;
                        (anime?r1:r2).setTags(((EditText)findViewById(R.id.entry_tags_edit)).getText().toString());
                        executeUpdate(view);
                    }else{
                        findViewById(R.id.entry_tags_edit).setVisibility(View.VISIBLE);
                        findViewById(R.id.entry_tags).setVisibility(View.GONE);
                        tageditshown=true;

                    }
                }
            });
            final List<View>views=new ArrayList<>();
            views.add(getLayoutInflater().inflate(R.layout.episode_updater,null));
            if(!anime)views.add(getLayoutInflater().inflate(R.layout.episode_updater,null));
            if(anime) {
                ((TextView) views.get(0).findViewById(R.id.updater_episode_static)).setText(r1.getMyEpisodes() + "/" + r1.getEpisodes());
                ((TextView) views.get(0).findViewById(R.id.updater_episode_total)).setText(Integer.toString(r1.getEpisodes()));
                ((EditText) views.get(0).findViewById(R.id.updater_new_episode)).setText(Integer.toString(r1.getMyEpisodes()));
                views.get(0).findViewById(R.id.updater_adder).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        r1.setMyEpisodes(((Button)(views.get(0).findViewById(R.id.updater_adder))).getText().toString()=="+1"?(r1.getMyEpisodes()+1):Integer.parseInt(((EditText)views.get(0).findViewById(R.id.updater_new_episode)).getText().toString()));
                        returnToDefault(views.get(0));
                        if(r1.getMyEpisodes()==r1.getEpisodes()){
                            makeCompleted(view);
                        }
                        else
                            executeUpdate(view);
                        ((TextView)views.get(0).findViewById(R.id.updater_episode_static)).setText(r1.getMyEpisodes()+"/"+r1.getEpisodes());
                        ((EditText)views.get(0).findViewById(R.id.updater_new_episode)).setText(Integer.toString (r1.getMyEpisodes()));
                    }
                });
            }else{
                ((TextView) views.get(0).findViewById(R.id.updater_episode_static)).setText(r2.getMyChapter() + "/" + r2.getChapter());
                ((TextView) views.get(0).findViewById(R.id.updater_episode_total)).setText(Integer.toString(r2.getChapter()));
                ((EditText) views.get(0).findViewById(R.id.updater_new_episode)).setText(Integer.toString(r2.getMyChapter()));
                views.get(0).findViewById(R.id.updater_adder).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        r2.setMyChapter(
                                ((Button)(views.get(0).findViewById(R.id.updater_adder))).getText().toString()=="+1"?(r2.getMyChapter()+1):Integer.parseInt(((EditText)views.get(0).findViewById(R.id.updater_new_episode)).getText().toString()));
                        returnToDefault(views.get(0));
                        if(r2.getMyChapter()==r2.getChapter()){
                            makeCompleted(view);
                        }
                        else
                            executeUpdate(view);
                        ((TextView)views.get(0).findViewById(R.id.updater_episode_static)).setText(r2.getMyChapter()+"/"+r2.getChapter());
                        ((EditText)views.get(0).findViewById(R.id.updater_new_episode)).setText(Integer.toString (r2.getMyChapter()));
                    }
                });
                ((TextView) views.get(1).findViewById(R.id.updater_episode_static)).setText(r2.getMyVolumes() + "/" + r2.getVolumes());
                ((TextView) views.get(1).findViewById(R.id.updater_episode_total)).setText(Integer.toString(r2.getVolumes()));
                ((EditText) views.get(1).findViewById(R.id.updater_new_episode)).setText(Integer.toString(r2.getMyVolumes()));
                views.get(1).findViewById(R.id.updater_adder).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        r2.setMyVolumes(
                        ((Button)(views.get(1).findViewById(R.id.updater_adder))).getText().toString()=="+1"?(r2.getMyVolumes()+1):Integer.parseInt(((EditText)views.get(1).findViewById(R.id.updater_new_episode)).getText().toString()));
                        returnToDefault(views.get(1));
                        if(r2.getMyVolumes()==r2.getVolumes()){
                            makeCompleted(view);
                        }
                        else
                            executeUpdate(view);
                        ((TextView)views.get(1).findViewById(R.id.updater_episode_static)).setText(r2.getMyVolumes()+"/"+r2.getVolumes());
                        ((EditText)views.get(1).findViewById(R.id.updater_new_episode)).setText(Integer.toString (r2.getMyVolumes()));
                    }
                });
            }
            for(int a=0;a<views.size();a++){
                final int jj=a;
                views.get(a).findViewById(R.id.updater_open_episodes_menu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setListener(views.get(jj));
                    }
                });
                ((LinearLayout)findViewById(R.id.is_in_list)).addView(views.get(a));
            }
    }

    void executeUpdate(View view){
        MalAPI.update(view.getContext(),anime?r1:r2);
    }
    void setListener(View view){
        boolean f=view.findViewById(R.id.updater_sub_layout).getVisibility()==View.VISIBLE;
        view.findViewById(R.id.updater_sub_layout).setVisibility(f?View.GONE:View.VISIBLE);
        view.findViewById(R.id.updater_episode_static).setVisibility(!f?View.GONE:View.VISIBLE);
        ((Button)view.findViewById(R.id.updater_open_episodes_menu)).setText(f?"✍":"X");
        ((Button)view.findViewById(R.id.updater_adder)).setText(f?"+1":"✔");
    }
    void returnToDefault(View view){
        view.findViewById(R.id.updater_sub_layout).setVisibility(View.GONE);
        view.findViewById(R.id.updater_episode_static).setVisibility(View.VISIBLE);
        ((Button)view.findViewById(R.id.updater_open_episodes_menu)).setText("✍");
        ((Button)view.findViewById(R.id.updater_adder)).setText("+1");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.entry_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_remove:
                MalAPI.remove(this,(anime?r1:r2).getID(),anime);
                r1=null;r2=null;
                loadListNull();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    void makeCompleted(final View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((anime?"Anime":"Manga")+" completed");
        builder.setMessage("Set this "+(anime?"Anime":"Manga")+" as completed?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                (anime?r1:r2).setMyStatus(2);
                executeUpdate(view);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                ((Spinner)findViewById(R.id.entry_status)).setSelection(1);
                executeUpdate(view);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.cancel();
                executeUpdate(view);
            }
        });
        builder.show();
    }
}
