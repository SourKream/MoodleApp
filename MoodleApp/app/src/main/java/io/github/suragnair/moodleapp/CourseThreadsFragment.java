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

    private List<ThreadActivity.CourseThread> threadList = new ArrayList<ThreadActivity.CourseThread>();
    private ListView threadListView;
    public String CourseName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        CourseName = bundle.getString("coursename");
        addThreads();
        sortThreads();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_threads, container, false);
        threadListView = (ListView) view.findViewById(R.id.ThreadList);
        threadListView.setAdapter(new CustomListAdapter(this.getActivity(), threadList));
        threadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ThreadActivity.class);
                intent.putExtra("thread_id", threadList.get(position).ID);
                startActivity(intent);
            }
        });
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
            // reload threads
            threadList.clear();
            addThreads();
            sortThreads();
            CourseActivity.newThreadPosted = false;
        }
    }

    public void newThreadClicked (View view){
        Intent intent = new Intent(getActivity(), NewThreadActivity.class);
        intent.putExtra("courseCode",CourseName);
        intent.putExtra("activity","first");
        startActivity(intent);
    }

    public void addThreads()
    {
        Networking.getData(8, new String[]{CourseName}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonThreadList = new JSONArray(response.getString("course_threads"));

                    if (jsonThreadList.length()==0)
                        getActivity().findViewById(R.id.emptyCourseThread).setVisibility(View.VISIBLE);

                    else {
                        for (int i = 0; i < jsonThreadList.length(); i++)
                            threadList.add(new ThreadActivity.CourseThread(jsonThreadList.getString(i)));
                        CustomListAdapter adapter = (CustomListAdapter) threadListView.getAdapter();
                        adapter.notifyDataSetChanged();
                        getActivity().findViewById(R.id.emptyCourseThread).setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    Log.d("JSON Exception : ", e.getMessage());
                }
            }
        });
    }

    //Sorting threads on the basis of recently updated one

    public void sortThreads()
    {
        Collections.sort(threadList, new Comparator<ThreadActivity.CourseThread>() {
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            @Override
            public int compare(ThreadActivity.CourseThread o1, ThreadActivity.CourseThread o2) {
                try {
                    return f.parse(o2.updatedAt).compareTo(f.parse(o1.updatedAt));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

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

            threadID.setText(Integer.toString(position + 1));
            threadDescription.setText(thread.description);
            threadCreatedAt.setText(thread.createdAt);
            threadLastUpdate.setText(thread.updatedAt);

            return convertView;
        }
    }
}