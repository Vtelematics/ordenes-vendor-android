package ordenese.vendor.model;

import java.util.ArrayList;

/**
 * Created by user on 8/22/2018.
 */

public class Delivery_product_details {

    private String name,image,quantity,total;

    public ArrayList<OptionValueModel> optionValueModels = new ArrayList<>();

    public ArrayList<OptionValueModel> getOptionValueModels() {
        return optionValueModels;
    }

    public void setOptionValueModels(ArrayList<OptionValueModel> optionValueModels) {
        this.optionValueModels = optionValueModels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

}
