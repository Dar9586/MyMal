package com.dar.mymal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.dar.mymal.entries.api.Anime;
import com.dar.mymal.entries.api.Entry;
import com.dar.mymal.entries.inspector.IDInspector;
import com.dar.mymal.entries.api.Manga;
import com.dar.mymal.entries.inspector.MasterInspector;
import com.dar.mymal.global.Settings;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.global.EntryList;
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
    static EntryActivity mainThis;
    static int id;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    static boolean anime,added=false;
    String url,title;
    IDInspector entry;
    Menu menu;
    Tuple2<Integer,Integer>where;
    Anime r1;Manga r2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        mainThis=this;
        title=getIntent().getStringExtra("ENTRY_TITLE");
        id=getIntent().getIntExtra("ENTRY_ID",-1);
        anime=getIntent().getBooleanExtra("ENTRY_ISANIME",true);
        url="http://www.myanimelist.net/"+(anime?"anime":"manga")+'/'+id;
        entry=new IDInspector(id,anime);
        List<String>files=MALUtils.getCacheFile();


        List<String> arl = new ArrayList<>(Arrays.asList(Entry.getMyStatusList(anime)));
        ((Spinner) findViewById(R.id.entry_status)).setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arl));
        ((TextView)findViewById(R.id.entry_title)).setText(title);

        if(files.contains("A"+id+".jpg")){
            ((ImageView)findViewById(R.id.entry_image)).setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/A"+id+".jpg")));
        }else if(!Settings.isUsingLessData()){
            new DownloadImage(((ImageView)findViewById(R.id.entry_image)),Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache","A"+Integer.toString(id)).execute(entry.getImageURL());
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(8);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager,true);
        findViewById(R.id.add_to_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MalAPI.add(id,anime);
                findViewById(R.id.add_to_list).setVisibility(View.GONE);
                findViewById(R.id.is_in_list).setVisibility(View.VISIBLE);
                findViewById(R.id.entry_tags).setVisibility(View.VISIBLE);
                findViewById(R.id.is_in_list).setVisibility(View.VISIBLE);
                findViewById(R.id.entry_dates).setVisibility(View.VISIBLE);
                added=true;
                loadList();

            }
        });
        findViewById(R.id.entry_tags).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTags();
            }
        });
        ((Spinner)findViewById(R.id.entry_status)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(where!=null&&(anime?r1:r2)!=null){
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
        loadList();
    }

    @Override
    public void onBackPressed() {
        mainThis=null;
        PlaceholderFragment.ok=true;
        PlaceholderFragment.registeredFragments.clear();
        finish();
    }

    private void updateTags() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set tags:");
        View v=getLayoutInflater().inflate(R.layout.edit_tag,null);
        builder.setView(v);
        final EditText edt=(EditText)v.findViewById(R.id.tags_edit);
        edt.setText((anime?r1:r2).getTags());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                (anime?r1:r2).setTags(edt.getText().toString());
                executeUpdate();
                ((EditText)findViewById(R.id.entry_tags)).setText((anime?r1:r2).getTags());
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.show();
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
        findViewById(R.id.entry_tags).setVisibility(View.GONE);
        findViewById(R.id.entry_dates).setVisibility(View.GONE);
        findViewById(R.id.add_to_list).setVisibility(View.VISIBLE);
    }

    void updateEpisodes(final int type){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set "+(type==0?"episodes":type==1?"chapters":"volumes")+":");
        View v=getLayoutInflater().inflate(R.layout.episode_updater,null);
        builder.setView(v);
        final EditText edt=(EditText)v.findViewById(R.id.episode_updater_edit);
        int total=type==0?r1.getEpisodes():type==1?r2.getChapter():r2.getVolumes();
        ((TextView)v.findViewById(R.id.episode_updater_total)).setText(""+(total==0?"-":total));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                switch (type){
                    case 0:r1.setMyEpisodes(Integer.parseInt(edt.getText().toString()));break;
                    case 1:r2.setMyChapter (Integer.parseInt(edt.getText().toString()));break;
                    case 2:r2.setMyVolumes (Integer.parseInt(edt.getText().toString()));break;
                }
                executeUpdate();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.show();
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
            ((EditText) findViewById(R.id.entry_start)).setText (r.getMyStart ()==null?"UNKNOWN":r.getMyStart ("dd-MM-yyyy"));
            ((EditText) findViewById(R.id.entry_finish)).setText(r.getMyFinish()==null?"UNKNOWN":r.getMyFinish("dd-MM-yyyy"));
        setEpisodeTexts();
        if(anime){
            findViewById(R.id.updater_master_1).setVisibility(View.GONE);
            findViewById(R.id.updater_master_2).setVisibility(View.GONE);
            findViewById(R.id.updater_adder_0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(r1.getMyEpisodes()<r1.getEpisodes()||r1.getEpisodes()==0){r1.setMyEpisodes(r1.getMyEpisodes()+1);if(r1.getMyEpisodes()==r1.getEpisodes())makeCompleted(v);executeUpdate();}
                }
            });
            findViewById(R.id.updater_remover_0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(r1.getMyEpisodes()>0){r1.setMyEpisodes(r1.getMyEpisodes()-1);executeUpdate();}
                }
            });
            findViewById(R.id.updater_static_0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateEpisodes(0);
                }
            });
        }else{
            findViewById(R.id.updater_master_0).setVisibility(View.GONE);
            findViewById(R.id.updater_adder_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(r2.getMyChapter()<r2.getChapter()||r2.getChapter()==0){r2.setMyChapter(r2.getMyChapter()+1);if(r2.getMyChapter()==r2.getChapter())makeCompleted(v);executeUpdate();}
                }
            });
            findViewById(R.id.updater_remover_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(r2.getMyChapter()>0){r2.setMyChapter(r2.getMyChapter()-1);executeUpdate();}
                }
            });
            findViewById(R.id.updater_static_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateEpisodes(1);
                }
            });
            findViewById(R.id.updater_adder_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(r2.getMyVolumes()<r2.getVolumes()||r2.getVolumes()==0){r2.setMyVolumes(r2.getMyVolumes()+1);if(r2.getMyVolumes()==r2.getVolumes())makeCompleted(v);executeUpdate();}
                }
            });
            findViewById(R.id.updater_remover_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(r2.getMyVolumes()>0){r2.setMyVolumes(r2.getMyVolumes()-1);executeUpdate();}
                }
            });
            findViewById(R.id.updater_static_2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateEpisodes(2);
                }
            });

        }
    }

    void setEpisodeTexts(){
        if(anime) {
            ((TextView) findViewById(R.id.updater_static_0)).setText(r1.getMyEpisodes() + "/" + (r1.getEpisodes()==0?"-":r1.getEpisodes()));
        }else {
            ((TextView) findViewById(R.id.updater_static_1)).setText(r2.getMyChapter() + "/" + (r2.getChapter()==0?"-":r2.getChapter()));
            ((TextView) findViewById(R.id.updater_static_2)).setText(r2.getMyVolumes() + "/" + (r2.getVolumes()==0?"-":r2.getVolumes()));
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
        MalAPI.update(anime?r1:r2);
        if((anime?r1:r2).getMyStatus()==2){
            if((anime?r1:r2).getRewatch()){
                if(anime){
                    findViewById(R.id.updater_adder_0).setVisibility(View.VISIBLE);
                    findViewById(R.id.updater_remover_0).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.updater_adder_1).setVisibility(View.VISIBLE);
                    findViewById(R.id.updater_adder_2).setVisibility(View.VISIBLE);
                    findViewById(R.id.updater_remover_1).setVisibility(View.VISIBLE);
                    findViewById(R.id.updater_remover_2).setVisibility(View.VISIBLE);}
            }else{
                findViewById(R.id.updater_adder_0).setVisibility(View.GONE);
                findViewById(R.id.updater_remover_0).setVisibility(View.GONE);
                findViewById(R.id.updater_adder_1).setVisibility(View.GONE);
                findViewById(R.id.updater_adder_2).setVisibility(View.GONE);
                findViewById(R.id.updater_remover_1).setVisibility(View.GONE);
                findViewById(R.id.updater_remover_2).setVisibility(View.GONE);
            }
        }
        setEpisodeTexts();
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
                MalAPI.remove((anime?r1:r2).getID(),anime);
                r1=null;r2=null;
                findViewById(R.id.is_in_list).setVisibility(View.GONE);
                EntryList.reloadOwn(anime);
                loadList();
                break;
            case R.id.action_view_on_mal:
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
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




















    public static class PlaceholderFragment extends Fragment {
        public static SparseArray<View> registeredFragments = new SparseArray<>();
        static boolean ok=true;
        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt("section_number", sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            int y=getArguments().getInt("section_number");
            View master=inflater.inflate((y<3||y==7)?R.layout.void_linear_layout:R.layout.void_recycler, container, false);
            registeredFragments.put(y, master);
            Log.d("OnMALDebug","length: "+ registeredFragments.size());
            if(registeredFragments.size()==8&&ok){
                ok=false;
                View s[]=new View[8];
                for(int a=0;a<8;a++)s[a]=registeredFragments.get(a);
                new MasterInspector(EntryActivity.id,EntryActivity.anime,s,EntryActivity.mainThis);
            }

            return master;
        }
    }
/*else if(y>2&&y<7){
                master=inflater.inflate(R.layout.void_recycler, container, false);
                RecyclerView rootView = (RecyclerView) master.findViewById(R.id.recycler);
                rootView.setLayoutManager(new LinearLayoutManager(getActivity()));
                rootView.setAdapter(
                        y==3?entry.getRecommendationAdapter():
                                y==4?entry.getReviewAdapter():
                                        y==5?entry.getStaffAdapter():
                        /*6entry.getCharacterAdapter()
                );
            }*/
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        /*public SparseArray<Fragment> getRegisteredFragments() {
            return registeredFragments;
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }*/
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            titles=anime?new String[]{"General","Info","Relations","Recommendations","Reviews","Staff","Characters","Songs"}:new String[]{"General","Info","Relations","Recommendations","Reviews","Characters"};
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        String[]titles;

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}