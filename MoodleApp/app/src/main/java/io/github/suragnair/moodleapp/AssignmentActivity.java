package io.github.suragnair.moodleapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by karan on 20/2/16.
 */
public class AssignmentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String CourseTitle;
    private String AssignmentId;

    private String Title;
    private String Details;
    private String DateOfSubmission;
    private String LateDaysAllowed;
    private String TimeRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CourseTitle = getIntent().getStringExtra("coursename");
        AssignmentId = getIntent().getStringExtra("assignment_id");

        setTitle(CourseTitle);
        setContentView(R.layout.activity_assignment);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getData();

        TextView title = (TextView) findViewById(R.id.assign_title);
        TextView details = (TextView) findViewById(R.id.details);
        TextView dos = (TextView) findViewById(R.id.DOS_text);
        TextView late_days_allowed = (TextView) findViewById(R.id.late_days_text);
        TextView timeRem = (TextView) findViewById(R.id.time_rem_text);

        title.setText(Title);
        details.setText(Details);
        dos.setText(DateOfSubmission);
        late_days_allowed.setText(LateDaysAllowed);
        timeRem.setText(getTimeRem());


    }

    public void getData()
    {
        Networking.getData(6, new String[]{AssignmentId}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONObject assignment = response.getJSONObject("assignment");
                    Title = assignment.getString("name");
                    Details = assignment.getString("description");
                    DateOfSubmission = assignment.getString("deadline");
                    LateDaysAllowed = assignment.getString("late_days_allowed");

                } catch (JSONException e) {
                    Log.d("Json Exception", "Response from server wasn't JSON");
                }
            }
        });
    }

    public String getTimeRem()
    {
        String time_rem = "";
        //TODO Write function for remaining time
        return time_rem;
    }

}

