package io.github.suragnair.moodleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

//TODO: sk- am able to post a new thread without title
public class NewThreadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_thread);
        TextView title = (TextView) findViewById(R.id.newThreadHeader);
        title.setTypeface(MainActivity.MyriadPro);
    }

    public void postThreadClicked(View view){

        EditText title = (EditText) findViewById(R.id.newThreadTitle);
        EditText description = (EditText) findViewById(R.id.newThreadDescription);

        String threadTitle = title.getText().toString();
        String threadDescription = description.getText().toString();
        String courseCode = getIntent().getStringExtra("courseCode");

        if (threadTitle.isEmpty()) {title.setHint("Title (Required)"); return;}
        if (threadDescription.isEmpty()) {description.setHint("Description (Required)"); return;}

        Networking.getData(10, new String[]{threadTitle.replace(" ", "%20"), threadDescription.replace(" ", "%20"), courseCode.toLowerCase()}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    if (response.getString("success").equals("true")) {
                        CourseActivity.newThreadPosted = true;
                        startThreadActivity(response.getInt("thread_id"));

                    }
                } catch (JSONException e) {
                    Log.d("JSON Exception", e.getMessage());
                }
            }
        });
    }

    private void startThreadActivity (int ThreadID){
        Intent intent = new Intent(this, ThreadActivity.class);
        intent.putExtra("thread_id",ThreadID);
        startActivity(intent);
        finish();
    }
}
