package ordenese.vendor.fragment.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.CouponPageHandler;
import ordenese.vendor.common.instant_transfer.CouponProductHandler;
import ordenese.vendor.common.instant_transfer.LoginPageHandler;
import ordenese.vendor.model.Coupon_Products;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dialog_coupons extends DialogFragment implements CouponProductHandler {

    Spinner sp_type, sp_status;
    Activity activity;
    String[] Status;
    String[] Discount;
    private String Status_id, Discount_type;
    private int status_id;
    TextView start_date, end_date, error_coupon_name, error_coupon_code, tv_coupon_title, btn_cancel, btn_save, tv_products;
    EditText ed_coupon_name, ed_coupon_code, ed_discount, ed_total_amount, ed_use_per_coupon, ed_use_per_customer;
    String key = "";
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    ApiInterface apiInterface;
    private CouponPageHandler CouponPageHandler;
    private String Coupon_id, ProductIds, Coupon_type, discount_type;
    private ArrayList<Coupon_Products> CouponProductList;
    CouponProductHandler couponProductHandler;
    ProgressBar progressBar;
    LoginPageHandler loginPageHandler;
    private ArrayList<Integer> Select_Products_id  = new ArrayList<>();


    @Override
    public void onAttach(Context context) {

        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        this.loginPageHandler = (LoginPageHandler) context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getActivity() != null) {

                getActivity().getWindow().getDecorView().setLayoutDirection(
                        "ar".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                                View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

            }
        }

    }


    public void setAddCouponInterface(CouponPageHandler couponPageHandler) {
        this.CouponPageHandler = couponPageHandler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Coupon_id = getArguments().getString("coupon_id");
            Coupon_type = getArguments().getString("coupon_type");
        }
        this.couponProductHandler = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        if (!Coupon_type.equals("Edit")){
            LoadProducts();
        }

        Status = new String[] {getResources().getString(R.string.disabled), getResources().getString(R.string.enabled)};
        Discount = new String[] {getResources().getString(R.string.percentage), getResources().getString(R.string.fixed_amount)};

        sp_type = view.findViewById(R.id.sp_type);
        sp_status = view.findViewById(R.id.sp_status);
        start_date = view.findViewById(R.id.tv_start_date);
        end_date = view.findViewById(R.id.tv_end_date);
        ed_coupon_name = view.findViewById(R.id.ed_coupon_name);
        ed_coupon_code = view.findViewById(R.id.ed_coupon_code);
        ed_discount = view.findViewById(R.id.ed_coupon_discount);
        ed_total_amount = view.findViewById(R.id.ed_coupon_total_amount);
        ed_use_per_coupon = view.findViewById(R.id.ed_uses_per_coupon);
        ed_use_per_customer = view.findViewById(R.id.ed_uses_per_customer);
        error_coupon_name = view.findViewById(R.id.error_coupon_name);
        error_coupon_code = view.findViewById(R.id.error_coupon_code);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_save = view.findViewById(R.id.btn_save);
        tv_products = view.findViewById(R.id.tv_products);
        tv_coupon_title = view.findViewById(R.id.tv_coupon_title);
        progressBar = view.findViewById(R.id.progressBar);
        btn_save.setOnClickListener(v -> {
            if (ed_coupon_name.getText().toString().length() > 2) {
                error_coupon_name.setVisibility(View.GONE);
            } else {
                error_coupon_name.setVisibility(View.VISIBLE);
            }
            if (ed_coupon_code.getText().length() > 2) {
                error_coupon_code.setVisibility(View.GONE);
            } else {
                error_coupon_code.setVisibility(View.VISIBLE);

            }

            JSONObject selectProductIds = new JSONObject();
            if (ProductIds != null) {
                String[] Product_Ids = ProductIds.split(",");

                if (Product_Ids.length > 0) {
                    for (int i = 0; i < Product_Ids.length; i++) {
                        try {
                            selectProductIds.put(String.valueOf(i), Product_Ids[i]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            if (ed_coupon_name.getText().length() > 2
                    && ed_coupon_code.getText().length() > 2
                    ) {
                String CouponName = ed_coupon_name.getText().toString();
                String CouponCode = ed_coupon_code.getText().toString();
                String Discount = ed_discount.getText().toString();
                String TotalAmount = ed_total_amount.getText().toString();
                String UsesPerCoupon = ed_use_per_coupon.getText().toString();
                String UsesPerCustomer = ed_use_per_customer.getText().toString();
                String Start_Date = start_date.getText().toString();
                String End_Date = end_date.getText().toString();
                String language = LanguageDetailsDB.getInstance(activity).get_language_id();

                if (!Coupon_type.equals("Edit")) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("name", CouponName);
                        jsonObject.put("code", CouponCode);
                        jsonObject.put("type", discount_type);
                        jsonObject.put("discount", Discount);
                        jsonObject.put("total", TotalAmount);
                        jsonObject.put("coupon_product", selectProductIds);
                        jsonObject.put("date_start", Start_Date);
                        jsonObject.put("date_end", End_Date);
                        jsonObject.put("uses_total", UsesPerCoupon);
                        jsonObject.put("uses_customer", UsesPerCustomer);
                        jsonObject.put("status", status_id);
                        jsonObject.put("language_id",language);

                        progressBar.setVisibility(View.VISIBLE);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                        Call<String> call = apiInterface.AddCoupon(Constant.DataGetValue(activity, Constant.Token), body);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response.body());
                                        JSONObject jsonObject2 = jsonObject1.getJSONObject("success");
                                        Constant.showToast(jsonObject2.getString("message"));
                                        CouponPageHandler.Refresher();
                                        dismiss();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        ResponseBody requestBody = response.errorBody();
                                        BufferedReader r = new BufferedReader(new InputStreamReader(requestBody.byteStream()));
                                        StringBuilder total = new StringBuilder();
                                        String line;
                                        while ((line = r.readLine()) != null) {
                                            total.append(line).append('\n');
                                        }

                                        JSONObject jObjError = new JSONObject(total.toString());
                                        if (!jObjError.isNull("error")) {
                                            JSONObject jsonErrorObject = jObjError.getJSONObject("error");
                                            if (!jsonErrorObject.isNull("message")) {
                                                Constant.showToast(jsonErrorObject.getString("message"));
                                            } else {
                                                Constant.showToast(getString(R.string.error));
                                            }
                                        } else {
                                            Constant.showToast(getString(R.string.error));
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Constant.showToast(getString(R.string.error));
                                    }
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
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("coupon_id", Coupon_id);
                        jsonObject.put("name", CouponName);
                        jsonObject.put("code", CouponCode);
                        jsonObject.put("type", discount_type);
                        jsonObject.put("discount", Discount);
                        jsonObject.put("total", TotalAmount);
                        jsonObject.put("coupon_product", selectProductIds);
                        jsonObject.put("date_start", Start_Date);
                        jsonObject.put("date_end", End_Date);
                        jsonObject.put("uses_total", UsesPerCoupon);
                        jsonObject.put("uses_customer", UsesPerCustomer);
                        jsonObject.put("status", status_id);
                        jsonObject.put("language_id",language);

                        progressBar.setVisibility(View.VISIBLE);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                        Call<String> call = apiInterface.EditCoupon(Constant.DataGetValue(activity, Constant.Token), body);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response.body());
                                        JSONObject jsonObject2 = jsonObject1.getJSONObject("success");
                                        Constant.showToast(jsonObject2.getString("message"));
                                        CouponPageHandler.Refresher();
                                        dismiss();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        ResponseBody requestBody = response.errorBody();
                                        BufferedReader r = new BufferedReader(new InputStreamReader(requestBody.byteStream()));
                                        StringBuilder total = new StringBuilder();
                                        String line;
                                        while ((line = r.readLine()) != null) {
                                            total.append(line).append('\n');
                                        }

                                        JSONObject jObjError = new JSONObject(total.toString());
                                        if (!jObjError.isNull("error")) {
                                            JSONObject jsonErrorObject = jObjError.getJSONObject("error");
                                            if (!jsonErrorObject.isNull("message")) {
                                                Constant.showToast(jsonErrorObject.getString("message"));
                                            } else {
                                                Constant.showToast(getString(R.string.error));
                                            }
                                        } else {
                                            Constant.showToast(getString(R.string.error));
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Constant.showToast(getString(R.string.error));
                                    }
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
                }


            }

        });
        btn_cancel.setOnClickListener(v -> dismiss());
        start_date.setOnClickListener(v -> {
            loadDate();
            int Year = 0, Month = 0, Date = 0;
            String Start_Date = start_date.getText().toString();
            if (Start_Date.length() > 0) {
                String list[] = Start_Date.split("-");
                if (list.length > 0 && list.length == 3) {
                    Year = Integer.valueOf(list[0]);
                    Month = Integer.valueOf(list[1]);
                    Date = Integer.valueOf(list[2]);
                }
            } else {
                Year = myCalendar.get(Calendar.YEAR);
                Month = myCalendar.get(Calendar.MONTH);
                Date = myCalendar.get(Calendar.DAY_OF_MONTH);
            }

            new DatePickerDialog(activity, date, Year, Month, Date).show();
            key = "Start_Date";
        });
        end_date.setOnClickListener(v -> {
            loadDate();
            int Year = 0, Month = 0, Date = 0;
            String End_Date = end_date.getText().toString();
            if (End_Date.length() > 0) {
                String list[] = End_Date.split("-");
                if (list.length > 0 && list.length == 3) {
                    Year = Integer.valueOf(list[0]);
                    Month = Integer.valueOf(list[1]);
                    Date = Integer.valueOf(list[2]);
                }
            } else {
                Year = myCalendar.get(Calendar.YEAR);
                Month = myCalendar.get(Calendar.MONTH);
                Date = myCalendar.get(Calendar.DAY_OF_MONTH);
            }
            new DatePickerDialog(activity, date, Year, Month, Date).show();
            key = "End_Date";
        });

        LoadSpinners();

        if (Coupon_type.equals("Edit")) {
            tv_coupon_title.setText(R.string.txt_edit_coupon);
            LoadCouponInfo(Coupon_id);
        } else {
            tv_coupon_title.setText(R.string.txt_add_coupon);
        }
        tv_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Select_Products_id != null) {
                    if (Select_Products_id.size() > 0) {
                        for (int i = 0; i < CouponProductList.size(); i++) {
                            for (int j = 0; j < Select_Products_id.size(); j++) {
                                if (CouponProductList.get(i).getProduct_id().equals(String.valueOf(Select_Products_id.get(j)))) {
                                    CouponProductList.get(i).setSelected(true);
                                }

                            }
                        }
                    }
                }
                LoadProductDialog(CouponProductList);
            }
        });
        return view;
    }


    private void LoadProducts() {
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getProducts(Constant.DataGetValue(activity, Constant.Token),language);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    CouponProductList = ContentJsonParser.getCouponProducts(response.body());
                    if (Select_Products_id != null) {
                        if (Select_Products_id.size() > 0) {
                            StringBuilder S_name = new StringBuilder();
                            StringBuilder Ids = new StringBuilder();
                            for (int i = 0; i < CouponProductList.size(); i++) {
                                for (int j = 0; j < Select_Products_id.size(); j++) {
                                    if (CouponProductList.get(i).getProduct_id().equals(String.valueOf(Select_Products_id.get(j)))) {
                                        if (S_name.length() > 0) {
                                            S_name.append(",").append(CouponProductList.get(i).getName());
                                            Ids.append(",").append(CouponProductList.get(i).getProduct_id());
                                        } else {
                                            S_name.append(CouponProductList.get(i).getName());
                                            Ids.append(CouponProductList.get(i).getProduct_id());
                                        }
                                    }

                                }
                            }
                          //  Log.e("setting: ", S_name.toString());
                           // Log.e("setting id: ", Ids.toString());
                            tv_products.setText(S_name);
                            ProductIds = Ids.toString();
                        }else {
                            tv_products.setText(getString(R.string.text_select));
                        }
                    } else {
                        tv_products.setText(getString(R.string.text_select));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    private void LoadProductDialog(ArrayList<Coupon_Products> couponProductList) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("products_lists", couponProductList);
        Dialog_coupon_products dialog_coupon_products = new Dialog_coupon_products();
        dialog_coupon_products.setStyle(STYLE_NO_TITLE, 0);
        dialog_coupon_products.setCouponProductHandler((couponProductHandler));
        dialog_coupon_products.setArguments(bundle);
        dialog_coupon_products.show(getChildFragmentManager(), "products_lists");
    }

    private void LoadSpinners() {
        SpinnerDiscountAdapter spinnerDiscountAdapter = new SpinnerDiscountAdapter(Discount);
        sp_type.setAdapter(spinnerDiscountAdapter);

        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Discount_type = Discount[position];
                if (Discount_type.equals(getResources().getString(R.string.percentage))) {
                    discount_type = "P";
                } else {
                    discount_type = "F";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpinnerStatusAdapter spinnerStatusAdapter = new SpinnerStatusAdapter(Status);
        sp_status.setAdapter(spinnerStatusAdapter);

        sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Status_id = Status[position];
                if (Status_id.equals(getResources().getString(R.string.enabled))) {
                    status_id = 1;
                } else {
                    status_id = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void LoadCouponProducts(String id, String name) {
        tv_products.setText(name);
        ProductIds = id;

    }

    private class SpinnerStatusAdapter extends BaseAdapter implements SpinnerAdapter {
        private final String[] Status;

        SpinnerStatusAdapter(String[] status) {
            this.Status = status;
        }

        @Override
        public int getCount() {
            return Status.length;
        }

        @Override
        public Object getItem(int position) {
            return Status[position];
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
            txt.setText(Status[position]);
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(Status[position]);
            return txt;
        }
    }

    private class SpinnerDiscountAdapter extends BaseAdapter implements SpinnerAdapter {
        private final String[] Discount;

        SpinnerDiscountAdapter(String[] discount) {
            this.Discount = discount;
        }

        @Override
        public int getCount() {
            return Discount.length;
        }

        @Override
        public Object getItem(int position) {
            return Discount[position];
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
            txt.setText(Discount[position]);
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(Discount[position]);
            return txt;
        }
    }

    private void loadDate() {

        myCalendar = Calendar.getInstance();

        date = (view1, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
           // Log.e("onCreateView: ", "testing");
          //  Log.e("onCreateView: ", " " + year + " - " + monthOfYear + " - " + dayOfMonth);
            updateDate(year, monthOfYear, dayOfMonth, key);
        };
    }

    private void updateDate(int year, int monthOfYear, int dayOfMonth, String key) {
        switch (key) {
            case "Start_Date":
                start_date.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                break;
            default:
                end_date.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                break;
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

    private void LoadCouponInfo(String coupon_id) {
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getCouponInfo(Constant.DataGetValue(activity, Constant.Token), coupon_id,language);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                LoadProducts();
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONObject jsonObject1 = jsonObject.getJSONObject("coupon_info");
                    ed_coupon_name.setText(jsonObject1.getString("name"));
                    ed_coupon_code.setText(jsonObject1.getString("code"));
                    ed_discount.setText(jsonObject1.getString("discount"));
                    ed_total_amount.setText(jsonObject1.getString("total"));
                    ed_use_per_coupon.setText(jsonObject1.getString("uses_total"));
                    ed_use_per_customer.setText(jsonObject1.getString("uses_customer"));
                    start_date.setText(jsonObject1.getString("date_start"));
                    end_date.setText(jsonObject1.getString("date_end"));
                    String sectionStatus = jsonObject1.getString("status");
                    String discount = jsonObject1.getString("type");
                    if (Status != null) {
                        if (Status.length > 0) {
                            for (int i = 0; i < Status.length; i++) {

                                sp_status.setSelection(Integer.parseInt(sectionStatus));


                            }
                        }
                    }
                   // Log.e( "onResponse: ",discount);
                    if (discount.equals("F")) {
                        sp_type.setSelection(1);
                    }
                    if (discount.equals("P")) {
                        sp_type.setSelection(0);
                    }

                   JSONArray jsonArray = jsonObject.getJSONArray("coupon_product");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        Select_Products_id.add(jsonObject2.getInt("product_id"));
                    }


                } catch (JSONException e) {
                  //  e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }
}
