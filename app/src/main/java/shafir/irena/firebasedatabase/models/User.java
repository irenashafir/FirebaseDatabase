package shafir.irena.firebasedatabase.models;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by irena on 20/06/2017.
 */

public class User {
    // a User Model Class-  we want to save objects in the date base
    // to do it we need to :
    //  1. must have an empty default constructor
    //  2. must have getters/ setters
    //  the getValue(User.class) RQ that

    private String displayName;
    private String profileImage;  // not all the users have a profile pic..
    private String uid;
    private String email;

    public User() {
    }

    public User(FirebaseUser user) {
        this.displayName = user.getDisplayName();
        if (user.getPhotoUrl() != null) {
        this.profileImage = user.getPhotoUrl().toString();
    }
        this.uid = user.getUid();
        this.email = user.getEmail();
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(String profileImage) {
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "displayName='" + displayName + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}


