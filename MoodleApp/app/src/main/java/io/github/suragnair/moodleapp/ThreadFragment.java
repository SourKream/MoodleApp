package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ThreadFragment extends Fragment{

    private List<Thread> threadList = new ArrayList<Thread>();

    public ThreadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addThreads();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.thread_fragment, container, false);
        ListView threadListView = (ListView) view.findViewById(R.id.ThreadList);
        threadListView.setAdapter(new CustomListAdapter(this.getActivity(), threadList));
        return view;
    }

    public void addThreads()
    {
        threadList.add(new Thread("Minor-1", "12:01 am", "updated 1 hour ago"));
        threadList.add(new Thread("Minor-2", "12:01 am", "updated 1 hour ago"));
        threadList.add(new Thread("Minor-3", "12:01 am", "updated 1 hour ago"));
        threadList.add(new Thread("Minor-3", "12:01 am", "updated 1 hour ago"));
        threadList.add(new Thread("Minor-3", "12:01 am", "updated 1 hour ago"));
        threadList.add(new Thread("Minor-3", "12:01 am", "updated 1 hour ago"));
        threadList.add(new Thread("Minor-3", "12:01 am", "updated 1 hour ago"));
        threadList.add(new Thread("Minor-3", "12:01 am", "updated 1 hour ago"));
        threadList.add(new Thread("Minor-3", "12:01 am", "updated 1 hour ago"));
        threadList.add(new Thread("Minor-3", "12:01 am", "updated 1 hour ago"));

    }

    public class Thread{
        public String Title;
        public String LastUpdated;
        public String CreatedAt;

        public Thread()
        {

        }

        public Thread(String title, String createdAt, String lastUpdated)
        {
            Title = title;
            LastUpdated = lastUpdated;
            CreatedAt = createdAt;
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
                    threadCreatedAt.setText(thread.CreatedAt);
                    threadLastUpdate.setText(thread.LastUpdated);

                    return convertView;
                }
            }

}