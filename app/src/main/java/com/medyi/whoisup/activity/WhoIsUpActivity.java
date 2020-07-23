package com.medyi.whoisup.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medyi.whoisup.dialog.WaitDialog;
import com.medyi.whoisup.model.User;
import com.medyi.whoisup.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhoIsUpActivity extends AppCompatActivity {
    private static String TAG = WhoIsUpActivity.class.getName();
    public static final String EXTRA_USER = WhoIsUpActivity.class.getName() + "_EXTRA_USER";

    private RecyclerView usersRecyclerView;
    private Spinner whoIsUpFilterSpinner;
    private User user;

    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whoisup);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        usersRecyclerView = findViewById(R.id.users_recycler_view);
        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        user = (User) getIntent().getSerializableExtra(EXTRA_USER);

        initializeViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        whoIsUpFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().toString().equals("All")){
                    WaitDialog waitDialog = new WaitDialog(WhoIsUpActivity.this);
                    waitDialog.show();

                    FirebaseFirestore.getInstance().collection("users")
                            .get().addOnCompleteListener(WhoIsUpActivity.this, task -> {
                        if (task.isSuccessful()){
                            waitDialog.dismiss();
                            userList.clear();
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {
                                userList.add(new User(document.getId(),
                                        document.getString("email"),
                                        document.getString("firstName"),
                                        document.getString("lastName"),
                                        document.getDate("joinDate"),
                                        document.getString("upForWhat"),
                                        document.getString("instagram"),
                                        document.getString("discord"),
                                        document.getString("phone")));
                            }
                            Collections.sort(userList, (first, second) -> first.getFirstName().compareTo(second.getFirstName()));
                            usersRecyclerView.getAdapter().notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "onComplete: ", task.getException());
                        }
                    });
                } else {
                    WaitDialog waitDialog = new WaitDialog(WhoIsUpActivity.this);
                    waitDialog.show();

                    FirebaseFirestore.getInstance().collection("users").
                            whereEqualTo("upForWhat", parent.getSelectedItem().toString()).
                            get().addOnCompleteListener(WhoIsUpActivity.this, task -> {
                        if (task.isSuccessful()){
                            waitDialog.dismiss();
                            userList.clear();
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {
                                userList.add(new User(document.getId(),
                                        document.getString("email"),
                                        document.getString("firstName"),
                                        document.getString("lastName"),
                                        document.getDate("joinDate"),
                                        document.getString("upForWhat"),
                                        document.getString("instagram"),
                                        document.getString("discord"),
                                        document.getString("phone")));
                            }
                            Collections.sort(userList, (first, second) -> first.getFirstName().compareTo(second.getFirstName()));
                            usersRecyclerView.getAdapter().notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "onComplete: ", task.getException());
                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        WaitDialog waitDialog = new WaitDialog(this);
        waitDialog.show();
        FirebaseFirestore.getInstance().collection("users").
                get()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        waitDialog.dismiss();

                        List<DocumentSnapshot> documents = task.getResult().getDocuments();

                        for (DocumentSnapshot document : documents) {
                            userList.add(new User(document.getId(),
                                    document.getString("email"),
                                    document.getString("firstName"),
                                    document.getString("lastName"),
                                    document.getDate("joinDate"),
                                    document.getString("upForWhat"),
                                    document.getString("instagram"),
                                    document.getString("discord"),
                                    document.getString("phone")));
                        }

                        Collections.sort(userList, (first, second) -> first.getFirstName().compareTo(second.getFirstName()));

                        usersRecyclerView.setAdapter(new RecyclerViewAdapter(this, userList, user -> {

                            Intent intent = new Intent(WhoIsUpActivity.this, ProfileActivityOther.class);
                            intent.putExtra(ProfileActivityOther.EXTRA_USER, user);
                            startActivity(intent);
                        }));
                    } else {
                        Log.e(TAG, "onComplete: ", task.getException());
                    }
                });
        usersRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + v);
            }
        });
    }



    public void initializeViews(){
        whoIsUpFilterSpinner = findViewById(R.id.who_is_up_filter_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(WhoIsUpActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.upforwhat_filter_list));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whoIsUpFilterSpinner.setAdapter(spinnerAdapter);
    }



    private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RowHolder> {

        private Context context;
        private List<User> userList;
        private OnItemClickListener listener;

        public RecyclerViewAdapter(Context context, List<User> userList, OnItemClickListener listener) {
            this.context = context;
            this.userList = userList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RowHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RowHolder holder, int position) {
            User user = userList.get(position);
            holder.bind(context, user, listener);
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public static class RowHolder extends RecyclerView.ViewHolder {

            ImageView iconImageView;
            TextView nameTextView;
            TextView professionTextView;

            public RowHolder(@NonNull View itemView) {
                super(itemView);
                iconImageView = itemView.findViewById(R.id.icon_image_view);
                nameTextView = itemView.findViewById(R.id.name_text_view);
                professionTextView = itemView.findViewById(R.id.profession_teamup_text);
            }

            public void bind(Context context, User user, OnItemClickListener listener) {
                nameTextView.setText(user.getFirstName() + " " + user.getLastName());
                professionTextView.setText(user.getUpForWhat());
                itemView.setOnClickListener(v -> listener.onItemClick(user));
            }
        }

        public interface OnItemClickListener {
            void onItemClick(User user);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        whoIsUpFilterSpinner.setSelection(0);
        userList.clear();
    }
}
