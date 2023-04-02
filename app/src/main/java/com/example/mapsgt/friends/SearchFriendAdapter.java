package com.example.mapsgt.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsgt.R;
import com.example.mapsgt.data.entities.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.UserViewHolder> implements Filterable {

    private List<User> mListUser;
    private List<User> mListUsersOld;

    public SearchFriendAdapter(List<User> mListUser){
        this.mListUser = mListUser;
        this.mListUsersOld = mListUser;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mListUser.get(position);
        if (user == null) {
            return;
        }

        //holder.imgUser.setImageResource(user.getProfilePicture()); ((TODO: add picture image))
        String nickname = user.getFirstName() + " " + user.getLastName();
        holder.tvName.setText(nickname);
        holder.tvPhoneNumber.setText(user.getPhone());
    }

    @Override
    public int getItemCount() {
        if (mListUser != null) {
            return mListUser.size();
        }
        return 0;
    }


    public class UserViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView imgUser;
        private TextView tvName;
        private TextView tvPhoneNumber;

        public UserViewHolder(@NonNull View itemView){
            super(itemView);
            //imgUser = itemView.findViewById(R.id.img_user);

            tvName = itemView.findViewById(R.id.tv_username);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constrain) {
                String strSearch = constrain.toString();
                if (strSearch.isEmpty()) {
                    mListUser = mListUsersOld;
                } else {
                    List<User> list = new ArrayList<>();
                    for (User user : mListUsersOld) {
                        if (user.getFirstName().toLowerCase().contains(strSearch.toLowerCase()) || user.getLastName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(user);
                        }
                        else if (user.getPhone().contains(strSearch)){
                            list.add(user);
                        }
                    }
                    mListUser = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListUser;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                mListUser = (List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
