package io.github.suragnair.moodleapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    // Course code for the course that populates the activity
    public String CourseTitle;
    public static boolean newThreadPosted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // Getting course code from the Intent
        CourseTitle = getIntent().getStringExtra("coursename");
        setTitle(CourseTitle.toUpperCase() + " : Assignments");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting up the view pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                String title = CourseTitle.toUpperCase() + " : ";
                switch (position){
                    case 0: title = title + "Assignments";
                            break;
                    case 1: title = title + "Threads";
                            break;
                    case 2: title = title + "Grades";
                            break;
                }
                setTitle(title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // Setting up the tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        try {
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_insert_drive_file_white_24dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_forum_white_24dp);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_spellcheck_white_24dp);
        } catch (java.lang.NullPointerException e) {
            Log.d("Null Pointer: ",e.getMessage());
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("coursename", CourseTitle);

        Fragment assignment_fragment = new CourseAssignmentFragment();
        Fragment thread_fragment = new CourseThreadsFragment();
        Fragment grade_fragment = new CourseGradesFragment();

        assignment_fragment.setArguments(bundle);
        thread_fragment.setArguments(bundle);
        grade_fragment.setArguments(bundle);

        adapter.addFragment(assignment_fragment, "ASSIGNMENTS");
        adapter.addFragment(thread_fragment,     "THREADS");
        adapter.addFragment(grade_fragment,      "GRADES");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}