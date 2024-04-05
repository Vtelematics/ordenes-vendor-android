package ordenese.vendor.firebase_chat;

public class Users {
    String user_name,image,uid,email,time,type;

    public Users(String user_name, String uid, String email,String time,String type) {
        this.user_name = user_name;
        this.uid = uid;
        this.email = email;
        this.time = time;
        this.type = type;
    }

    public Users(){

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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
