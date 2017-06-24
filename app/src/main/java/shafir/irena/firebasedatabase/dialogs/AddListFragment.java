package shafir.irena.firebasedatabase.dialogs;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import shafir.irena.firebasedatabase.R;
import shafir.irena.firebasedatabase.models.ShoppingList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddListFragment extends DialogFragment {


    @BindView(R.id.etListName)
    EditText etListName;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    Unbinder unbinder;

    public AddListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnAdd)
    public void onViewClicked() {
        //get the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // if user is null we cant do anything,
        if (user == null) return;

        // get the ref
        DatabaseReference ref =
                    FirebaseDatabase.getInstance().getReference("UserLists").child(user.getUid());

            String listName = etListName.getText().toString();

            //push returns the ID of the new row & from that we can get the list UID
            DatabaseReference row = ref.push();

            // get the UID
            String listUID = row.getKey();

            // construct the model
            ShoppingList model = new ShoppingList(user.getUid(), listUID, listName);

            // set value of the new row into the model
            row.setValue(model);

        // dismiss dialog fragment
            dismiss();
    }


}
