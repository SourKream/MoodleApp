package io.github.suragnair.moodleapp;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private List<Notification> notifications = new ArrayList<>();
    private ListView notificationsListView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);

        populateNotifications();

        notificationsListView = (ListView) view.findViewById(R.id.NotificationsListView);
        notificationsListView.setAdapter(new CustomListAdapter(this.getActivity(), notifications));
        return view;
    }

    private void populateNotifications(){
        Networking.getData(3, new String[0], new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonNotificationsList = new JSONArray(response.getString("notifications"));
                    for (int i=0; i<jsonNotificationsList.length(); i++){
                        JSONObject notification = jsonNotificationsList.getJSONObject(i);
                        notifications.add(new Notification(notification.getString("description")));
                    }
                    CustomListAdapter adapter = (CustomListAdapter) notificationsListView.getAdapter();
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });
    }

    public class Notification{
        public String notificationDescription;

        public Notification (String description){
            notificationDescription = description;
        }
    }

    public class CustomListAdapter extends BaseAdapter {

        private Activity activity;
        private LayoutInflater inflater;
        private List<Notification> notificationsList;

        public CustomListAdapter(Activity activity, List<Notification> list) {
            this.activity = activity;
            this.notificationsList = list;
        }

        @Override
        public int getCount() {
            return notificationsList.size();
        }

        @Override
        public Object getItem(int position) {
            return notificationsList.get(position);
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
                convertView = inflater.inflate(R.layout.notifications_list_item, null);

            TextView notificationDescription = (TextView) convertView.findViewById(R.id.notification);

            Notification notification = notificationsList.get(position);
            notificationDescription.setText(notification.notificationDescription);

            return convertView;
        }
    }
}
