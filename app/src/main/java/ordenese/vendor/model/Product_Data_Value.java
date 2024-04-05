package ordenese.vendor.model;

/**
 * Created by happy on 11/30/2018.
 */

public class Product_Data_Value {
    private String Language_name,Product_Name,Product_description,Delivery_note,Item_note;

    public String getProduct_Name() {
        return Product_Name;
    }

    public void setProduct_Name(String product_Name) {
        Product_Name = product_Name;
    }

    public String getProduct_description() {
        return Product_description;
    }

    public void setProduct_description(String product_description) {
        Product_description = product_description;
    }

    public String getDelivery_note() {
        return Delivery_note;
    }

    public void setDelivery_note(String delivery_note) {
        Delivery_note = delivery_note;
    }

    public String getItem_note() {
        return Item_note;
    }

    public void setItem_note(String item_note) {
        Item_note = item_note;
    }

    public String getLanguage_name() {
        return Language_name;
    }

    public void setLanguage_name(String language_name) {
        Language_name = language_name;
    }
}
