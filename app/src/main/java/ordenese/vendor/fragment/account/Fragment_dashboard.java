package ordenese.vendor.fragment.account;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.HomePageHandler;
import ordenese.vendor.model.Home_page;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_dashboard extends Fragment {
    ApiInterface apiInterface;
   // String range = "year";
    Activity activity;
    TextView total_order_value, sale_value, total_complete_sales_value, tv_sales, tv_balance,total_products_value,store_name;
    Spinner sp_status;
   // String[] Status = {"day", "week","month","year"};
    ProgressBar progressBar;
    LinearLayout total_sales,total_orders,total_complete_sales,total_products;
    HomePageHandler homePageHandler;
    CircleImageView store_image;

    @Override
    public void onAttach(Context context) {

        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        this.homePageHandler = (HomePageHandler) context;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_page, container, false);
        total_order_value = view.findViewById(R.id.tv_total_order_value);
        sale_value = view.findViewById(R.id.tv_sale_value);
        total_complete_sales_value = view.findViewById(R.id.tv_total_complete_sales_value);
        total_products_value = view.findViewById(R.id.tv_total_products_value);
        tv_sales = view.findViewById(R.id.tv_sales_value);
        tv_balance = view.findViewById(R.id.tv_balance_value);
        sp_status = view.findViewById(R.id.sp_status);
        progressBar = view.findViewById(R.id.progressBar);
        total_orders = view.findViewById(R.id.total_orders);
        total_sales = view.findViewById(R.id.total_sales);
        total_complete_sales = view.findViewById(R.id.total_complete_sales);
        total_products = view.findViewById(R.id.total_products);
        store_image = view.findViewById(R.id.store_image);
        store_name = view.findViewById(R.id.store_name);
        total_sales.setOnClickListener(v -> homePageHandler.LoadOrderList());
        total_orders.setOnClickListener(v -> homePageHandler.LoadOrderList());
        total_complete_sales.setOnClickListener(v -> homePageHandler.LoadReportOrderList());
        total_products.setOnClickListener(v -> homePageHandler.LoadProducts());
        LoadDashboard();
        return view;
    }

    private void LoadDashboard() {
        progressBar.setVisibility(View.VISIBLE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        String language = LanguageDetailsDB.getInstance(activity).get_language_id();

        Call<String> call = apiInterface.getDashboard(Constant.DataGetValue(activity,Constant.Token),language);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){

                    Home_page HomePage = ContentJsonParser.getHomePage(response.body());
                    if (HomePage != null){
                        total_order_value.setText(HomePage.getTotal_orders());
                        sale_value.setText(HomePage.getTotal_sales());
                        total_complete_sales_value.setText(HomePage.getTotal_complete_sales());
                        total_products_value.setText(HomePage.getTotal_products());
                        Glide.with(activity).load(HomePage.getImage()).into(store_image);
                        store_name.setText(HomePage.getHost_name());

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }






}
