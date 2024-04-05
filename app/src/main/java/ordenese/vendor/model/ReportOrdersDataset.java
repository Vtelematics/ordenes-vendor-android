package ordenese.vendor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ReportOrdersDataset {
    @SerializedName("text_date")
    @Expose
    public String text_date;
    @SerializedName("product")
    @Expose
    private ArrayList<ReportOrdersList> product = new ArrayList<>();
    @SerializedName("total")
    @Expose
    public String total;
    @SerializedName("total_amount")
    @Expose
    public String totalAmount;
    @SerializedName("success")
    @Expose
    private Success success;

    public ArrayList<ReportOrdersList> getProduct() {
        return product;
    }

    public void setProduct(ArrayList<ReportOrdersList> product) {
        this.product = product;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }
}
