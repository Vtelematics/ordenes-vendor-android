package ordenese.vendor.model;

import java.util.ArrayList;

/**
 * Created by happy on 11/21/2018.
 */

public class Restaurant_Info {

    private StoreInfo storeInfo;
    private ArrayList<Store_Cuisine> store_cuisines;
    private ArrayList<Payment_method> payment_methods;
    private String non_available_date;

    public Restaurant_Info() {
    }

    public StoreInfo getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(StoreInfo storeInfo) {
        this.storeInfo = storeInfo;
    }

    public ArrayList<Store_Cuisine> getStore_cuisines() {
        return store_cuisines;
    }

    public void setStore_cuisines(ArrayList<Store_Cuisine> store_cuisines) {
        this.store_cuisines = store_cuisines;
    }


    public ArrayList<Payment_method> getPayment_methods() {
        return payment_methods;
    }

    public void setPayment_methods(ArrayList<Payment_method> payment_methods) {
        this.payment_methods = payment_methods;
    }

    public String getNon_available_date() {
        return non_available_date;
    }

    public void setNon_available_date(String non_available_date) {
        this.non_available_date = non_available_date;
    }
}
