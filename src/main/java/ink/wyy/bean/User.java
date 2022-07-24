package ink.wyy.bean;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.Objects;

public class User {

    private String username;
    private String password;
    private Integer id;
    private String signature;
    @SerializedName("avatar_uri")
    private String avatarUri;
    @SerializedName("create_date")
    private Date createDate;
    private Integer level;  // 1普通，2管理员，3超管
    private String errorMsg;

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String username, String password, int id) {
        this.username = username;
        this.password = password;
        this.id = id;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean equals(User user) {
        if (user == null) {
            return false;
        }
        if (!username.equals(user.username)) {
            return false;
        }
        if (!password.equals(user.password)) {
            return false;
        }
        if (!signature.equals(user.signature)) {
            return false;
        }
        if (!avatarUri.equals(user.avatarUri)) {
            return false;
        }
        if (!level.equals(user.level)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", id=" + id +
                ", signature='" + signature + '\'' +
                ", avatarUri='" + avatarUri + '\'' +
                ", createDate=" + createDate +
                ", level=" + level +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
