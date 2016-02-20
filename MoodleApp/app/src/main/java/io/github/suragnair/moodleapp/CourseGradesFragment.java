package io.github.suragnair.moodleapp;

import android.app.Activity;
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

public class CourseGradesFragment extends Fragment{

    private List<Grade> gradeList = new ArrayList<Grade>();
    private ListView GradeListView = null;
    public String CourseName;

    public CourseGradesFragment() {
        // Required empty public constructor
    }

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
        GradeListView.setAdapter(new CustomListAdapter(this.getActivity(), gradeList));
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

                        for (int i = 0; i < jsonGradeList.length(); i++) {
                            JSONObject grade = jsonGradeList.getJSONObject(i);
                            gradeList.add(new Grade(String.valueOf(i + 1), grade.getString("name"),
                                    grade.getString("score"), grade.getString("out_of"),
                                    grade.getString("weightage")));
                        }

                    CustomListAdapter adapter = (CustomListAdapter) GradeListView.getAdapter();
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });

    }

    public class Grade{
        public String SerialNo;
        public String ItemName;
        public String Score;
        public String OutOf;
        public String Weight;

        public Grade(String serialNo, String itemName, String score, String outof, String weight)
        {
            SerialNo = serialNo;
            ItemName = itemName;
            Score = score;
            OutOf = outof;
            Weight = weight;
        }
    }

    public class CustomListAdapter extends BaseAdapter {

        private Activity activity;
        private LayoutInflater inflater;
        private List<Grade> gradesList;

        public CustomListAdapter(Activity activity, List<Grade> gradesList){
            this.activity = activity;
            this.gradesList = gradesList;
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

            TextView ItemName = (TextView) convertView.findViewById(R.id.GradeItem);
            TextView SNo = (TextView) convertView.findViewById(R.id.S_No_);
            TextView score = (TextView) convertView.findViewById(R.id.score);
            TextView weight = (TextView) convertView.findViewById(R.id.weight);
            TextView absoluteMarks = (TextView) convertView.findViewById(R.id.marks);
            Grade grade = gradesList.get(position);

            String scoreText = grade.Score + " out of " + grade.OutOf;
            int sc = Integer.valueOf(grade.Score);
            int of = Integer.valueOf(grade.OutOf);
            int wt = Integer.valueOf(grade.Weight);
            int mks = (sc/of)*wt;
            String Marks = String.valueOf(mks);

            ItemName.setText(grade.ItemName);
            SNo.setText(grade.SerialNo);
            score.setText(scoreText);
            weight.setText(grade.Weight);
            absoluteMarks.setText(Marks);

            return convertView;
        }
    }

}