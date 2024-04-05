package ordenese.vendor.fragment.home;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
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
import ordenese.vendor.model.Model_Status;
import ordenese.vendor.model.Order_List;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_OrderListHome extends Fragment {

    private View v_OrderListHolder;
    private Activity activity;
    //  private TabLayout tl_OrderList;
    // private ViewPager vp_Order;
    private ProgressBar pb_LoaderOrder;
    private ApiInterface apiInterface;
    private ArrayList<Model_Status> orderStatusList = new ArrayList<>();
    ImageView refresh_btn;
    private RecyclerView mTitleListRecycler, Rc_OrderList;
    private RecyclerView.LayoutManager mTitleListLayoutMgr;
    private TitleListAdapter mTitleListAdapter;
    String orderListUrl, orderStatus = "";
    private int page_count = 1, total = 0;
    private ArrayList<Order_List> orderLists = new ArrayList<>();
    private RecyclerViewOrderListAdapter recyclerViewOrderListAdapter;

    private Handler handler;
    private Runnable runnable;

    private Boolean mIsAlarmStarted = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setLayoutDirection(
                    "ar".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public Fragment_OrderListHome() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_OrderListHolder = inflater.inflate(R.layout.fragment_order_list_home, container, false);

        load();

        return v_OrderListHolder;
    }

    private void load() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        // tl_OrderList = v_OrderListHolder.findViewById(R.id.tl_order_list_home);
        //  vp_Order = v_OrderListHolder.findViewById(R.id.vp_order_home);
        pb_LoaderOrder = v_OrderListHolder.findViewById(R.id.pb_order_list_loader_home);
        mTitleListRecycler = v_OrderListHolder.findViewById(R.id.recycler_restaurant_menu_title_list);
        Rc_OrderList = v_OrderListHolder.findViewById(R.id.rc_order_list);
        refresh_btn = v_OrderListHolder.findViewById(R.id.refresh_btn);

        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadOrderStatus();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        LoadOrderStatus();

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
                    orderStatusList = ContentJsonParser.getOrderStatusListHome(activity,response.body());
                    if (orderStatusList != null) {
                        if (orderStatusList.size() > 0) {

                            mTitleListLayoutMgr = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
                            mTitleListRecycler.setLayoutManager(mTitleListLayoutMgr);
                            mTitleListAdapter = new TitleListAdapter(orderStatusList, 0, false);
                            mTitleListRecycler.setAdapter(mTitleListAdapter);

                            page_count = 1;
                            orderStatus = "1"; // New status.
                            urlSetup();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                pb_LoaderOrder.setVisibility(View.GONE);
            }
        });

    }

    class HomeViewAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> getList = new ArrayList<>();
        private final ArrayList<String> fragmentTitleList = new ArrayList<>();

        HomeViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getList.get(position);
        }

        @Override
        public int getCount() {
            return getList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        void setFragment(Fragment fragment, String title) {
            getList.add(fragment);
            fragmentTitleList.add(title);
        }
    }

    //    ***********************   ***************************  **********************************

    public class TitleListAdapter extends RecyclerView.Adapter<TitleListAdapter.DataObjectHolder> {

        private ArrayList<Model_Status> mTitleList;
        private View mTempBarView;
        private TextView mTempMenuTitleName;
        private Boolean mIsInitial = true;
        private int mPositionToMove;
        private Boolean mIsFromItemTouch;


        public TitleListAdapter(ArrayList<Model_Status> titleList, int positionToMove, Boolean isFromItemTouch) {
            this.mTitleList = titleList;
            this.mPositionToMove = positionToMove;
            this.mIsFromItemTouch = isFromItemTouch;
        }

        @Override
        public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_menu_title_list_row, parent, false);
            return new DataObjectHolder(mView);
        }


        @Override
        public void onBindViewHolder(final DataObjectHolder holder, final int position) {

            holder.mMenuTitleName.setText(mTitleList.get(position).getName());

            if (mIsFromItemTouch) {

                if (mPositionToMove == position) {

                    if (mTempBarView != null) {
                        mTempBarView.setVisibility(View.INVISIBLE);
                    }
                    if (mTempMenuTitleName != null) {
                        if (getActivity() != null) {
                            mTempMenuTitleName.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_home_un_select_title));
                        }
                    }

                    holder.mBarView.setVisibility(View.VISIBLE);
                    //  holder.mMenuTitleName.setTextColor(R.color.text_color);
                    if (getActivity() != null) {
                        holder.mMenuTitleName.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                    }
                    mTempBarView = holder.mBarView;
                    mTempMenuTitleName = holder.mMenuTitleName;
                } else {
                    holder.mBarView.setVisibility(View.INVISIBLE);
                    // holder.mMenuTitleName.setTextColor(R.color.grey_500);
                    if (getActivity() != null) {
                        holder.mMenuTitleName.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_home_un_select_title));
                    }
                }


            } else {
                if (mIsInitial) {

                    //safe check :-
                    if (mTempBarView != null) {
                        mTempBarView.setVisibility(View.INVISIBLE);
                    }
                    if (mTempMenuTitleName != null) {
                        if (getActivity() != null) {
                            mTempMenuTitleName.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_home_un_select_title));
                        }
                    }

                    holder.mBarView.setVisibility(View.VISIBLE);
                    // holder.mMenuTitleName.setTextColor(R.color.text_color);
                    if (getActivity() != null) {
                        holder.mMenuTitleName.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                    }
                    mTempBarView = holder.mBarView;
                    mTempMenuTitleName = holder.mMenuTitleName;
                    mIsInitial = false;
                } else {
                    holder.mBarView.setVisibility(View.INVISIBLE);
                    // holder.mMenuTitleName.setTextColor(R.color.grey_500);
                    if (getActivity() != null) {
                        holder.mMenuTitleName.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_home_un_select_title));
                    }
                }
            }

            holder.mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mIsFromItemTouch = false;

                    // mPageHeader.setExpanded(false);

                    if (mTempBarView != null) {
                        mTempBarView.setVisibility(View.INVISIBLE);
                    }
                    if (mTempMenuTitleName != null) {
                        if (getActivity() != null) {
                            mTempMenuTitleName.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_home_un_select_title));
                        }
                    }

                    holder.mBarView.setVisibility(View.VISIBLE);
                    // holder.mMenuTitleName.setTextColor(R.color.text_color);
                    if (getActivity() != null) {
                        holder.mMenuTitleName.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                    }
                    mTempBarView = holder.mBarView;
                    mTempMenuTitleName = holder.mMenuTitleName;

                    // Toast.makeText(getActivity(), mTitleList.get(position).getName() + " = " + mTitleList.get(position).getValue(), Toast.LENGTH_SHORT).show();
                    //  mMenuListViewer.smoothScrollToPosition(mTitleList.get(position).getPosition());

                    mTitleListRecycler.smoothScrollToPosition(position);

                    page_count = 1;
                    mLogE("313", "*called*");
                    orderStatus = mTitleList.get(position).getValue();
                    urlSetup();
                }
            });

        }

        @Override
        public int getItemCount() {
            return mTitleList.size();
        }

        public class DataObjectHolder extends RecyclerView.ViewHolder {

            private TextView mMenuTitleName;
            private View mBarView, mTitleView;
            private ConstraintLayout mRow;


            public DataObjectHolder(View view) {
                super(view);

                mMenuTitleName = view.findViewById(R.id.tv_store_menu_title);
                mBarView = view.findViewById(R.id.view_store_menu_title_bar);
                //  mTitleView = view.findViewById(R.id.view_store_menu_title);
                mRow = view.findViewById(R.id.lay_store_menu_title_list_row);

            }
        }


    }

    private void mLogE(String title, String value) {
        // Log.e(title, value);
    }

    private void urlSetup() {

        mLogE("359", "*called*");

        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        orderListUrl = ApiClient.base_url + Constant.MY_ORDER + page_count + "&language_id=" + language;
        if (orderStatusList != null) {
            //  mLogE("363", "called");
            if (orderStatusList.size() > 0) {
                //  mLogE("365", "called");
                // if (Integer.valueOf(orderStatus) > -1) {
                // mLogE("367", "called");
                orderListUrl = orderListUrl + Constant.ORDER_STATUS + orderStatus;
                LoadOrderList();
                // }else {
                ///   mLogE("371","called");
                //}
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

                            Rc_OrderList.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                            recyclerViewOrderListAdapter = new RecyclerViewOrderListAdapter();
                            Rc_OrderList.setAdapter(recyclerViewOrderListAdapter);

                            recyclerViewOrderListAdapter.setOnLoadMoreListener(() -> {
                                if (page_count <= ((total / 10) + 1)) {
                                    if (Constant.isNetworkAvailable()) {
                                        page_count++;
                                        //  urlSetup();
                                        pb_LoaderOrder.setVisibility(View.VISIBLE);
                                        apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                        try {
                                            JSONObject object = new JSONObject();
                                            object.put("page_per_unit", "10");
                                            object.put("page", String.valueOf(page_count));
                                            object.put("order_status_id", orderStatus);
                                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

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

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
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

                orderListViewHolder.Customer_name.setText(": " + orderLists.get(position).getName());
                orderListViewHolder.Customer_phone.setText(": " + orderLists.get(position).getTelephone());
                orderListViewHolder.Order_date.setText(": " + orderLists.get(position).getDate());
                orderListViewHolder.tv_time.setText(orderLists.get(position).getOrder_time());
                orderListViewHolder.Order_total.setText(": " + orderLists.get(position).getTotal());
                orderListViewHolder.Order_status.setText(": " + orderLists.get(position).getStatus());
                orderListViewHolder.order_id.setText(": " + orderLists.get(position).getOrder_id());
//              orderListViewHolder.delivery_type.setText(": " + orderLists.get(position).getDelivery_type());

                if (orderLists.get(position).getOrder_type().equals("2")) {
                    orderListViewHolder.delivery_type.setText(": " +activity.getResources().getString(R.string.reg_pickup));
                } else {
                    orderListViewHolder.delivery_type.setText(": " +activity.getResources().getString(R.string.reg_delivery));
                }

                if (orderLists.get(position).getSchedule_status() != null && orderLists.get(position).getSchedule_status().equals("1")) {
                    orderListViewHolder.schedule_title.setVisibility(View.VISIBLE);
                    orderListViewHolder.tv_schedule.setVisibility(View.VISIBLE);
                    orderListViewHolder.tv_schedule.setText(": " + orderLists.get(position).getSchedule_date() +" " + orderLists.get(position).getSchedule_time());
                }else {
                    orderListViewHolder.schedule_title.setVisibility(View.GONE);
                    orderListViewHolder.tv_schedule.setVisibility(View.GONE);
                }

                orderListViewHolder.order_edit.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, Activity_BackBtn_Container.class);
                    intent.putExtra("Type", "OrderInfo");
                    intent.putExtra("order_id", orderLists.get(position).getOrder_id());
                    startActivity(intent);
                });


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
            TextView Customer_name, Customer_phone, Order_date, Order_total, Order_status,tv_time, order_id, delivery_type,tv_schedule,schedule_title,order_edit;
            // ConstraintLayout constraintLayout;

            OrderListViewHolder(View itemView) {
                super(itemView);
                Customer_name = itemView.findViewById(R.id.tv_name);
                Customer_phone = itemView.findViewById(R.id.tv_phone);
                Order_date = itemView.findViewById(R.id.tv_date);
                Order_total = itemView.findViewById(R.id.tv_total);
                tv_time = itemView.findViewById(R.id.tv_time);
                Order_status = itemView.findViewById(R.id.tv_status);
                order_edit = itemView.findViewById(R.id.im_order_edit);
                order_id = itemView.findViewById(R.id.tv_order_id);
                delivery_type = itemView.findViewById(R.id.tv_delivery_type);
                tv_schedule = itemView.findViewById(R.id.tv_schedule);
                schedule_title = itemView.findViewById(R.id.schedule);
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


    //*************************  ***************************************** ************************
}
