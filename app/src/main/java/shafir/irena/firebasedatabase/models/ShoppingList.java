package shafir.irena.firebasedatabase.models;

/**
 * Created by irena on 23/06/2017.
 */

public class ShoppingList {
    private String ownerUID;
    private String listUID;
    private String mame;

    // empty constructor is needed to create the model from firebase (snapShot.getValue)
    public ShoppingList() {
    }
    public ShoppingList(String ownerUID, String listUID, String mame) {
        this.ownerUID = ownerUID;
        this.listUID = listUID;
        this.mame = mame;
    }
    public String getOwnerUID() {
        return ownerUID;
    }
    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }
    public String getListUID() {
        return listUID;
    }
    public void setListUID(String listUID) {
        this.listUID = listUID;
    }
    public String getMame() {
        return mame;
    }
    public void setMame(String mame) {
        this.mame = mame;
    }
    @Override
    public String toString() {
        return "ShoppingList{" +
                "ownerUID='" + ownerUID + '\'' +
                ", listUID='" + listUID + '\'' +
                ", mame='" + mame + '\'' +
                '}';
    }


}
