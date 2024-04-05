package ordenese.vendor.common.instant_transfer;

/**
 * Created by happy on 12/14/2018.
 */

public interface Filter_Orders {
    void LoadFilterOrders(String start_Date, String end_Date, String delivery_Date, String order_id, String customer, String order_amount, String order_status_id);
}
