package io.github.suragnair.moodleapp;

import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AssignmentFragment extends Fragment{

    private List<Assignment> assignmentList = new ArrayList<Assignment>();
    private ListView AssgnListView =null;
    public String CourseName;

    public AssignmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        CourseName = bundle.getString("coursename");
        addAssignments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.assignment_fragment, container, false);


        AssgnListView = (ListView) view.findViewById(R.id.AssignmentList);
        AssgnListView.setAdapter(new CustomListAdapter(this.getActivity(), assignmentList));
        return view;
    }

    public void addAssignments()
    {
        Networking.getData(5, new String[]{CourseName}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonAssignmentList = new JSONArray(response.getString("assignments"));
                    int length = jsonAssignmentList.length();
                    if(length>0) {
                        for (int i = 0; i < length; i++) {
                            JSONObject assignment = jsonAssignmentList.getJSONObject(i);
                            assignmentList.add(new Assignment(String.valueOf(i + 1), assignment.getString("name"), assignment.getString("deadline")));
                        }
                    }
                    CustomListAdapter adapter = (CustomListAdapter) AssgnListView.getAdapter();
                    adapter.notifyDataSetChanged();
                } catch (JSONException e){
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });

    }

    public class Assignment{
        public String Name;
        public String SerialNo;
        public String TimeRemaining;

        public Assignment(String serialNo, String name, String timeRemaining)
        {
            Name = name;
            SerialNo = serialNo;
            TimeRemaining = timeRemaining;
        }
    }

    public class CustomListAdapter extends BaseAdapter {

        private Activity activity;
        private LayoutInflater inflater;
        private List<Assignment> assignmentsList;

        public CustomListAdapter(Activity activity, List<Assignment> assignmentsList){
            this.activity = activity;
            this.assignmentsList = assignmentsList;
        }

        @Override
        public int getCount() {
            return assignmentsList.size();
        }

        @Override
        public Object getItem(int position) {
            return assignmentsList.get(position);
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
                convertView = inflater.inflate(R.layout.assignment_list_item, null);

            TextView assgnName = (TextView) convertView.findViewById(R.id.Name);
            TextView assgnSNo = (TextView) convertView.findViewById(R.id.S_No_);
            TextView assgnTime = (TextView) convertView.findViewById(R.id.timeRemainig);

            Assignment assignment = assignmentsList.get(position);
            assgnName.setText(assignment.Name);
            assgnSNo.setText(assignment.SerialNo);
            assgnTime.setText(assignment.TimeRemaining);

            return convertView;
        }
    }


}