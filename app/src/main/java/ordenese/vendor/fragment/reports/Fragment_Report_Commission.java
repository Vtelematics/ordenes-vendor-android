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
import ordenese.vendor.common.instant_transfer.Filter_Commission;
import ordenese.vendor.fragment.dialog.Filter_dialog.Dialog_filter_commission;
import ordenese.vendor.model.Model_ReportCommission;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Report_Commission extends Fragment implements Filter_Commission {

    private View v_CommissionHolder;
    private Activity activity;
    private RecyclerView rc_CommissionList;
    private ApiInterface apiInterface;
    private ProgressBar pb_Loader;
    private ArrayList<Model_ReportCommission> reportCommissionList=new ArrayList<>();
    private int total_count = 1, page = 1;
    private String url;
    FloatingActionButton floatingActionButton;
    String Start_date = "", End_date = "";
    Filter_Commission filter_commission;

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
        filter_commission = this;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_CommissionHolder = inflater.inflate(R.layout.fragment_report_commission_list, container, false);
        load();
        return v_CommissionHolder;
    }

    private void load() {
        rc_CommissionList = v_CommissionHolder.findViewById(R.id.rc_report_commission_list);
        rc_CommissionList.setLayoutManager(new LinearLayoutManager(activity));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        pb_Loader = v_CommissionHolder.findViewById(R.id.pb_loader);
        floatingActionButton = v_CommissionHolder.findViewById(R.id.filter_edit);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDilaogFilter(Start_date, End_date);
            }
        });
        LoadCommissionList(Start_date,End_date);
    }

    private void LoadDilaogFilter(String start_date, String end_date) {
        Bundle bundle = new Bundle();
        bundle.putString("start_date", start_date);
        bundle.putString("end_date", end_date);
        Dialog_filter_commission dialog_filter_commission = new Dialog_filter_commission();
        dialog_filter_commission.setArguments(bundle);
        dialog_filter_commission.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog_filter_commission.AssignCommissionhandler(filter_commission);
        dialog_filter_commission.show(getChildFragmentManager(), "Filter");
    }


    private void LoadCommissionList(String start_date, String end_date) {
        pb_Loader.setVisibility(View.VISIBLE);
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getCommissionListReport(Constant.DataGetValue(activity,
                Constant.Token),start_date,end_date,language);
        //Call<String> call = apiInterface.getCommissionListReport(Constant.DataGetValue(activity, Constant.Token),url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                pb_Loader.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                     reportCommissionList = ContentJsonParser.getReportCommissionList(response.body());
                    rc_CommissionList.setAdapter(new CommissionReportAdapter(reportCommissionList));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                pb_Loader.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void LoadFilterCommission(String start_date, String end_date) {
        LoadCommissionList(start_date,end_date);
    }

    class CommissionReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Model_ReportCommission> reportCommissionList;

        CommissionReportAdapter(ArrayList<Model_ReportCommission> reportCommissionList) {
            this.reportCommissionList = reportCommissionList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new CommissionViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_report_commission, parent, false));
            } else if (viewType == 3) {
                return new CommissionTotalAmountViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_commission_total, parent, false));
            } else {
                return new CommissionEmptyViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                CommissionViewHolder commissionViewHolder = (CommissionViewHolder) holder;
               // Log.e( "onBindViewHolder: ", reportCommissionList.get(position - 1).getOrder_id()+"");
                commissionViewHolder.tv_OrderId.setText(": " +reportCommissionList.get(position - 1).getOrder_id());
                commissionViewHolder.tv_Store.setText(": " +reportCommissionList.get(position - 1).getRestaurant().trim());
                commissionViewHolder.tv_Total.setText(": " +reportCommissionList.get(position - 1).getTotal());
                commissionViewHolder.tv_Commissions.setText(": " +reportCommissionList.get(position - 1).getCommission());
                commissionViewHolder.tv_Balance.setText(": " +reportCommissionList.get(position - 1).getBalance());

            } else if (holder.getItemViewType() == 3) {
                CommissionTotalAmountViewHolder commissionTotalAmountViewHolder = (CommissionTotalAmountViewHolder) holder;
                String commissionTitle = getString(R.string.txt_report_commission_total) + " : " + reportCommissionList.get(position).getTotalCommission();
                commissionTotalAmountViewHolder.tv_Total.setText(commissionTitle);
            } else {
                CommissionEmptyViewHolder commissionEmptyViewHolder = (CommissionEmptyViewHolder) holder;
                commissionEmptyViewHolder.tv_CommissionEmpty.setText(getString(R.string.txt_report_commission_empty));
            }
        }

        @Override
        public int getItemCount() {
            if (reportCommissionList != null) {
                if (reportCommissionList.size() > 0) {
                    return reportCommissionList.size() + 1;

                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (reportCommissionList != null) {
                if (reportCommissionList.size() > 0) {
                    if (position == 0) {
                        return 3;
                    } else {
                        return 1;
                    }
                } else {
                    return 2;
                }
            } else {
                return 2;
            }
        }

        class CommissionViewHolder extends RecyclerView.ViewHolder {
            TextView tv_OrderId, tv_Store, tv_Total, tv_Commissions, tv_Balance;

            CommissionViewHolder(View itemView) {
                super(itemView);
                tv_OrderId = itemView.findViewById(R.id.tv_report_order_id_value);
                tv_Store = itemView.findViewById(R.id.tv_report_store_value);
                tv_Total = itemView.findViewById(R.id.tv_report_total_value);
                tv_Commissions = itemView.findViewById(R.id.tv_report_commission_value);
                tv_Balance = itemView.findViewById(R.id.tv_report_balance_value);
            }
        }

        class CommissionTotalAmountViewHolder extends RecyclerView.ViewHolder {
            TextView tv_Total;

            CommissionTotalAmountViewHolder(View itemView) {
                super(itemView);
                tv_Total = itemView.findViewById(R.id.tv_commission_total);
            }
        }

        class CommissionEmptyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_CommissionEmpty;

            CommissionEmptyViewHolder(View itemView) {
                super(itemView);
                tv_CommissionEmpty = itemView.findViewById(R.id.tv_empty);
            }
        }
    }
}
