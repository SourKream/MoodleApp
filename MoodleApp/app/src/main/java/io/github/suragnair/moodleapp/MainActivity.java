package io.github.suragnair.moodleapp;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private String[] NavigationMenuList = {"Courses","Grades","Notifications"};
    private DrawerLayout drawerLayout;
    private ListView drawerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new CourseListFragment()).commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_drawer);

        drawerListView.setAdapter(new ArrayAdapter<>(this, R.layout.navigation_list_item, NavigationMenuList));
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeFragment(position);
            }
        });

        Networking.getData(0, new String[]{"cs1110200", "john"}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {}
        });
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
        }
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        drawerLayout.closeDrawer(drawerListView);
    }

}
