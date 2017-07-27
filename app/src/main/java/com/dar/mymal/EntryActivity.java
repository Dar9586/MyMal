package com.dar.mymal;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.dar.mymal.entries.Anime;
import com.dar.mymal.entries.Entry;
import com.dar.mymal.entries.IDInspector;
import com.dar.mymal.entries.Manga;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.tuple.Tuple3;
import com.dar.mymal.utils.EntryList;
import com.dar.mymal.utils.MALUtils;
import com.dar.mymal.utils.MalAPI;
import com.dar.mymal.downloader.DownloadImage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EntryActivity extends AppCompatActivity {
    int id;
    boolean anime,tageditshown=false,added=false;
    String url,title;
    IDInspector entry;
    Menu menu;
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
        List<String>files=ListLoader.getCacheFile();
        Tuple2<String, List<Tuple2<String, String>>>[] infos=entry.getInfo();
        final List<Tuple2<String,List<Tuple3<Integer,Boolean,String>>>> rela=entry.getRel();


        List<String> arl = new ArrayList<>(Arrays.asList(Entry.getMyStatusList(anime)));
        ((Spinner) findViewById(R.id.entry_status)).setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arl));
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

        findViewById(R.id.entry_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.entry_titles).setVisibility(findViewById(R.id.entry_titles).getVisibility()==View.GONE?View.VISIBLE:View.GONE);
            }
        });
        findViewById(R.id.entry_view_mal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        findViewById(R.id.view_rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), RecommendationsActivity.class);
                i.putExtra("ENTRY_ID",id);
                i.putExtra("ENTRY_TITLE",title);
                i.putExtra("ENTRY_ISANIME",anime);
                i.putExtra("ENTRY_ISREW",false);
                v.getContext().startActivity(i);
            }
        });
        findViewById(R.id.view_rew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), RecommendationsActivity.class);
                i.putExtra("ENTRY_ID",id);
                i.putExtra("ENTRY_TITLE",title);
                i.putExtra("ENTRY_ISANIME",anime);
                i.putExtra("ENTRY_ISREW",true);
                v.getContext().startActivity(i);
            }
        });
        findViewById(R.id.add_to_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MalAPI.add(view.getContext(),id,anime);
                findViewById(R.id.add_to_list).setVisibility(View.GONE);
                findViewById(R.id.is_in_list).setVisibility(View.VISIBLE);
                findViewById(R.id.entry_tags_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.is_in_list).setVisibility(View.VISIBLE);
                findViewById(R.id.entry_dates).setVisibility(View.VISIBLE);
                added=true;
                loadList();

            }
        });
        ((Spinner)findViewById(R.id.entry_status)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(where!=null&&(anime?r1:r2)!=null){
                    int y=(anime?r1:r2).getMyStatus();
                    if(anime?r1.getEpisodes()==0:r2.getChapter()==0||r2.getVolumes()==0){
                        ((Spinner) findViewById(R.id.entry_status)).setSelection(y==6?4:y-1);
                        return;
                    }
                (anime?r1:r2).setMyStatus(position==4?6:(position+1));

                    if(position==1){
                        findViewById(R.id.entry_rewatch).setVisibility(View.VISIBLE);
                        if(anime){
                            r1.setMyEpisodes(r1.getEpisodes());
                            if(r1.getMyFinish()==null) {
                                r1.setMyFinish(Calendar.getInstance().getTime());
                                ((EditText) findViewById(R.id.entry_finish)).setText(r1.getMyFinish("dd-MM-yyyy"));

                            }
                        }else{
                            r2.setMyChapter(r2.getChapter());r2.setMyVolumes(r2.getVolumes());
                            if(r2.getMyFinish()==null){
                                r2.setMyFinish(Calendar.getInstance().getTime());
                                ((EditText) findViewById(R.id.entry_finish)).setText(r2.getMyFinish("dd-MM-yyyy"));
                            }
                        }
                    }else{findViewById(R.id.entry_rewatch).setVisibility(View.GONE);}
                executeUpdate();}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((Spinner)findViewById(R.id.entry_score)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if((anime?r1:r2)!=null){
                    (anime?r1:r2).setScore(position);
                executeUpdate();}
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        findViewById(R.id.entry_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate(true);
            }
        });
        findViewById(R.id.entry_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate(false);
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
                    executeUpdate();
                }else{
                    findViewById(R.id.entry_tags_edit).setVisibility(View.VISIBLE);
                    findViewById(R.id.entry_tags).setVisibility(View.GONE);
                    tageditshown=true;

                }
            }
        });
        ((Switch)findViewById(R.id.entry_rewatch)).setText(anime?"Rewatch":"Reread");
        ((Switch)findViewById(R.id.entry_rewatch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                (anime?r1:r2).setRewatch(isChecked);
                Log.d("OnMALDebug","IS "+(anime?r1:r2).getMyStatus()+isChecked);
                if((anime?r1:r2).getMyStatus()==2){
                    if(!isChecked){
                        if(anime){
                            r1.setMyEpisodes(r1.getEpisodes());
                        }else {
                            r2.setMyChapter(r2.getChapter());
                            r2.setMyVolumes(r2.getVolumes());
                        }
                        setEpisodeTexts();
                    }
                }
                executeUpdate();
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
        loadList();
    }

    private void updateDate(final boolean b) {
        Date h=b?(anime?r1:r2).getMyStart():(anime?r1:r2).getMyFinish();
        Calendar mcurrentDate=Calendar.getInstance();
        if(h!=null)mcurrentDate.setTime(h);
        int mYear=mcurrentDate.get(Calendar.YEAR),mMonth=mcurrentDate.get(Calendar.MONTH),mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mDatePicker=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int year, int month, int day) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    if(b)(anime ? r1 : r2).setMyStart (format.parse(String.format("%4d-%2d-%2d", year, month, day)));
                    else (anime ? r1 : r2).setMyFinish(format.parse(String.format("%4d-%2d-%2d", year, month, day)));
                    executeUpdate();
                    if(b)((TextView) findViewById(R.id.entry_start )).setText((anime?r1:r2).getMyStart ("dd-MM-yyyy"));
                    else ((TextView) findViewById(R.id.entry_finish)).setText((anime?r1:r2).getMyFinish("dd-MM-yyyy"));
                }catch (ParseException e){Log.e("OnMALError","Error parsing "+e.getMessage());}
            }
        },mYear, mMonth, mDay);
        mDatePicker.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(b)(anime ? r1 : r2).setMyStart(null);else(anime?r1:r2).setMyFinish(null);
                executeUpdate();
                ((TextView) findViewById(b?R.id.entry_start:R.id.entry_finish)).setText("UNKNOWN");
            }
        });
        mDatePicker.setTitle("Select "+(b?"start":"finish")+" date");
        mDatePicker.setCancelable(true);
        mDatePicker.show();
    }

    void loadList(){
        where=MALUtils.getIdIndex(EntryList.getOwnlist()[anime?0:1],id);
        if(where!=null)loadListVariables();
        else loadListNull();
    }
    void loadListNull(){
        if(menu!=null)menu.findItem(R.id.action_remove).setVisible(false);
        findViewById(R.id.is_in_list).setVisibility(View.GONE);
        findViewById(R.id.entry_tags_layout).setVisibility(View.GONE);
        findViewById(R.id.entry_dates).setVisibility(View.GONE);
        findViewById(R.id.add_to_list).setVisibility(View.VISIBLE);
    }

    void loadListVariables(){
        if(menu!=null)menu.findItem(R.id.action_remove).setVisible(true);
            Entry r = EntryList.getOwnlist()[anime ? 0 : 1][where.getA()].get(where.getB());
            if (anime) r1 = (Anime) r;
            else r2 = (Manga) r;
        if(added&&r.getMyStart()==null){
            (anime?r1:r2).setMyStart(Calendar.getInstance().getTime());
            executeUpdate();
            added=false;
        }
            if(r.getMyStatus()==1)findViewById(R.id.entry_rewatch).setVisibility(View.VISIBLE);
            ((Switch)findViewById(R.id.entry_rewatch)).setChecked(r.getRewatch());
            ((Spinner) findViewById(R.id.entry_score)).setSelection(r.getScore());
            ((Spinner) findViewById(R.id.entry_status)).setSelection(r.getMyStatus()==6?4:r.getMyStatus() - 1);
            ((TextView) findViewById(R.id.entry_tags)).setText(r.getTags());
            ((TextView) findViewById(R.id.entry_tags_edit)).setText(r.getTags());
            ((EditText) findViewById(R.id.entry_start)).setText (r.getMyStart ()==null?"UNKNOWN":r.getMyStart ("dd-MM-yyyy"));
            ((EditText) findViewById(R.id.entry_finish)).setText(r.getMyFinish()==null?"UNKNOWN":r.getMyFinish("dd-MM-yyyy"));
            ((TextView) findViewById(R.id.entry_tags_edit)).setText(r.getTags());
        setEpisodeTexts();
        if(anime){
            final Button btn0=(Button)findViewById(R.id.updater_adder_0);
            final EditText edit0=(EditText)findViewById(R.id.updater_edit_0);
            findViewById(R.id.updater_master_1).setVisibility(View.GONE);
            findViewById(R.id.updater_master_2).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.updater_total_0)).setText(r1.getEpisodes()==0?"-":""+r1.getEpisodes());
            btn0.setText("+1");
            btn0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    r1.setMyEpisodes(btn0.getText().toString() .equals("+1")? r1.getMyEpisodes() + 1 : Integer.parseInt(edit0.getText().toString()));
                    updateOpener(findViewById(R.id.updater_sub_0), findViewById(R.id.updater_static_0), ((Button) findViewById(R.id.updater_opener_0)), btn0, true);
                    setEpisodeTexts();
                    if(r1.getEpisodes()==r1.getMyEpisodes())makeCompleted(view);
                    else executeUpdate();
                }
            });
            findViewById(R.id.updater_opener_0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateOpener(findViewById(R.id.updater_sub_0), findViewById(R.id.updater_static_0), ((Button) findViewById(R.id.updater_opener_0)), btn0);
                }
            });
        }else{
            findViewById(R.id.updater_master_0).setVisibility(View.GONE);
            final Button btn1=(Button)findViewById(R.id.updater_adder_1);
            final EditText edit1=(EditText)findViewById(R.id.updater_edit_1);
            final Button btn2=(Button)findViewById(R.id.updater_adder_2);
            final EditText edit2=(EditText)findViewById(R.id.updater_edit_2);
            ((TextView)findViewById(R.id.updater_total_1)).setText(r2.getChapter()==0?"-":""+r2.getChapter());
            ((TextView)findViewById(R.id.updater_total_2)).setText(r2.getVolumes()==0?"-":""+r2.getVolumes());
            btn1.setText("+1");btn2.setText("+1");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    r2.setMyChapter(btn1.getText().toString() =="+1"? r2.getMyChapter() + 1 : Integer.parseInt(edit1.getText().toString()));
                    updateOpener(findViewById(R.id.updater_sub_1), findViewById(R.id.updater_static_1), ((Button) findViewById(R.id.updater_opener_1)), btn1, true);
                    setEpisodeTexts();
                    if(r2.getChapter()==r2.getMyChapter())makeCompleted(view);
                    else executeUpdate();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    r2.setMyVolumes(btn2.getText().toString() =="+1"? r2.getMyVolumes() + 1 : Integer.parseInt(edit2.getText().toString()));
                    updateOpener(findViewById(R.id.updater_sub_2), findViewById(R.id.updater_static_2), ((Button) findViewById(R.id.updater_opener_2)), btn2, true);
                    setEpisodeTexts();
                    if(r2.getVolumes()==r2.getMyVolumes())makeCompleted(view);
                    else executeUpdate();
                }
            });
            findViewById(R.id.updater_opener_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateOpener(findViewById(R.id.updater_sub_1), findViewById(R.id.updater_static_1), ((Button) findViewById(R.id.updater_opener_1)), btn1);
                }
            });
            findViewById(R.id.updater_opener_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateOpener(findViewById(R.id.updater_sub_2), findViewById(R.id.updater_static_2), ((Button) findViewById(R.id.updater_opener_2)), btn2);
                }
            });

        }
    }

    void setEpisodeTexts(){
        if(anime) {
            ((TextView) findViewById(R.id.updater_static_0)).setText(r1.getMyEpisodes() + "/" + (r1.getEpisodes()==0?"-":r1.getEpisodes()));
            ((EditText)findViewById(R.id.updater_edit_2)).setText(""+r1.getMyEpisodes());
        }else {
            ((TextView) findViewById(R.id.updater_static_1)).setText(r2.getMyChapter() + "/" + (r2.getChapter()==0?"-":r2.getChapter()));
            ((TextView) findViewById(R.id.updater_static_2)).setText(r2.getMyVolumes() + "/" + (r2.getVolumes()==0?"-":r2.getVolumes()));
            ((EditText)findViewById(R.id.updater_edit_2)).setText(""+r2.getMyChapter());
            ((EditText)findViewById(R.id.updater_edit_2)).setText(""+r2.getMyVolumes());
        }
    }

    void updateOpener(View v1,View v2,Button b1,Button b2){
        updateOpener(v1,v2,b1,b2,false);
    }
    void updateOpener(View v1,View v2,Button b1,Button b2,boolean def){
        boolean f=v1.getVisibility()==View.VISIBLE||def;
        v1.setVisibility(f?View.GONE:View.VISIBLE);
        v2.setVisibility(f?View.VISIBLE:View.GONE);
        b1.setText(f?"✍":"X");
        b2.setText(f?"+1":"✔");
    }

    void executeUpdate(){
        MalAPI.update(this,anime?r1:r2);
        if((anime?r1:r2).getMyStatus()==2){
            if((anime?r1:r2).getRewatch()){
                if(anime){
                    findViewById(R.id.updater_adder_0).setVisibility(View.VISIBLE);
                    findViewById(R.id.updater_opener_0).setVisibility(View.VISIBLE);}else{
                    findViewById(R.id.updater_adder_1).setVisibility(View.VISIBLE);
                    findViewById(R.id.updater_adder_2).setVisibility(View.VISIBLE);
                    findViewById(R.id.updater_opener_1).setVisibility(View.VISIBLE);
                    findViewById(R.id.updater_opener_2).setVisibility(View.VISIBLE);}
            }else{
                findViewById(R.id.updater_adder_0).setVisibility(View.GONE);
                findViewById(R.id.updater_opener_0).setVisibility(View.GONE);
                findViewById(R.id.updater_adder_1).setVisibility(View.GONE);
                findViewById(R.id.updater_adder_2).setVisibility(View.GONE);
                findViewById(R.id.updater_opener_1).setVisibility(View.GONE);
                findViewById(R.id.updater_opener_2).setVisibility(View.GONE);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.entry_menu, menu);
        menu.findItem(R.id.action_remove).setVisible(where!=null);
        this.menu=menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_remove:
                MalAPI.remove(this,(anime?r1:r2).getID(),anime);
                r1=null;r2=null;
                findViewById(R.id.is_in_list).setVisibility(View.GONE);
                EntryList.reloadOwn(anime);
                loadList();
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
                //(anime?r1:r2).setMyStatus(2);
                ((Spinner)findViewById(R.id.entry_status)).setSelection(1);
                executeUpdate();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                executeUpdate();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.cancel();
                executeUpdate();
            }
        });
        builder.show();
    }
}
