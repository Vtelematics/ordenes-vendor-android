package ordenese.vendor.fragment.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiClientGson;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.activity.Activity_BackBtn_Container;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.databinding.FragmentFoodProductListingBinding;
import ordenese.vendor.databinding.FragmentGroceryProductListingBinding;
import ordenese.vendor.databinding.GroceryCmpBottomBinding;
import ordenese.vendor.model.CategoryList;
import ordenese.vendor.model.GroceryCategoryDataSet;
import ordenese.vendor.model.GroceryProducts;
import ordenese.vendor.model.GroceryProductsList;
import ordenese.vendor.model.SubCategoryDataSet;
import ordenese.vendor.model.SubCategoryList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodProductListing extends Fragment {

    FragmentFoodProductListingBinding binding;
    int category_position = 0;
    Activity activity;
    TitleListAdapter adapter;
    ProductListAdapter productListAdapter;
    ApiInterface retrofitInterface;
    ArrayList<GroceryProductsList> groceryProductsListArrayList = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading;
    int page = 1, total = 0;

    public FoodProductListing() {
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
        binding = FragmentFoodProductListingBinding.inflate(inflater, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        binding.laySearchTextSearchItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, Activity_BackBtn_Container.class);
                intent.putExtra("Type", "search_product");
                startActivity(intent);

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

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        get_category();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void get_category() {

        if (getActivity() != null) {
            if (Constant.isNetworkAvailable()) {
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("language_id", "");

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    retrofitInterface = ApiClientGson.getClient().create(ApiInterface.class);
                    Call<CategoryList> Call = retrofitInterface.category_list(Constant.DataGetValue(activity, Constant.Token), body);
                    mProgressDialog.show();
                    Call.enqueue(new Callback<CategoryList>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NonNull Call<CategoryList> call, @NonNull Response<CategoryList> response) {
                            if (response.isSuccessful()) {
                                CategoryList categoryDataSet = response.body();
                                if (categoryDataSet != null) {
                                    if (categoryDataSet.getCategory().size() != 0) {
                                        binding.categoryRecView.setVisibility(View.VISIBLE);
                                        binding.productListRecView.setVisibility(View.VISIBLE);

                                        binding.emptyProductList.setVisibility(View.GONE);

                                        binding.categoryRecView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
                                        adapter = new TitleListAdapter(categoryDataSet.getCategory(), activity, category_position, true);
                                        binding.categoryRecView.setAdapter(adapter);

                                        get_food_product(categoryDataSet.getCategory().get(0).category_id);

                                    } else {
                                        binding.categoryRecView.setVisibility(View.GONE);
                                        binding.productListRecView.setVisibility(View.GONE);

                                        binding.emptyProductList.setVisibility(View.VISIBLE);
                                        mProgressDialog.cancel();
                                    }
                                } else {
                                    binding.categoryRecView.setVisibility(View.GONE);
                                    binding.productListRecView.setVisibility(View.GONE);

                                    binding.emptyProductList.setVisibility(View.VISIBLE);
                                    mProgressDialog.cancel();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<CategoryList> call, @NonNull Throwable t) {
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

    private void get_food_product(String categoryId) {

        if (getActivity() != null) {
            if (Constant.isNetworkAvailable()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("vendor_type_id", "2");
                    jsonObject.put("category_id", categoryId);
                    jsonObject.put("page", page);
                    jsonObject.put("page_per_unit", "10");
                    jsonObject.put("search", "");

                    jsonObject.put("language_id", "");

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
                                binding.categoryRecView.setVisibility(View.VISIBLE);
                                if (groceryProducts != null) {
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

                                setOnLoadMoreListener(() -> {
                                    page++; // to avoid unnecessary api hit
                                    if (page <= ((total / 10) + 1)) {
                                        mProgressDialog.show();
                                        try {

                                            jsonObject.put("vendor_type_id", "2");
                                            jsonObject.put("category_id", categoryId);
                                            jsonObject.put("page", page);
                                            jsonObject.put("page_per_unit", "10");
                                            jsonObject.put("search", "");

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
                Constant.LoadNetworkError(getChildFragmentManager());
            }
        }

    }

    class TitleListAdapter extends RecyclerView.Adapter<TitleListAdapter.DataObjectHolder> {

        private final ArrayList<GroceryCategoryDataSet> mTitleList;
        private TextView mTempMenuTitleName;
        private Boolean mIsInitial = true;
        private final int mPositionToMove;
        private Boolean mIsFromItemTouch;
        Activity activity;


        public TitleListAdapter(ArrayList<GroceryCategoryDataSet> titleList, Activity activity, int positionToMove, Boolean isFromItemTouch) {
            this.mTitleList = titleList;
            this.activity = activity;
            this.mPositionToMove = positionToMove;
            this.mIsFromItemTouch = isFromItemTouch;
        }

        @Override
        public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_menu_title_list_row, parent, false);
            return new DataObjectHolder(mView);
        }


        @Override
        public void onBindViewHolder(final DataObjectHolder holder, final int position) {

            holder.mMenuTitleName.setText(mTitleList.get(position).name);

//            //Log.e("onBindViewHolder: ", mPositionToMove + " /" + position);

            if (mIsFromItemTouch) {
                if (mPositionToMove == position) {
                    if (mTempMenuTitleName != null) {
                        if (activity != null) {
                            mTempMenuTitleName.setTextColor(ContextCompat.getColor(activity, R.color.grey_500));
                            mTempMenuTitleName.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                        }
                    }
                    if (activity != null) {
                        holder.mMenuTitleName.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                        holder.mMenuTitleName.setBackground(activity.getResources().getDrawable(R.drawable.bg_category_selected));
                    }
                    mTempMenuTitleName = holder.mMenuTitleName;
                } else {
                    if (activity != null) {
                        holder.mMenuTitleName.setTextColor(ContextCompat.getColor(activity, R.color.grey_500));
                        holder.mMenuTitleName.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                    }
                }
            } else {
                if (mIsInitial) {
                    //safe check :-
                    if (mTempMenuTitleName != null) {
                        if (activity != null) {
                            mTempMenuTitleName.setTextColor(ContextCompat.getColor(activity, R.color.grey_500));
                            mTempMenuTitleName.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                        }
                    }
                    if (activity != null) {
                        holder.mMenuTitleName.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                        holder.mMenuTitleName.setBackground(activity.getResources().getDrawable(R.drawable.bg_category_selected));
                    }
                    mTempMenuTitleName = holder.mMenuTitleName;
                    mIsInitial = false;
                } else {
                    if (activity != null) {
                        holder.mMenuTitleName.setTextColor(ContextCompat.getColor(activity, R.color.grey_500));
                        holder.mMenuTitleName.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                    }
                }
            }

            holder.mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsFromItemTouch = false;
                    if (mTempMenuTitleName != null) {
                        if (activity != null) {
                            mTempMenuTitleName.setTextColor(ContextCompat.getColor(activity, R.color.grey_500));
                            mTempMenuTitleName.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                        }
                    }
                    if (activity != null) {
                        holder.mMenuTitleName.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
                        holder.mMenuTitleName.setBackground(activity.getResources().getDrawable(R.drawable.bg_category_selected));
                    }
                    mTempMenuTitleName = holder.mMenuTitleName;

                    get_food_product(mTitleList.get(position).category_id);
                }
            });

            if (position == mTitleList.size() - 1) {
                holder.mTitleView.setVisibility(View.GONE);
            } else {
                holder.mTitleView.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return mTitleList.size();
        }

        class DataObjectHolder extends RecyclerView.ViewHolder {

            private final TextView mMenuTitleName;
            //            private final View mBarView;
            private final View mTitleView;
            private final LinearLayout mRow;

            public DataObjectHolder(View view) {
                super(view);
                mMenuTitleName = view.findViewById(R.id.tv_store_menu_title);
//                mBarView = view.findViewById(R.id.view_store_menu_title_bar);
                mTitleView = view.findViewById(R.id.view_store_menu_title);
                mRow = view.findViewById(R.id.lay_store_menu_title_list_row);
            }
        }

    }

    class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

        ArrayList<GroceryProductsList> mProductList;

        public ProductListAdapter(ArrayList<GroceryProductsList> category_List) {
            this.mProductList = category_List;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.grocery_category_product_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
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
            private TextView mCategoryName, mPrice;
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

    public static class CategoriesListBottom extends BottomSheetDialogFragment {

        GroceryCmpBottomBinding binding;
        ArrayList<GroceryCategoryDataSet> categoryListingArrayList;
        String vendor_id = "", vendor_name = "", vendor_status = "";

        public CategoriesListBottom() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
            if (getArguments() != null) {
                categoryListingArrayList = (ArrayList<GroceryCategoryDataSet>) getArguments().getSerializable("category_list");
                vendor_id = getArguments().getString("vendor_id");
                vendor_name = getArguments().getString("vendor_name");
                vendor_status = getArguments().getString("vendor_status");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            binding = GroceryCmpBottomBinding.inflate(inflater, container, false);


            binding.recyclerGCmpBList.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            binding.recyclerGCmpBList.setAdapter(new AllCategoryListAdapter(categoryListingArrayList));
            binding.imgGCmpBCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                }
            });
            return binding.getRoot();
        }

        public class AllCategoryListAdapter extends RecyclerView.Adapter<AllCategoryListAdapter.ViewHolder> {

            private ArrayList<GroceryCategoryDataSet> m_Category_List;
            private LinearLayout mListEmptyContainer;
            private RecyclerView mListView;


            public AllCategoryListAdapter(ArrayList<GroceryCategoryDataSet> category_List) {
                this.m_Category_List = category_List;

            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.grocery_category_list_row, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final ViewHolder holder, final int position) {

                holder.mCategoryName.setText(m_Category_List.get(position).name);

                if (getActivity() != null) {
                    holder.mCategoryImg.setVisibility(View.VISIBLE);
                    holder.mCategoryImgMoreThan11.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(m_Category_List.get(position).picture).into(holder.mCategoryImg);
                }

                holder.mLayRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toPerformCategoryProductCall(position);
                    }
                });
            }

            private void toPerformCategoryProductCall(int currentPosition) {

//                FragmentTransaction mFT = getParentFragmentManager().beginTransaction();
//                GroceryProductListing groceryProductListing = new GroceryProductListing();
//                Bundle mBundle = new Bundle();
//                mBundle.putString("category_id", m_Category_List.get(currentPosition).category_id);
//                mBundle.putString("vendor_id", vendor_id);
//                mBundle.putString("vendor_name", vendor_name);
//                mBundle.putString("vendor_status", vendor_status);
//                mBundle.putInt("category_position", currentPosition);
//                mBundle.putSerializable("category_list", m_Category_List);
//                groceryProductListing.setArguments(mBundle);
//                mFT.replace(R.id.layout_app_home_body, groceryProductListing, "groceryProductListing");
//                mFT.addToBackStack("groceryProductListing");
//                mFT.commit();

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
                return m_Category_List.size();
            }

            public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


                private TextView mCategoryName;
                private ImageView mCategoryImg, mCategoryImgMoreThan11;
                private LinearLayout mLayRow;

                public ViewHolder(View itemView) {
                    super(itemView);

                    mCategoryName = itemView.findViewById(R.id.tv_gc_l_category_name);

                    mCategoryImgMoreThan11 = itemView.findViewById(R.id.iv_gc_l_category_image_more_than_11);
                    mCategoryImg = itemView.findViewById(R.id.iv_gc_l_category_image);
                    mLayRow = itemView.findViewById(R.id.lay_gc_list_row);

                }

                @Override
                public void onClick(View v) {

                }
            }
        }


    }


}