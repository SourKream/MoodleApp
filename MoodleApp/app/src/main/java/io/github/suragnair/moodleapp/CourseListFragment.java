package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CourseListFragment extends Fragment {

    private List<Course> courseList = new ArrayList<Course>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        populateCourseList();
        ListView courseListView = (ListView) view.findViewById(R.id.courseList);
        courseListView.setAdapter(new CustomListAdapter(this.getActivity(), courseList));

        return view;

    }

    public void populateCourseList(){
        courseList.add(new Course("COP290","Design Practices"));
        courseList.add(new Course("ELL201","Digital Electronics"));
    }

    public class Course{
        public String CourseCode;
        public String CourseDescription;

        public Course (){

        }

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

            Course course = courseList.get(position);
            courseCode.setText(course.CourseCode);
            courseDescription.setText(course.CourseDescription);

            return convertView;
        }
    }

}
