package ordenese.vendor.fragment.reports;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.SunmiPrinterSDK.dataset.Printer_DataSet;
import ordenese.vendor.SunmiPrinterSDK.utils.SunmiPrintHelper;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.Filter_Orders;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.fragment.dialog.Filter_dialog.Dialog_filter_order;
import ordenese.vendor.model.Model_ReportOrderHolder;

import java.util.ArrayList;

import ordenese.vendor.model.ReportOrdersList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Report_OrderList extends Fragment implements Filter_Orders {

    private Activity activity;
    private View v_OrderList;
    private RecyclerView rc_OrderList;
    private ApiInterface apiInterface;
    private ProgressBar pb_Loader;
    private ArrayList<Model_ReportOrderHolder> reportOrderList = new ArrayList<>();
    private int total_count = 1, page = 1;
    private String url;
    FloatingActionButton floatingActionButton;
    String Order_id = "", Customer = "", Order_Amount = "", Delivery_date = "", Start_date = "", End_date = "", Order_Status = "";
    Filter_Orders filter_orders;


    ArrayList<Model_ReportOrderHolder> orderList;
    @Override
    public void onAttach(Context context) {

        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        filter_orders = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getActivity() != null) {

                getActivity().getWindow().getDecorView().setLayoutDirection(
                        "ar".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                                View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v_OrderList = inflater.inflate(R.layout.fragment_report_order_list, container, false);
        load();
        return v_OrderList;
    }

    private void load() {
        rc_OrderList = v_OrderList.findViewById(R.id.rc_report_order_list);
        rc_OrderList.setLayoutManager(new LinearLayoutManager(activity));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        pb_Loader = v_OrderList.findViewById(R.id.pb_loader);
        floatingActionButton = v_OrderList.findViewById(R.id.filter_edit);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDialogFilter(Order_id, Customer, Order_Amount, Delivery_date, Start_date, End_date, Order_Status);
            }
        });

        LoadOrderList(Order_id, Customer, Order_Amount, Delivery_date, Start_date, End_date, Order_Status);
    }

    private void LoadDialogFilter(String order_id, String customer, String order_amount, String delivery_date, String start_date, String end_date, String order_status) {
        Bundle bundle = new Bundle();
        bundle.putString("start_date", start_date);
        bundle.putString("end_date", end_date);
        bundle.putString("delivery_date", delivery_date);
        bundle.putString("order_id", order_id);
        bundle.putString("order_customer", customer);
        bundle.putString("order_amount", order_amount);
        bundle.putString("order_status", order_status);
        Dialog_filter_order dialog_filter_order = new Dialog_filter_order();
        dialog_filter_order.setArguments(bundle);
        dialog_filter_order.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog_filter_order.AssignOrderhandler(filter_orders);
        dialog_filter_order.show(getChildFragmentManager(), "Filter");
    }



    private void urlSetup() {
        url = ApiClient.base_url + Constant.REPORT_ORDER + page;
    }

    private void LoadOrderList(String order_id, String customer, String order_Amount, String delivery_date, String start_date, String end_date, String order_Status) {
        pb_Loader.setVisibility(View.VISIBLE);
        urlSetup();
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getOrderListReport(Constant.DataGetValue(activity, Constant.Token),
                start_date, end_date, delivery_date,
                order_Status, order_id,
                customer, order_Amount,language);
        //Call<String> call = apiInterface.getOrderListReport(Constant.DataGetValue(activity, Constant.Token), url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                pb_Loader.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    reportOrderList = ContentJsonParser.getReportOrderList(response.body());
                    total_count = ContentJsonParser.getTotalCount(response.body());
                    OrderListReportAdapter orderListReportAdapter = new OrderListReportAdapter(reportOrderList);
                    rc_OrderList.setAdapter(orderListReportAdapter);

                    /*if (total_count > 1) {
                        orderListReportAdapter.setOnLoadMoreListener(() -> {
                            if (page <= ((total_count / 6) + 1)) {
                                page++;
                                urlSetup();
                                Call<String> call1 = apiInterface.getOrderListReport(Constant.DataGetValue(activity, Constant.Token), url);
                                call1.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(@NonNull Call<String> call1, @NonNull Response<String> responsePaging) {
                                        pb_Loader.setVisibility(View.GONE);
                                        if (responsePaging.isSuccessful()) {

                                            ArrayList<Model_ReportOrderHolder> temp_section_list = ContentJsonParser.getReportOrderList(responsePaging.body());

                                            if (temp_section_list != null) {
                                                if (temp_section_list.size() > 0) {
                                                    reportOrderList.addAll(temp_section_list);
                                                }

                                            }
                                            orderListReportAdapter.notifyDataSetChanged();
                                            orderListReportAdapter.setLoaded();

                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<String> call1, @NonNull Throwable t) {
                                        pb_Loader.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    }*/

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                pb_Loader.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void LoadFilterOrders(String start_Date, String end_Date, String delivery_Date, String order_id, String customer, String order_amount, String order_status_id) {
        LoadOrderList(order_id, customer, order_amount, delivery_Date, start_Date, end_Date, order_status_id);
        Order_id = order_id;
        Customer = customer;
        Order_Amount = order_amount;
        Delivery_date = delivery_Date;
        Start_date = start_Date;
        End_date = end_Date;
        Order_Status = order_status_id;
    }

    class OrderListReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private int visibleThreshold = 6;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean loading;

        OrderListReportAdapter(ArrayList<Model_ReportOrderHolder> orderList) {
            orderList = orderList;
            if (rc_OrderList.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rc_OrderList.getLayoutManager();
                rc_OrderList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }
                });
            }
        }

        void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        void setLoaded() {
            loading = false;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if (viewType == 1) {
                return new OrderTotalViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_report_order_total, parent, false));
            } else if (viewType == 2) {
                return new OrderViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_report_order, parent, false));
            } else {
                return new OrderViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                OrderTotalViewHolder orderTotalViewHolder = (OrderTotalViewHolder) holder;
                orderTotalViewHolder.tv_TotalOrder.setText(String.valueOf(orderList.get(position).getTotal_order()));
                orderTotalViewHolder.tv_TotalProductCount.setText(String.valueOf(orderList.get(position).getTotal_products()));
                orderTotalViewHolder.tv_TotalOrderAmount.setText(String.valueOf(orderList.get(position).getTotal_price()));

                String todayTotal, weekTotal, monthTotal, allTotal, todayOrders, weekOrders, monthOrders, allOrders;

                todayTotal = getString(R.string.txt_total_order_title) + " : " + orderList.get(position).getTodayTotal();
                weekTotal = getString(R.string.txt_total_order_title) + " : " + orderList.get(position).getWeekTotal();
                monthTotal = getString(R.string.txt_total_order_title) + " : " + orderList.get(position).getMonthTotal();
                allTotal = getString(R.string.txt_total_order_title) + " : " + orderList.get(position).getTotal_order();

                orderTotalViewHolder.tv_TodayTotalValue.setText(todayTotal);
                orderTotalViewHolder.tv_WeekTotalValue.setText(weekTotal);
                orderTotalViewHolder.tv_MonthTotalValue.setText(monthTotal);
                orderTotalViewHolder.tv_TotalValue.setText(allTotal);

                todayOrders = getString(R.string.txt_total_cost_title) + " : " + orderList.get(position).getTodayOrders();
                weekOrders = getString(R.string.txt_total_cost_title) + " : " + orderList.get(position).getWeekOrders();
                monthOrders = getString(R.string.txt_total_cost_title) + " : " + orderList.get(position).getMonthOrders();
                allOrders = getString(R.string.txt_total_cost_title) + " : " + orderList.get(position).getTotal_price();

                orderTotalViewHolder.tv_TodayOrderValue.setText(todayOrders);
                orderTotalViewHolder.tv_WeekOrderValue.setText(weekOrders);
                orderTotalViewHolder.tv_MonthOrderValue.setText(monthOrders);
                orderTotalViewHolder.tv_TotalOrderValue.setText(allOrders);

            } else if (holder.getItemViewType() == 2) {
                OrderViewHolder orderViewHolder = (OrderViewHolder) holder;
                orderViewHolder.tv_OrderId.setText(": " + orderList.get(position).getOrderDetail().getOrder_id());
                orderViewHolder.tv_CustomerName.setText(": " + orderList.get(position).getOrderDetail().getCustomerName());
                orderViewHolder.tv_Store.setText(": " + orderList.get(position).getOrderDetail().getRestaurant());
                orderViewHolder.tv_Products.setText(": " + orderList.get(position).getOrderDetail().getProducts());
                orderViewHolder.tv_Type.setText(": " + orderList.get(position).getOrderDetail().getOrder_type());
                orderViewHolder.tv_PaymentType.setText(": " + orderList.get(position).getOrderDetail().getPayment_method());
                orderViewHolder.tv_TotalAmount.setText(": " + orderList.get(position).getOrderDetail().getTotal());
            } else {


                OrderEmptyViewHolder orderEmptyViewHolder = (OrderEmptyViewHolder) holder;
                orderEmptyViewHolder.tv_OrderListEmpty.setText(getString(R.string.empty));
            }
        }

        @Override
        public int getItemCount() {
            if (orderList != null) {
                if (orderList.size() > 0) {
                    return orderList.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (orderList != null) {
                if (orderList.size() > 0) {
                    if (orderList.get(position).getType().equals(Constant.REPORT_TYPE_TOTAL)) {
                        return 1;
                    } else {
                        return 2;
                    }
                } else {
                    return 3;
                }
            } else {
                return 3;
            }
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {

            TextView tv_OrderId, tv_CustomerName, tv_Store, tv_Products, tv_Type, tv_PaymentType, tv_TotalAmount;

            OrderViewHolder(View itemView) {
                super(itemView);
                tv_OrderId = itemView.findViewById(R.id.tv_report_order_id_value);
                tv_CustomerName = itemView.findViewById(R.id.tv_report_customer_value);
                tv_Store = itemView.findViewById(R.id.tv_report_store_value);
                tv_Products = itemView.findViewById(R.id.tv_report_products_value);
                tv_Type = itemView.findViewById(R.id.tv_report_type_value);
                tv_PaymentType = itemView.findViewById(R.id.tv_report_payment_type_value);
                tv_TotalAmount = itemView.findViewById(R.id.tv_report_total_order_amount_value);
            }
        }

        class OrderTotalViewHolder extends RecyclerView.ViewHolder {

            TextView tv_TotalOrder, tv_TotalProductCount, tv_TotalOrderAmount;

            TextView tv_TodayTotalValue, tv_WeekTotalValue, tv_MonthTotalValue, tv_TotalValue,
                    tv_TodayOrderValue, tv_WeekOrderValue, tv_MonthOrderValue, tv_TotalOrderValue;

            OrderTotalViewHolder(View itemView) {
                super(itemView);
//                tv_TotalOrder = itemView.findViewById(R.id.tv_report_total_order_value);
//                tv_TotalProductCount = itemView.findViewById(R.id.tv_report_total_product_value);
//                tv_TotalOrderAmount = itemView.findViewById(R.id.tv_report_order_total_value);
//
//                tv_TodayTotalValue = itemView.findViewById(R.id.tv_today_total_value);
//                tv_WeekTotalValue = itemView.findViewById(R.id.tv_week_total_value);
//                tv_MonthTotalValue = itemView.findViewById(R.id.tv_month_total_value);
//                tv_TotalValue = itemView.findViewById(R.id.tv_total_value);
//
//                tv_TodayOrderValue = itemView.findViewById(R.id.tv_today_order_value);
//                tv_WeekOrderValue = itemView.findViewById(R.id.tv_week_order_value);
//                tv_MonthOrderValue = itemView.findViewById(R.id.tv_month_order_value);
//                tv_TotalOrderValue = itemView.findViewById(R.id.tv_order_value);
            }
        }

        class OrderEmptyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_OrderListEmpty;

            OrderEmptyViewHolder(View itemView) {
                super(itemView);
                tv_OrderListEmpty = itemView.findViewById(R.id.tv_empty);
            }
        }
    }



}
