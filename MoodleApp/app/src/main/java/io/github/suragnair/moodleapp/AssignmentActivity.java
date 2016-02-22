package io.github.suragnair.moodleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class AssignmentActivity extends AppCompatActivity {

    private CourseAssignmentFragment.Assignment assignment;
    private TextView assignTitle;
    private TextView assignDescription;
    private TextView assignDeadline;
    private TextView assignLDA;
    private TextView assignCreateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

//        String CourseTitle = getIntent().getStringExtra("coursename");
        Integer AssignmentID = getIntent().getIntExtra("assignment_id",0);

        assignTitle = (TextView) findViewById(R.id.assign_title);
        assignDescription = (TextView) findViewById(R.id.assign_details);
        assignDeadline = (TextView) findViewById(R.id.deadline_text);
        assignCreateDate = (TextView) findViewById(R.id.created_on_text);
        assignLDA = (TextView) findViewById(R.id.LDA_text);

        getAssignmentFromServer(AssignmentID);
    }

    private void UpdateUI (){
        assignTitle.setText(assignment.Name);
        assignTitle.setTypeface(MainActivity.Garibaldi);

        assignDescription.setText(Html.fromHtml(assignment.Description));
        assignDeadline.setText(assignment.Deadline);
        assignLDA.setText(String.format("%d",assignment.LateDaysAllowed));
        assignCreateDate.setText(assignment.CreatedAt);
    }

    public void getAssignmentFromServer (int AssignmentID)
    {
        Networking.getData(6, new String[]{Integer.toString(AssignmentID)}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    assignment = new CourseAssignmentFragment.Assignment(response.getString("assignment"));
                    UpdateUI();
                } catch (JSONException e) {
                    Log.d("JSON Exception : ", e.getMessage());
                }
            }
        });
    }
}

