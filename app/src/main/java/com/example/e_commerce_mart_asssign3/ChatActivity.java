package com.example.e_commerce_mart_asssign3;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etInput;
    private ImageView ivSend, ivBack;
    private TextView tvHeader;
    
    private ChatAdapter adapter;
    private List<Message> messageList;
    
    private DatabaseReference chatRef;
    private String currentUserId;
    private String otherUserId = "seller_admin"; // Default for this assignment
    private String chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null) {
            finish();
            return;
        }

        // Create a unique chat room ID based on both users
        // Alphabetical sort ensures both see the same room
        if (currentUserId.compareTo(otherUserId) < 0) {
            chatRoomId = currentUserId + "_" + otherUserId;
        } else {
            chatRoomId = otherUserId + "_" + currentUserId;
        }

        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId);

        initViews();
        setupRecyclerView();
        listenForMessages();
    }

    private void initViews() {
        rvMessages = findViewById(R.id.rv_chat_messages);
        etInput = findViewById(R.id.et_message_input);
        ivSend = findViewById(R.id.iv_send_message);
        ivBack = findViewById(R.id.iv_chat_back);
        tvHeader = findViewById(R.id.tv_chat_name);

        ivBack.setOnClickListener(v -> finish());
        ivSend.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        adapter = new ChatAdapter(this, messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Newest messages at bottom
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);
    }

    private void listenForMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message msg = data.getValue(Message.class);
                    if (msg != null) {
                        messageList.add(msg);
                    }
                }
                adapter.notifyDataSetChanged();
                if (!messageList.isEmpty()) {
                    rvMessages.smoothScrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String text = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        long timestamp = System.currentTimeMillis();
        Message message = new Message(currentUserId, otherUserId, text, timestamp);

        chatRef.push().setValue(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                etInput.setText("");
            } else {
                Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
