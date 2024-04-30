package ordenese.vendor.fragment.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.activity.Activity_BackBtn_Container;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.model.Order_List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewOrdersList extends Fragment {

    View mView;
    ProgressBar progressBar;
    Activity activity;
    RecyclerView recyclerView;
    ArrayList<Order_List> orderLists = new ArrayList<>();
    DatabaseReference reference;
    RecyclerViewOrderListAdapter adapter;

    public NewOrdersList() {
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
        this.activity = (Activity) context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getActivity() != null) {
                getActivity().getWindow().getDecorView().setLayoutDirection(
                        "ar".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                                View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_new_orders_list, container, false);

        progressBar = mView.findViewById(R.id.progress_bar);
        recyclerView = mView.findViewById(R.id.new_order_list_rec);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new_orders_db();
    }

    private void new_orders_db() {

        orderLists.clear();
        progressBar.setVisibility(View.VISIBLE);
        reference = FirebaseDatabase.getInstance().getReference("new_order").child(Constant.DataGetValue(activity, Constant.StoreId));

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                if (dataSnapshot != null) {
                    if (String.valueOf(dataSnapshot.child("order_status_id").getValue()).equals("1")) {
                        Order_List order_list = new Order_List();
                        order_list.setOrder_id(String.valueOf(dataSnapshot.child("order_id").getValue()));
                        order_list.setDate(String.valueOf(dataSnapshot.child("order_date").getValue()));
                        order_list.setOrder_time(String.valueOf(dataSnapshot.child("order_time").getValue()));
                        if (dataSnapshot.child("order_type").getValue().equals("2")) {
                            order_list.setDelivery_type(activity.getResources().getString(R.string.reg_pickup));
                        } else {
                            order_list.setDelivery_type(activity.getResources().getString(R.string.reg_delivery));
                        }
                        order_list.setName(String.valueOf(dataSnapshot.child("name").getValue()));
                        order_list.setOrder_status_id(String.valueOf(dataSnapshot.child("order_status_id").getValue()));
                        order_list.setStatus(String.valueOf(dataSnapshot.child("status").getValue()));
                        order_list.setTelephone(String.valueOf(dataSnapshot.child("telephone").getValue()));
                        order_list.setTotal(String.valueOf(dataSnapshot.child("total").getValue()));
                        order_list.setSchedule_status(String.valueOf(dataSnapshot.child("schedule_status").getValue()));
                        order_list.setSchedule_time(String.valueOf(dataSnapshot.child("schedule_time").getValue()));
                        order_list.setSchedule_date(String.valueOf(dataSnapshot.child("schedule_date").getValue()));
                        orderLists.add(order_list);
                    }
                }

                adapter = new RecyclerViewOrderListAdapter(orderLists);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                if (dataSnapshot != null) {
                    if (orderLists != null) {
                        for (int i = 0; i < orderLists.size(); i++) {
                            if (orderLists.get(i).getOrder_id().equals(dataSnapshot.getKey())) {
                                orderLists.remove(i);
                            }
                        }
                    }

                    if (String.valueOf(dataSnapshot.child("order_status_id").getValue()).equals("1")) {
                        Order_List order_list = new Order_List();
                        order_list.setOrder_id(String.valueOf(dataSnapshot.child("order_id").getValue()));
                        order_list.setDate(String.valueOf(dataSnapshot.child("order_date").getValue()));
                        order_list.setOrder_time(String.valueOf(dataSnapshot.child("order_time").getValue()));
                        if (dataSnapshot.child("order_type").getValue().equals("2")) {
                            order_list.setDelivery_type(activity.getResources().getString(R.string.reg_pickup));
                        } else {
                            order_list.setDelivery_type(activity.getResources().getString(R.string.reg_delivery));
                        }
                        order_list.setName(String.valueOf(dataSnapshot.child("name").getValue()));
                        order_list.setOrder_status_id(String.valueOf(dataSnapshot.child("order_status_id").getValue()));
                        order_list.setStatus(String.valueOf(dataSnapshot.child("status").getValue()));
                        order_list.setTelephone(String.valueOf(dataSnapshot.child("telephone").getValue()));
                        order_list.setTotal(String.valueOf(dataSnapshot.child("total").getValue()));
                        order_list.setSchedule_status(String.valueOf(dataSnapshot.child("schedule_status").getValue()));
                        order_list.setSchedule_time(String.valueOf(dataSnapshot.child("schedule_time").getValue()));
                        order_list.setSchedule_date(String.valueOf(dataSnapshot.child("schedule_date").getValue()));
                        orderLists.add(order_list);
                    }
                }
                adapter = new RecyclerViewOrderListAdapter(orderLists);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    if (orderLists != null) {
                        for (int i = 0; i < orderLists.size(); i++) {
                            if (orderLists.get(i).getOrder_id().equals(snapshot.getKey())) {
                                orderLists.remove(i);
                            }
                        }
                    }
                }
                adapter = new RecyclerViewOrderListAdapter(orderLists);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        progressBar.setVisibility(View.GONE);

        adapter = new RecyclerViewOrderListAdapter(orderLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                progressBar.setVisibility(View.VISIBLE);
//                orderLists.clear();
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    if (dataSnapshot != null) {
//                        if (String.valueOf(dataSnapshot.child("order_status_id").getValue()).equals("1")) {
//                            Order_List order_list = new Order_List();
//                            order_list.setOrder_id(String.valueOf(dataSnapshot.child("order_id").getValue()));
//                            order_list.setDate(String.valueOf(dataSnapshot.child("order_date").getValue()));
//                            order_list.setDelivery_type(String.valueOf(dataSnapshot.child("delivery_type").getValue()));
//                            order_list.setName(String.valueOf(dataSnapshot.child("name").getValue()));
//                            order_list.setOrder_status_id(String.valueOf(dataSnapshot.child("order_status_id").getValue()));
//                            order_list.setStatus(String.valueOf(dataSnapshot.child("status").getValue()));
//                            order_list.setTelephone(String.valueOf(dataSnapshot.child("telephone").getValue()));
//                            order_list.setTotal(String.valueOf(dataSnapshot.child("total").getValue()));
//                            orderLists.add(order_list);
//                        }
//                    }
//                }
//
//                adapter = new RecyclerViewOrderListAdapter(orderLists);
//                recyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
//                recyclerView.setAdapter(adapter);
//
//                progressBar.setVisibility(View.GONE);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    class RecyclerViewOrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<Order_List> orderLists;

        RecyclerViewOrderListAdapter(ArrayList<Order_List> orderLists) {
            this.orderLists = orderLists;
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
                if (orderLists.get(position).getOrder_time().equals("null")) {
                    orderListViewHolder.tv_time.setText("");
                } else {
                    orderListViewHolder.tv_time.setText(orderLists.get(position).getOrder_time());
                }
                orderListViewHolder.Order_total.setText(": " + orderLists.get(position).getTotal());
                orderListViewHolder.Order_status.setText(": " + orderLists.get(position).getStatus());
                orderListViewHolder.order_id.setText(": " + orderLists.get(position).getOrder_id());
                orderListViewHolder.delivery_type.setText(": " + orderLists.get(position).getDelivery_type());

                if (orderLists.get(position).getSchedule_status() != null && orderLists.get(position).getSchedule_status().equals("1")) {
                    orderListViewHolder.schedule_title.setVisibility(View.VISIBLE);
                    orderListViewHolder.tv_schedule.setVisibility(View.VISIBLE);
                    orderListViewHolder.tv_schedule.setText(": " + orderLists.get(position).getSchedule_date() + " " + orderLists.get(position).getSchedule_time());
                } else {
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
                emptyViewHolder.tv_OrderHistoryEmpty.setText(getString(R.string.no_new_order));

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

            TextView Customer_name, Customer_phone, Order_date, tv_time, Order_total, Order_status, order_id, delivery_type, tv_schedule, schedule_title, order_edit;

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