package com.medyi.whoisup.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medyi.whoisup.R;
import com.medyi.whoisup.model.User;

import java.text.SimpleDateFormat;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = ProfileActivity.class.getName();
    public static final String EXTRA_USER = ProfileActivity.class.getName() + "_EXTRA_USER";

    private TextView emailTextView;
    private Button saveButton;

    //editTextsFromDialog
    private EditText phoneEditTextView;
    private EditText instagramEditTextView;
    private EditText discordEditTextView;

    //parent layout for snackbar
    private LinearLayout parentLinear;

    private TextView joinDate;
    private TextView firstLastName;
    private TextView upForWhatText;
    private Spinner whoIsUpSpinner;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeViews();


        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        User user = new User(
                                userId,
                                documentSnapshot.getString("email"),
                                documentSnapshot.getString("firstName"),
                                documentSnapshot.getString("lastName"),
                                documentSnapshot.getDate("joinDate"),
                                documentSnapshot.getString("upForWhat"),
                                documentSnapshot.getString("instagram"),
                                documentSnapshot.getString("discord"),
                                documentSnapshot.getString("phone")

                        );
                        emailTextView.setText(user.getEmail());
                        firstLastName.setText(user.getFirstName() + " " + user.getLastName());
                        joinDate.setText("Join Date: " + new SimpleDateFormat("dd-MM-yyyy").format(user.getJoinDate()));

                        whoIsUpSpinner.setSelection(((ArrayAdapter<String>)whoIsUpSpinner.getAdapter()).getPosition(user.getUpForWhat()));
                        phoneEditTextView.setText(user.getPhone());
                        instagramEditTextView.setText(user.getInstagram());
                        discordEditTextView.setText(user.getDiscord());

                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("phone", phoneEditTextView.getText().toString());
                                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("instagram", instagramEditTextView.getText().toString());
                                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("discord", discordEditTextView.getText().toString());

                                Snackbar.make(parentLinear, "Your social information have been saved.", Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });


                        whoIsUpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("upForWhat", parent.getSelectedItem().toString());
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    public void initializeViews(){
        emailTextView = findViewById(R.id.profile_email_text);
        firstLastName = findViewById(R.id.profile_first_last_name);
        joinDate = findViewById(R.id.profile_join_date);

        saveButton = findViewById(R.id.profile_save_button);
        phoneEditTextView = findViewById(R.id.profile_phone_edit_text);
        instagramEditTextView = findViewById(R.id.profile_instagram_edit_text);
        discordEditTextView = findViewById(R.id.profile_discord_edit_text);

        parentLinear = findViewById(R.id.parent_linear);
        //Spinner
        whoIsUpSpinner = findViewById(R.id.who_is_up_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.upforwhat_list));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whoIsUpSpinner.setAdapter(spinnerAdapter);

    }





}
