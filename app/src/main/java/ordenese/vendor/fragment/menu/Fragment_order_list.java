package ordenese.vendor.fragment.menu;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.LoginPageHandler;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.model.Order_List;
import ordenese.vendor.model.Order_Status;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_order_list extends Fragment {

    private View v_OrderListHolder;
    private RecyclerView Rc_OrderList;
    private Activity activity;
    private ProgressBar pb_LoaderOrder;
    private ApiInterface apiInterface;
    private int page = 1, total = 0;
    private RecyclerViewOrderListAdapter recyclerViewOrderListAdapter;
    private ArrayList<Order_List> orderLists = new ArrayList<>();
    private LoginPageHandler homePageHandler;
    private String url, orderStatus = null;
    //private TextView tv_Filter;
    private EditText et_OrderId, et_CustomerName, et_OrderAmount;
    private Spinner s_OrderStatus;
    private TextView tv_FilterOrderDate, tv_DeliveryDate;
    private ArrayList<Order_Status> orderStatusList = new ArrayList<>();
    private LinearLayout ll_FilterContainer;

    public Fragment_order_list() {

    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        this.homePageHandler = (LoginPageHandler) context;
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setLayoutDirection(
                    "ar".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_OrderListHolder = inflater.inflate(R.layout.fragment_order_list, container, false);
        setHasOptionsMenu(true);
        load();
        return v_OrderListHolder;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_filter:
                //tv_Filter.setVisibility(View.GONE);
                ll_FilterContainer.setVisibility(View.VISIBLE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        urlSetup();
        LoadOrderList();
    }

    private void load() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Button btn_Apply, btn_Cancel;
        btn_Apply = v_OrderListHolder.findViewById(R.id.btn_apply_order);
        btn_Cancel = v_OrderListHolder.findViewById(R.id.btn_cancel_order);


        et_OrderId = v_OrderListHolder.findViewById(R.id.et_order_order_id);
        et_CustomerName = v_OrderListHolder.findViewById(R.id.et_order_customer);
        s_OrderStatus = v_OrderListHolder.findViewById(R.id.s_order_status_order);
        tv_FilterOrderDate = v_OrderListHolder.findViewById(R.id.tv_order_start_date);
        et_OrderAmount = v_OrderListHolder.findViewById(R.id.et_order_amount);
        tv_DeliveryDate = v_OrderListHolder.findViewById(R.id.tv_order_delivery_date);
        //tv_Filter = v_OrderListHolder.findViewById(R.id.tv_filter_order);
        ll_FilterContainer = v_OrderListHolder.findViewById(R.id.ll_filter_order_bg);

        Rc_OrderList = v_OrderListHolder.findViewById(R.id.rc_order_list);
        pb_LoaderOrder = v_OrderListHolder.findViewById(R.id.progressBar);

        urlSetup();

        if (orderLists != null) {
            if (orderLists.size() > 0) {
                page = 1;

                for (int i = 0; i < orderLists.size(); i++) {
                    orderLists.remove(i);
                }
            }
        }

        tv_FilterOrderDate.setOnClickListener(v -> loadDateDialog(1));

        tv_DeliveryDate.setOnClickListener(v -> loadDateDialog(2));

       /* tv_Filter.setOnClickListener(v -> {
            tv_Filter.setVisibility(View.GONE);
            ll_FilterContainer.setVisibility(View.VISIBLE);
        });*/

        btn_Cancel.setOnClickListener(v -> {
            //tv_Filter.setVisibility(View.VISIBLE);
            ll_FilterContainer.setVisibility(View.GONE);
        });

        btn_Apply.setOnClickListener(v -> {
            page = 1;
            urlSetup();
            LoadOrderList();
            //tv_Filter.setVisibility(View.VISIBLE);
            ll_FilterContainer.setVisibility(View.GONE);
        });

        ll_FilterContainer.setOnClickListener(v -> {
            ll_FilterContainer.setVisibility(View.GONE);
            //tv_Filter.setVisibility(View.VISIBLE);
        });

        LoadOrderStatus();
        LoadOrderList();
    }

    private void urlSetup() {

        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        url = ApiClient.base_url + Constant.MY_ORDER + page + "&language_id=" + language;

        if (et_OrderId.getText().toString().length() > 0) {
            url = url + Constant.ORDER_ID + et_OrderId.getText().toString();
        }

        if (orderStatus != null) {
            if (orderStatus.length() > 0) {
                if (Integer.valueOf(orderStatus) > 0)
                    url = url + Constant.ORDER_STATUS + orderStatus;
            }
        }

        if (!tv_FilterOrderDate.getText().toString().equals(getString(R.string.order_date))) {
            url = url + Constant.ADD_DATE + tv_FilterOrderDate.getText().toString();
        }

        if (!tv_DeliveryDate.getText().toString().equals(getString(R.string.filter_delivery_date))) {
            url = url + Constant.DATE_DELIVERY + tv_DeliveryDate.getText().toString();
        }

        if (et_OrderAmount.getText().toString().length() > 0) {
            url = url + Constant.TOTAL + et_OrderAmount.getText().toString();
        }

        if (et_CustomerName.getText().toString().length() > 0) {
            url = url + Constant.CUSTOMER + et_CustomerName.getText().toString();
        }

    }

    private void LoadOrderStatus() {

        pb_LoaderOrder.setVisibility(View.VISIBLE);
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getOrderStatusListReport(Constant.DataGetValue(activity, Constant.Token), language);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                pb_LoaderOrder.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    orderStatusList = ContentJsonParser.getOrderStatus(response.body());
                    s_OrderStatus.setAdapter(new SpinnerStatusAdapter(orderStatusList));

                    s_OrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (orderStatusList != null)
                                if (orderStatusList.get(position).getOrder_ststus_id() != null)
                                    if (!orderStatusList.get(position).getOrder_ststus_id().equals("-1")) {
                                        orderStatus = orderStatusList.get(position).getOrder_ststus_id();
                                    }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                pb_LoaderOrder.setVisibility(View.GONE);
            }
        });
    }

    private void LoadOrderList() {

        if (Constant.isNetworkAvailable()) {
            try {
                JSONObject object = new JSONObject();
                object.put("page_per_unit", "10");
                object.put("page", String.valueOf(page));
                object.put("order_status_id", orderStatus);
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

                Call<String> call = apiInterface.getOrderList(Constant.DataGetValue(activity, Constant.Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        pb_LoaderOrder.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            total = ContentJsonParser.getOrderTotalCount(response.body());

                            orderLists = ContentJsonParser.getOrderLIst(response.body());
                            Rc_OrderList.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerViewOrderListAdapter = new RecyclerViewOrderListAdapter();

                            Rc_OrderList.setAdapter(recyclerViewOrderListAdapter);
                            recyclerViewOrderListAdapter.setOnLoadMoreListener(() -> {

                                if (page <= ((total / 10) + 1)) {
                                    if (Constant.isNetworkAvailable()) {
                                        page++;
                                        urlSetup();
                                        pb_LoaderOrder.setVisibility(View.VISIBLE);
                                        apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                        Call<String> call1 = apiInterface.getOrderList(Constant.DataGetValue(activity, Constant.Token), body);
                                        call1.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(@NonNull Call<String> call1, @NonNull Response<String> response1) {
                                                pb_LoaderOrder.setVisibility(View.GONE);
                                                if (response1.isSuccessful()) {
                                                    if (response1.body() != null) {
                                                        ArrayList<Order_List> temp_order_lists = ContentJsonParser.getOrderLIst(response1.body());
                                                        if (temp_order_lists != null) {
                                                            if (temp_order_lists.size() > 0) {
                                                                orderLists.addAll(temp_order_lists);
                                                            }
                                                        }
                                                        recyclerViewOrderListAdapter.notifyDataSetChanged();
                                                        recyclerViewOrderListAdapter.setLoaded();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<String> call1, @NonNull Throwable t) {
                                                pb_LoaderOrder.setVisibility(View.GONE);
                                            }
                                        });

                                    } else {
                                        Constant.LoadNetworkError(getChildFragmentManager());
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        pb_LoaderOrder.setVisibility(View.GONE);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Constant.LoadNetworkError(getChildFragmentManager());
        }
    }

    private void loadDateDialog(int status) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    if (status == 1) {
                        tv_FilterOrderDate.setText(date);
                    } else {
                        tv_DeliveryDate.setText(date);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    class RecyclerViewOrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean loading;

        RecyclerViewOrderListAdapter() {
            if (Rc_OrderList.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) Rc_OrderList.getLayoutManager();
                Rc_OrderList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            if (viewType == 2) {
                return new OrderListViewHolder(LayoutInflater.from(activity).inflate(R.layout.fragment_order_list_adapter, parent, false));
            } else {
                return new EmptyViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 2) {
                OrderListViewHolder orderListViewHolder = (OrderListViewHolder) holder;
                orderListViewHolder.Customer_name.setText(": " + orderLists.get(position).getName());
                orderListViewHolder.Customer_phone.setText(": " + orderLists.get(position).getTelephone());
                orderListViewHolder.Order_date.setText(": " + orderLists.get(position).getDate());
                orderListViewHolder.Order_total.setText(": " + orderLists.get(position).getTotal());
                orderListViewHolder.Order_status.setText(": " + orderLists.get(position).getStatus());
                orderListViewHolder.order_id.setText(": " + orderLists.get(position).getOrder_id());
                if (orderLists.get(position).getSchedule_status() != null && orderLists.get(position).getSchedule_status().equals("1")) {
                    orderListViewHolder.schedule_title.setVisibility(View.VISIBLE);
                    orderListViewHolder.tv_schedule.setVisibility(View.VISIBLE);
                    orderListViewHolder.tv_schedule.setText(": " + orderLists.get(position).getSchedule_date() +" " + orderLists.get(position).getSchedule_time());
                }else {
                    orderListViewHolder.schedule_title.setVisibility(View.GONE);
                    orderListViewHolder.tv_schedule.setVisibility(View.GONE);
                }
                if (orderLists.get(position).getOrder_type().equals("2")) {
                    orderListViewHolder.delivery_type.setText(": " + activity.getResources().getString(R.string.reg_pickup));
                } else {
                    orderListViewHolder.delivery_type.setText(": " + activity.getResources().getString(R.string.reg_delivery));
                }
//                orderListViewHolder.delivery_type.setText(": " + orderLists.get(position).getDelivery_type());
                orderListViewHolder.order_edit.setOnClickListener(v -> {
                    homePageHandler.LoadOrderInfo(orderLists.get(position).getOrder_id());
                });
                orderListViewHolder.Customer_name.setOnClickListener(v -> homePageHandler.LoadOrderInfo(orderLists.get(position).getOrder_id()));
                orderListViewHolder.Customer_phone.setOnClickListener(v -> homePageHandler.LoadOrderInfo(orderLists.get(position).getOrder_id()));
                orderListViewHolder.Order_date.setOnClickListener(v -> homePageHandler.LoadOrderInfo(orderLists.get(position).getOrder_id()));
                orderListViewHolder.Order_status.setOnClickListener(v -> homePageHandler.LoadOrderInfo(orderLists.get(position).getOrder_id()));

            } else {
                EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
                emptyViewHolder.tv_OrderHistoryEmpty.setText(getString(R.string.or_empty));
            }
        }

        @Override
        public int getItemCount() {
            if (orderLists != null) {
                if (orderLists.size() > 0) {
                    return orderLists.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (orderLists != null) {
                if (orderLists.size() > 0) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }

        }

        class OrderListViewHolder extends RecyclerView.ViewHolder {
            TextView Customer_name, Customer_phone, Order_date, Order_total, Order_status, order_id, delivery_type,tv_schedule,schedule_title,order_edit;

            OrderListViewHolder(View itemView) {
                super(itemView);
                Customer_name = itemView.findViewById(R.id.tv_name);
                Customer_phone = itemView.findViewById(R.id.tv_phone);
                Order_date = itemView.findViewById(R.id.tv_date);
                Order_total = itemView.findViewById(R.id.tv_time);
                Order_status = itemView.findViewById(R.id.tv_status);
                order_edit = itemView.findViewById(R.id.im_order_edit);
                order_id = itemView.findViewById(R.id.tv_order_id);
                delivery_type = itemView.findViewById(R.id.tv_delivery_type);
                tv_schedule = itemView.findViewById(R.id.tv_schedule);
                schedule_title = itemView.findViewById(R.id.schedule);
            }
        }

        private class EmptyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_OrderHistoryEmpty;

            EmptyViewHolder(View itemView) {
                super(itemView);
                tv_OrderHistoryEmpty = itemView.findViewById(R.id.tv_empty);
            }
        }
    }

    private class SpinnerStatusAdapter extends BaseAdapter implements SpinnerAdapter {
        private ArrayList<Order_Status> Status;

        SpinnerStatusAdapter(ArrayList<Order_Status> status) {
            this.Status = status;
        }

        @Override
        public int getCount() {
            return Status.size();
        }

        @Override
        public Object getItem(int position) {
            return Status.get(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(10, 5, 5, 5);
            txt.setTextSize(14);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_grey_500_24dp, 0);
            txt.setText(Status.get(position).getName());
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(Status.get(position).getName());
            return txt;
        }
    }

}
