package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;

public class CourseThreadsFragment extends Fragment{

    // List of threads to populate list
    private List<ThreadActivity.CourseThread> threadList = new ArrayList<>();
    private ListView threadListView;
    public String CourseName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        CourseName = bundle.getString("coursename");
        addThreads();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_threads, container, false);

        // Initialise listview by setting adapter and item click listener
        threadListView = (ListView) view.findViewById(R.id.ThreadList);
        threadListView.setAdapter(new CustomListAdapter(this.getActivity(), threadList));
        threadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Go to the specific thread activity when item is clicked
                // Thread ID passed in Intent to populate next activity with the required data
                Intent intent = new Intent(getActivity(), ThreadActivity.class);
                intent.putExtra("thread_id", threadList.get(position).ID);
                startActivity(intent);
            }
        });

        // Set item click listener to button for New Thread
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newThreadClicked(v);
            }
        });

        if (threadList.isEmpty()) view.findViewById(R.id.emptyCourseThread).setVisibility(View.VISIBLE);

        return view;
    }

    public void onResume() {
        super.onResume();

        if (CourseActivity.newThreadPosted){
            // reload threads if new thread posted
            threadList.clear();
            addThreads();
            CourseActivity.newThreadPosted = false;
        }
    }

    public void newThreadClicked (View view){
        // Activity for creating new thread in given course started
        Intent intent = new Intent(getActivity(), NewThreadActivity.class);
        intent.putExtra("courseCode",CourseName);
        intent.putExtra("activity","first");
        startActivity(intent);
    }

    public void addThreads() {
        // Get data from server
        Networking.getData(8, new String[]{CourseName}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Parse response
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonThreadList = new JSONArray(response.getString("course_threads"));

                    if (jsonThreadList.length() == 0) {
                        getActivity().findViewById(R.id.emptyCourseThread).setVisibility(View.VISIBLE);
                    } else {
                        // Add thread to thread list
                        for (int i = 0; i < jsonThreadList.length(); i++)
                            threadList.add(new ThreadActivity.CourseThread(jsonThreadList.getString(i)));
                        // Notify listview adapter to update UI
                        ((CustomListAdapter) threadListView.getAdapter()).notifyDataSetChanged();
                        getActivity().findViewById(R.id.emptyCourseThread).setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    Log.d("JSON Exception : ", e.getMessage());
                }
            }
        });
    }

    // Custom adapter to populate list view
    public class CustomListAdapter extends BaseAdapter{

        private Activity activity;
        private LayoutInflater inflater;
        private List<ThreadActivity.CourseThread> threadList;

        public CustomListAdapter(Activity activity, List<ThreadActivity.CourseThread> threadList){
            this.activity = activity;
            this.threadList = threadList;
        }

        @Override
        public int getCount() {
                    return threadList.size();
                }

        @Override
        public Object getItem(int position) {
                    return threadList.get(position);
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
                convertView = inflater.inflate(R.layout.thread_list_item, null);

            TextView threadTitle       = (TextView) convertView.findViewById(R.id.thread_title);
            TextView threadCreatedAt   = (TextView) convertView.findViewById(R.id.thread_createdAt);
            TextView threadLastUpdate  = (TextView) convertView.findViewById(R.id.thread_lastUpdate);
            TextView threadDescription = (TextView) convertView.findViewById(R.id.thread_description);
            TextView threadID         = (TextView) convertView.findViewById(R.id.thread_SNo);

            ThreadActivity.CourseThread thread = threadList.get(position);

            threadTitle.setText(thread.title);
            threadTitle.setTypeface(MainActivity.Garibaldi);
            threadID.setText(String.format("%d",position+1));
            threadDescription.setText(thread.description);
            threadCreatedAt.setText(thread.createdAt);
            threadLastUpdate.setText(thread.updatedAt);
            return convertView;
        }
    }
}