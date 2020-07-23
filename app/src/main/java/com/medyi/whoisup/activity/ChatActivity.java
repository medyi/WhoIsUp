package com.medyi.whoisup.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medyi.whoisup.R;
import com.medyi.whoisup.dialog.WaitDialog;
import com.medyi.whoisup.model.Room;
import com.medyi.whoisup.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = ChatActivity.class.getName();

    private RecyclerView roomsRecyclerView;
    private User user;
    private User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        roomsRecyclerView = findViewById(R.id.rooms_recycler_view);
        roomsRecyclerView.setHasFixedSize(true);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Didn't use "intent" and put extra user in here because I got a lot of "null point exception" in Room activity.
        //Sometimes the user was sent, sometimes it didn't so I got the null pointer exception, so I decided to solve the problem by requesting the user data in here.
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task1 -> {
                    DocumentSnapshot documentSnapshot = task1.getResult();
                    newUser = new User(
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            documentSnapshot.getString("email"),
                            documentSnapshot.getString("firstName"),
                            documentSnapshot.getString("lastName"),
                            documentSnapshot.getDate("joinDate"),
                            documentSnapshot.getString("upForWhat"),
                            documentSnapshot.getString("instagram"),
                            documentSnapshot.getString("discord"),
                            documentSnapshot.getString("phone"));
                });
    }

    @Override
    protected void onStart() {
        super.onStart();




        WaitDialog waitDialog = new WaitDialog(this);
        waitDialog.show();
        FirebaseFirestore.getInstance().collection("rooms").get()
                .addOnCompleteListener(this, task -> {
                    waitDialog.dismiss();
                    if (task.isSuccessful()) {
                        List<Room> roomList = new ArrayList<>();
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            roomList.add(new Room(document.getId(), document.getString("name"), document.getDate("createdTime"), document.getString("url")));
                        }
                        Collections.sort(roomList, (first, second) -> first.getName().compareTo(second.getName()));
                        roomsRecyclerView.setAdapter(new RecyclerViewAdapter(ChatActivity.this, roomList, room -> {
                            Intent intent = new Intent(ChatActivity.this, RoomActivity.class);
                            intent.putExtra(RoomActivity.EXTRA_ROOM, room);
                            intent.putExtra(RoomActivity.EXTRA_USER, newUser);
                            startActivity(intent);
                        }));
                    } else {
                        Log.e(TAG, "onComplete: ", task.getException());
                    }
                });
        roomsRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + v);
            }
        });

    }


    private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RowHolder> {

        private Context context;
        private List<Room> roomList;
        private OnItemClickListener listener;

        public RecyclerViewAdapter(Context context, List<Room> roomList, OnItemClickListener listener) {
            this.context = context;
            this.roomList = roomList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RowHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RowHolder holder, int position) {
            Room room = roomList.get(position);
            holder.bind(context, room, listener);
        }

        @Override
        public int getItemCount() {
            return roomList.size();
        }

        public static class RowHolder extends RecyclerView.ViewHolder {

            ImageView iconImageView;
            TextView nameTextView;
            TextView createdTimeTextView;

            public RowHolder(@NonNull View itemView) {
                super(itemView);
                iconImageView = itemView.findViewById(R.id.icon_image_view);
                nameTextView = itemView.findViewById(R.id.name_text_view);
                createdTimeTextView = itemView.findViewById(R.id.created_time_text_view);
            }

            public void bind(Context context, Room room, OnItemClickListener listener) {
                nameTextView.setText(room.getName());
                createdTimeTextView.setText(new SimpleDateFormat("dd-MM-yyyy").format(room.getCreatedTime()));
                Glide.with(context).load(room.getUrl()).into(iconImageView);
                itemView.setOnClickListener(v -> listener.onItemClick(room));
            }
        }

        public interface OnItemClickListener {
            void onItemClick(Room room);
        }
    }
}
