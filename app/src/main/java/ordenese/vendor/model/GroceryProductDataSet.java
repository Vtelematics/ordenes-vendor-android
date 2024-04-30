package ordenese.vendor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroceryProductDataSet {

    @SerializedName("product_item_id")
    @Expose
    public String product_item_id;

    @SerializedName("item_name")
    @Expose
    public String item_name;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("price")
    @Expose
    public String price;

    @SerializedName("qty")
    @Expose
    public String qty;

    @SerializedName("discount")
    @Expose
    public String discount;

    @SerializedName("picture")
    @Expose
    public String picture;


}
