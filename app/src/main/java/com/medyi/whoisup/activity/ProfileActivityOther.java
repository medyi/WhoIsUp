package com.medyi.whoisup.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.medyi.whoisup.R;
import com.medyi.whoisup.model.User;


import java.text.SimpleDateFormat;

public class ProfileActivityOther extends AppCompatActivity {

    public static final String TAG = ProfileActivityOther.class.getName();
    public static final String EXTRA_USER = ProfileActivityOther.class.getName() + "_EXTRA_USER";

    private TextView emailTextView;
    private TextView whatIsUpForText;
    private User user;
    //editTextsFromDialog
    private TextView phoneEditTextView;
    private TextView instagramEditTextView;
    private TextView discordEditTextView;

    private TextView joinDate;
    private TextView firstLastName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_other);
        initializeViews();

        user = (User) getIntent().getSerializableExtra(EXTRA_USER);

        emailTextView.setText(user.getEmail());
        firstLastName.setText(user.getFirstName() + " " + user.getLastName());
        joinDate.setText("Join Date: " + new SimpleDateFormat("dd-MM-yyyy").format(user.getJoinDate()));
        whatIsUpForText.setText(user.getUpForWhat());
        phoneEditTextView.setText(user.getPhone());
        discordEditTextView.setText(user.getDiscord());
        instagramEditTextView.setText(user.getInstagram());


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void initializeViews(){
        emailTextView = findViewById(R.id.other_profile_email_text);
        firstLastName = findViewById(R.id.other_first_last_name);
        joinDate = findViewById(R.id.other_profile_join_date);
        whatIsUpForText = findViewById(R.id.other_what_is_up_text);
        phoneEditTextView = findViewById(R.id.other_profile_phone_edit_text);
        instagramEditTextView = findViewById(R.id.other_profile_instagram_edit_text);
        discordEditTextView = findViewById(R.id.other_profile_discord_edit_text);
    }

}
