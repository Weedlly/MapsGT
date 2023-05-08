package com.example.mapsgt.ui.communicate;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Database;

import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.relations.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private ImageButton sendMessageBtn, sendImageFileBtn;
    private EditText userMessageInput;

    private RecyclerView userMessageList;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;


    private String messageReceiverID, messageReceiverName, messageSenderID, saveCurrentDate, saveCurrentTime;

    private TextView receiverName;
    private CircleImageView receiverProfileImage;
    private DatabaseReference RootRof;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //TODO: change mAuth.getCurrentUser().getUid(); by below ID to testing
        messageSenderID = mAuth.getCurrentUser().getUid();

        RootRof = FirebaseDatabase.getInstance().getReference();

        Intent chatIntent = getIntent();
        messageReceiverID = chatIntent.getStringExtra("visit_user_id");
        messageReceiverName = chatIntent.getStringExtra("userName");

        IntializeFields();

        DisplayReceiverInfo();
        
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

        FetchMessage();
    }

    private void FetchMessage() {
        RootRof.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists())
                        {
                            Message message = snapshot.getValue(Message.class);
                            messageList.add(message);
                            messageAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendMessage() {
        String messageText = userMessageInput.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT);
        }
        else
        {
            String message_sender_ref = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String message_receiver_ref = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference user_message_key = RootRof.child("Messages").child(messageSenderID).child(messageReceiverID).push();
            String message_push_id = user_message_key.getKey();

            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime = currentTime.format(calFordTime.getTime());

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref + "/" + message_push_id, messageTextBody );
            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody );

            RootRof.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT);
                        userMessageInput.setText("");
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "Error: " + message, Toast.LENGTH_SHORT);
                    }


                }
            });
        }
    }

    private void DisplayReceiverInfo() {
        receiverName.setText(messageReceiverName);
        RootRof.child("users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    final String profileImage = snapshot.child("profilePicture").getValue().toString();
                    Picasso.with(ChatActivity.this).load(profileImage).placeholder(R.drawable.ic_profile).into(receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void IntializeFields() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        receiverName = (TextView) findViewById(R.id.custom_profile_name);
        receiverProfileImage = (CircleImageView) findViewById(R.id.custom_profile_image);

        sendMessageBtn = (ImageButton) findViewById(R.id.send_message_btn);
        sendImageFileBtn = (ImageButton) findViewById(R.id.send_image_file_btn);
        userMessageInput = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messageList);
        userMessageList = (RecyclerView) findViewById(R.id.message_list_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setHasFixedSize(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);
    }

}
