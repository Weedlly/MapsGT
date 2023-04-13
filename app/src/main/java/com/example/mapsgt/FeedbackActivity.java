package com.example.mapsgt;


import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mapsgt.data.entities.Feedback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {
    EditText name_data, message_data;
    TextView name_bug, message_bug;
    Button sent_fb, view_details;
    private DatabaseReference allFBFef = FirebaseDatabase.getInstance().getReference("Feedback");
    private String currentUserId;
    //private static String email = "";
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_feedback);

        name_data = (EditText) findViewById(R.id.fb_name);
        name_bug = (TextView) findViewById(R.id.bug_name);
        name_bug.setVisibility(View.INVISIBLE);

        message_data = (EditText) findViewById(R.id.fb_content);
        message_bug = (TextView) findViewById(R.id.bug_content);
        message_bug.setVisibility(View.INVISIBLE);

        sent_fb = (Button) findViewById(R.id.btn_send_fb);
        view_details = (Button) findViewById(R.id.btn_view_send_detail);

        currentUserId = "1";
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("User");

        UsersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    final String email = snapshot.child("email").getValue().toString();
                    Log.d(TAG, "Email: " + email);
                    sent_fb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            view_details.setEnabled(true);

                            if (name_data.getText().toString().isEmpty() || message_data.getText().toString().isEmpty())
                            {
                                // Check if sender name is null
                                if (name_data.getText().toString().isEmpty())
                                {
                                    Log.d(TAG, "visible name");
                                    name_bug.setVisibility(View.VISIBLE);
                                    //return;
                                }
                                if (!name_data.getText().toString().isEmpty()){
                                    Log.d(TAG, "Invisible name");
                                    name_bug.setVisibility(View.INVISIBLE);
                                }

                                //check if message context is null
                                if (message_data.getText().toString().isEmpty())
                                {
                                    Log.d(TAG, "visible context");
                                    message_bug.setVisibility(View.VISIBLE);
                                    //return;
                                }
                                if (!message_data.getText().toString().isEmpty()){
                                    Log.d(TAG, "Invisible context");
                                    message_bug.setVisibility(View.INVISIBLE);
                                }

                                Toast.makeText(FeedbackActivity.this, "Name or Message is null! Fail", Toast.LENGTH_SHORT)
                                        .show();

                                return;
                            }

                            final String name = name_data.getText().toString();
                            final String message = message_data.getText().toString();

                            Feedback FB = new Feedback(name, email ,message);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(currentUserId, FB);

                            DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
                            fb.child("Feedback").setValue(hashMap);
                            Toast.makeText(FeedbackActivity.this, "Send feedback successful", Toast.LENGTH_SHORT)
                                    .show();


                            view_details.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
                                    builder.setTitle("Sending Details:")
                                            .setMessage("Name: " + name + "\n\nEmail: " + email + "\n\nMessage: " + message)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // User clicked OK button
                                                }
                                            });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void OptionFeedbackByGoogleAccount() {
        Intent i = new Intent (Intent.ACTION_SEND);
        i.setType("message/html");
        i.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "datnguyen180502@gmail.com" });
        i.putExtra(Intent.EXTRA_SUBJECT, "Feedback From App");
        i.putExtra(Intent.EXTRA_TEXT, "Name: "+ name_data.getText() + "\n Message: " + message_data.getText());
        try {

            startActivity(Intent.createChooser(i, "Please select Email"));
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(FeedbackActivity.this, "There are no Email Clients", Toast.LENGTH_SHORT);
        }
    }
}
