package io.github.suragnair.moodleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import io.github.suragnair.moodleapp.CourseAssignmentFragment.Assignment;

public class AssignmentActivity extends AppCompatActivity {

    // Assignment object that holds the Activity's data
    private Assignment assignment;

    // Textviews in the activity view
    private TextView assignTitle;
    private TextView assignDescription;
    private TextView assignDeadline;
    private TextView assignLDA;
    private TextView assignCreateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        // Assignment ID of the assignment to be displayed retreived from Intent
        Integer AssignmentID = getIntent().getIntExtra("assignment_id",0);

        // Textviews initialised
        assignTitle = (TextView) findViewById(R.id.assign_title);
        assignDescription = (TextView) findViewById(R.id.assign_details);
        assignDeadline = (TextView) findViewById(R.id.deadline_text);
        assignCreateDate = (TextView) findViewById(R.id.created_on_text);
        assignLDA = (TextView) findViewById(R.id.LDA_text);

        // Assignment loaded from server
        getAssignmentFromServer(AssignmentID);
    }

    public void getAssignmentFromServer (int AssignmentID) {
        // Send request to server
        Networking.getData(6, new String[]{Integer.toString(AssignmentID)}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Parsing response
                    JSONObject response = new JSONObject(result);
                    assignment = new CourseAssignmentFragment.Assignment(response.getString("assignment"));

                    // Update UI once data is parsed and loaded
                    UpdateUI();
                } catch (JSONException e) {
                    Log.d("JSON Exception : ", e.getMessage());
                }
            }
        });
    }

    private void UpdateUI (){
        assignTitle.setText(assignment.Name);
        assignTitle.setTypeface(MainActivity.Garibaldi);

        assignDescription.setText(Html.fromHtml(assignment.Description));
        assignDeadline.setText(assignment.Deadline);
        assignLDA.setText(String.format("%d",assignment.LateDaysAllowed));
        assignCreateDate.setText(assignment.CreatedAt);
    }
}

