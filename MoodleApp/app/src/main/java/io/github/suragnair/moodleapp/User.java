package io.github.suragnair.moodleapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

class User {
    int ID;
    String firstName;
    String lastName;
    String username;
    String email;
    String entryNo;

    public User (String JsonString){
        try {
            JSONObject user = new JSONObject(JsonString);
            ID = user.getInt("id");
            username = user.getString("username");
            firstName = user.getString("first_name");
            lastName = user.getString("last_name");
            email = user.getString("email");
            entryNo = user.getString("entry_no");
        } catch (JSONException e) {
            Log.d("JSON Exception : ", e.getMessage());
        }
    }
}
