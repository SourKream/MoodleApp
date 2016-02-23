package io.github.suragnair.moodleapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.suragnair.moodleapp.CourseGradesFragment.Grade;
import io.github.suragnair.moodleapp.CourseGradesFragment.CustomGradesListAdapter;
import io.github.suragnair.moodleapp.CourseListFragment.Course;

public class GradesFragment extends Fragment {

    // List of grades to populate list view
    private List<Grade> grades = new ArrayList<>();
    // List of courses corresponding to each grade
    private List<Course> courses = new ArrayList<>();
    private ListView gradesListView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Grades");

        // Fetch data from server
        if (((MyApplication) getActivity().getApplication()).isUserLoggedIn())
            populateGradesFromServer();

        // Initialise listview by setting adapter
        gradesListView = (ListView) view.findViewById(R.id.NotificationsListView);
        gradesListView.setAdapter(new CustomGradesListAdapter(this.getActivity(), grades, courses));
        return view;
    }

    private void populateGradesFromServer(){
        // Get data from server
        Networking.getData(4, new String[0], new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Parse response
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonGradesList = new JSONArray(response.getString("grades"));
                    JSONArray jsonCoursesList = new JSONArray(response.getString("courses"));
                    // Add grades info to list
                    for (int i = 0; i < jsonGradesList.length(); i++) {
                        grades.add(new Grade(jsonGradesList.getString(i)));
                        courses.add(new Course(jsonCoursesList.getString(i)));
                    }
                    // Notify listview adapter to update UI
                    ((CustomGradesListAdapter) gradesListView.getAdapter()).notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });
    }
}
