package ordenese.vendor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {
   @SerializedName("date")
   @Expose
   public String date;
   @SerializedName("total")
   @Expose
   public String total;
   @SerializedName("order_count")
   @Expose
   public String orderCount;


}
