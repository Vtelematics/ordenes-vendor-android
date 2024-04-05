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
import ordenese.vendor.common.instant_transfer.Filter_Coupon;
import ordenese.vendor.fragment.dialog.Filter_dialog.Dialog_filter_coupon;
import ordenese.vendor.model.Model_ReportCoupon;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Report_Coupon extends Fragment implements Filter_Coupon{

    private View v_CouponHolder;
    private Activity activity;
    private RecyclerView rc_CouponList;
    private ApiInterface apiInterface;
    private ProgressBar pb_Loader;
    private ArrayList<Model_ReportCoupon> reportCouponList = new ArrayList<>();
    private int total_count = 1, page = 1;
    private String url;
    FloatingActionButton floatingActionButton;
    String Start_date = "", End_date = "";
    Filter_Coupon filter_coupon;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filter_coupon = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_CouponHolder = inflater.inflate(R.layout.fragment_report_coupon_list, container, false);
        load();
        return v_CouponHolder;
    }

    private void load() {
        rc_CouponList = v_CouponHolder.findViewById(R.id.rc_report_coupon_list);
        rc_CouponList.setLayoutManager(new LinearLayoutManager(activity));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        pb_Loader = v_CouponHolder.findViewById(R.id.pb_loader);
        floatingActionButton = v_CouponHolder.findViewById(R.id.filter_edit);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDialogFilter(Start_date, End_date);
            }
        });
        LoadCouponList(Start_date,End_date);
    }

    private void LoadDialogFilter(String start_date, String end_date) {
        Bundle bundle = new Bundle();
        bundle.putString("start_date", start_date);
        bundle.putString("end_date", end_date);
        Dialog_filter_coupon dialog_filter_coupon = new Dialog_filter_coupon();
        dialog_filter_coupon.setArguments(bundle);
        dialog_filter_coupon.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog_filter_coupon.AssignCouponhandler(filter_coupon);
        dialog_filter_coupon.show(getChildFragmentManager(), "Filter");
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


    private void LoadCouponList(String start_date, String end_date) {
        pb_Loader.setVisibility(View.VISIBLE);
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getCouponListReport(Constant.DataGetValue(activity, Constant.Token),
                start_date,end_date,language);
        // Call<String> call = apiInterface.getCouponListReport(Constant.DataGetValue(activity, Constant.Token),url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                pb_Loader.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    reportCouponList = ContentJsonParser.getReportCouponList(response.body());
                    rc_CouponList.setAdapter(new CouponReportAdapter(reportCouponList));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                pb_Loader.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void LoadFilterCoupon(String start_date, String end_date) {
        LoadCouponList(start_date,end_date);
        Start_date = start_date;
        End_date = end_date;
    }



    class CouponReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Model_ReportCoupon> reportCouponList;

        CouponReportAdapter(ArrayList<Model_ReportCoupon> reportCouponList) {
            this.reportCouponList = reportCouponList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new CouponViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_report_coupon, parent, false));
            } else {
                return new CouponEmptyViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                CouponViewHolder couponViewHolder = (CouponViewHolder) holder;
                couponViewHolder.tv_CouponName.setText(": " + reportCouponList.get(position).getCoupon_name());
                couponViewHolder.tv_Code.setText(": " + reportCouponList.get(position).getCode());
                couponViewHolder.tv_Orders.setText(": " + reportCouponList.get(position).getOrders());
                couponViewHolder.tv_Total.setText(": " + reportCouponList.get(position).getTotal());

            } else {
                CouponEmptyViewHolder CouponEmptyViewHolder = (CouponEmptyViewHolder) holder;
                CouponEmptyViewHolder.tv_CouponEmpty.setText(getString(R.string.txt_report_coupon_empty));
            }
        }

        @Override
        public int getItemCount() {
            if (reportCouponList != null) {
                if (reportCouponList.size() > 0) {
                    return reportCouponList.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (reportCouponList != null) {
                if (reportCouponList.size() > 0) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                return 2;
            }
        }

        class CouponViewHolder extends RecyclerView.ViewHolder {
            TextView tv_CouponName, tv_Code, tv_Orders, tv_Total;

            CouponViewHolder(View itemView) {
                super(itemView);
                tv_CouponName = itemView.findViewById(R.id.tv_report_coupon_name_value);
                tv_Code = itemView.findViewById(R.id.tv_report_code_value);
                tv_Orders = itemView.findViewById(R.id.tv_report_orders_value);
                tv_Total = itemView.findViewById(R.id.tv_report_total_value);
            }
        }

        class CouponEmptyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_CouponEmpty;

            CouponEmptyViewHolder(View itemView) {
                super(itemView);
                tv_CouponEmpty = itemView.findViewById(R.id.tv_empty);
            }
        }
    }
}
