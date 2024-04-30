package ordenese.vendor.fragment.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.instant_transfer.CouponProductHandler;
import ordenese.vendor.model.Coupon_Products;

import java.util.ArrayList;


public class Dialog_coupon_products extends DialogFragment {
    RecyclerView recyclerView;
    Activity activity;
    Button btn_cancel, btn_ok;
    CouponProductHandler couponProductHandler;
    private ArrayList<Coupon_Products> Coupon_Products_Lists;
    private StringBuilder product_id,Product_Name;

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

    public void setCouponProductHandler(CouponProductHandler couponProductHandler){
        this.couponProductHandler = couponProductHandler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            Coupon_Products_Lists  = (ArrayList<Coupon_Products>) getArguments().getSerializable("products_lists");

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_coupon_products, container, false);
        btn_cancel = view.findViewById(R.id.button_cancel);
        btn_ok = view.findViewById(R.id.button_ok);
        recyclerView = view.findViewById(R.id.rc_coupon_products_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        if (Coupon_Products_Lists != null) {

            RecyclerViewListAdapter recyclerViewListAdapter = new RecyclerViewListAdapter();
            recyclerView.setAdapter(recyclerViewListAdapter);
        }
        btn_cancel.setOnClickListener(v -> dismiss());
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Coupon_Products_Lists != null){
                    if (Coupon_Products_Lists.size() > 0){
                        product_id = new StringBuilder();
                        Product_Name = new StringBuilder();
                        for (int i = 0; i < Coupon_Products_Lists.size(); i++) {

                            if (Coupon_Products_Lists.get(i).isSelected()){
                                if (product_id.length() > 0){
                                    product_id.append(",").append(Coupon_Products_Lists.get(i).getProduct_id());
                                    Product_Name.append(",").append(Coupon_Products_Lists.get(i).getName());
                                }else {
                                    product_id.append(Coupon_Products_Lists.get(i).getProduct_id());
                                    Product_Name.append(Coupon_Products_Lists.get(i).getName());
                                }

                            }

                        }
                        if (product_id != null) {
                            if (product_id.length() > 0){
                                couponProductHandler.LoadCouponProducts(product_id.toString(),Product_Name.toString());
                                dismiss();
                            }else {
                                Constant.showToast(getResources().getString(R.string.please_select_checkbox));
                            }
                        } else {
                            Constant.showToast(getResources().getString(R.string.please_select_checkbox));
                        }




                    }
                }
            }
        });
        return view;
    }

    private class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private boolean isFromView = false;


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == 1) {
                view = LayoutInflater.from(activity).inflate(R.layout.coupon_product_list_adapter, parent, false);
                return new ProductViewHolder(view);
            } else {
                return new EmptyViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                ProductViewHolder productViewHolder = (ProductViewHolder) holder;
                productViewHolder.mCheckBox.setText(Coupon_Products_Lists.get(position).getName());

                // To check weather checked event fire from getview() or user input
                isFromView = true;
                productViewHolder.mCheckBox.setChecked(Coupon_Products_Lists.get(position).isSelected());
                isFromView = false;

                productViewHolder.mCheckBox.setTag(position);
                productViewHolder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int getPosition = (Integer) buttonView.getTag();

                    if (!isFromView) {
                        Coupon_Products_Lists.get(getPosition).setSelected(isChecked);
                    }
                });
            } else {
                EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
                emptyViewHolder.tv_CouponEmpty.setText(getString(R.string.txt_report_products_empty));
            }
        }



        @Override
        public int getItemCount() {
            if (Coupon_Products_Lists != null) {
                if (Coupon_Products_Lists.size() > 0) {
                    return Coupon_Products_Lists.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }

        }

        @Override
        public int getItemViewType(int position) {
            if (Coupon_Products_Lists != null) {
                if (Coupon_Products_Lists.size() > 0) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                return 2;
            }
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            private CheckBox mCheckBox;

            ProductViewHolder(View itemView) {
                super(itemView);

                mCheckBox = itemView
                        .findViewById(R.id.checkBox);
            }
        }

        class EmptyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_CouponEmpty;
            EmptyViewHolder(View view) {
                super(view);
                tv_CouponEmpty = itemView.findViewById(R.id.tv_empty);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);

        if (getDialog() == null)
            return;

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }
}
