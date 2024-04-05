package ordenese.vendor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrderSummaryDataSet {
   @SerializedName("text_date")
   @Expose
   public String textDate;
   @SerializedName("total_amount")
   @Expose
   public String total_amount;
   @SerializedName("total_count")
   @Expose
   public String total_count;
   @SerializedName("order_list")
   @Expose
   public ArrayList<Order> orderList;
   @SerializedName("success")
   @Expose
   public Success success;


}
