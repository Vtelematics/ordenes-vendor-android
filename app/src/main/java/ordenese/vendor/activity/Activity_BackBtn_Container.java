package ordenese.vendor.activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.LoginPageHandler;
import ordenese.vendor.firebase_chat.FirebaseChat;
import ordenese.vendor.firebase_chat.UserListFragment;
import ordenese.vendor.fragment.account.Fragment_coupon_list;
import ordenese.vendor.fragment.account.Fragment_edit_store;
import ordenese.vendor.fragment.account.Fragment_section_list;
import ordenese.vendor.fragment.home.Fragment_OrderListHome;
import ordenese.vendor.fragment.menu.FoodProductListing;
import ordenese.vendor.fragment.menu.ForgotPassword;
import ordenese.vendor.fragment.menu.FragmentSignIn;
import ordenese.vendor.fragment.menu.Fragment_order_info;
import ordenese.vendor.fragment.menu.Fragment_order_list;
import ordenese.vendor.fragment.menu.GroceryProductListing;
import ordenese.vendor.fragment.menu.OrderReports;
import ordenese.vendor.fragment.menu.ProductSearch;
import ordenese.vendor.fragment.option.Fragment_OptionList;
import ordenese.vendor.fragment.reports.FragmentEarningHistory;
import ordenese.vendor.fragment.reports.Fragment_Report_Commission;
import ordenese.vendor.fragment.reports.Fragment_Report_Coupon;
import ordenese.vendor.fragment.reports.Fragment_Report_Products;
import ordenese.vendor.fragment.reports.Fragment_Report_Shipping;
import ordenese.vendor.fragment.user.Fragment_Registration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_BackBtn_Container extends AppCompatActivity implements LoginPageHandler {

    Toolbar toolbar;
    ApiInterface apiInterface;
    Application application;

    public static LinearLayout linearLayout;
    public static TextView order_info_txt;
    ImageView image_view;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppLanguageSupport.onAttach(base));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(
                    "ar".equals(AppLanguageSupport.getLanguage(Activity_BackBtn_Container.this)) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
    }

    public Activity_BackBtn_Container(Application application) {
        this.application = application;
    }

    public Activity_BackBtn_Container() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__home__container);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setVisibility(View.GONE);

        linearLayout = findViewById(R.id.value_linear);
        image_view = findViewById(R.id.image_view);
        image_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        linearLayout.setVisibility(View.GONE);

        order_info_txt = findViewById(R.id.order_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            Intent intent = getIntent();
            String value = intent.getStringExtra("Type");
            String Order_id = intent.getStringExtra("order_id");

            if (value != null) {
                if (value.equals("OrderList")) {
                    OrderList();
                } else if (value.equals("OrderInfo")) {
                    if (Order_id != null) {
                        LoadOrderInfo(Order_id);
                    }
                } else if (value.equals("Coupons")) {
                    LoadCoupon();
                } else if (value.equals("Sections")) {
                    LoadSection();
                } else if (value.equals("Products")) {
                    LoadProducts();
                } else if (value.equals("Options")) {
                    LoadOptions();
                } else if (value.equals("Category")) {
                    //   LoadCategory();
                } else if (value.equals("EditAccount")) {
                    LoadStoreInformation();
                } else if (value.equals("Reports")) {
                    //   LoadReports();
                } else if (value.equals("Hours")) {
                    //   LoadHours();
                } else if (value.equals("Order_Report")) {
                    LoadReportOrder();
                } else if (value.equals("Shipping_Report")) {
                    LoadReportShipping();
                } else if (value.equals("Coupon_Report")) {
                    LoadReportCoupon();
                } else if (value.equals("Commission_Report")) {
                    LoadReportCommission();
                } else if (value.equals("Products_Report")) {
                    LoadReportProducts();
                } else if (value.equals("Earning_History")) {
                    EarningHistory();
                } else if (value.equals("Language")) {
                    change_language();
                } else if (value.equals("UsersList")) {
                    LoadUsersList();
                } else if (value.equals("Admin_Chat")) {
                    Admin_Chat();
                } else if (value.equals("search_product")) {
                    search_product();
                } else if (value.equals("AllOrderList")) {
                    AllOrderList();
                }

            } else {
                LoadSignIn();
            }
        } else {
            LoadSignIn();
        }
    }


    private void LoadStoreInformation() {
        String language = LanguageDetailsDB.getInstance(this).get_language_id();
        Call<String> call = apiInterface.getStoreInfo(Constant.DataGetValue(Activity_BackBtn_Container.this, Constant.Token), language);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    LoadStore(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void search_product() {

        getSupportActionBar().show();
        getSupportActionBar().setTitle(R.string.search);
        Fragment productSearch = new ProductSearch();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_container_without_tb, productSearch, "productSearch").addToBackStack("productSearch");
        fragmentTransaction.commitAllowingStateLoss();

    }

    private void AllOrderList() {
        getSupportActionBar().show();
        getSupportActionBar().setTitle("");
        Fragment_OrderListHome fragment_orderListHome = new Fragment_OrderListHome();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_container_without_tb, fragment_orderListHome, "AllOrderList").addToBackStack("AllOrderList");
        fragmentTransaction.commit();

//        Fragment productSearch = new ProductSearch();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
//                android.R.anim.fade_out);
//        fragmentTransaction.replace(R.id.home_container_without_tb, productSearch, "productSearch").addToBackStack("productSearch");
//        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void LoadSignIn() {
        getSupportActionBar().hide();
        Fragment fragmentSignIn = new FragmentSignIn();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_container_without_tb, fragmentSignIn, "SignIn").addToBackStack("SignIn");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void LoadSignUp() {
        getSupportActionBar().show();
        getSupportActionBar().setTitle(R.string.text_signup);
        Fragment fragmentSignIn = new Fragment_Registration();
        Bundle bundle = new Bundle();
        bundle.putString("type", "");
        fragmentSignIn.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_container_without_tb, fragmentSignIn, "Registration").addToBackStack("Registration");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void loadRefreshMenu() {

    }

    @Override
    public void CloseActivity() {

    }

    @Override
    public void LoadForgot() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        forgotPassword.show(getSupportFragmentManager(), "forgotPassword");
    }

    @Override
    public void BackPressed() {
        onBackPressed();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void OrderList() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle(getString(R.string.text_order_list));
            getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.toolbar_background));
        }
        Fragment fragment = new Fragment_order_list();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_container_without_tb, fragment, "Orders").addToBackStack("OrderList");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void LoadOrderInfo(String Order_id) {
        toolbarSetup(getString(R.string.text_order_info));
        Bundle bundle = new Bundle();
        bundle.putString("order_id", Order_id);
        Fragment fragment = new Fragment_order_info();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_container_without_tb, fragment, "Order_Info").addToBackStack("Order_Info");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void LoadStore(String mRestaurant_Info) {
        toolbarSetup(getString(R.string.text_edit_store));
        Fragment fragment = new Fragment_edit_store();
        Bundle bundle = new Bundle();
        bundle.putString("type", "Edit");
        bundle.putString("Restaurant_info", mRestaurant_Info);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_container_without_tb, fragment, "store_edit").addToBackStack("store_edit");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void LoadCoupon() {
        toolbarSetup(getString(R.string.text_coupons));
        Fragment fragment = new Fragment_coupon_list();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_container_without_tb, fragment, "coupon").addToBackStack("coupon");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void LoadSection() {
        toolbarSetup(getString(R.string.text_category));
        Fragment fragment = new Fragment_section_list();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_container_without_tb, fragment, "section").addToBackStack("section");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void LoadProducts() {
        getSupportActionBar().show();
        getSupportActionBar().setTitle(R.string.text_product);
        toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
        if (Constant.DataGetValue(this, "vendor_type").equals("2")) {
            GroceryProductListing fragment = new GroceryProductListing();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.home_container_without_tb, fragment, "ProductListing").addToBackStack("ProductListing");
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            FoodProductListing fragment = new FoodProductListing();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.home_container_without_tb, fragment, "ProductListing").addToBackStack("ProductListing");
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void LoadReportOrder() {
        toolbarSetup(getString(R.string.txt_report_order));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container_without_tb, new OrderReports(), "ReportOrderList")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("ReportOrderList")
                .commitAllowingStateLoss();
    }

    @Override
    public void LoadReportShipping() {
        toolbarSetup(getString(R.string.txt_report_shipping));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container_without_tb, new Fragment_Report_Shipping(), "ReportShippingList")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("ReportShippingList")
                .commitAllowingStateLoss();
    }

    @Override
    public void LoadReportCommission() {
        toolbarSetup(getString(R.string.txt_report_commission));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container_without_tb, new Fragment_Report_Commission(), "ReportCommissionList")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("ReportCommissionList")
                .commitAllowingStateLoss();
    }

    @Override
    public void LoadReportCoupon() {
        toolbarSetup(getString(R.string.text_coupons));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container_without_tb, new Fragment_Report_Coupon(), "ReportCouponList")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("ReportCouponList")
                .commitAllowingStateLoss();
    }

    @Override
    public void LoadReportProducts() {
        toolbarSetup(getString(R.string.txt_report_product_title));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container_without_tb, new Fragment_Report_Products(), "ReportProductsList")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("ReportProductsList")
                .commitAllowingStateLoss();
    }

    @Override
    public void LoadUsersList() {
        toolbarSetup("Chat");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container_without_tb, new UserListFragment(), "LoadUsersList")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("LoadUsersList")
                .commitAllowingStateLoss();
    }

    @Override
    public void Admin_Chat() {
        toolbarSetup(getString(R.string.support));
        FirebaseChat firebaseChat = new FirebaseChat();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", Constant.DataGetValue(getApplicationContext(), "admin_uid"));
        firebaseChat.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container_without_tb, firebaseChat, "firebaseChat")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("firebaseChat")
                .commitAllowingStateLoss();
    }

    @Override
    public void EarningHistory() {
        toolbarSetup(getString(R.string.earning_history));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container_without_tb, new FragmentEarningHistory(), "Earning_History")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("Earning_History")
                .commitAllowingStateLoss();
    }

    @Override
    public void change_language() {
    }

    @Override
    public void LoadOptions() {
        getSupportActionBar().show();
        getSupportActionBar().setTitle(R.string.txt_option_title);
        Fragment fragment = new Fragment_OptionList();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_container_without_tb, fragment, "option").addToBackStack("option");
        fragmentTransaction.commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void toolbarSetup(String title) {
        getSupportActionBar().show();
        getSupportActionBar().setTitle(title);
        toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
    }

    private void mLogE(String title, String msg) {
        Log.e(title, msg);
    }

    private void mLogD(String title, String msg) {
        // Log.d("*******************","************************");
        // Log.d(title,msg);
        //Log.d("*******************","************************");
    }
}
