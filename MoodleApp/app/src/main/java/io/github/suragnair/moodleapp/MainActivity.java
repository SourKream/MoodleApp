package io.github.suragnair.moodleapp;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.github.suragnair.moodleapp.Networking;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Networking.getData(0, new String[]{"cs1110200", "john"}, new Networking.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Networking.getData(2, new String[0], new Networking.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        alertMessageBox("lulz", result);
                    }
                });
            }
        });


    }


    private void alertMessageBox (String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton("OKAY", null);
        builder.show();
    }
}
