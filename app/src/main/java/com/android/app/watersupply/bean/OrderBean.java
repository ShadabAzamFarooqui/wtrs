package com.android.app.watersupply.bean;

import java.util.List;

public class OrderBean {
    String bookingDate;
    String deliveryDate;
    List<Combine> waterTypeQuantity;
    String amount;
    String comment;
    boolean cashOnDelivery;
    String address;
    String orderId;
    String status;
    String reason;

    UserBean user;
    UserBean supplier;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String date) {
        this.bookingDate = date;
    }

    public List<Combine> combine() {
        return waterTypeQuantity;
    }

    public void setWaterTypeQuantity(List<Combine> waterTypeQuantity) {
        this.waterTypeQuantity = waterTypeQuantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isCashOnDelivery() {
        return cashOnDelivery;
    }

    public void setCashOnDelivery(boolean cashOnDelivery) {
        this.cashOnDelivery = cashOnDelivery;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public UserBean getSupplier() {
        return supplier;
    }

    public void setSupplier(UserBean supplier) {
        this.supplier = supplier;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
