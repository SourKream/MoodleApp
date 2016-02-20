package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.app.Notification;
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
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // TODO Fragment populated from server every time
        if (((MyApplication) getActivity().getApplication()).isUserLoggedIn())
            populateNotificationsFromServer();

        notificationsListView = (ListView) view.findViewById(R.id.NotificationsListView);
        notificationsListView.setAdapter(new CustomListAdapter(this.getActivity(), notifications));
        // TODO Add OnItemClickListener to notificationsListView
        return view;
    }

    private void populateNotificationsFromServer(){
        Networking.getData(3, new String[0], new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray jsonNotificationsList = new JSONArray(response.getString("notifications"));
                    for (int i=0; i<jsonNotificationsList.length(); i++)
                        notifications.add(new Notification(jsonNotificationsList.getString(i)));
                    ((CustomListAdapter) notificationsListView.getAdapter()).notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });
    }

    public static class Notification{
        String createdAt;
        String description;
        Integer ID;
        Integer isSeen;
        Integer UserID;

        public Notification (String JsonString){
            try {
                JSONObject notification = new JSONObject(JsonString);
                ID = notification.getInt("id");
                isSeen = notification.getInt("is_seen");
                UserID = notification.getInt("user_id");
                createdAt = notification.getString("created_at");
                description = notification.getString("description");
            } catch (JSONException e) {
                Log.d("JSON Exception : ", e.getMessage());
            }
        }
    }

    class CustomListAdapter extends BaseAdapter {

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
            notificationDescription.setText(notification.description);

            return convertView;
        }
    }
}
