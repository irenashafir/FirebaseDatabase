package shafir.irena.firebasedatabase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import shafir.irena.firebasedatabase.dialogs.userRecyclerFragment;
import shafir.irena.firebasedatabase.models.User;

public class MainActivity extends AppCompatActivity implements userRecyclerFragment.onUserPicked{
    // properties:
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser user;


    private static final int RC_SIGN_IN = 1;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            // if user == null
            user = firebaseAuth.getCurrentUser();

            // go login : new intent
            if (user == null) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                                .setPermissions(Arrays.asList(Scopes.PROFILE, Scopes.EMAIL)).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());

                Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                        .setLogo(R.mipmap.ic_launcher)
                        .setAvailableProviders(providers)
                        .build();

                startActivityForResult(intent, RC_SIGN_IN);
            }
            // startActivityForResult is an activity that gets a result
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // create a user
                //save the user- convert it to a user
                // ref to the table
                // push()... setValue

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                User user = new User(currentUser);
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("Users").child(user.getUid());

                ref.setValue(user);

            } else if (idpResponse != null) {// TODO:add idpResponse.getError //)

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.users:
                userRecyclerFragment userFrag = new userRecyclerFragment();
                userFrag.show(getSupportFragmentManager(), "userList");

                return true;
            case R.id.sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void userPicked() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("userList");
        if (fragment != null){
            Toast.makeText(this, "picked", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new WhatsappFragment();
                case 1:
                    return new ShoppingListFragment();
                default:
                    return new Fragment();
            }
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Chat";
                case 1:
                    return "shopping lists";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }


    //Generating a Development Key Hash- on facebook
    // keytool -exportcert -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore | openssl sha1 -binary | openssl base64
    // change the "keytook" to the path of the keytool on the computer
    // change openssl to  path (after download) ---  "C:\Users\irena\Downloads\openssl-0.9.8h-1-bin (1)\bin\openssl"
    //"C:\Program Files\Java\jdk1.8.0_121\bin\keytool" -exportcert -alias AndroidDebugKey -keystore %HOMEPATH%\.android\debug.keystore | "C:\Users\irena\Downloads\openssl-0.9.8h-1-bin (1)\bin\openssl" sha1 -binary | "C:\Users\irena\Downloads\openssl-0.9.8h-1-bin (1)\bin\openssl" base64

}





