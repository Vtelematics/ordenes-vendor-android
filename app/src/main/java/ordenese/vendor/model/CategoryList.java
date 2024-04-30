package ordenese.vendor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CategoryList {

    @SerializedName("category")
    @Expose
    private ArrayList<GroceryCategoryDataSet> category = new ArrayList<>();
    @SerializedName("success")
    @Expose
    private Success success;

    public ArrayList<GroceryCategoryDataSet> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<GroceryCategoryDataSet> category) {
        this.category = category;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

}
