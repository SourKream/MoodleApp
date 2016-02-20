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

//TODO: add colours to styles and use from there

public class CourseListFragment extends Fragment {

    private List<Course> courseList = new ArrayList<>();
    private ListView courseListView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        // TODO Fragment populated from server every time
        if (((MyApplication) getActivity().getApplication()).isUserLoggedIn())
            populateCourseListFromServer();
        
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Courses");

        courseListView = (ListView) view.findViewById(R.id.courseList);
        courseListView.setAdapter(new CustomListAdapter(this.getActivity(), courseList));
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("coursename",courseList.get(position).CourseCode);
                startActivity(intent);
            }
        });
        return view;
    }

    public void populateCourseListFromServer(){
        Networking.getData(2, new String[0], new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonCourseList = new JSONArray(response.getString("courses"));
                    for (int i=0; i<jsonCourseList.length(); i++)
                        courseList.add(new Course(jsonCourseList.getString(i)));
                    ((CustomListAdapter) courseListView.getAdapter()).notifyDataSetChanged();
                } catch (JSONException e){
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });
    }

    public static class Course{
        int ID;
        int Credits;
        String CourseCode;
        String CourseName;
        String CourseDescription;
        String LTP;

/*        public Course (String code, String desc, String name, String ltp, int id, int credits){
            CourseCode = code;
            CourseDescription = desc;
            CourseName = name;
            LTP = ltp;
            Credits = credits;
            ID = id;
        } */

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
            TextView courseDescription = (TextView) convertView.findViewById(R.id.courseDescription);
            TextView LTP = (TextView) convertView.findViewById(R.id.ltp);
            Course course = courseList.get(position);

            courseCode.setText(course.CourseCode.toUpperCase());
            courseCode.setTypeface(MainActivity.MyriadPro);

            courseDescription.setText(course.CourseDescription);
            courseDescription.setTypeface(MainActivity.Garibaldi);

            LTP.setText("(2-0-2)"); //TODO: link here pls sk
            LTP.setTypeface(MainActivity.MyriadPro);

            return convertView;
        }
    }
}
