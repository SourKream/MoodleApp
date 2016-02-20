package io.github.suragnair.moodleapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private String[] NavigationMenuList = {"Courses","Grades","Notifications"};
    private DrawerLayout drawerLayout;
    private RelativeLayout drawerSliderLayout;
    private int currentFragment = 0;
    public static Typeface Garibaldi;
    public static Typeface MyriadPro;
    private boolean COURSES_FRAGMENT_FLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // fonts
        Garibaldi = Typeface.createFromAsset(getAssets(), "fonts/Garibaldi.ttf");
        MyriadPro = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro.ttf");

        // Setting up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerSliderLayout);
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerSliderLayout = (RelativeLayout) findViewById(R.id.left_drawer);

        // Initialising Navigation List
        ListView drawerListView = (ListView) findViewById(R.id.left_drawer_list);
        drawerListView.setAdapter(new ArrayAdapter<>(this, R.layout.navigation_list_item, NavigationMenuList));
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (drawerLayout.isDrawerOpen(drawerSliderLayout))
                    drawerLayout.closeDrawer(drawerSliderLayout);
                if (currentFragment != position) {
                    currentFragment = position;
                    changeFragment(currentFragment);
                }
            }
        });
    }

    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        if ((item.getItemId() == R.id.notificationButton)&&(currentFragment != 2)){
            currentFragment = 2;
            changeFragment(currentFragment);
            return true;
        }
            return super.onOptionsItemSelected(item);
    }

    private void changeFragment (int position){
        android.support.v4.app.Fragment fragment = null;
        switch (position) {
            case 0: fragment = new CourseListFragment();
                    break;
            case 1: fragment = new GradesFragment();
                    break;
            case 2: fragment = new NotificationsFragment();
                    break;
            default: return;
        }
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        if (drawerLayout.isDrawerOpen(drawerSliderLayout))
            drawerLayout.closeDrawer(drawerSliderLayout);
    }

    public void signOutClicked (View view){
        drawerLayout.closeDrawer(drawerSliderLayout);
        Networking.getData(1, new String[0], new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                ((MyApplication) getApplication()).setMyUser(null);
                loginUser();
            }
        });
    }

    private void loginUser(){
        MyApplication myApplication = (MyApplication) getApplication();
        if (!myApplication.isUserLoggedIn()) {
            COURSES_FRAGMENT_FLAG = true;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void onResume() {
        super.onResume();

        if (COURSES_FRAGMENT_FLAG){
            currentFragment = 0;
            changeFragment(currentFragment);
            COURSES_FRAGMENT_FLAG = false;
        }
        loginUser();
    }

}
