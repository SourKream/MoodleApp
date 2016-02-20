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
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Courses");
        populateCourseList();


        courseListView = (ListView) view.findViewById(R.id.courseList);
        courseListView.setAdapter(new CustomListAdapter(this.getActivity(), courseList));
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String coursename = courseList.get(position).CourseCode;
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("coursename", coursename);
                startActivity(intent);
            }
        });

        return view;
    }

    public void populateCourseList(){
        Networking.getData(2, new String[0], new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonCourseList = new JSONArray(response.getString("courses"));
                    for (int i=0; i<jsonCourseList.length(); i++){
                        JSONObject course = jsonCourseList.getJSONObject(i);
                        courseList.add(new Course(course.getString("code").toUpperCase(),course.getString("name")));
                    }
                    CustomListAdapter adapter = (CustomListAdapter) courseListView.getAdapter();
                    adapter.notifyDataSetChanged();
                } catch (JSONException e){
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });
    }

    public class Course{
        public String CourseCode;
        public String CourseDescription;

        public Course (String code, String description){
            CourseCode = code;
            CourseDescription = description;
        }
    }

    public class CustomListAdapter extends BaseAdapter{

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

            courseCode.setText(course.CourseCode);
            courseCode.setTypeface(MainActivity.MyriadPro);

            courseDescription.setText(course.CourseDescription);
            courseDescription.setTypeface(MainActivity.Garibaldi);

            LTP.setText("(2-0-2)"); //TODO: link here pls sk
            LTP.setTypeface(MainActivity.MyriadPro);

            return convertView;
        }
    }

}
