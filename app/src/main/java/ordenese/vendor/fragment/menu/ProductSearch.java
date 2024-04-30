package ordenese.vendor.fragment.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiClientGson;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.databinding.FragmentProductSearchBinding;
import ordenese.vendor.model.GroceryProducts;
import ordenese.vendor.model.GroceryProductsList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductSearch extends Fragment {

    FragmentProductSearchBinding binding;
    ProductListAdapter productListAdapter;
    ApiInterface retrofitInterface;
    Activity activity;
    ArrayList<GroceryProductsList> groceryProductsListArrayList = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading;
    int page = 1, total = 0;

    long delay = 800; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();

    public ProductSearch() {
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
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setLayoutDirection(
                    "ae".equalsIgnoreCase(AppLanguageSupport.getLanguage(getActivity())) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductSearchBinding.inflate(inflater, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);

        binding.searchName.addTextChangedListener(new TextWatcher() {

            boolean isTyping = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0){
                    binding.clearLinear.setVisibility(View.VISIBLE);
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                }else {
                    binding.clearLinear.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.nestedView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY > oldScrollY) {
                        if (scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) {
                            //code to fetch more data for endless scrolling
                            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.productListRecView.getLayoutManager();
                            assert linearLayoutManager != null;
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }

                        }
                    }
                }
            }
        });

        binding.clearLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchName.setText("");
            }
        });

        return binding.getRoot();
    }

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                get_food_product(binding.searchName.getText().toString());
            }
        }
    };

    private void get_food_product(String data) {
        if (getActivity() != null) {
            if (Constant.isNetworkAvailable()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("page", page);
                    jsonObject.put("page_per_unit", "10");
                    jsonObject.put("search", data);
                    jsonObject.put("language_id", "");

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    retrofitInterface = ApiClientGson.getClient().create(ApiInterface.class);
                    Call<GroceryProducts> Call = retrofitInterface.product_search(Constant.DataGetValue(activity, Constant.Token), body);
                    mProgressDialog.show();
                    Call.enqueue(new Callback<GroceryProducts>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NonNull Call<GroceryProducts> call, @NonNull Response<GroceryProducts> response) {
                            if (response.isSuccessful()) {
                                GroceryProducts groceryProducts = response.body();
                                if (groceryProducts != null) {
                                    if (groceryProducts.getSuccess() != null) {
                                        total = Integer.parseInt(groceryProducts.getTotal());
                                        if (groceryProducts.getProduct().size() != 0) {
                                            groceryProductsListArrayList = groceryProducts.getProduct();
                                            binding.productListRecView.setVisibility(View.VISIBLE);
                                            binding.emptyProductList.setVisibility(View.GONE);

                                            binding.productListRecView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                                            productListAdapter = new ProductListAdapter(groceryProductsListArrayList);
                                            binding.productListRecView.setAdapter(productListAdapter);

                                            productListAdapter.notifyDataSetChanged();
                                        } else {
                                            binding.emptyProductList.setVisibility(View.VISIBLE);
                                            binding.productListRecView.setVisibility(View.GONE);
                                        }
                                    } else {
                                        binding.emptyProductList.setVisibility(View.VISIBLE);
                                        binding.productListRecView.setVisibility(View.GONE);
                                    }
                                } else {
                                    binding.emptyProductList.setVisibility(View.VISIBLE);
                                    binding.productListRecView.setVisibility(View.GONE);
                                }
                                setOnLoadMoreListener(() -> {
                                    page++; // to avoid unnecessary api hit
                                    if (page <= ((total / 10) + 1)) {
                                        mProgressDialog.show();
                                        try {

                                            jsonObject.put("page", page);
                                            jsonObject.put("page_per_unit", "10");
                                            jsonObject.put("search", data);

                                            jsonObject.put("language_id", "1");

                                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                                            retrofitInterface = ApiClientGson.getClient().create(ApiInterface.class);
                                            Call<GroceryProducts> Call = retrofitInterface.product(Constant.DataGetValue(activity, Constant.Token), body);
                                            mProgressDialog.show();
                                            Call.enqueue(new Callback<GroceryProducts>() {
                                                @SuppressLint("NotifyDataSetChanged")
                                                @Override
                                                public void onResponse(@NonNull Call<GroceryProducts> call, @NonNull Response<GroceryProducts> response) {
                                                    if (response.isSuccessful()) {
                                                        GroceryProducts groceryProducts = response.body();
                                                        if (groceryProducts != null) {
                                                            if (groceryProducts.getProduct().size() != 0) {
                                                                ArrayList<GroceryProductsList> productModelArrayTemp = groceryProducts.getProduct();
                                                                groceryProductsListArrayList.addAll(productModelArrayTemp);

                                                                productListAdapter.notifyDataSetChanged();
                                                                setLoaded();
                                                            } else {
                                                                binding.emptyProductList.setVisibility(View.VISIBLE);
                                                                binding.productListRecView.setVisibility(View.GONE);
                                                            }
                                                        } else {
                                                            binding.emptyProductList.setVisibility(View.VISIBLE);
                                                            binding.productListRecView.setVisibility(View.GONE);
                                                        }
                                                    }
                                                    mProgressDialog.cancel();
                                                }

                                                @Override
                                                public void onFailure(@NonNull Call<GroceryProducts> call, @NonNull Throwable t) {
                                                    mProgressDialog.cancel();
                                                }
                                            });
                                        } catch (JSONException e) {
                                            mProgressDialog.cancel();
                                            //Log.e("415 Excep ", e.toString());
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                            mProgressDialog.cancel();
                        }

                        @Override
                        public void onFailure(@NonNull Call<GroceryProducts> call, @NonNull Throwable t) {
                            mProgressDialog.cancel();
                        }
                    });
                } catch (JSONException e) {
                    mProgressDialog.cancel();
                    //Log.e("415 Excep ", e.toString());
                    e.printStackTrace();
                }

            } else {
                mProgressDialog.cancel();
                Constant.LoadNetworkError(getChildFragmentManager());
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(input_finish_checker);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(input_finish_checker);
    }

    class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

        ArrayList<GroceryProductsList> mProductList;

        public ProductListAdapter(ArrayList<GroceryProductsList> category_List) {
            this.mProductList = category_List;
        }

        @Override
        public ProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.grocery_category_product_list, parent, false);
            return new ProductListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ProductListAdapter.ViewHolder holder, final int position) {
            //  ////Log.e("CartProductListAdapter onBindViewHolder","");

            holder.mCategoryName.setText(mProductList.get(position).getItemName());
            holder.mPrice.setText(activity.getResources().getString(R.string.price) + "  : " + mProductList.get(position).getPrice());

            holder.labeledSwitch.setOn(mProductList.get(position).getStatus().equals("1"));

            holder.labeledSwitch.setOnToggledListener(new OnToggledListener() {
                @Override
                public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                    setStatus(mProductList.get(position).getProductItemId());
                }
            });

        }

        private void setStatus(String id) {

            if (getActivity() != null) {
                if (Constant.isNetworkAvailable()) {
                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put("product_id", id);
                        jsonObject.put("language_id", "");

                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                        retrofitInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<String> Call = retrofitInterface.product_status_update(Constant.DataGetValue(activity, Constant.Token), body);
                        mProgressDialog.show();
                        Call.enqueue(new Callback<String>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful()) {

                                }
                                mProgressDialog.cancel();
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                mProgressDialog.cancel();
                            }

                        });

                    } catch (JSONException e) {
                        mProgressDialog.cancel();
                        //Log.e("415 Excep ", e.toString());
                        e.printStackTrace();
                    }

                } else {
                    Constant.LoadNetworkError(getChildFragmentManager());
                }
            }

        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return mProductList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView mCategoryName, mPrice;
            LabeledSwitch labeledSwitch;

            public ViewHolder(View itemView) {
                super(itemView);
                mPrice = itemView.findViewById(R.id.product_price);
                mCategoryName = itemView.findViewById(R.id.product_name);
                labeledSwitch = itemView.findViewById(R.id.product_toggle_button);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }

    void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    void setLoaded() {
        loading = false;
    }
}