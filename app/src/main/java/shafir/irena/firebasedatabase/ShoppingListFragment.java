package shafir.irena.firebasedatabase;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import shafir.irena.firebasedatabase.dialogs.AddListFragment;
import shafir.irena.firebasedatabase.dialogs.ShareFragment;
import shafir.irena.firebasedatabase.models.ShoppingList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingListFragment extends Fragment {

    @BindView(R.id.fabAddList)
    FloatingActionButton fabAddList;
    @BindView(R.id.rvShoppingLists)
    RecyclerView rvShoppingLists;
    Unbinder unbinder;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        FragmentManager fragmentManager = getChildFragmentManager();

        // query the data for the view
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)return view;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserLists").child(user.getUid());
        // create a shopping list adapter
        ShoppingListAdapter adapter = new ShoppingListAdapter(ref, fragmentManager);
        // set the layoutManager & adapter of the recycler
        rvShoppingLists.setAdapter(adapter);
        rvShoppingLists.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.fabAddList)
    public void onFabAddListClicked() {
        AddListFragment dialog = new AddListFragment();
        dialog.show(getChildFragmentManager(), "AddListDialog");
    }




    public static class ShoppingListAdapter
            extends FirebaseRecyclerAdapter<ShoppingList, ShoppingListAdapter.ShoppingListViewHolder>{

        FragmentManager fragmentManager;

        public ShoppingListAdapter(Query query, FragmentManager fragmentManager) {
            super(ShoppingList.class, R.layout.shopping_list_name_item, ShoppingListViewHolder.class, query);
            this.fragmentManager = fragmentManager;
        }

        @Override
        protected void populateViewHolder(ShoppingListViewHolder viewHolder, ShoppingList model, int position) {
            viewHolder.tvListName.setText(model.getMame());
            viewHolder.fabListShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareFragment share = new ShareFragment();
                    share.show(fragmentManager, "users");
                }
            });
        }

        public static class ShoppingListViewHolder extends RecyclerView.ViewHolder {
            TextView tvListName;
            FloatingActionButton fabListShare;

            public ShoppingListViewHolder(View itemView) {
                super(itemView);
                tvListName = (TextView) itemView.findViewById(R.id.tvListName);
                fabListShare = (FloatingActionButton) itemView.findViewById(R.id.fabListShare);
            }
        }
    }

}
