package ordenese.vendor.fragment.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import ordenese.vendor.activity.Activity_BackBtn_Container;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.model.Order_List;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_SingleOrderStatusList extends Fragment {

    private View v_OrderListHolder;
    private RecyclerView Rc_OrderList;
    private Activity activity;
    private ProgressBar pb_LoaderOrder;
    private ApiInterface apiInterface;
    private int page_count = 1, total = 0;
    private RecyclerViewOrderListAdapter recyclerViewOrderListAdapter;
    private ArrayList<Order_List> orderLists = new ArrayList<>();
    private String url, orderStatus = null;

    public Fragment_SingleOrderStatusList() {

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getActivity() != null) {

                getActivity().getWindow().getDecorView().setLayoutDirection(
                        "ar".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                                View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

            }
        }
    }

    private void mLogE(String title, String value) {
        //Log.e(title,value);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderStatus = bundle.getString(Constant.ORDER_STATUS_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_OrderListHolder = inflater.inflate(R.layout.fragment_order_list, container, false);
        load();
        return v_OrderListHolder;
    }


    @Override
    public void onResume() {
        super.onResume();
        page_count = 1;
        urlSetup();
        LoadOrderList();
    }

    private void load() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

      /*  TextView tv_Filter = v_OrderListHolder.findViewById(R.id.tv_filter_order);
        LinearLayout ll_FilterContainer = v_OrderListHolder.findViewById(R.id.ll_filter_order_bg);
*/
        Rc_OrderList = v_OrderListHolder.findViewById(R.id.rc_order_list);
        pb_LoaderOrder = v_OrderListHolder.findViewById(R.id.progressBar);

       /* ll_FilterContainer.setVisibility(View.GONE);
        tv_Filter.setVisibility(View.GONE);*/

        urlSetup();

        if (orderLists != null) {
            if (orderLists.size() > 0) {
                page_count = 1;

                for (int i = 0; i < orderLists.size(); i++) {
                    orderLists.remove(i);
                }
            }
        }

        LoadOrderList();
    }

    private void urlSetup() {
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        url = ApiClient.base_url + Constant.MY_ORDER + page_count + "&language_id=" + language;
        if (orderStatus != null) {
            if (orderStatus.length() > 0) {
                if (Integer.valueOf(orderStatus) > -1)
                    url = url + Constant.ORDER_STATUS + orderStatus;
            }
        }
    }

    private void LoadOrderList() {

        if (Constant.isNetworkAvailable()) {
            try {
                JSONObject object = new JSONObject();

                object.put("page_per_unit", "10");
                object.put("page", String.valueOf(page_count));
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
                                if (page_count <= ((total / 10) + 1)) {
                                    if (Constant.isNetworkAvailable()) {
                                        page_count++;
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

                /*orderListViewHolder.Customer_name.setText(orderLists.get(position).getName());
                orderListViewHolder.Customer_phone.setText(orderLists.get(position).getTelephone());
                orderListViewHolder.Order_date.setText(orderLists.get(position).getDate());
                orderListViewHolder.Order_total.setText(orderLists.get(position).getTotal());
                orderListViewHolder.Order_status.setText(orderLists.get(position).getStatus());
                orderListViewHolder.order_id.setText(orderLists.get(position).getOrder_id());
                orderListViewHolder.delivery_type.setText(orderLists.get(position).getDelivery_type());*/

                orderListViewHolder.Customer_name.setText(orderLists.get(position).getName());
                orderListViewHolder.Customer_phone.setText(orderLists.get(position).getTelephone());
                orderListViewHolder.Order_date.setText(orderLists.get(position).getDate());
                orderListViewHolder.Order_total.setText(orderLists.get(position).getTotal());
                orderListViewHolder.Order_status.setText(orderLists.get(position).getStatus());
                orderListViewHolder.order_id.setText(orderLists.get(position).getOrder_id());

                if (orderLists.get(position).getOrder_type().equals("2")) {
                    orderListViewHolder.delivery_type.setText(activity.getResources().getString(R.string.reg_pickup));
                } else {
                    orderListViewHolder.delivery_type.setText(activity.getResources().getString(R.string.reg_delivery));
                }

//                orderListViewHolder.delivery_type.setText(orderLists.get(position).getDelivery_type());

                orderListViewHolder.order_edit.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, Activity_BackBtn_Container.class);
                    intent.putExtra("Type", "OrderInfo");
                    intent.putExtra("order_id", orderLists.get(position).getOrder_id());
                    startActivity(intent);
                });

                /*orderListViewHolder.constraintLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, Activity_BackBtn_Container.class);
                    intent.putExtra("Type", "OrderInfo");
                    intent.putExtra("order_id", orderLists.get(position).getOrder_id());
                    startActivity(intent);
                });*/

                //orderListViewHolder.Customer_name.setOnClickListener(v -> homePageHandler.LoadOrderInfo(orderLists.get(position).getOrder_id()));
                // orderListViewHolder.Customer_phone.setOnClickListener(v -> homePageHandler.LoadOrderInfo(orderLists.get(position).getOrder_id()));
                //  orderListViewHolder.Order_date.setOnClickListener(v -> homePageHandler.LoadOrderInfo(orderLists.get(position).getOrder_id()));
                //  orderListViewHolder.Order_status.setOnClickListener(v -> homePageHandler.LoadOrderInfo(orderLists.get(position).getOrder_id()));


            } else {
                EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
                emptyViewHolder.tv_OrderHistoryEmpty.setText(getString(R.string.empty));

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
            TextView Customer_name, Customer_phone, Order_date, Order_total, Order_status, order_id, delivery_type;
            ImageView order_edit;
            // ConstraintLayout constraintLayout;

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
                // constraintLayout = itemView.findViewById(R.id.order_details_constraint);

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

}
