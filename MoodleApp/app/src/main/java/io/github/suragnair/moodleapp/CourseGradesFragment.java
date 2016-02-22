package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import io.github.suragnair.moodleapp.CourseListFragment.Course;

public class CourseGradesFragment extends Fragment{

    private List<Grade> gradeList = new ArrayList<Grade>();
    private ListView GradeListView = null;
    public String CourseName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        CourseName = bundle.getString("coursename");
        populateGradeList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_grade, container, false);
        GradeListView = (ListView) view.findViewById(R.id.GradesList);
        GradeListView.setAdapter(new CustomGradesListAdapter(this.getActivity(), gradeList));

        if (gradeList.isEmpty()) view.findViewById(R.id.emptyCourseGrade).setVisibility(View.VISIBLE);

        return view;
    }
    public void populateGradeList()
    {
        Networking.getData(7, new String[]{CourseName}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonGradeList = new JSONArray(response.getString("grades"));

                    if (jsonGradeList.length()==0)
                        getActivity().findViewById(R.id.emptyCourseGrade).setVisibility(View.VISIBLE);

                    else {
                        Integer totalWeight = 0;
                        Double totalMarks = 0.0;
                        for (int i = 0; i < jsonGradeList.length(); i++){
                            Grade grade = new Grade(jsonGradeList.getString(i));
                            gradeList.add(grade);
                            totalWeight = totalWeight + grade.Weightage;
                            totalMarks = totalMarks + ((((double) grade.Score)/grade.OutOf) * grade.Weightage);
                        }

                        gradeList.add(new Grade(totalWeight, totalMarks)); // For Total
                        CustomGradesListAdapter adapter = (CustomGradesListAdapter) GradeListView.getAdapter();
                        adapter.notifyDataSetChanged();
                        getActivity().findViewById(R.id.emptyCourseGrade).setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });

    }

    public static class Grade{
        Integer ID;
        String Name = "TOTAL";
        Integer OutOf = 0;
        Integer CourseID;
        Integer Score = 0;
        Integer UserID;
        Integer Weightage;
        Double Total;

        public Grade(Integer weight, Double total){
            Weightage = weight;
            Total = total;
        }

        public Grade (String JsonString){
            try {
                JSONObject grade = new JSONObject(JsonString);
                ID = grade.getInt("id");
                Name = grade.getString("name");
                OutOf = grade.getInt("out_of");
                CourseID = grade.getInt("registered_course_id");
                Score = grade.getInt("score");
                UserID = grade.getInt("user_id");
                Weightage = grade.getInt("weightage");
                Total = ((((double) Score)/OutOf) * Weightage);
            } catch (JSONException e) {
                Log.d("JSON Exception : ", e.getMessage());
            }
        }
    }

    public static class CustomGradesListAdapter extends BaseAdapter {

        private Activity activity;
        private LayoutInflater inflater;
        private List<Grade> gradesList;
        private List<Course> courseList;
        boolean showTotal;

        public CustomGradesListAdapter(Activity activity, List<Grade> gradesList){
            this.activity = activity;
            this.gradesList = gradesList;
            this.courseList = null;
            showTotal = true;
        }

        public CustomGradesListAdapter(Activity activity, List<Grade> gradesList, List<Course> courseList){
            this.activity = activity;
            this.gradesList = gradesList;
            this.courseList = courseList;
            showTotal = false;
        }

        @Override
        public int getCount() {
            return gradesList.size();
        }

        @Override
        public Object getItem(int position) {
            return gradesList.get(position);
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
                convertView = inflater.inflate(R.layout.grade_item_layout, null);

            if (courseList == null)
                convertView.findViewById(R.id.CourseLabel).setVisibility(View.GONE);
            else {
                convertView.findViewById(R.id.CourseLabel).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.courseCodeLabel)).setText(courseList.get(position).CourseCode.toUpperCase());
            }

            TextView Name = (TextView) convertView.findViewById(R.id.courseGradeTitle);
            TextView SerialNo = (TextView) convertView.findViewById(R.id.courseGradeSNo);
            TextView Score = (TextView) convertView.findViewById(R.id.courseGradeScore);
            TextView Weight = (TextView) convertView.findViewById(R.id.courseGradeWeight);
            TextView Marks = (TextView) convertView.findViewById(R.id.courseGradeMarks);

            Grade grade = gradesList.get(position);

            convertView.findViewById(R.id.ScoreLabel).setVisibility(View.VISIBLE);
            SerialNo.setVisibility(View.VISIBLE);
            Name.setText(grade.Name);
            Name.setTypeface(MainActivity.Garibaldi);
            SerialNo.setText(String.format("%d", position + 1));
            Score.setText(String.format("%d/%d", grade.Score, grade.OutOf));
            Weight.setText(String.format("%d", grade.Weightage));
            Marks.setText(String.format("%.1f", grade.Total));
            if((position == (getCount() - 1))&&(showTotal)) {
                convertView.findViewById(R.id.ScoreLabel).setVisibility(View.INVISIBLE);
                SerialNo.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

}