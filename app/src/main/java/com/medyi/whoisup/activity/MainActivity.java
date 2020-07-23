package com.medyi.whoisup.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.medyi.whoisup.R;
import com.medyi.whoisup.dialog.CustomDialog;
import com.medyi.whoisup.model.User;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName() ;
    public static final String EXTRA_USER = MainActivity.class.getName() + "_EXTRA_USER";

    private CardView profileCardView;
    private CardView connectionsCardView;
    private CardView findCardView;
    private CardView logOutCardView;
    private User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();


        user = (User) getIntent().getSerializableExtra(EXTRA_USER);


    }

    public void initViews() {
        profileCardView = findViewById(R.id.profile_card_view);
        connectionsCardView = findViewById(R.id.connections_card_view);
        findCardView = findViewById(R.id.find_card_view);
        logOutCardView = findViewById(R.id.log_out_card_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addActions();
    }

    public void addActions() {
        profileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            }
        });

        logOutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    new CustomDialog(MainActivity.this, "Sign Out", "Are you sure you want to sign out?", true, true, result -> {
                        if (result == CustomDialog.ActionListener.ActionResult.OK) {
                            signOut();
                        }
                    }).show();
                }
            }
        });

        findCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WhoIsUpActivity.class);
                startActivity(intent);
            }
        });

        connectionsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);

                startActivity(intent);
                Log.d(TAG, "onClick from MainActivity: " + user);
            }
        });


    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
