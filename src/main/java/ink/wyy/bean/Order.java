package ink.wyy.bean;

import com.google.gson.annotations.SerializedName;

public class Order {

    private Integer id;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("commodity_id")
    private Integer commodityId;

    @SerializedName("address")
    private String address;

    @SerializedName("phone")
    private String phone;

    @SerializedName("remark")
    private String remark;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("is_ship")
    private Boolean isShip;

    @SerializedName("buy_num")
    private Integer buyNum;

    public Integer getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(Integer buyNum) {
        this.buyNum = buyNum;
    }

    public String getCommodityTitle() {
        return commodityTitle;
    }

    public void setCommodityTitle(String commodityTitle) {
        this.commodityTitle = commodityTitle;
    }

    public String getCommodityPic() {
        return commodityPic;
    }

    public void setCommodityPic(String commodityPic) {
        this.commodityPic = commodityPic;
    }

    @SerializedName("ship_time")
    private String shipTime;

    @SerializedName("order_time")
    private String orderTime;

    @SerializedName("commodity_title")
    private String commodityTitle;

    @SerializedName("commodity_pic")
    private String commodityPic;

    private String errorMsg;

    public String getReceiptTime() {
        return receiptTime;
    }

    public void setReceiptTime(String receiptTime) {
        this.receiptTime = receiptTime;
    }

    @SerializedName("seller_id")
    private Integer sellerId;

    @SerializedName("receipt_time")
    private String receiptTime;

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Order() {
    }

    public Order(Integer userId, Integer commodityId, String address, String phone) {
        this.userId = userId;
        this.commodityId = commodityId;
        this.address = address;
        this.phone = phone;
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

    public Integer getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getShip() {
        return isShip;
    }

    public void setShip(Boolean ship) {
        isShip = ship;
    }

    public String getShipTime() {
        return shipTime;
    }

    public void setShipTime(String shipTime) {
        this.shipTime = shipTime;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
