package com.twsyt.merchant.model.order;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.twsyt.merchant.model.PaymentInfo;
import com.twsyt.merchant.model.User;
import com.twsyt.merchant.model.menu.DeliveryZone;
import com.twsyt.merchant.model.menu.Items;
import com.twsyt.merchant.model.menu.OrderAction;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Raman on 3/30/2016.
 */
public class OrderHistory implements Serializable {
    private String locality_2;

    private String locality_1;

    private String city;

    @SerializedName("outlet_name")
    private String outletName;

    String phone;

    @SerializedName("delivery_experience")
    private double deliveryExperience;

    @SerializedName("items")
    private ArrayList<Items> items = new ArrayList<Items>();

    @SerializedName("address")
    private Address address;

    @SerializedName("is_favourite")
    private boolean isFavourite;

    @SerializedName("order_cost")
    private Double orderCost;

    @SerializedName("cashback")
    private Double cashBack;

    @SerializedName("order_date")
    private String orderDate;

    @SerializedName("order_status")
    private String orderStatus;

    @SerializedName("menu_id")
    private String menuId;

    @SerializedName("outlet")
    private String outletId;

    @SerializedName("_id")
    private String orderID;

    @SerializedName("order_number")
    private String orderNumber;

    @SerializedName("background")
    private String background;

    @SerializedName("delivery_zone")
    private ArrayList<DeliveryZone> delivery_zone;

    @SerializedName("logo")
    private String logo;

    @SerializedName("actions")
    private ArrayList<OrderAction> orderActionsList;

    public boolean isNotified_am() {
        return notified_am;
    }

    public void setNotified_am(boolean notified_am) {
        this.notified_am = notified_am;
    }

    private boolean notified_am;
    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    @SerializedName("payment_info")
    private PaymentInfo paymentInfo;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @SerializedName("user")
    private User user;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public ArrayList<Items> getItems() {
        return items;
    }

    public void setItems(ArrayList<Items> items) {
        this.items = items;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public double getDeliveryExperience() {
        return deliveryExperience;
    }

    public void setDeliveryExperience(double deliveryExperience) {
        this.deliveryExperience = deliveryExperience;
    }

    public ArrayList<OrderAction> getOrderActionsList() {
        return orderActionsList;
    }

    public void setOrderActionsList(ArrayList<OrderAction> orderActionsList) {
        this.orderActionsList = orderActionsList;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Double getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(Double orderCost) {
        this.orderCost = orderCost;
    }

    public Double getCashBack() {
        return cashBack;
    }

    public void setCashBack(Double cashBack) {
        this.cashBack = cashBack;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isDelivered() {
        switch (orderStatus.toUpperCase()) {
            case "DELIVERED":
                return true;
        }
        return false;
    }

    public boolean isTrackable() {
        switch (orderStatus.toUpperCase()) {
            case "CLOSED":
                return false;
            case "CHECKOUT":
                return false;
            case "PAYMENT_FAILED":
                return false;
            case "REJECTED":
                return false;
            case "CANCELLED":
                return false;
        }
        return true; // Pending, Delivered, ACCEPTED, DISPATCHED
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public ArrayList<DeliveryZone> getDelivery_zone() {
        return delivery_zone;
    }

    public void setDelivery_zone(ArrayList<DeliveryZone> delivery_zone) {
        this.delivery_zone = delivery_zone;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality_1() {
        return locality_1;
    }

    public void setLocality_1(String locality_1) {
        this.locality_1 = locality_1;
    }

    public String getLocality_2() {
        return locality_2;
    }

    public void setLocality_2(String locality_2) {
        this.locality_2 = locality_2;
    }


    public String addressString() {
        String s = "";
        boolean added = false;
        if (!TextUtils.isEmpty(getLocality_1())) {
            s = getLocality_1();
            added = true;
        }

        if (!TextUtils.isEmpty(getLocality_2())) {
            if (added) {
                s = s + ", ";
            }
            s = s + getLocality_2();
            added = true;
        }

        if (!TextUtils.isEmpty(getCity())) {
            if (added) {
                s = s + ", ";
            }
            s = s + getCity();
        }

        return s;
    }


}
