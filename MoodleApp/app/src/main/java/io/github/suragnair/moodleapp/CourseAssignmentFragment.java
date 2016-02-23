package io.github.suragnair.moodleapp;

import android.content.Intent;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CourseAssignmentFragment extends Fragment{

    // List of all assignments for the course
    private List<Assignment> assignmentList = new ArrayList<>();
    private ListView AssignListView =null;
    public String CourseName;

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
        View view = inflater.inflate(R.layout.fragment_course_assignment, container, false);

        // Initialise the listview by assigning adapter and setting on item click listener
        AssignListView = (ListView) view.findViewById(R.id.AssignmentList);
        AssignListView.setAdapter(new CustomListAdapter(this.getActivity(), assignmentList));
        AssignListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Tapping on assignment starts intent to go to the Assignment's activity
                // Assignment ID passed through the intent to load the required assignment
                Intent intent = new Intent(getActivity(), AssignmentActivity.class);
                intent.putExtra("assignment_id", assignmentList.get(position).ID);
                startActivity(intent);
            }
        });

        if (assignmentList.isEmpty()) view.findViewById(R.id.emptyCourseAssignment).setVisibility(View.VISIBLE);

        return view;
    }

    public void addAssignments() {
        // Load all assignments from the server
        Networking.getData(5, new String[]{CourseName}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Parse response
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonAssignmentList = new JSONArray(response.getString("assignments"));

                    if (jsonAssignmentList.length()==0) {
                        getActivity().findViewById(R.id.emptyCourseAssignment).setVisibility(View.VISIBLE);
                    } else {
                        // Add assignments to the list
                        for (int i = 0; i < jsonAssignmentList.length(); i++)
                            assignmentList.add(new Assignment(jsonAssignmentList.getString(i)));

                        // Notify ListviewAdapter to update UI
                        ((CustomListAdapter) AssignListView.getAdapter()).notifyDataSetChanged();
                        getActivity().findViewById(R.id.emptyCourseAssignment).setVisibility(View.GONE);
                    }
                } catch (JSONException e){
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });
    }

    // Class for Assignment Details
    public static class Assignment{
        String CreatedAt;
        String Deadline;
        String Description;
        Integer ID;
        Integer LateDaysAllowed;
        String Name;
        Integer CourseID;

        // Constructor automatically parses JSON and stores data in the onject
        public Assignment (String JsonString){
            try {
                JSONObject assignment = new JSONObject(JsonString);
                CreatedAt = assignment.getString("created_at");
                Deadline = assignment.getString("deadline");
                Description = assignment.getString("description");
                ID = assignment.getInt("id");
                LateDaysAllowed = assignment.getInt("late_days_allowed");
                Name = assignment.getString("name");
                CourseID = assignment.getInt("registered_course_id");
            } catch (JSONException e) {
                Log.d("JSON Exception : ", e.getMessage());
            }
        }
    }

    // List View Adapter to populate Custom Assignment List View
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

            TextView assgnName     = (TextView) convertView.findViewById(R.id.Name);
            TextView assgnDeadline = (TextView) convertView.findViewById(R.id.timeRemaining);
            TextView assgnCreated  = (TextView) convertView.findViewById(R.id.datePosted);
            TextView assgnSNo      = (TextView) convertView.findViewById(R.id.SNo);

            Assignment assignment = assignmentsList.get(position);

            assgnName.setText(assignment.Name);
            assgnName.setTypeface(MainActivity.Garibaldi);
            assgnSNo.setText(String.format("%d",position+1));
            //assgnSNo.setTypeface(MainActivity.Garibaldi);
            assgnDeadline.setText(assignment.Deadline);
            //assgnDeadline.setTypeface(MainActivity.MyriadPro);
            assgnCreated.setText(assignment.CreatedAt);
            //assgnCreated.setTypeface(MainActivity.MyriadPro);

            return convertView;
        }
    }


}