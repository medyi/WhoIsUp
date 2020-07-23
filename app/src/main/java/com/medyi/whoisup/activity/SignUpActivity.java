package com.medyi.whoisup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medyi.whoisup.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static String TAG = SignUpActivity.class.getName();

    private TextInputEditText firstNameEditText;
    private TextInputEditText lastNameEditText;
    private Button signUpButton;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeViews();
        registerViewEvents();
    }


    private void initializeViews(){
        signUpButton = findViewById(R.id.sign_up_button);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
    }

    private void registerViewEvents(){
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email", username);
                                    user.put("firstName", firstNameEditText.getText().toString());
                                    user.put("lastName", lastNameEditText.getText().toString());
                                    user.put("joinDate", new Date());
                                    user.put("upForWhat", "Empty/Not Selected");
                                    user.put("instagram", "Empty");
                                    user.put("discord", "Empty");
                                    user.put("phone", "Empty");


                                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}
