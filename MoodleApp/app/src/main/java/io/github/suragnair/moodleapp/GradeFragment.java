package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GradeFragment extends Fragment{

    private List<Grade> gradeList = new ArrayList<Grade>();

    public GradeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        populateGradeList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.grade_fragment, container, false);
        ListView GradeListView = (ListView) view.findViewById(R.id.GradesList);
        GradeListView.setAdapter(new CustomListAdapter(this.getActivity(), gradeList));
        return view;
    }
    public void populateGradeList()
    {
        gradeList.add(new Grade("1.", "Basic UI Learning", "5/30", "20 %", "5"));
        gradeList.add(new Grade("2.", "Moodle App", "20/20", "56 %", "20"));
    }

    public class Grade{
        public String SerialNo;
        public String ItemName;
        public String Score;
        public String Weight;
        public String Marks;

        public Grade()
        {

        }

        public Grade(String serialNo, String itemName, String score, String weight, String marks)
        {
            SerialNo = serialNo;
            ItemName = itemName;
            Score = score;
            Weight = weight;
            Marks = marks;
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
            ItemName.setText(grade.ItemName);
            SNo.setText(grade.SerialNo);
            score.setText(grade.Score);
            weight.setText(grade.Weight);
            absoluteMarks.setText(grade.Marks);

            return convertView;
        }
    }

}