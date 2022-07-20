package ink.wyy.bean;

import com.google.gson.annotations.SerializedName;

public class Notice {

    private Integer id;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("order_id")
    private Integer orderId;

    @SerializedName("commodity_id")
    private Integer commodityId;

    @SerializedName("class")
    private String classification;

    private String content;

    @SerializedName("from_user")
    private Integer fromUser;

    private String time;

    private Boolean unread;

    public Notice() {
    }

    public Notice(Integer userId, String classification, String content) {
        this.userId = userId;
        this.classification = classification;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getFromUser() {
        if (fromUser == null) {
            return -1;
        }
        return fromUser;
    }

    public void setFromUser(Integer fromUser) {
        this.fromUser = fromUser;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getUnread() {
        return unread;
    }

    public void setUnread(Boolean unread) {
        this.unread = unread;
    }
}
