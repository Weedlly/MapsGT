package com.example.mapsgt.ui.communicate;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.relations.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> userMessageList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private DatabaseReference usersDatabaseRef;

    public MessageAdapter (List<Message> userMessageList)
    {
        this.userMessageList = userMessageList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView SenderMessageText, ReceiverMessageText;
        public CircleImageView receiverProfileImage;
        public MessageViewHolder(View itemView)
        {
            super(itemView);

            SenderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            ReceiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_users, parent, false);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position)
    {
        //TODO: change mAuth.getCurrentUser().getUid(); by below ID to testing
        String messageSenderID = "v63SkgeB9nXSmm3aICpri24mWfj1";
        Message message = userMessageList.get(position);

        String fromUserID = message.getFrom();
        String fromMessageType = message.getType();

        usersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersDatabaseRef.child(messageSenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String image = snapshot.child("profilePicture").getValue().toString();

                    Picasso.with(holder.receiverProfileImage.getContext()).load(image)
                            .placeholder(R.drawable.ic_profile).into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (fromMessageType.equals("text"))
        {
            holder.ReceiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);

            if (fromUserID.equals(messageSenderID))
            {
                holder.SenderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.SenderMessageText.setTextColor(Color.WHITE);
                holder.SenderMessageText.setGravity(Gravity.LEFT);
                holder.SenderMessageText.setText(message.getMessage());
            }
            else
            {
                holder.SenderMessageText.setVisibility(View.INVISIBLE);

                holder.ReceiverMessageText.setVisibility(View.VISIBLE);
                holder.ReceiverMessageText.setVisibility(View.VISIBLE);

                holder.ReceiverMessageText.setBackgroundResource(R.drawable.receiver_message_text_background);
                holder.ReceiverMessageText.setTextColor(Color.WHITE);
                holder.ReceiverMessageText.setGravity(Gravity.LEFT);
                holder.ReceiverMessageText.setText(message.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }
}
