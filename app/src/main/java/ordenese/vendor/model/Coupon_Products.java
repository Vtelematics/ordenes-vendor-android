package ordenese.vendor.model;

/**
 * Created by happy on 11/28/2018.
 */

public class Coupon_Products {
    private String product_id,name;
    private boolean selected;
    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
