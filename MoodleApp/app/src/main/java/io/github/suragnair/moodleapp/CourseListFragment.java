package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private List<Course> courseList = new ArrayList<>();
    private ListView courseListView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        populateCourseList();

        courseListView = (ListView) view.findViewById(R.id.courseList);
        courseListView.setAdapter(new CustomListAdapter(this.getActivity(), courseList));
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String coursename = courseList.get(position).CourseCode;
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("coursename",coursename);
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

    public static class Course{
        public String CourseCode;
        public String CourseDescription;
        String CourseName;
        int ID;
        int Credits;
        String LTP;

        // TODO Correct this class
        public Course (String code, String description){
            CourseCode = code;
            CourseDescription = description;
        }

        public Course (String code, String desc, String name, String ltp, int id, int credits){
            CourseCode = code;
            CourseDescription = desc;
            CourseName = name;
            LTP = ltp;
            Credits = credits;
            ID = id;
        }

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

            Course course = courseList.get(position);
            courseCode.setText(course.CourseCode);
            courseDescription.setText(course.CourseDescription);

            return convertView;
        }
    }

}
