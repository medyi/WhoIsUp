package com.medyi.whoisup.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.medyi.whoisup.R;
import com.medyi.whoisup.model.Message;
import com.medyi.whoisup.model.Room;
import com.medyi.whoisup.model.User;
import com.medyi.whoisup.util.SoundUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomActivity extends AppCompatActivity {

    public static final String TAG = RoomActivity.class.getName();
    public static final String EXTRA_ROOM = RoomActivity.class.getName() + "_EXTRA_ROOM";
    public static final String EXTRA_USER = RoomActivity.class.getName() + "_EXTRA_USER";

    private Room room;
    private User user;

    private ListView messageListView;
    private EditText messageEditText;

    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        room = (Room) getIntent().getSerializableExtra(EXTRA_ROOM);
        user = (User) getIntent().getSerializableExtra(EXTRA_USER);

        Log.d(TAG, "onCreate from RoomActivity (room): " + room);
        Log.d(TAG, "onCreate from RoomActivity (user): " + user);

        ((TextView) findViewById(R.id.toolbar_title_text_view)).setText(room.getName());
        Glide.with(this).load(room.getUrl()).into((ImageView) findViewById(R.id.toolbar_icon_image_view));

        messageListView = findViewById(R.id.message_list_view);
        messageListView.setAdapter(new ListViewAdapter(this, messageList));

        messageEditText = findViewById(R.id.message_edit_text);
        findViewById(R.id.send_message_image_button).setOnClickListener(v -> {
            String text = messageEditText.getText().toString();
            if (text.trim().length() > 0) {
                Message message = new Message();
                message.setUser(user);
                message.setText(text);
                FirebaseFirestore.getInstance()
                        .collection("rooms").document(room.getId())
                        .collection("messages").document()
                        .set(message)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                messageEditText.setText("");
                            } else {
                                Log.e(TAG, "onComplete: ", task.getException());
                            }
                        });
            }
        });


        FirebaseFirestore.getInstance()
                .collection("rooms").document(room.getId())
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
                    if (e == null) {
                        messageList.clear();
                        List<Message> messages = queryDocumentSnapshots.toObjects(Message.class);
                        if (messages.size() > 0) {
                            messageList.addAll(messages);
                            ((ListViewAdapter) messageListView.getAdapter()).notifyDataSetChanged();
                            messageListView.setSelection(messageList.size() - 1);
                            SoundUtil.playSound(RoomActivity.this, R.raw.popup);
                        }
                    } else {
                        Log.e(TAG, "onEvent: ", e);
                    }
                });
    }

    private static class ListViewAdapter extends ArrayAdapter<Message> {

        private List<Message> messageList;

        public ListViewAdapter(@NonNull Context context, List<Message> messageList) {
            super(context, R.layout.row_chat);
            this.messageList = messageList;
        }

        @Override
        public int getCount() {
            return messageList.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_chat, parent, false);

            TextView senderTextView = convertView.findViewById(R.id.sender_text_view);
            TextView messageTextView = convertView.findViewById(R.id.message_text_view);
            TextView timestampTextView = convertView.findViewById(R.id.timestamp_text_view);

            Message message = messageList.get(position);
            senderTextView.setText(message.getUser().getFirstName() + " " + message.getUser().getLastName());
            messageTextView.setText(message.getText());

            if (message.getTimestamp() == null) message.setTimestamp(new Date());

            timestampTextView.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(message.getTimestamp()));

            convertView.findViewById(R.id.container_relative_layout).setBackgroundResource(message.getUser().getId().equals(FirebaseAuth.getInstance().getUid()) ? R.drawable.sent_message_rounded_bg : R.drawable.received_message_rounded_bg);
            ((LinearLayout)convertView.findViewById(R.id.container_linear_layout)).setGravity(message.getUser().getId().equals(FirebaseAuth.getInstance().getUid()) ? Gravity.END : Gravity.START);
            return convertView;
        }
    }
}
