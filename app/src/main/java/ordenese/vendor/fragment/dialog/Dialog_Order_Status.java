package ordenese.vendor.fragment.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.LoginPageHandler;
import ordenese.vendor.model.Order_Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dialog_Order_Status extends DialogFragment {

    private ArrayList<Order_Status> Order_status_list;
    Activity activity;
    Spinner sp_order_status;
    TextView btn_cancel, btn_save;
    private String Order_status_id;
    private String Order_id, order_status_id;
    EditText et_comment;
    CheckBox checkBox;
    private boolean Notify_Me;
    ApiInterface apiInterface;
    ProgressBar progressBar;
    LoginPageHandler loginPageHandler;

    @Override
    public void onAttach(Context context) {

        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        loginPageHandler = (LoginPageHandler) context;
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
        if (getArguments() != null) {
            Order_status_list = (ArrayList<Order_Status>) getArguments().getSerializable("Order_Status");
            Order_id = getArguments().getString("Order_id");
            order_status_id = getArguments().getString("order_status_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_order_status, container, false);
        sp_order_status = view.findViewById(R.id.sp_order_status);
        et_comment = view.findViewById(R.id.short_comment);
        checkBox = view.findViewById(R.id.cb_notify);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_save = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.ld_status);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Notify_Me = true;
            } else {
                Notify_Me = false;
            }
        });
        btn_save.setOnClickListener(v -> LoadUpdateStatus());
        btn_cancel.setOnClickListener(v -> dismiss());
        LoadStatus();
        return view;
    }

    private void LoadStatus() {
        SpinnerOrderStatus spinnerOrderStatus = new SpinnerOrderStatus(Order_status_list);
        sp_order_status.setAdapter(spinnerOrderStatus);
        for (int i = 0; i < Order_status_list.size(); i++) {
            if (order_status_id.equals(Order_status_list.get(i).getOrder_ststus_id())) {
                sp_order_status.setSelection(i);
            }
        }

        sp_order_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Order_status_id = Order_status_list.get(position).getOrder_ststus_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private class SpinnerOrderStatus extends BaseAdapter implements SpinnerAdapter {
        private final ArrayList<Order_Status> OrderStatus;

        SpinnerOrderStatus(ArrayList<Order_Status> order_status_list) {
            this.OrderStatus = order_status_list;
        }

        @Override
        public int getCount() {
            return OrderStatus.size();
        }

        @Override
        public Object getItem(int position) {
            return OrderStatus.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(10, 5, 5, 5);
            txt.setTextSize(14);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_grey_500_24dp, 0);
            txt.setText(OrderStatus.get(position).getName());
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(OrderStatus.get(position).getName());
            return txt;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setTitle(getString(R.string.order_add_status));
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);

        if (getDialog() == null)
            return;

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void LoadUpdateStatus() {

        if (Constant.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("order_id", Order_id);
                jsonObject.put("order_status_id", Order_status_id);
                jsonObject.put("comment", et_comment.getText().toString());
                jsonObject.put("notify", Notify_Me);
                jsonObject.put("language_id", language);

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Call<String> call = apiInterface.UpdateOrderStatus(Constant.DataGetValue(activity, Constant.Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            loginPageHandler.LoadOrderInfo(Order_id);
                            dismiss();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Constant.LoadNetworkError(getChildFragmentManager());
        }
    }
}
