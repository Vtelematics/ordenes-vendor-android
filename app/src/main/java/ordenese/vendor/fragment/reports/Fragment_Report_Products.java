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
import ordenese.vendor.common.instant_transfer.Filter_Product;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.fragment.dialog.Filter_dialog.Dialog_filter_product;
import ordenese.vendor.model.Model_ReportProducts;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Report_Products extends Fragment implements Filter_Product {

    private View v_ProductsHolder;
    private Activity activity;
    private RecyclerView rc_ProductsList;
    private ApiInterface apiInterface;
    private ProgressBar pb_Loader;
    private ArrayList<Model_ReportProducts> reportProductsList = new ArrayList<>();
    private int total_count = 1, page = 1;
    private String url;
    FloatingActionButton floatingActionButton;
    String Start_date = "", End_date = "", Order_Status = "";
    Filter_Product filter_product;

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
        filter_product = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_ProductsHolder = inflater.inflate(R.layout.fragment_report_products_list, container, false);
        load();
        return v_ProductsHolder;
    }

    private void load() {
        rc_ProductsList = v_ProductsHolder.findViewById(R.id.rc_report_products_list);
        rc_ProductsList.setLayoutManager(new LinearLayoutManager(activity));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        pb_Loader = v_ProductsHolder.findViewById(R.id.pb_loader);
        floatingActionButton = v_ProductsHolder.findViewById(R.id.filter_edit);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDialogFilter(Start_date, End_date, Order_Status);
            }
        });
        LoadProductsList(Start_date, End_date, Order_Status);
    }

    private void LoadDialogFilter(String start_date, String end_date, String status) {
        Bundle bundle = new Bundle();
        bundle.putString("start_date", start_date);
        bundle.putString("end_date", end_date);
        bundle.putString("order_status", status);
        Dialog_filter_product dialog_filter_product = new Dialog_filter_product();
        dialog_filter_product.setArguments(bundle);
        dialog_filter_product.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog_filter_product.AssignProducthandler(filter_product);
        dialog_filter_product.show(getChildFragmentManager(), "Filter");
    }



    private void urlSetup() {
        url = ApiClient.base_url + Constant.REPORT_ORDER + page;
    }

    private void LoadProductsList(String start_date, String end_date, String order_Status) {
        pb_Loader.setVisibility(View.VISIBLE);
        urlSetup();
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getProductsListReport(Constant.DataGetValue(activity, Constant.Token),
                start_date, end_date, order_Status,language);
        //Call<String> call = apiInterface.getProductsListReport(Constant.DataGetValue(activity, Constant.Token),url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                pb_Loader.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    reportProductsList = ContentJsonParser.getReportProductsList(response.body());
                    ProductsReportAdapter productsReportAdapter = new ProductsReportAdapter(reportProductsList);
                    rc_ProductsList.setAdapter(productsReportAdapter);
                    /*if (total_count > 1) {
                        productsReportAdapter.setOnLoadMoreListener(() -> {
                            if (page <= ((total_count / 6) + 1)) {
                                page++;
                                urlSetup();
                                Call<String> call1 = apiInterface.getOrderListReport(Constant.DataGetValue(activity, Constant.Token), url);
                                call1.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(@NonNull Call<String> call1, @NonNull Response<String> responsePaging) {
                                        pb_Loader.setVisibility(View.GONE);
                                        if (responsePaging.isSuccessful()) {

                                            ArrayList<Model_ReportProducts> temp_product_list = ContentJsonParser.getReportProductsList(responsePaging.body());

                                            if (temp_product_list != null) {
                                                if (temp_product_list.size() > 0) {
                                                    reportProductsList.addAll(temp_product_list);
                                                }

                                            }
                                            productsReportAdapter.notifyDataSetChanged();
                                            productsReportAdapter.setLoaded();

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
    public void LoadFilterProduct(String start_date, String end_date, String Order_status) {
        LoadProductsList(start_date, end_date, Order_status);
        Start_date = start_date;
        End_date = end_date;
        Order_Status = Order_status;
    }

    class ProductsReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Model_ReportProducts> reportProductsList;
        private int visibleThreshold = 6;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean loading;

        ProductsReportAdapter(ArrayList<Model_ReportProducts> reportProductsList) {
            this.reportProductsList = reportProductsList;
            if (rc_ProductsList.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rc_ProductsList.getLayoutManager();
                rc_ProductsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                return new ProductsViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_report_products, parent, false));
            } else {
                return new ProductsEmptyViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                ProductsViewHolder ProductsViewHolder = (ProductsViewHolder) holder;
                ProductsViewHolder.tv_ProductsName.setText(": " + reportProductsList.get(position).getProduct_name());
                ProductsViewHolder.tv_Quantity.setText(": " + reportProductsList.get(position).getQuantity());
                ProductsViewHolder.tv_Total.setText(": " + reportProductsList.get(position).getTotal());

            } else {
                ProductsEmptyViewHolder ProductsEmptyViewHolder = (ProductsEmptyViewHolder) holder;
                ProductsEmptyViewHolder.tv_ProductsEmpty.setText(getString(R.string.txt_report_products_empty));
            }
        }

        @Override
        public int getItemCount() {
            if (reportProductsList != null) {
                if (reportProductsList.size() > 0) {
                    return reportProductsList.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (reportProductsList != null) {
                if (reportProductsList.size() > 0) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                return 2;
            }
        }

        class ProductsViewHolder extends RecyclerView.ViewHolder {
            TextView tv_ProductsName, tv_Quantity, tv_Total;

            ProductsViewHolder(View itemView) {
                super(itemView);
                tv_ProductsName = itemView.findViewById(R.id.tv_report_product_name_value);
                tv_Quantity = itemView.findViewById(R.id.tv_report_quantity_value);
                tv_Total = itemView.findViewById(R.id.tv_report_total_value);
            }
        }

        class ProductsEmptyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_ProductsEmpty;

            ProductsEmptyViewHolder(View itemView) {
                super(itemView);
                tv_ProductsEmpty = itemView.findViewById(R.id.tv_empty);
            }
        }
    }
}
