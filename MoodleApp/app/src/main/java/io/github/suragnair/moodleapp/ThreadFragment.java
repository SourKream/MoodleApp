package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThreadFragment extends Fragment{

    private List<Thread> threadList = new ArrayList<Thread>();
    private ListView threadListView;
    public String CourseName;

    public ThreadFragment() {
        // Required empty public constructor
    }

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_threads, container, false);
        threadListView = (ListView) view.findViewById(R.id.ThreadList);
        threadListView.setAdapter(new CustomListAdapter(this.getActivity(), threadList));
        return view;
    }

    public void addThreads()
    {
        Networking.getData(8, new String[]{CourseName}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonThreadList = new JSONArray(response.getString("course_threads"));

                        for (int i = 0; i < jsonThreadList.length(); i++) {
                            JSONObject thread = jsonThreadList.getJSONObject(i);
                            threadList.add(new Thread(thread.getString("title"), thread.getString("description"), thread.getString("updated_at")));
                        }

                    CustomListAdapter adapter = (CustomListAdapter) threadListView.getAdapter();
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });
    }

    public class Thread{
        public String Title;
        public String Description;
        public String UpdatedAt;

        public Thread(String title, String description, String updatedat)
        {
            Title = title;
            Description = description;
            UpdatedAt = updatedat;
        }
    }

    public class CustomListAdapter extends BaseAdapter{

                private Activity activity;
                private LayoutInflater inflater;
                private List<Thread> threadList;

                public CustomListAdapter(Activity activity, List<Thread> threadList){
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

                    TextView threadTitle = (TextView) convertView.findViewById(R.id.title);
                    TextView threadCreatedAt = (TextView) convertView.findViewById(R.id.createdAt);
                    TextView threadLastUpdate = (TextView) convertView.findViewById(R.id.lastUpdate);

                    Thread thread = threadList.get(position);
                    threadTitle.setText(thread.Title);
                    threadCreatedAt.setText(thread.Description);
                    threadLastUpdate.setText(thread.UpdatedAt);

                    return convertView;
                }
            }

}