package util;
import android.app.Application;
public class viltradeUser extends Application {
    private String username;
    private String userID;
    private static viltradeUser instance;
    //Mengikuti Singleton design pattern
    public static viltradeUser getInstance(){
        if (instance == null){
            instance = new viltradeUser();
        }
        return instance;
    }
    public viltradeUser() {
// Konstruktor kosong
    }
    //Getter
    public String getUsername(){
        return username;
    }
    public String getUserID() {
        return userID;
    }
    //Setter
    public void setUsername(String username){this.username = username;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
}