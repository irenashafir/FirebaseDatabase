package shafir.irena.firebasedatabase;


import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import shafir.irena.firebasedatabase.models.ChatMessage;
import shafir.irena.firebasedatabase.models.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class WhatsappFragment extends Fragment {

    FirebaseDatabase mDatabase;
    FirebaseUser user;

    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.btnSend)
    Button btnSend;
    @BindView(R.id.rvChat)
    RecyclerView rvChat;
    Unbinder unbinder;

    public WhatsappFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_whatsapp, container, false);
        unbinder = ButterKnife.bind(this, view);

        // TODO: discuss sharedInstance
        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        setUpRecycler();
        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnSend)
    public void onBtnSendClick() {

        String text = etMessage.getText().toString();
        if (text.isEmpty())return;

//        // reference to a table Chat
//        DatabaseReference chatTable = mDatabase.getReference("Chat");
//        // add a new record & get a reference to the new record
//        DatabaseReference currentRow = chatTable.push();
//        // set the value
//        currentRow.setValue(text);

        // functional programing:
//        mDatabase.getReference("Chat").push().setValue(text);


        ChatMessage chat = new ChatMessage(new User(user), text);
        mDatabase.getReference("BetterChat").push().setValue(chat);

        etMessage.setText(null);

        //TODO: save the user

    }

    private void readFromDb(){
        // get the ref to the table
        // add a listener to the table
        final List<String> items = new ArrayList<>();

        DatabaseReference chatRef = mDatabase.getReference("Chat");
        // once at the beginning & again from start at any change
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> rows = dataSnapshot.getChildren();

                for (DataSnapshot row : rows) {
                    String text = row.getValue(String.class);
                    // we can get the data & insert into a list to use later
//                    items.add(text);
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
        private void readOnce(){
            // get a ref to the table
            // add a listener

            // get the data once from the server. not updating unless we run the query again
            mDatabase.getReference("Chat").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot row : dataSnapshot.getChildren()) {
                        Toast.makeText(getContext(), row.getValue(String.class), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void readIncremental(){
            mDatabase.getReference("Chat").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // at the start - once get all the table
                    // once a new item is added we will only get the new child
                    String text = dataSnapshot.getValue(String.class);
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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



    private void setUpRecycler() {
        BetterChatAdapter adapter = new BetterChatAdapter(getContext(), mDatabase.getReference("BetterChat"));
        rvChat.setAdapter(adapter);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        mDatabase.getReference("BetterChat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // home work- play with the Recycler
                rvChat.animate().rotationBy(360);
                dataSnapshot.getChildren();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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


        public static class BetterChatAdapter extends
                FirebaseRecyclerAdapter<ChatMessage, BetterChatAdapter.BetterChatViewHolder>{

            private Context context;
            public BetterChatAdapter(Context context,Query query) {
                super(ChatMessage.class, R.layout.chat_item, BetterChatViewHolder.class, query);
                this.context = context;
            }

            @Override
            protected void populateViewHolder(BetterChatViewHolder viewHolder, ChatMessage model, int position) {
                viewHolder.tvMessage.setText(model.getMessage());
                viewHolder.tvSenderTime.setText(model.getSender() + " " + model.getTime());
                Glide.with(context).load(model.getProfileImage()).into(viewHolder.ivProfile);
            }


            public static class BetterChatViewHolder extends RecyclerView.ViewHolder {
                TextView tvSenderTime;
                TextView tvMessage;
                CircularImageView ivProfile;

                public BetterChatViewHolder(View itemView) {
                    super(itemView);
                    tvMessage = (TextView) itemView.findViewById(R.id.tvChat);
                    tvSenderTime = (TextView) itemView.findViewById(R.id.tvSenderTime);
                    ivProfile = (CircularImageView) itemView.findViewById(R.id.ivProfile);
                }
            }
        }

// home work - add dialog fragment for all the users

    public static class ChatAdapter extends FirebaseRecyclerAdapter<String, ChatAdapter.ChatViewHolder> {


        //            /**
//             * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
//             * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
//             *                        instance of the corresponding view with the data from an instance of modelClass.
//             * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
//             * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
//             *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
//             */
        public ChatAdapter(Query ref) {
            super(String.class, R.layout.chat_item, ChatViewHolder.class, ref);

        }

        @Override
        protected void populateViewHolder(ChatViewHolder v, String text, int position) {
            v.tvChat.setText(text);
        }

        public static class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView tvChat;

            public ChatViewHolder(View itemView) {
                super(itemView);
                tvChat = (TextView) itemView.findViewById(R.id.tvChat);

            }

            public TextView getTvChat() {
                return tvChat;
            }

            @Override
            public String toString() {
                return "ChatViewHolder{" +
                        "tvChat=" + tvChat +
                        '}';
            }
        }
    }


}
