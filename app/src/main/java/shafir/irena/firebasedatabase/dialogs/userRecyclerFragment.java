package shafir.irena.firebasedatabase.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;

import shafir.irena.firebasedatabase.R;
import shafir.irena.firebasedatabase.models.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class userRecyclerFragment extends DialogFragment {
    FirebaseDatabase mDatabase;
    FirebaseUser user;
    RecyclerView rvUsers;
    private onUserPicked listener;


     public interface onUserPicked{
        void userPicked();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onUserPicked){
            listener = (onUserPicked) context;
        }
        else
            throw new RuntimeException("must implement onUserPicked interface");

    }


    public userRecyclerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_recycler, container, false);
        rvUsers = (RecyclerView) v.findViewById(R.id.rvUsers);

        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        usersAdapter();

        return v;
    }



    private void usersAdapter() {
        userAdapter adapter = new userAdapter(getContext(), mDatabase.getReference("BetterChat"));
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    public static class userAdapter extends FirebaseRecyclerAdapter<User, userAdapter.UserViewHolder>{

        Context context;
        public userAdapter(Context context, Query query) {
            super(User.class, R.layout.user_item, UserViewHolder.class, query);
            this.context=context;
        }

        @Override
        protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {
            viewHolder.tvDisplayName.setText(model.getDisplayName());
            viewHolder.tvEmail.setText(model.getEmail());
            viewHolder.tvUid.setText(model.getUid());
            if (model.getProfileImage() != null) {
                Glide.with(context).load(model.getProfileImage()).into(viewHolder.ivProfile);
            }
            else Glide.with(context).load(R.mipmap.ic_default).into(viewHolder.ivProfile);
        }

        public static class UserViewHolder extends RecyclerView.ViewHolder{
            private TextView tvDisplayName;
            private TextView tvEmail;
            private TextView tvUid;
            private CircularImageView ivProfile;


            public UserViewHolder(View itemView) {
                super(itemView);
                tvDisplayName = (TextView) itemView.findViewById(R.id.tvDisplayName);
                tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
                tvUid = (TextView) itemView.findViewById(R.id.tvUid);
                ivProfile = (CircularImageView) itemView.findViewById(R.id.ivProfile);
            }
        }




    }






}
