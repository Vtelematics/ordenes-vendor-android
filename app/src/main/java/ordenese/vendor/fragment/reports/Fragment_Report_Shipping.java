package ordenese.vendor.fragment.reports;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.Filter_Shipping;
import ordenese.vendor.fragment.dialog.Filter_dialog.Dialog_filter_shipping;
import ordenese.vendor.model.Model_ReportShipping;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Report_Shipping extends Fragment implements Filter_Shipping {

    private Activity activity;
    private View v_ShippingHolder;
    private RecyclerView rc_ShippingList;
    private ApiInterface apiInterface;
    private ProgressBar pb_Loader;
    private int total_count = 1, page = 1;
    private String url;
    Filter_Shipping filter_shipping;
    FloatingActionButton floatingActionButton;
    String Start_date = "", End_date = "", Order_Status = "",Group = "";

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filter_shipping = this;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_ShippingHolder = inflater.inflate(R.layout.fragment_report_shipping_list, container, false);
        load();
        return v_ShippingHolder;
    }

    private void load() {
        rc_ShippingList = v_ShippingHolder.findViewById(R.id.rc_shipping_list);
        rc_ShippingList.setLayoutManager(new LinearLayoutManager(activity));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        pb_Loader = v_ShippingHolder.findViewById(R.id.pb_loader);
        floatingActionButton = v_ShippingHolder.findViewById(R.id.filter_edit);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDialogFilter(Start_date, End_date, Order_Status,Group);
            }
        });
        LoadShippingList(Start_date, End_date, Order_Status,Group);
    }

    private void LoadDialogFilter(String start_date, String end_date, String order_status, String group) {
        Bundle bundle = new Bundle();
        bundle.putString("start_date", start_date);
        bundle.putString("end_date", end_date);
        bundle.putString("order_status", order_status);
        bundle.putString("group",group);
        Dialog_filter_shipping dialog_filter_shipping = new Dialog_filter_shipping();
        dialog_filter_shipping.setArguments(bundle);
        dialog_filter_shipping.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog_filter_shipping.AssignShippinghandler(filter_shipping);
        dialog_filter_shipping.show(getChildFragmentManager(), "Filter");
    }

    private void LoadShippingList(String start_date, String end_date, String order_Status, String group) {
        pb_Loader.setVisibility(View.VISIBLE);
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getShippingListReport(Constant.DataGetValue(activity, Constant.Token),start_date,
                end_date,order_Status,group,language);
        //Call<String> call = apiInterface.getShippingListReport(Constant.DataGetValue(activity, Constant.Token),url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                pb_Loader.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ArrayList<Model_ReportShipping> reportShippingList = ContentJsonParser.getReportShippingList(response.body());
                    rc_ShippingList.setAdapter(new ShippingAdapter(reportShippingList));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                pb_Loader.setVisibility(View.GONE);
            }
        });
    }



    @Override
    public void LoadFilterShipping(String start_date, String end_date, String Order_status, String Group_id) {
        LoadShippingList(start_date,end_date,Order_status,Group);
        Start_date = start_date;
        End_date = end_date;
        Order_Status = Order_status;
        Group = Group_id;
    }

    class ShippingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Model_ReportShipping> shippingList = new ArrayList<>();

        ShippingAdapter(ArrayList<Model_ReportShipping> shippingList) {
            this.shippingList = shippingList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new ShippingViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_report_shipping, parent, false));
            } else {
                return new ShippingEmptyViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                ShippingViewHolder shippingViewHolder = (ShippingViewHolder) holder;
                shippingViewHolder.tv_DateStart.setText(": "+shippingList.get(position).getDateStart());
                shippingViewHolder.tv_DateEnd.setText(": "+shippingList.get(position).getDateEnd());
                shippingViewHolder.tv_Title.setText(": "+shippingList.get(position).getTitle());
                shippingViewHolder.tv_Orders.setText(": "+shippingList.get(position).getOrders());
                shippingViewHolder.tv_Total.setText(": "+shippingList.get(position).getTotal());
            } else {
                ShippingEmptyViewHolder shippingEmptyViewHolder = (ShippingEmptyViewHolder) holder;
                shippingEmptyViewHolder.tv_ShippingEmpty.setText(getString(R.string.txt_report_shipping_empty));
            }
        }

        @Override
        public int getItemCount() {
            if (shippingList != null) {
                if (shippingList.size() > 0) {
                    return shippingList.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (shippingList != null) {
                if (shippingList.size() > 0) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                return 2;
            }
        }

        class ShippingViewHolder extends RecyclerView.ViewHolder {
            TextView tv_DateStart, tv_DateEnd, tv_Title, tv_Orders, tv_Total;

            ShippingViewHolder(View itemView) {
                super(itemView);

                tv_DateStart = itemView.findViewById(R.id.tv_report_date_start_value);
                tv_DateEnd = itemView.findViewById(R.id.tv_report_date_end_value);
                tv_Title = itemView.findViewById(R.id.tv_report_shipping_value);
                tv_Orders = itemView.findViewById(R.id.tv_report_no_orders_value);
                tv_Total = itemView.findViewById(R.id.tv_report_total_value);

            }
        }

        class ShippingEmptyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_ShippingEmpty;

            ShippingEmptyViewHolder(View itemView) {
                super(itemView);
                tv_ShippingEmpty = itemView.findViewById(R.id.tv_empty);
            }
        }
    }
}
