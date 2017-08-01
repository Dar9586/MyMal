package com.dar.mymal;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.adapters.RecommendationAdapter;
import com.dar.mymal.adapters.ReviewAdapter;
import com.dar.mymal.adapters.StaffAdapter;
import com.dar.mymal.entries.Characters;
import com.dar.mymal.entries.IDInspector;
import com.dar.mymal.entries.recommendation.Recommendations;
import com.dar.mymal.entries.review.Reviews;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.tuple.Tuple3;

import java.util.List;

public class EntryViewer extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    String title;
    int id;
    boolean anime;
    private static LinearLayoutManager llm;
    public static IDInspector entry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_viewer);
        title=getIntent().getStringExtra("ENTRY_TITLE");
        id=getIntent().getIntExtra("ENTRY_ID",-1);
        anime=getIntent().getBooleanExtra("ENTRY_ISANIME",true);
        llm=new LinearLayoutManager(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),entry,this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entry_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        static IDInspector entry;
        static Context cont;
        public static PlaceholderFragment newInstance(int sectionNumber,IDInspector mentry,Context mcont) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            entry=mentry;
            cont=mcont;
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            LinearLayout masterView=null;
            int y=getArguments().getInt(ARG_SECTION_NUMBER);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            if(y==1){
                masterView=(LinearLayout)inflater.inflate(R.layout.void_linear_layout, container, false);
                LinearLayout rootView =(LinearLayout)masterView.findViewById(R.id.entry_infos);
                Tuple2<String,List<Tuple2<String,String>>>[] infos=entry.getInfo();
                for(int a=0;a<infos.length;a++){
                    View v=inflater.inflate(R.layout.entry_info_title,null);
                    ((TextView)v.findViewById(R.id.entry_info_header)).setText(infos[a].getA());
                    rootView.addView(v);
                    for(int b=0;b<infos[a].getB().size();b++){
                        View v1=inflater.inflate(R.layout.entry_info,null);
                        ((TextView)v1.findViewById(R.id.name)) .setText(infos[a].getB().get(b).getA());
                        ((TextView)v1.findViewById(R.id.value)).setText(infos[a].getB().get(b).getB());
                        rootView.addView(v1);
                    }
                }
            }
            else if(y==2){
                masterView=(LinearLayout)inflater.inflate(R.layout.void_linear_layout, container, false);
                LinearLayout rootView =(LinearLayout)masterView.findViewById(R.id.entry_infos);
                final List<Tuple2<String,List<Tuple3<Integer,Boolean,String>>>> rela=entry.getRel();
                for(int a=0;a<rela.size();a++){
                    View v=inflater.inflate(R.layout.entry_info_title,null);
                    ((TextView)v.findViewById(R.id.entry_info_header)).setText(rela.get(a).getA());
                    rootView.addView(v);
                    for(int b=0;b<rela.get(a).getB().size();b++){
                        View v1=inflater.inflate(R.layout.basic_relation,null);
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
                        rootView.addView(v1);
                    }
                }
            }
            else if(y==3||y==4){
                boolean useLessData=false;
                masterView=(LinearLayout)inflater.inflate(R.layout.void_recycler, container, false);
                RecyclerView rootView = (RecyclerView) masterView.findViewById(R.id.recycler);

                rootView.setLayoutManager(new LinearLayoutManager(getActivity()));
                rootView.setAdapter(
                        y==4?
                                new ReviewAdapter(cont,new Reviews(entry.getId(),entry.isAnime()),useLessData,ListLoader.getCacheFile()):
                                new RecommendationAdapter(cont,new Recommendations(entry.getId(),entry.isAnime()),useLessData,ListLoader.getCacheFile())
                );
            }
            else if(y==5||y==6){
                Characters o=new Characters(entry.getId(),entry.isAnime());
                boolean useLessData=false;
                masterView=(LinearLayout)inflater.inflate(R.layout.void_recycler, container, false);
                RecyclerView rootView = (RecyclerView) masterView.findViewById(R.id.recycler);
                rootView.setLayoutManager(new LinearLayoutManager(getActivity()));
                rootView.setAdapter(
                        y==5?
                                new StaffAdapter(useLessData,cont,ListLoader.getCacheFile(),o.getStaffList()):
                                new StaffAdapter(useLessData,cont,ListLoader.getCacheFile(),o.getCharacterList())
                );
            }
            return masterView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        IDInspector entry;
        Context cont;
        public SectionsPagerAdapter(FragmentManager fm,IDInspector entry,Context cont) {
            super(fm);
            this.entry=entry;
            this.cont=cont;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position,entry,cont);
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "General";
                case 1:
                    return "Info";
                case 2:
                    return "Relations";
                case 3:
                    return "Recommendations";
                case 4:
                    return "Reviews";
                case 5:
                    return "Staff";
                case 6:
                    return "Characters";
            }
            return null;
        }
    }
}
