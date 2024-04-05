package ordenese.vendor.fragment.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.common.instant_transfer.Refresher;
import ordenese.vendor.model.Product_List;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_product_list extends Fragment implements Refresher {

    Activity activity;
    RecyclerView rc_product_list;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    private ArrayList<Product_List> mProductList = new ArrayList<>();
    Button button;
    private int page = 1, total = 0;
    ProductListAdapter productListAdapter;
    private Refresher refresher;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        refresher = this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(true);
        rc_product_list = view.findViewById(R.id.rc_product_list);
        button = view.findViewById(R.id.btn_add_product);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadAddProduct("", "");
            }
        });
        LoadProductList();
        return view;
    }

    private void LoadProductList() {
        if (mProductList != null) {
            if (mProductList.size() > 0) {
                page = 1;

                for (int i = 0; i < mProductList.size(); i++) {
                    mProductList.remove(i);
                }
            }
        }
        if (Constant.isNetworkAvailable()) {

            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
            Call<String> call = apiInterface.getProductsList(Constant.DataGetValue(activity, Constant.Token), page,language);
            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful()) {
                        total = ContentJsonParser.getTotalCount(response.body());
                        mProductList = ContentJsonParser.getProductList(response.body());
                        rc_product_list.setLayoutManager(new LinearLayoutManager(activity));
                        productListAdapter = new ProductListAdapter();
                        rc_product_list.setAdapter(productListAdapter);
                        productListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {
                                progressDialog.show();
                                if (page <= ((total / 10))) {
                                    if (Constant.isNetworkAvailable()) {
                                        page++;
                                        apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                                        Call<String> call = apiInterface.getProductsList(Constant.DataGetValue(activity, Constant.Token), page,language);
                                        call.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (response.isSuccessful()) {
                                                    progressDialog.cancel();
                                                    if (response.body() != null) {
                                                        ArrayList<Product_List> temp_product_list = ContentJsonParser.getProductList(response.body());

                                                        if (temp_product_list != null) {
                                                            if (temp_product_list.size() > 0) {
                                                                mProductList.addAll(temp_product_list);
                                                            }

                                                        }
                                                        productListAdapter.notifyDataSetChanged();
                                                        productListAdapter.setLoaded();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                progressDialog.cancel();
                                            }
                                        });

                                    }
                                } else {
                                    progressDialog.cancel();
                                }
                            }
                        });


                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    progressDialog.cancel();
                }
            });






        } else {
            Constant.LoadNetworkError(getChildFragmentManager());
        }

    }

    @Override
    public void refresher() {
        page = 1;
        LoadProductList();
    }

    private class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean loading;

        ProductListAdapter() {
            if (rc_product_list.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rc_product_list.getLayoutManager();
                rc_product_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            View view;
            if (viewType == 1) {
                view = LayoutInflater.from(activity).inflate(R.layout.product_list_adapter, parent, false);
                return new ViewHolderProduct(view);
            } else {
                return new EmptyViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                ViewHolderProduct viewHolderProduct = (ViewHolderProduct) holder;
                viewHolderProduct.p_name.setText(mProductList.get(position).getName());
                viewHolderProduct.p_price.setText(mProductList.get(position).getPrice());
                viewHolderProduct.p_quantity.setText(mProductList.get(position).getQuantity());
                viewHolderProduct.p_status.setText(mProductList.get(position).getStatus());
                Glide.with(activity).load(Constant.base_url+"image/"+mProductList.get(position).getImage()).into(viewHolderProduct.p_image);
                viewHolderProduct.iv_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadAddProduct(mProductList.get(position).getProduct_id(), "Edit");
                    }
                });
                viewHolderProduct.iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadDeleteProduct(mProductList.get(position).getProduct_id());
                    }
                });

            } else {
                EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
                emptyViewHolder.tv_CouponEmpty.setText(getString(R.string.txt_report_products_empty));
            }
        }

        private void LoadDeleteProduct(String product_id) {
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                        LoadProductDelete(product_id);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                    .create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle(activity.getString(R.string.delete_confirmation_title));
            alertDialog.setMessage(activity.getString(R.string.delete_message));
            alertDialog.show();
        }

        private void LoadProductDelete(String product_id) {
            if (Constant.isNetworkAvailable()) {
                progressDialog.show();
                String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("product_id", product_id);
                    jsonObject.put("language_id",language);
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                    Call<String> callDelete = apiInterface.DeleteProduct(Constant.DataGetValue(activity, Constant.Token), body);
                    callDelete.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful()) {
                                progressDialog.dismiss();
                                LoadProductList();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            progressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                Constant.LoadNetworkError(getChildFragmentManager());
            }

        }

        @Override
        public int getItemCount() {
            if (mProductList != null) {
                if (mProductList.size() > 0) {
                    return mProductList.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mProductList != null) {
                if (mProductList.size() > 0) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                return 2;
            }
        }

        class ViewHolderProduct extends RecyclerView.ViewHolder {
            ImageView p_image, iv_edit, iv_delete;
            TextView p_price, p_quantity, p_name, p_status;

            ViewHolderProduct(View itemView) {
                super(itemView);
                p_image = itemView.findViewById(R.id.product_image);
                p_price = itemView.findViewById(R.id.tv_price);
                p_quantity = itemView.findViewById(R.id.tv_quantity);
                p_name = itemView.findViewById(R.id.tv_product_name);
                p_status = itemView.findViewById(R.id.tv_status);
                iv_edit = itemView.findViewById(R.id.iv_edit);
                iv_delete = itemView.findViewById(R.id.iv_delete);
            }
        }

        private class EmptyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_CouponEmpty;

            EmptyViewHolder(View itemView) {
                super(itemView);
                tv_CouponEmpty = itemView.findViewById(R.id.tv_empty);
            }
        }
    }

    public void LoadAddProduct(String product_id, String Type) {
        Fragment_Add_Product fragment = new Fragment_Add_Product();
        fragment.setRefresher(refresher);
        Bundle bundle = new Bundle();
        bundle.putString("type", Type);
        bundle.putString("product_id", product_id);
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        fragment.show(getChildFragmentManager(), "Add Product");
    }
}
