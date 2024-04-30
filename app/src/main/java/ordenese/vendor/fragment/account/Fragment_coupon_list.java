package ordenese.vendor.fragment.account;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.CouponPageHandler;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.fragment.dialog.Dialog_coupons;
import ordenese.vendor.model.Coupon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_coupon_list extends Fragment implements CouponPageHandler {

    ApiInterface apiInterface;
    Activity activity;
    private ArrayList<Coupon> mCouponList;
    private int page = 1, total = 0;
    RecyclerView rc_coupon_list;
    CouponListAdapter couponListAdapter;
    ProgressBar progressBar;
    Button btn_add_new;
    CouponPageHandler couponPageHandler;

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
        View view = inflater.inflate(R.layout.fragment_coupon_list,container,false);
        rc_coupon_list = view.findViewById(R.id.rc_coupon_list);
        progressBar = view.findViewById(R.id.progressBar);
        btn_add_new = view.findViewById(R.id.btn_add_new);

        btn_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              LoadAddCoupon("","");
            }
        });

        LoadCouponList();
        couponPageHandler = this;
        return view;
    }

    private void LoadCouponList() {
        if (mCouponList != null) {
            if (mCouponList.size() > 0) {
                page = 1;

                for (int i = 0; i < mCouponList.size(); i++) {
                    mCouponList.remove(i);
                }
            }
        }
        if (Constant.isNetworkAvailable()) {
            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
            Call<String> call = apiInterface.getCouponList(Constant.DataGetValue(activity, Constant.Token), page, 5,language);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        total = ContentJsonParser.getCouponTotalCount(response.body());
                        mCouponList = ContentJsonParser.getCouponList(response.body());
                        rc_coupon_list.setLayoutManager(new LinearLayoutManager(activity));
                        couponListAdapter = new CouponListAdapter();
                        rc_coupon_list.setAdapter(couponListAdapter);
                        couponListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {
                                progressBar.setVisibility(View.VISIBLE);
                                if (page <= ((total / 10) + 1)) {
                                    if (Constant.isNetworkAvailable()) {
                                        page++;
                                        apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                                        Call<String> call = apiInterface.getCouponList(Constant.DataGetValue(activity, Constant.Token), page, 5,language);
                                        call.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (response.isSuccessful()) {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (response.body() != null) {
                                                        ArrayList<Coupon> temp_coupon_list = ContentJsonParser.getCouponList(response.body());

                                                        if (temp_coupon_list != null) {
                                                            if (temp_coupon_list.size() > 0) {
                                                                mCouponList.addAll(temp_coupon_list);
                                                            }

                                                        }
                                                        couponListAdapter.notifyDataSetChanged();
                                                        couponListAdapter.setLoaded();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });

                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        } else {
            Constant.LoadNetworkError(getChildFragmentManager());
        }
    }

    @Override
    public void LoadAddCoupon(String id, String type) {
        Dialog_coupons fragment_coupons = new Dialog_coupons();
        //add_menu_item.setDialogInterface(alert_dialog_handler);
        fragment_coupons.setStyle(DialogFragment.STYLE_NO_TITLE,0);
        if(couponPageHandler!=null) {
            fragment_coupons.setAddCouponInterface(couponPageHandler);
        }
        Bundle bundle = new Bundle();
        bundle.putString("coupon_id",id);
        bundle.putString("coupon_type",type);
        fragment_coupons.setArguments(bundle);
        fragment_coupons.show(getChildFragmentManager(), "Add coupon");
    }

    @Override
    public void Refresher() {
        page = 1;
        LoadCouponList();
    }

    private class CouponListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean loading;

        CouponListAdapter() {
            if (rc_coupon_list.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rc_coupon_list.getLayoutManager();
                rc_coupon_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        @Override
        public int getItemCount() {
            if (mCouponList != null) {
                if (mCouponList.size() > 0) {
                    return mCouponList.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mCouponList != null) {
                if (mCouponList.size() > 0) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 2) {
                return new ViewHolderCoupon(LayoutInflater.from(activity).inflate(R.layout.fragment_coupon_list_adapter, parent, false));
            } else {
                return new ViewHolderEmpty(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 2) {
                ViewHolderCoupon viewHolderCoupon = (ViewHolderCoupon) holder;
                viewHolderCoupon.coupon_name.setText(" : " + mCouponList.get(position).getName());
                viewHolderCoupon.coupon_status.setText(" : " + mCouponList.get(position).getStatus());
                viewHolderCoupon.coupon_code.setText(" : " + mCouponList.get(position).getCode());
                viewHolderCoupon.coupon_discount.setText(" : " + mCouponList.get(position).getDiscount());

                viewHolderCoupon.iv_coupon_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogAlertDelete(activity,mCouponList.get(position).getCoupon_id());
                    }
                });
                viewHolderCoupon.iv_coupon_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadAddCoupon(mCouponList.get(position).getCoupon_id(),"Edit");
                    }
                });
            } else {
                ViewHolderEmpty viewHolderEmpty = (ViewHolderEmpty) holder;
                viewHolderEmpty.tv_couponListEmpty.setText(getString(R.string.coupon_empty));
            }
        }
//
        void DialogAlertDelete(Context mContext, String id){
            AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                    .setPositiveButton(R.string.btn_ok, (dialogInterface, i) -> {
                        LoadCouponDelete(id);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle(mContext.getString(R.string.delete_confirmation_title));
            alertDialog.setMessage(mContext.getString(R.string.delete_message));
            alertDialog.show();
        }


        private void LoadCouponDelete(String coupon_id) {
            if (Constant.isNetworkAvailable()) {
                progressBar.setVisibility(View.VISIBLE);
                String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                Call<String> call = apiInterface.DeleteCoupon(Constant.DataGetValue(activity, Constant.Token), coupon_id,language);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject1 = new JSONObject(response.body());
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("success");
                                Constant.showToast(jsonObject2.getString("message"));
                                LoadCouponList();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });


            } else {
                Constant.LoadNetworkError(getChildFragmentManager());
            }

        }


        private class ViewHolderCoupon extends RecyclerView.ViewHolder {
            TextView coupon_name, coupon_status,coupon_code,coupon_discount;
            ImageView iv_coupon_edit, iv_coupon_delete;
            CardView Card_view;

            ViewHolderCoupon(View view) {
                super(view);
                Card_view = view.findViewById(R.id.Card_view);
                coupon_name = view.findViewById(R.id.tv_name_value);
                coupon_status = view.findViewById(R.id.tv_status_value);
                coupon_code = view.findViewById(R.id.tv_code_value);
                coupon_discount = view.findViewById(R.id.tv_discount_value);
                iv_coupon_edit = view.findViewById(R.id.iv_coupon_edit);
                iv_coupon_delete = view.findViewById(R.id.iv_coupon_delete);
            }
        }

        private class ViewHolderEmpty extends RecyclerView.ViewHolder {
            TextView tv_couponListEmpty;

            ViewHolderEmpty(View view) {
                super(view);
                tv_couponListEmpty = view.findViewById(R.id.tv_empty);
            }
        }
    }
}
