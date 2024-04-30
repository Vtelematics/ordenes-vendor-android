package ordenese.vendor.fragment.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.vendor.Api.ApiClientGson;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.SunmiPrinterSDK.dataset.Printer_DataSet;
import ordenese.vendor.SunmiPrinterSDK.threadHelp.ThreadPoolManager;
import ordenese.vendor.SunmiPrinterSDK.utils.SunmiPrintHelper;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.databinding.FragmentOrderReportsBinding;
import ordenese.vendor.fragment.reports.Fragment_Report_OrderList;
import ordenese.vendor.model.GroceryProducts;
import ordenese.vendor.model.GroceryProductsList;
import ordenese.vendor.model.Model_ReportOrderHolder;
import ordenese.vendor.model.OrderSummaryDataSet;
import ordenese.vendor.model.Order_Info;
import ordenese.vendor.model.ReportDataSet;
import ordenese.vendor.model.ReportModelList;
import ordenese.vendor.model.ReportOrdersDataset;
import ordenese.vendor.model.ReportOrdersList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderReports extends Fragment {

    FragmentOrderReportsBinding binding;
    Activity activity;
    ApiInterface retrofitInterface;
    private ProgressDialog mProgressDialog;
    OrderListAdapter orderListAdapter;
    ReportAdapter reportAdapter;
    ArrayList<ReportOrdersList> reportOrdersListArrayList = new ArrayList<>();
    ArrayList<ReportModelList> reportModelListArrayList = new ArrayList<>();

    private int visibleThreshold = 6;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading;
    int page = 1, total = 0;
    ArrayList<String> colors_list;
    Bitmap bitmap_img_icon_banner;

    String filter_id = "";
    String history_filter_id = "";
    public OrderReports() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setLayoutDirection(
                    "ae".equalsIgnoreCase(AppLanguageSupport.getLanguage(getActivity())) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderReportsBinding.inflate(inflater, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        colors_list = new ArrayList<>();
        colors_list.add("#30B0C7");
        colors_list.add("#941751");
        colors_list.add("#FFC314");
        colors_list.add("#009193");
        colors_list.add("#9E080F");

        binding.printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!filter_id.equals("")){
                    getOrderSummary(filter_id);
                } else if(!history_filter_id.equals("")) {
                    getOrderListHistory(history_filter_id);
                }else {
                    Constant.showToast("please select Report option!");
                }
            }
        });
        binding.nestedView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY > oldScrollY) {
                        if (scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) {
                            //code to fetch more data for endless scrolling
                            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.orderList.getLayoutManager();
                            assert linearLayoutManager != null;
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }

                        }
                    }
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getReport();
    }

    private void getReport() {

        if (getActivity() != null) {
            if (Constant.isNetworkAvailable()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("language_id", "");

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    retrofitInterface = ApiClientGson.getClient().create(ApiInterface.class);
                    Call<ReportDataSet> Call = retrofitInterface.report_filter(Constant.DataGetValue(activity, Constant.Token), body);
                    mProgressDialog.show();
                    Call.enqueue(new Callback<ReportDataSet>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NonNull Call<ReportDataSet> call, @NonNull Response<ReportDataSet> response) {
                            if (response.isSuccessful()) {
                                ReportDataSet dataSet = response.body();
                                if (dataSet != null && dataSet.getSuccess() != null && dataSet.getFilter() != null) {
                                    reportModelListArrayList = dataSet.getFilter();
                                    binding.dashboardList.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                                    reportAdapter = new ReportAdapter(reportModelListArrayList, colors_list);
                                    binding.dashboardList.setAdapter(reportAdapter);

                                    getOrderList(reportModelListArrayList.get(0).getId());
                                }
                            }
                            mProgressDialog.cancel();
                        }

                        @Override
                        public void onFailure(@NonNull Call<ReportDataSet> call, @NonNull Throwable t) {
                            mProgressDialog.cancel();
                        }
                    });
                } catch (JSONException e) {
                    mProgressDialog.cancel();
                    //Log.e("415 Excep ", e.toString());
                    e.printStackTrace();
                }

            } else {
                mProgressDialog.cancel();
                Constant.LoadNetworkError(getChildFragmentManager());
            }
        }

    }

    private void getOrderList(String id) {

        page= 1;
        reportOrdersListArrayList.clear();

        if (getActivity() != null) {
            if (Constant.isNetworkAvailable()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("page", page);
                    jsonObject.put("page_per_unit", "10");
                    jsonObject.put("language_id", "");
                    jsonObject.put("filter", id);
                    jsonObject.put("pagination_status", "1");

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    retrofitInterface = ApiClientGson.getClient().create(ApiInterface.class);
                    Call<ReportOrdersDataset> Call = retrofitInterface.report_order_list(Constant.DataGetValue(activity, Constant.Token), body);
                    mProgressDialog.show();
                    Call.enqueue(new Callback<ReportOrdersDataset>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NonNull Call<ReportOrdersDataset> call, @NonNull Response<ReportOrdersDataset> response) {
                            if (response.isSuccessful()) {
                                ReportOrdersDataset dataset = response.body();
                                if (dataset != null) {

//                                    ArrayList<Printer_DataSet> dataList = PrepareSendDataHistory(dataset);
//                                    try {
//                                        if (dataset != null && dataset.getProduct().size() != 0) {
//                                            if (dataList != null) {
//                                                ThreadPoolManager.getInstance().executeTask(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        for (int i = 0; i <= dataList.size() - 1; i++) {
//                                                            if (dataList.get(i).getNewline()) {
//                                                                PrintNewLine();
//                                                            } else if (dataList.get(i).getImageExist()) {
//                                                                PrintImage(dataList.get(i).getmBitmapImage());
////                                            PrintBitmapImage(dataList.get(i).getmBitmapImage());
//                                                            } else {
//                                                                PrintText(dataList.get(i).getPrint_Content(), dataList.get(i).getFontSize(), dataList.get(i).isBold, dataList.get(i).getUnderLine(), dataList.get(i).getFontName());
//                                                            }
//                                                            try {
//                                                                Thread.sleep(500);
//                                                            } catch (InterruptedException e) {
//                                                                break;
//                                                            }
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }

                                    reportOrdersListArrayList = dataset.getProduct();
                                    total = Integer.parseInt(dataset.getTotal());

                                    binding.orderList.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                                    orderListAdapter = new OrderListAdapter(reportOrdersListArrayList);
                                    binding.orderList.setAdapter(orderListAdapter);

                                    setOnLoadMoreListener(() -> {
                                        page++; // to avoid unnecessary api hit
                                        if (page <= ((total / 10) + 1)) {
                                            mProgressDialog.show();
                                            try {
                                                jsonObject.put("filter", id);
                                                jsonObject.put("page", page);
                                                jsonObject.put("page_per_unit", "10");
                                                jsonObject.put("language_id", "1");

                                                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                                                retrofitInterface = ApiClientGson.getClient().create(ApiInterface.class);
                                                Call<ReportOrdersDataset> Call = retrofitInterface.report_order_list(Constant.DataGetValue(activity, Constant.Token), body);
                                                mProgressDialog.show();
                                                Call.enqueue(new Callback<ReportOrdersDataset>() {
                                                    @SuppressLint("NotifyDataSetChanged")
                                                    @Override
                                                    public void onResponse(@NonNull Call<ReportOrdersDataset> call, @NonNull Response<ReportOrdersDataset> response) {
                                                        if (response.isSuccessful()) {
                                                            ReportOrdersDataset dataset = response.body();
                                                            if (dataset != null && dataset.getSuccess() != null) {
                                                                ArrayList<ReportOrdersList> reportOrdersListArrayListTemp;
                                                                reportOrdersListArrayListTemp = dataset.getProduct();
                                                                reportOrdersListArrayList.addAll(reportOrdersListArrayListTemp);

                                                                orderListAdapter.notifyDataSetChanged();
                                                                setLoaded();
                                                            }
                                                        }
                                                        mProgressDialog.cancel();
                                                    }

                                                    @Override
                                                    public void onFailure(@NonNull Call<ReportOrdersDataset> call, @NonNull Throwable t) {
                                                        mProgressDialog.cancel();
                                                    }
                                                });
                                            } catch (JSONException e) {
                                                mProgressDialog.cancel();
                                                //Log.e("415 Excep ", e.toString());
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }else {
                                binding.orderList.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                                orderListAdapter = new OrderListAdapter(reportOrdersListArrayList);
                                binding.orderList.setAdapter(orderListAdapter);
                            }
                            mProgressDialog.cancel();
                        }

                        @Override
                        public void onFailure(@NonNull Call<ReportOrdersDataset> call, @NonNull Throwable t) {
                            mProgressDialog.cancel();
                        }
                    });
                } catch (JSONException e) {
                    mProgressDialog.cancel();
                    //Log.e("415 Excep ", e.toString());
                    e.printStackTrace();
                }

            } else {
                mProgressDialog.cancel();
                Constant.LoadNetworkError(getChildFragmentManager());
            }
        }

    }


    void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    void setLoaded() {
        loading = false;
    }

    class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<ReportOrdersList> orderList;

        OrderListAdapter(ArrayList<ReportOrdersList> orderList) {
            this.orderList = orderList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 2) {
                return new OrderViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_report_order, parent, false));
            } else {
                return new OrderEmptyViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 2) {
                OrderViewHolder orderViewHolder = (OrderViewHolder) holder;
                orderViewHolder.tv_OrderId.setText(": " + orderList.get(position).getOrderId());
                orderViewHolder.tv_CustomerName.setText(": " + orderList.get(position).getCustomer());
                orderViewHolder.tv_Products.setText(": " + orderList.get(position).getProducts());
                orderViewHolder.tv_Type.setText(": " + orderList.get(position).getOrderType());
                orderViewHolder.tv_PaymentType.setText(": " + orderList.get(position).getPaymentMethod());
                orderViewHolder.tv_TotalAmount.setText(": " + orderList.get(position).getTotal());
                orderViewHolder.tv_Delivery_date.setText(": " + orderList.get(position).getDeliveryDate());
            } else {
                OrderEmptyViewHolder orderEmptyViewHolder = (OrderEmptyViewHolder) holder;
                orderEmptyViewHolder.tv_OrderListEmpty.setText(getString(R.string.empty));
            }
        }

        @Override
        public int getItemCount() {
            if (orderList.size() > 0) {
                return orderList.size();
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (orderList != null) {
                if (orderList.size() > 0) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {

            TextView tv_OrderId, tv_CustomerName, tv_Products, tv_Type, tv_PaymentType, tv_TotalAmount, tv_Delivery_date;

            OrderViewHolder(View itemView) {
                super(itemView);
                tv_OrderId = itemView.findViewById(R.id.tv_report_order_id_value);
                tv_CustomerName = itemView.findViewById(R.id.tv_report_customer_value);
                tv_Products = itemView.findViewById(R.id.tv_report_products_value);
                tv_Type = itemView.findViewById(R.id.tv_report_type_value);
                tv_PaymentType = itemView.findViewById(R.id.tv_report_payment_type_value);
                tv_TotalAmount = itemView.findViewById(R.id.tv_report_total_order_amount_value);
                tv_Delivery_date = itemView.findViewById(R.id.tv_report_total_order_date_value);
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

    class ReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<ReportModelList> orderList;
        ArrayList<String> colors_list;
        ImageView checked_imageTemp;
        private boolean mFirstTime = true;
        private boolean mFirstTimeCheck = true;
        private boolean mFirstTimeHistory = false;
        private int selectedPosition = 0;
        private int selectedPositionCheck = 0;
        private int selectedPositionHistory= 0;

        private int selectedSummaryPosition = RecyclerView.NO_POSITION;
        private int selectedHistoryPosition = RecyclerView.NO_POSITION;

        ReportAdapter(ArrayList<ReportModelList> orderList, ArrayList<String> colors_list) {
            this.orderList = orderList;
            this.colors_list = colors_list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 2) {
                return new OrderTotalViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_report_order_total, parent, false));
            } else {
                return new OrderEmptyViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            OrderTotalViewHolder orderTotalViewHolder = (OrderTotalViewHolder) holder;
            if (holder.getItemViewType() == 2) {
                orderTotalViewHolder.tv_TotalOrder.setText(activity.getResources().getString(R.string.no_of_orders) + " : " + orderList.get(position).getCount());
                orderTotalViewHolder.tv_TotalOrderAmount.setText(activity.getResources().getString(R.string.cost) + " : " + orderList.get(position).getAmount());
                orderTotalViewHolder.tv_OrderValue.setText(orderList.get(position).getName());

                orderTotalViewHolder.report_linear.setBackgroundColor(Color.parseColor(colors_list.get(position)));

                if (position == 0) {
                    orderTotalViewHolder.checked_image.setVisibility(View.VISIBLE);
                    checked_imageTemp = orderTotalViewHolder.checked_image;
                    orderTotalViewHolder.history_report.setVisibility(View.VISIBLE);
//                    orderTotalViewHolder.history_report.setBackgroundResource(R.drawable.button_unselect);
                }else {
                    orderTotalViewHolder.history_report.setVisibility(View.GONE);
                }

                orderTotalViewHolder.report_linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int position = holder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            selectedPositionCheck = position;
                            notifyDataSetChanged();
                        }
//                        if (checked_imageTemp != null) {
//                            checked_imageTemp.setVisibility(View.GONE);
//                        }

                        checked_imageTemp = orderTotalViewHolder.checked_image;

                        getOrderList(orderList.get(position).getId());
                    }
                });
            } else {
                OrderEmptyViewHolder orderEmptyViewHolder = (OrderEmptyViewHolder) holder;
                orderEmptyViewHolder.tv_OrderListEmpty.setText(getString(R.string.empty));
            }

            orderTotalViewHolder.summary_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        selectedPosition = position;
                        selectedSummaryPosition = position;
                        selectedHistoryPosition = RecyclerView.NO_POSITION;
                        filter_id = orderList.get(position).getId();
                        history_filter_id = "";
                        notifyDataSetChanged();
                    }
                }
            });
            orderTotalViewHolder.history_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        selectedPosition = position;
                        selectedHistoryPosition = position;
                        selectedSummaryPosition = RecyclerView.NO_POSITION;
                        history_filter_id = orderList.get(position).getId();
                        filter_id = "";
                        notifyDataSetChanged();
                    }


                }
            });
            if (!mFirstTime) {
                if (position == selectedSummaryPosition) {
                    orderTotalViewHolder.summary_report.setBackgroundResource(R.drawable.button_border_select);
                    orderTotalViewHolder.history_report.setBackgroundResource(R.drawable.button_unselect);
                } else if (position == selectedHistoryPosition) {
                    orderTotalViewHolder.history_report.setBackgroundResource(R.drawable.button_border_select);
                    orderTotalViewHolder.summary_report.setBackgroundResource(R.drawable.button_unselect);
                } else {
                    orderTotalViewHolder.summary_report.setBackgroundResource(R.drawable.button_unselect);
                    orderTotalViewHolder.history_report.setBackgroundResource(R.drawable.button_unselect);
                }

                // Your other logic for checked_image
            } else {
                mFirstTime = false;
            }
            if (!mFirstTimeCheck) {
                if (position == selectedPositionCheck) {
                    orderTotalViewHolder.checked_image.setVisibility(View.VISIBLE);
                } else {
                    orderTotalViewHolder.checked_image.setVisibility(View.GONE);
                }
            } else{
                mFirstTimeCheck = false;
            }

        }

        @Override
        public int getItemCount() {
            if (orderList.size() > 0) {
                return orderList.size();
            }else {
                return 3;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (orderList != null) {
                if (orderList.size() > 0) {
                    return 2;
                } else {
                    return 3;
                }
            } else {
                return 3;
            }
        }

        class OrderTotalViewHolder extends RecyclerView.ViewHolder {

            TextView tv_TotalOrder, tv_TotalOrderAmount, tv_OrderValue;
            LinearLayout report_linear;
            ImageView checked_image;
            Button history_report,summary_report;

            OrderTotalViewHolder(View itemView) {
                super(itemView);
                tv_TotalOrder = itemView.findViewById(R.id.tv_total_value);
                checked_image = itemView.findViewById(R.id.checked_image);
                tv_TotalOrderAmount = itemView.findViewById(R.id.tv_order_value);
                tv_OrderValue = itemView.findViewById(R.id.tv_total_title);
                report_linear = itemView.findViewById(R.id.report_linear);
                history_report = itemView.findViewById(R.id.history_report);
                summary_report = itemView.findViewById(R.id.summary_report);
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

    public ArrayList<Printer_DataSet> PrepareSendData(OrderSummaryDataSet orderSummaryDataSet) {

        String vendor_name = "";

        try {
            JSONObject jsonObject = new JSONObject(Constant.DataGetValue(activity, Constant.StoreDetails));
            // Log.e("onCreate: ", jsonObject + "");
//            store_info.setText(Html.fromHtml(jsonObject.getString("vendor_name") + "<br>" + jsonObject.getString("email") + "<br>" + jsonObject.getString("mobile")));
            vendor_name = jsonObject.getString("vendor_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String fontName = "Sunmi monospace";
        String fontSizeContent = "18";
        String fontSizeHeading = "21";

        Printer_DataSet imageDataSet = new Printer_DataSet();
        ArrayList<Printer_DataSet> data_List = new ArrayList<>();

        //Set HeadingImage

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;
        if (bitmap_img_icon_banner == null) {
            bitmap_img_icon_banner = BitmapFactory.decodeResource(getResources(), R.drawable.logo_printer, options);
        }
        imageDataSet.setImageExist(true);
        imageDataSet.setmBitmapImage(bitmap_img_icon_banner);
        data_List.add(imageDataSet);

        //Initialize new line
        Printer_DataSet newLineDataSet = new Printer_DataSet();
        newLineDataSet.setNewline(true);
        data_List.add(newLineDataSet);


        //Set Header Text
        //Printer Accept 41 character in single line

        if(filter_id.equals("1")){

            data_List.add(getPackData(getCenterAlignPadding("Daily Summary Report" +"\n",31), "26", true, false,fontName));
        }else if(filter_id.equals("2")){
            data_List.add(getPackData(getCenterAlignPadding("Weekly Summary Report" +"\n",31), "26", true, false,fontName));
        }else if (filter_id.equals("3")){
            data_List.add(getPackData(getCenterAlignPadding("Monthly Summary Report" +"\n",31), "26", true, false,fontName));
        }else{
            data_List.add(getPackData(getCenterAlignPadding("Total Summary Report" +"\n",31), "26", true, false,fontName));
        }
        data_List.add(newLineDataSet);

        if (vendor_name != null && !vendor_name.isEmpty()) {
//            data_List.add(getPackData(""+getPadding(vendor_name +"\n",15), "26", true, false, fontName));
//            data_List.add(getPackData(""+getPadding(orderSummaryDataSet.textDate +"\n",15), "26", true, false, fontName));

            data_List.add(getPackData(" " + getPadding(vendor_name,15), "26", true, false, fontName));
            data_List.add(newLineDataSet);
            data_List.add(getPackData(" "+getPadding(orderSummaryDataSet.textDate,15), "26", true, false, fontName));
            data_List.add(newLineDataSet);
            if(orderSummaryDataSet.total_amount != null && !orderSummaryDataSet.total_amount.equals("")) {
                data_List.add(getPackData(" " + getPadding("Total Amount", 8) + ":" + getPadding(orderSummaryDataSet.total_amount, 15), "26", true, false, fontName));
                data_List.add(newLineDataSet);
            }
            if(orderSummaryDataSet.total_count != null && !orderSummaryDataSet.total_count.equals("")) {
                data_List.add(getPackData(" " + getPadding("Total Orders", 8) + ":" + getPadding(orderSummaryDataSet.total_count, 15), "25", true, false, fontName));
                data_List.add(newLineDataSet);
            }
            data_List.add(getPackData("  " + "----------------------------------------\n", fontSizeContent, false, false, fontName));

        } else {
            data_List.add(getPackData(" "+getPadding(activity.getResources().getString(R.string.app_name_in_print_receipt) ,15), "26", true, false, fontName));
            data_List.add(newLineDataSet);
            data_List.add(getPackData(" "+getPadding( orderSummaryDataSet.textDate,15), "26", true, false, fontName));
        }
        //ForNew Line
        data_List.add(newLineDataSet);
        //Order Details Content
        for(int i =0; i<orderSummaryDataSet.orderList.size(); i++){
            data_List.add(getPackData("  " + getPadding("Date", 16) + " : " + getCenterAlignPadding(orderSummaryDataSet.orderList.get(i).date + "\n", 10), "20", true, false, fontName));
            data_List.add(getPackData("  " + getPadding("Amount", 16) + " : " + getCenterAlignPadding(orderSummaryDataSet.orderList.get(i).total + "\n", 7), "20", true, false, fontName));
            data_List.add(getPackData("  " + getPadding("Number of Orders", 10) + " : " + getCenterAlignPadding(orderSummaryDataSet.orderList.get(i).orderCount + "\n", 1), "20", true, false, fontName));

            data_List.add(getPackData("  " + "----------------------------------------\n", fontSizeContent, false, false, fontName));
        }
        //ForNew Line
        data_List.add(newLineDataSet);

        //NewLine
//        data_List.add(newLineDataSet);


        //NewLine
        //data_List.add(newLineDataSet);

        //Total Heading
//        data_List.add(getPackData("  " + activity.getResources().getString(R.string.order_total) + "                    " + activity.getResources().getString(R.string.order_price) + "\n", fontSizeHeading, true, false, fontName));
//        data_List.add(getPackData("  " + "----------------------------------------", fontSizeContent, false, false, fontName));

        //ForNew Line
//        data_List.add(newLineDataSet);
        //NewLine
//        data_List.add(newLineDataSet);

//        data_List.add(getPackData("                  " + activity.getResources().getString(R.string.printer_text_thank_you) + "               " + "", fontSizeContent, true, false, fontName));

        //NewLine
//        data_List.add(newLineDataSet);

//        data_List.add(getPackData("   " + "---------------------------------------\n", fontSizeContent, false, false, fontName));

        //NewLine
        data_List.add(newLineDataSet);
        data_List.add(newLineDataSet);

        SunmiPrintHelper.getInstance().print1Line();
        SunmiPrintHelper.getInstance().cutpaper();

        return data_List;
    }

    public ArrayList<Printer_DataSet> PrepareSendDataHistory(ReportOrdersDataset dataset) {

        String vendor_name = "";

        try {
            JSONObject jsonObject = new JSONObject(Constant.DataGetValue(activity, Constant.StoreDetails));
            // Log.e("onCreate: ", jsonObject + "");
//            store_info.setText(Html.fromHtml(jsonObject.getString("vendor_name") + "<br>" + jsonObject.getString("email") + "<br>" + jsonObject.getString("mobile")));
            vendor_name = jsonObject.getString("vendor_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String fontName = "Sunmi monospace";
        String fontSizeContent = "18";
        String fontSizeHeading = "21";

        Printer_DataSet imageDataSet = new Printer_DataSet();
        ArrayList<Printer_DataSet> data_List = new ArrayList<>();

        //Set HeadingImage

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;
        if (bitmap_img_icon_banner == null) {
            bitmap_img_icon_banner = BitmapFactory.decodeResource(getResources(), R.drawable.logo_printer, options);
        }
        imageDataSet.setImageExist(true);
        imageDataSet.setmBitmapImage(bitmap_img_icon_banner);
        data_List.add(imageDataSet);

        //Initialize new line
        Printer_DataSet newLineDataSet = new Printer_DataSet();
        newLineDataSet.setNewline(true);
        data_List.add(newLineDataSet);

        //Set Header Text
        //Printer Accept 41 character in single line

        //Set Header Text
        //Printer Accept 41 character in single line
        data_List.add(getPackData(getCenterAlignPadding("Daily History Report" +"\n",31), "26", true, false,fontName));

        data_List.add(newLineDataSet);

        if (vendor_name != null && !vendor_name.isEmpty()) {
//            data_List.add(getPackData(""+getPadding(vendor_name +"\n",15), "26", true, false, fontName));
//            data_List.add(getPackData(""+getPadding(orderSummaryDataSet.textDate +"\n",15), "26", true, false, fontName));

            data_List.add(getPackData(" " + getPadding(vendor_name,15), "26", true, false, fontName));
            data_List.add(newLineDataSet);
            data_List.add(getPackData(" "+getPadding(dataset.text_date,15), "26", true, false, fontName));
            data_List.add(newLineDataSet);
            if(dataset.total != null && !dataset.total.equals("")) {
                data_List.add(getPackData(" " + getPadding("Total Order", 8) + ":" + getPadding(dataset.total, 15), "26", true, false, fontName));
                data_List.add(newLineDataSet);
            }
            if(dataset.total != null && !dataset.total.equals("")) {
                data_List.add(getPackData(" " + getPadding("Total Amount", 8) + ":" + getPadding(dataset.totalAmount, 15), "26", true, false, fontName));
                data_List.add(newLineDataSet);
            }
            data_List.add(getPackData("  " + "----------------------------------------\n", fontSizeContent, false, false, fontName));

        } else {
            data_List.add(getPackData(" "+getPadding(activity.getResources().getString(R.string.app_name_in_print_receipt) ,15), "26", true, false, fontName));
            data_List.add(newLineDataSet);
            data_List.add(getPackData(" "+getPadding( dataset.text_date,15), "26", true, false, fontName));

        }

        //ForNew Line
        data_List.add(newLineDataSet);

        //Order Details Content

        for(int i=0; i<dataset.getProduct().size(); i++) {
//        data_List.add(getPackData("  " + getPadding(activity.getResources().getString(R.string.report_order_id), 15) + " : " + getCenterAlignPadding(mOrder_Info.getOrder_id() + "\n", 23), fontSizeContent, false, false, fontName));
            data_List.add(getPackData("  " + getPadding("Order Number", 15) + " : " + getCenterAlignPadding(dataset.getProduct().get(i).getOrderId() + "\n", 3), fontSizeContent, true, false, fontName));
            data_List.add(getPackData("  " + getPadding("Customer", 15) + " : " + getCenterAlignPadding(dataset.getProduct().get(i).getCustomer() + "\n", 10), fontSizeContent, true, false, fontName));
            data_List.add(getPackData("  " + getPadding("Products", 15) + " : " + getCenterAlignPadding(dataset.getProduct().get(i).getProducts() + "\n", 1), fontSizeContent, true, false, fontName));
            data_List.add(getPackData("  " + getPadding("Type", 15) + " : " + getCenterAlignPadding(dataset.getProduct().get(i).getOrderType() + "\n", 9), fontSizeContent, true, false, fontName));
            data_List.add(getPackData("  " + getPadding(activity.getResources().getString(R.string.order_payment_type), 15) + " : " + getCenterAlignPadding(dataset.getProduct().get(i).getPaymentMethod() + "\n", 10), fontSizeContent, true, false, fontName));
            data_List.add(getPackData("  " + getPadding("Order Amount", 15) + " : " + getCenterAlignPadding(dataset.getProduct().get(i).getTotal() + "\n", 8), fontSizeContent, true, false, fontName));
            if(!dataset.getProduct().get(i).getDriver_name().equals("")){
                data_List.add(getPackData("  " + getPadding("Driver Name", 15) + " : " + getCenterAlignPadding(dataset.getProduct().get(i).getDriver_name() + "\n", 8), fontSizeContent, true, false, fontName));
            }
            data_List.add(getPackData("  " + getPadding("Delivery Date", 15) + " : " + getCenterAlignPadding(dataset.getProduct().get(i).getDeliveryDate() + "\n", 10), fontSizeContent, true, false, fontName));
            data_List.add(newLineDataSet);
            data_List.add(getPackData("   " + "---------------------------------------\n", fontSizeContent, false, false, fontName));
        }
        //NewLine
//        data_List.add(newLineDataSet);
        data_List.add(newLineDataSet);

        SunmiPrintHelper.getInstance().print1Line();
        SunmiPrintHelper.getInstance().cutpaper();

        return data_List;

    }
    private void getOrderSummary(String filter) {
        if (getActivity() != null) {
            if (Constant.isNetworkAvailable()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("filter", filter);

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    retrofitInterface = ApiClientGson.getClient().create(ApiInterface.class);
                    Call<OrderSummaryDataSet> Call = retrofitInterface.report_summary_list(Constant.DataGetValue(getActivity(), Constant.Token),body);
                    mProgressDialog.show();
                    Call.enqueue(new Callback<OrderSummaryDataSet>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NonNull Call<OrderSummaryDataSet> call, @NonNull Response<OrderSummaryDataSet> response) {
                            if (response.isSuccessful()) {
                                mProgressDialog.cancel();
                                OrderSummaryDataSet orderSummaryDataSet = new OrderSummaryDataSet();
                                orderSummaryDataSet = response.body();
                                ArrayList<Printer_DataSet> dataList = PrepareSendData(orderSummaryDataSet);

                                    if (orderSummaryDataSet != null && orderSummaryDataSet.orderList.size() != 0) {
                                        try {
                                        if (dataList != null) {
                                            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                                                @Override
                                                public void run() {
                                                    for (int i = 0; i <= dataList.size() - 1; i++) {
                                                        if (dataList.get(i).getNewline()) {
                                                            PrintNewLine();
                                                        } else if (dataList.get(i).getImageExist()) {
                                                            PrintImage(dataList.get(i).getmBitmapImage());
//                                            PrintBitmapImage(dataList.get(i).getmBitmapImage());
                                                        } else {
                                                            PrintText(dataList.get(i).getPrint_Content(), dataList.get(i).getFontSize(), dataList.get(i).isBold, dataList.get(i).getUnderLine(), dataList.get(i).getFontName());
                                                        }
                                                        try {
                                                            Thread.sleep(100);
                                                        } catch (InterruptedException e) {
                                                            break;
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                    }else {
                                        Constant.showToast("SummaryReport is Empty!");
                                    }
                            }else {
                                mProgressDialog.cancel();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<OrderSummaryDataSet> call, @NonNull Throwable t) {
                            mProgressDialog.cancel();
                        }
                    });
                } catch (JSONException e) {
                    mProgressDialog.cancel();
                    //Log.e("415 Excep ", e.toString());
                    e.printStackTrace();
                }
            } else {
                mProgressDialog.cancel();
                Constant.LoadNetworkError(getChildFragmentManager());
            }
        }
    }

    private void getOrderListHistory(String id){
        if (getActivity() != null) {
            if (Constant.isNetworkAvailable()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("page", 1);
                    jsonObject.put("page_per_unit", "10");
                    jsonObject.put("language_id", "");
                    jsonObject.put("filter", id);
                    jsonObject.put("pagination_status", "0");

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    retrofitInterface = ApiClientGson.getClient().create(ApiInterface.class);
                    Call<ReportOrdersDataset> Call = retrofitInterface.report_order_list(Constant.DataGetValue(activity, Constant.Token), body);
                    mProgressDialog.show();
                    Call.enqueue(new Callback<ReportOrdersDataset>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NonNull Call<ReportOrdersDataset> call, @NonNull Response<ReportOrdersDataset> response) {
                            if (response.isSuccessful()) {
                                mProgressDialog.cancel();
                                ReportOrdersDataset dataset = response.body();
                                if (dataset != null && dataset.getSuccess() != null) {

                                    ArrayList<Printer_DataSet> dataList = PrepareSendDataHistory(dataset);
                                    try {
                                        if (dataset != null && dataset.getProduct().size() != 0){
                                            if (dataList != null) {
                                                ThreadPoolManager.getInstance().executeTask(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        for (int i = 0; i <= dataList.size() - 1; i++) {
                                                            if (dataList.get(i).getNewline()) {
                                                                PrintNewLine();
                                                            } else if (dataList.get(i).getImageExist()) {
                                                                PrintImage(dataList.get(i).getmBitmapImage());
//                                            PrintBitmapImage(dataList.get(i).getmBitmapImage());
                                                            } else {
                                                                PrintText(dataList.get(i).getPrint_Content(), dataList.get(i).getFontSize(), dataList.get(i).isBold, dataList.get(i).getUnderLine(), dataList.get(i).getFontName());
                                                            }
                                                            try {
                                                                Thread.sleep(100);
                                                            } catch (InterruptedException e) {
                                                                break;
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }else {
                                            mProgressDialog.cancel();
                                            Constant.showToast("Order History is empty!");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else {
                                mProgressDialog.cancel();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<ReportOrdersDataset> call, @NonNull Throwable t){
                            mProgressDialog.cancel();
                        }
                    });
                } catch (JSONException e) {
                    mProgressDialog.cancel();
                    //Log.e("415 Excep ", e.toString());
                    e.printStackTrace();
                }
            } else {
                mProgressDialog.cancel();
                Constant.LoadNetworkError(getChildFragmentManager());
            }
        }
    }
    public Printer_DataSet getPackData(String content, String fontSize, Boolean isBold, Boolean isUnderLine, String fontName) {

        Printer_DataSet printerDataSet = new Printer_DataSet();
        printerDataSet.setPrint_Content(content);
        printerDataSet.setFontSize(fontSize);
        printerDataSet.setBold(isBold);
        printerDataSet.setUnderLine(isUnderLine);
        printerDataSet.setFontName(fontName);

        return printerDataSet;
    }
    public String getPadding(String data, int paddingsize) {
        if (data.length() >= paddingsize) {
            return data;
        }
        for (int i = data.length() + 1; i <= paddingsize; i++) {
            data = data + " ";
        }
        return data;
    }
    public String getCenterAlignPadding(String data, int paddingsize) {
        String data0;
        if (data.length() >= paddingsize) {
            return data;
        }
        data0 = data;
        if (data.contains("\n")) {
            data = data.replace("\n", "");
        }
        int remaining_padding;
        remaining_padding = paddingsize - data.length();
        for (int i = 0; i < remaining_padding / 2; i++) {
            data = " " + data;
        }
        for (int i = 0; i < remaining_padding / 2; i++) {
            data = data + " ";
        }

        if (data0.contains("\n")) {
            return data + "\n";
        } else {
            return data;
        }
    }
    public String getBilldetailwithAlign(String productname, String qty, String total) {
        String compineddata = "";
        String additionaldata = "";
        String extra_length_productname = "", extra_length_qty = "", extra_length_total = "";

        if (productname.length() <= 22) {
            compineddata = compineddata + getPadding(productname, 22) + " ";
        } else {
            if (productname.length() > 22) {
                compineddata = compineddata + getPadding(productname.substring(0, 22), 22) + " ";

                additionaldata = additionaldata + getPadding(productname.substring(22, productname.length()), 22);
            }
        }
        if (qty.length() <= 3) {
            compineddata = compineddata + getCenterAlignPadding(qty, 3);
        } else {
            if (qty.length() > 3) {
                compineddata = compineddata + getCenterAlignPadding(qty.substring(0, 3), 3);

                additionaldata = additionaldata + getCenterAlignPadding(qty.substring(3, qty.length()), 3);
            }
        }
        if (total.length() <= 14) {
            compineddata = compineddata + " " + getCenterAlignPadding(total, 14);
        } else {
            if (total.length() > 14) {
                compineddata = compineddata + " " + getCenterAlignPadding(total.substring(0, 14), 14);

                additionaldata = additionaldata + getCenterAlignPadding(total.substring(14, total.length()), 14);
            }
        }
        compineddata = compineddata + "\n" + additionaldata;
        return compineddata;
    }
    public void PrintText(String content, String fontSize, Boolean isBold, Boolean isUnderLine, String fontname) {
        try {
            float size = Integer.parseInt(fontSize);
            SunmiPrintHelper.getInstance().printText(content, size, isBold, isUnderLine, fontname);
            //Log.e("Test",""+content);
        } catch (Exception e) {
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void PrintImage(Bitmap bitmap) {
        try {
            SunmiPrintHelper.getInstance().printBitmap(bitmap);
            //Log.e("Test",""+content);
        } catch (Exception e) {
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void PrintNewLine() {
        try {
            //SunmiPrintHelper.getInstance().printNewLine();
            PrintText("\n", "14", false, false, "Sunmi monospace");
        } catch (Exception e) {
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}