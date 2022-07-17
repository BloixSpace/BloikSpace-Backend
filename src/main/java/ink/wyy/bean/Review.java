package ink.wyy.bean;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("id")
    private Integer id;

    @SerializedName("order_id")
    private Integer orderId;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("commodity_id")
    private Integer commodityId;

    @SerializedName("seller_id")
    private Integer sellerId;

    @SerializedName("star")
    private Integer star;

    @SerializedName("content")
    private String content;

    @SerializedName("update_time")
    private String updateTime;

    private String errorMsg;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Review() {
    }

    public Review(Integer orderId, Integer star, String content) {
        this.orderId = orderId;
        this.star = star;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
