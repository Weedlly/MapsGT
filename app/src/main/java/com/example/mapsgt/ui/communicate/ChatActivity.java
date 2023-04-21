package com.example.mapsgt.ui.communicate;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
//import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.mapsgt.R;

public class ChatActivity extends AppCompatActivity {
    private Toolbar chatToolBar;
    private ImageButton sendMessageBtn, sendImageFileBtn;
    private EditText userMessageInput;
    private RecyclerView userMessageList;

    private String messageReceiverID, messageReceiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("userName").toString();
        IntializeFields();
    }

    private void IntializeFields() {
        chatToolBar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolBar);

        sendMessageBtn = (ImageButton) findViewById(R.id.send_message_btn);
        sendImageFileBtn = (ImageButton) findViewById(R.id.send_image_file_btn);
        userMessageInput = (EditText) findViewById(R.id.input_message);
    }

}
