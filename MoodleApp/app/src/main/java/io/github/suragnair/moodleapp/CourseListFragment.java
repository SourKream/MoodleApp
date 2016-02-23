package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseListFragment extends Fragment {

    // List of courses to populate fragment
    private List<Course> courseList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Courses");

        courseList = ((MyApplication) getActivity().getApplication()).MyCourses;
        if (((MyApplication) getActivity().getApplication()).isUserLoggedIn() && (courseList.isEmpty())) {
            populateCourseListFromServer();
            courseList = ((MyApplication) getActivity().getApplication()).MyCourses;
        }

        // Initialise listview by setting adapter and item click listener
        ListView courseListView = (ListView) view.findViewById(R.id.courseList);
        courseListView.setAdapter(new CustomListAdapter(this.getActivity(), courseList));
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Go to the specific course activity when the course item is clicked
                // Pass coursecode in Intent to populate next activity with the required data
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("coursename", courseList.get(position).CourseCode);
                startActivity(intent);
            }
        });
        return view;
    }

    public void populateCourseListFromServer(){
        // Get data from server
        Networking.getData(2, new String[0], new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Parse response
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonCourseList = new JSONArray(response.getString("courses"));
                    // Add courses to course list
                    for (int i=0; i<jsonCourseList.length(); i++)
                        ((MyApplication) getActivity().getApplication()).MyCourses.add(new Course(jsonCourseList.getString(i)));
                } catch (JSONException e){
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });
    }

    // Class to store details of a course
    public static class Course{
        int ID;
        int Credits;
        String CourseCode;
        String CourseName;
        String CourseDescription;
        String LTP;

        // Constructor parses JSON string and stores data in object
        public Course (String JsonString){
            try {
                JSONObject course = new JSONObject(JsonString);
                ID = course.getInt("id");
                Credits = course.getInt("credits");
                CourseName = course.getString("name");
                CourseCode = course.getString("code");
                CourseDescription = course.getString("description");
                LTP = course.getString("l_t_p");
            } catch (JSONException e) {
                Log.d("JSON Exception : ", e.getMessage());
            }
        }
    }

    // Custom adapter to populate list view
    class CustomListAdapter extends BaseAdapter{
        private Activity activity;
        private LayoutInflater inflater;
        private List<Course> courseList;

        public CustomListAdapter(Activity activity, List<Course> courseList){
            this.activity = activity;
            this.courseList = courseList;
        }

        @Override
        public int getCount() {
            return courseList.size();
        }

        @Override
        public Object getItem(int position) {
            return courseList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (inflater == null)
                inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.courses_list_item, null);

            TextView courseCode = (TextView) convertView.findViewById(R.id.courseCode);
            TextView courseName = (TextView) convertView.findViewById(R.id.courseName);
            TextView LTP = (TextView) convertView.findViewById(R.id.ltp);

            Course course = courseList.get(position);

            courseCode.setText(course.CourseCode.toUpperCase());
            courseCode.setTypeface(MainActivity.MyriadPro);
            courseName.setText(course.CourseName);
            courseName.setTypeface(MainActivity.Garibaldi);
            LTP.setText(String.format("(%s)",course.LTP));
            LTP.setTypeface(MainActivity.MyriadPro);

            return convertView;
        }
    }
}
