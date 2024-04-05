package ordenese.vendor.model;

import java.util.ArrayList;

/**
 * Created by happy on 11/26/2018.
 */

public class Register_fields {

    private ArrayList<Store_Cuisine> store_cuisines;
    private ArrayList<Payment_method> payment_methods;

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
}
