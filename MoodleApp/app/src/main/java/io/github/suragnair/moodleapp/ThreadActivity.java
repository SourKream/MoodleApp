package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadActivity extends AppCompatActivity {

    private CourseThread courseThread = null;
    private ListView commentsListView = null;
    private TextView threadTitle;
    private TextView threadCreatorName;
    private boolean firstTime;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        firstTime = true;
        threadTitle = (TextView) findViewById(R.id.threadTitle);
        threadCreatorName = (TextView) findViewById(R.id.threadUserName);

        // load thread ID from Intent
        int threadID = getIntent().getIntExtra("thread_id",0);
        loadThreadData(threadID);
    }

    private void loadThreadData (int ID){
        // Get data from server
        Networking.getData(9, new String[]{Integer.toString(ID)}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Parse response and create new thraed object
                    JSONObject response = new JSONObject(result);
                    String jsonThread = response.getString("thread");
                    String jsonCourse = response.getString("course");
                    String jsonComments = response.getString("comments");
                    String jsonCommentUsers = response.getString("comment_users");
                    courseThread = new CourseThread(jsonThread, jsonCourse, jsonComments, jsonCommentUsers);

                    // Send extra request to get thread creator user's name
                    Networking.getData(12, new String[]{Integer.toString(courseThread.userID)}, new Networking.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject response = new JSONObject(result);
                                courseThread.setThreadCreator(response.getString("user"));
                                String fullName = courseThread.threadCreator.firstName + " " + courseThread.threadCreator.lastName;
                                threadCreatorName.setText(fullName);
                            } catch (JSONException e) {
                                Log.d("JSON Exception : ", e.getMessage());
                            }
                        }
                    });

                    // Initialise list view
                    initialiseListView(courseThread.description);
                    threadTitle.setText(courseThread.title);
                    threadTitle.setTypeface(MainActivity.Garibaldi);
                    updateComments();
                    timer.scheduleAtFixedRate(new UpdateCommentsTask(), 10000, 10000);
                } catch (JSONException e) {
                    Log.d("JSON Exception : ", e.getMessage());
                }
            }
        });
    }

    private void initialiseListView(String threadDescription){
        commentsListView = (ListView) findViewById(R.id.commentsListView);
        commentsListView.setAdapter(new CustomCommentListAdapter(this, courseThread.comments, courseThread.commentUsers, threadDescription));
    }

    private void updateComments(){
        CustomCommentListAdapter adapter = (CustomCommentListAdapter) commentsListView.getAdapter();
        adapter.notifyDataSetChanged();
        if (!firstTime)
            commentsListView.setSelection(commentsListView.getAdapter().getCount() - 1);
        else
            firstTime = false;
    }

    private class UpdateCommentsTask extends TimerTask{
        @Override
        public void run(){
            Log.d("REFRESH", "Fetching new comments now... ");
            firstTime = true;
            reloadCommentsFromServer();
        }
    }

    private void reloadCommentsFromServer(){
        // For periodic refresh of comments
        Networking.getData(9, new String[]{Integer.toString(courseThread.ID)}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    String jsonComments = response.getString("comments");
                    String jsonCommentUsers = response.getString("comment_users");
                    courseThread.updateComments(jsonComments, jsonCommentUsers);

                    updateComments();
                } catch (JSONException e) {
                    Log.d("JSON Exception : ", e.getMessage());
                }
            }
        });
    }

    public void postComment (View view){
        final EditText commentEditText = (EditText) findViewById(R.id.newComment);
        String comment = commentEditText.getText().toString();
        comment = comment.replace(" ","%20");

        if (comment.isEmpty()) return;

        // Post new comment to server
        Networking.getData(11, new String[]{Integer.toString(courseThread.ID), comment}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                commentEditText.setText("");
                reloadCommentsFromServer();
            }
        });
    }

    // Class to store details of a Thread
    public static class CourseThread{
        List<User> commentUsers;
        List<Comment> comments;
        CourseListFragment.Course course;

        String title;
        String description;
        int userID;
        int ID;
        String updatedAt;
        String createdAt;

        User threadCreator;

        // Constructor parses JSON string and stores data in object
        public CourseThread (String jsonThread, String jsonCourse, String jsonComments, String jsonCommentUsers){
            try {
                JSONObject thread = new JSONObject(jsonThread);
                title = thread.getString("title");
                description = thread.getString("description");
                userID = thread.getInt("user_id");
                ID = thread.getInt("id");
                updatedAt = thread.getString("updated_at");
                createdAt = thread.getString("created_at");
                comments = new ArrayList<>();
                commentUsers = new ArrayList<>();

                course = new CourseListFragment.Course(jsonCourse);
                JSONArray jsonCommentsArray = new JSONArray(jsonComments);
                for (int i=jsonCommentsArray.length()-1; i>=0; i--)
                    comments.add(new Comment(jsonCommentsArray.getString(i)));
                JSONArray jsonCommentUsersArray = new JSONArray(jsonCommentUsers);
                for (int i=jsonCommentsArray.length()-1; i>=0; i--)
                    commentUsers.add(new User(jsonCommentUsersArray.getString(i)));

            } catch (JSONException e) {
                Log.d("JSON Exception : ", e.getMessage());
            }
        }

        public CourseThread (String jsonThread){
            try {
                JSONObject thread = new JSONObject(jsonThread);
                title = thread.getString("title");
                description = thread.getString("description");
                userID = thread.getInt("user_id");
                ID = thread.getInt("id");
                updatedAt = thread.getString("updated_at");
                createdAt = thread.getString("created_at");
                comments = new ArrayList<>();
                commentUsers = new ArrayList<>();
            } catch (JSONException e) {
                Log.d("JSON Exception : ", e.getMessage());
            }
        }

        public void setThreadCreator (String jsonString){
            threadCreator = new User(jsonString);
        }

        public void updateComments (String jsonComments, String jsonCommentUsers){
            comments.clear();
            commentUsers.clear();
            try {
                JSONArray jsonCommentsArray = new JSONArray(jsonComments);
                for (int i=jsonCommentsArray.length()-1; i>=0; i--)
                    comments.add(new Comment(jsonCommentsArray.getString(i)));
                JSONArray jsonCommentUsersArray = new JSONArray(jsonCommentUsers);
                for (int i=jsonCommentsArray.length()-1; i>=0; i--)
                    commentUsers.add(new User(jsonCommentUsersArray.getString(i)));
            } catch (JSONException e) {
                Log.d("JSON Exception : ", e.getMessage());
            }
        }
    }

    // Class to store details of a comment
    public static class Comment{
        String createdAt;
        String Description;
        int ID;
        int ThreadID;
        int UserID;

        // Constructor parses JSON string and stores data in object
        public Comment (String JsonString){
            try {
                JSONObject comment = new JSONObject(JsonString);
                ID = comment.getInt("id");
                ThreadID = comment.getInt("thread_id");
                UserID = comment.getInt("user_id");
                createdAt = comment.getString("created_at");
                Description = comment.getString("description");
            } catch (JSONException e) {
                Log.d("JSON Exception : ", e.getMessage());
            }
        }
    }

    // Custom adapter to populate list view
    public static class CustomCommentListAdapter extends BaseAdapter {

        private Activity activity;
        private LayoutInflater inflater;
        private String threadDescription;
        private List<Comment> commentList;
        private List<User> commentUserList;

        public CustomCommentListAdapter(Activity activity, List<Comment> comment_List, List<User> comment_user_list, String thread_description){
            this.activity = activity;
            this.commentList = comment_List;
            this.commentUserList = comment_user_list;
            this.threadDescription = thread_description;
        }

        @Override
        public int getCount() {
            return commentList.size()+1;
        }

        @Override
        public Object getItem(int position) {
            if (position==0) return threadDescription;
            else return commentList.get(position-1);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (inflater == null)
                inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (position == 0) {
                    convertView = inflater.inflate(R.layout.thread_comment_list_header, null);
                    TextView threadViewDescription = (TextView) convertView.findViewById(R.id.threadHeaderDescription);
                    threadViewDescription.setText(threadDescription);
                }
                else {
                    convertView = inflater.inflate(R.layout.thread_comment_list_item, null);
                    TextView commentUsername = (TextView) convertView.findViewById(R.id.commentUsername);
                    TextView commentDescription = (TextView) convertView.findViewById(R.id.comment);

                    Comment comment = commentList.get(commentList.size() - position );
                    User user = commentUserList.get(commentUserList.size() - position );
                    commentDescription.setText(comment.Description);
                    commentUsername.setText(String.format("%s %s",user.firstName,user.lastName));
                }
            return convertView;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
