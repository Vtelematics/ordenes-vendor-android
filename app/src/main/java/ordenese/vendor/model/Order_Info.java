package ordenese.vendor.model;

import java.util.ArrayList;

/**
 * Created by happy on 10/3/2018.
 */

public class Order_Info {

    private String firstname, lastname, email, phone,address,order_id,date_added,status,delivery_type,order_type,payment_type,vendor_type_id,
            comment,order_status_id,flat_no,delivery_time,schedule_status,schedule_date, schedule_time,time_added;
    private ArrayList<Order_status_histories> order_status_histories;
    private Store_info store_info;
    private ArrayList<Order_total_list> order_total_lists;
    private ArrayList<Delivery_product_details> delivery_product_details;

    public String getTime_added() {
        return time_added;
    }

    public void setTime_added(String time_added) {
        this.time_added = time_added;
    }

    public String getSchedule_status() {
        return schedule_status;
    }

    public void setSchedule_status(String schedule_status) {
        this.schedule_status = schedule_status;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public String getVendor_type_id() {
        return vendor_type_id;
    }

    public void setVendor_type_id(String vendor_type_id) {
        this.vendor_type_id = vendor_type_id;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getFlat_no() {
        return flat_no;
    }

    public void setFlat_no(String flat_no) {
        this.flat_no = flat_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getOrder_status_id() {
        return order_status_id;
    }

    public void setOrder_status_id(String order_status_id) {
        this.order_status_id = order_status_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<Order_status_histories> getOrder_status_histories() {
        return order_status_histories;
    }

    public void setOrder_status_histories(ArrayList<Order_status_histories> order_status_histories) {
        this.order_status_histories = order_status_histories;
    }

    public Store_info getStore_info() {
        return store_info;
    }

    public void setStore_info(Store_info store_info) {
        this.store_info = store_info;
    }

    public ArrayList<Order_total_list> getOrder_total_lists() {
        return order_total_lists;
    }

    public void setOrder_total_lists(ArrayList<Order_total_list> order_total_lists) {
        this.order_total_lists = order_total_lists;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(String delivery_type) {
        this.delivery_type = delivery_type;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public ArrayList<Delivery_product_details> getDelivery_product_details() {
        return delivery_product_details;
    }

    public void setDelivery_product_details(ArrayList<Delivery_product_details> delivery_product_details) {
        this.delivery_product_details = delivery_product_details;
    }
}
