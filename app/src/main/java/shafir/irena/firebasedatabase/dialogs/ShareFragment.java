package shafir.irena.firebasedatabase.dialogs;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import shafir.irena.firebasedatabase.R;
import shafir.irena.firebasedatabase.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends DialogFragment {
    FirebaseDatabase mDatabase;
    FirebaseUser user;

    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;
    Unbinder unbinder;

    public ShareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_recycler, container, false);
        unbinder = ButterKnife.bind(this, view);
        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        usersAdapter();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void usersAdapter() {
        if (user == null)return;
        DatabaseReference ref = mDatabase.getReference("BetterChat");
        UsersAdapter adapter = new UsersAdapter(getContext(),ref);
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                dataSnapshot.getChildren();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public static class UsersAdapter extends FirebaseRecyclerAdapter<User, UsersAdapter.ShareViewHolder>{

        Context context;

        public UsersAdapter(Context context, Query query) {
            super(User.class, R.layout.user_item, ShareViewHolder.class, query);
            this.context = context;
        }

        @Override
        protected void populateViewHolder(ShareViewHolder viewHolder, User model, int position) {
            viewHolder.tvDisplayName.setText(model.getDisplayName());
            viewHolder.tvEmail.setText(model.getEmail());
            viewHolder.tvUid.setText(model.getUid());
            if (model.getProfileImage() != null) {
                Glide.with(context).load(model.getProfileImage()).into(viewHolder.ivProfile);
            }
            else Glide.with(context).load(R.mipmap.ic_default).into(viewHolder.ivProfile);
        }

        public static class ShareViewHolder extends RecyclerView.ViewHolder {
            private TextView tvDisplayName;
            private TextView tvEmail;
            private TextView tvUid;
            private CircularImageView ivProfile;

            public ShareViewHolder(View itemView) {
                super(itemView);
                tvDisplayName = (TextView) itemView.findViewById(R.id.tvDisplayName);
                tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
                tvUid = (TextView) itemView.findViewById(R.id.tvUid);
                ivProfile = (CircularImageView) itemView.findViewById(R.id.ivProfile);
            }
            public TextView getTvDisplayName() {
                return tvDisplayName;
            }
            public TextView getTvEmail() {
                return tvEmail;
            }
            public TextView getTvUid() {
                return tvUid;
            }
            public CircularImageView getIvProfile() {
                return ivProfile;
            }
            @Override
            public String toString() {
                return "ShareViewHolder{" +
                        "tvDisplayName=" + tvDisplayName +
                        ", tvEmail=" + tvEmail +
                        ", tvUid=" + tvUid +
                        ", ivProfile=" + ivProfile +
                        '}';
            }
        }
    }

}
