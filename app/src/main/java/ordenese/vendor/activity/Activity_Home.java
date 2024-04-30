package ordenese.vendor.activity;

import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.SunmiPrinterSDK.utils.SunmiPrintHelper;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.ApplicationContext;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.instant_transfer.HomePageHandler;
import ordenese.vendor.firebase_chat.Chat;
import ordenese.vendor.firebase_chat.Users;
import ordenese.vendor.fragment.account.Language;
import ordenese.vendor.fragment.home.Fragment_OrderListHome;
import ordenese.vendor.fragment.home.NewOrdersList;
import ordenese.vendor.fragment.menu.Fragment_change_password;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Home extends AppCompatActivity implements HomePageHandler,
        InstallStateUpdatedListener {

    Toolbar toolbar;
    DrawerLayout drawer;
    TextView tv_coupon, tv_order_list, tv_logout, tv_dashboard, store_info, tv_change_password, tv_section, tv_product, tv_report_order,
            tv_report_shipping, tv_report_commission, tv_report_coupons, tv_report_earning, tv_report_products, tv_ProductsOption,
            tv_language, tv_customer_chat;
    AppCompatTextView edit_account_info;
    Application application;

    LinearLayout mHomeBodyFullPageContainer;
    AppUpdateManager appUpdateManager;
    private static int MY_REQUEST_CODE = 4518, AUTOCOMPLETE_REQUEST_CODE = 23487;

    private String busyStatus = "";
    private ApiInterface apiInterface;
    private Activity mActivity;
    LabeledSwitch labeledSwitch;

    DatabaseReference reference;
    ArrayList<Users> usersArrayList = new ArrayList<>();


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppLanguageSupport.onAttach(base));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__home);
        toolbar = findViewById(R.id.toolbar);

        mActivity = Activity_Home.this;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        ApplicationContext.getAppContext();

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        labeledSwitch = toolbar.findViewById(R.id.switch_busy_status);
        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                if (isOn) {
                    SetBusyStatus("1");
                } else {
                    SetBusyStatus("0");
                }
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        mHomeBodyFullPageContainer = findViewById(R.id.layout_home_restaurant_body);
        tv_coupon = findViewById(R.id.tv_coupon);
        tv_order_list = findViewById(R.id.tv_order_list);
        tv_logout = findViewById(R.id.tv_logout);
        tv_dashboard = findViewById(R.id.tv_dashboard);
        store_info = findViewById(R.id.store_info);
        tv_section = findViewById(R.id.tv_section);
        tv_product = findViewById(R.id.tv_product);
        tv_change_password = findViewById(R.id.tv_change_password);
        edit_account_info = findViewById(R.id.tv_setting);
        tv_ProductsOption = findViewById(R.id.tv_option);

        tv_language = findViewById(R.id.tv_change_language);
        tv_customer_chat = findViewById(R.id.customer_chat);
        tv_report_order = findViewById(R.id.tv_report_order_list);
        tv_report_shipping = findViewById(R.id.tv_report_shipping_list);
        tv_report_commission = findViewById(R.id.tv_report_commission_list);
        tv_report_earning = findViewById(R.id.tv_report_earning_history);
        tv_report_coupons = findViewById(R.id.tv_report_coupon_list);
        tv_report_products = findViewById(R.id.tv_report_product_list);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        tv_coupon.setOnClickListener(v -> {
            LoadCoupons();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_dashboard.setOnClickListener(v -> {
            LoadDashboard();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_order_list.setOnClickListener(v -> {
            LoadOrderList();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_change_password.setOnClickListener(v -> {
            LoadChangePassword();
            drawer.closeDrawer(GravityCompat.START);
        });
        edit_account_info.setOnClickListener(v -> {
            LoadEditStore();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_logout.setOnClickListener(v -> {
            logout();
        });
        tv_section.setOnClickListener(v -> {
            LoadSections();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_product.setOnClickListener(v -> {
            LoadProducts();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_report_order.setOnClickListener(v -> {
            LoadReportOrderList();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_report_shipping.setOnClickListener(v -> {
            LoadReportShippingList();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_report_commission.setOnClickListener(v -> {
            LoadReportCommissionList();
            drawer.closeDrawer(GravityCompat.START);
        });

        tv_report_earning.setOnClickListener(v -> {
            EarningHistory();
            drawer.closeDrawer(GravityCompat.START);
        });

        tv_report_coupons.setOnClickListener(v -> {
            LoadReportCouponList();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_report_products.setOnClickListener(v -> {
            LoadReportProductsList();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_ProductsOption.setOnClickListener(v -> {
            LoadOptions();
            drawer.closeDrawer(GravityCompat.START);
        });

        tv_language.setOnClickListener(view -> {
            LoadLanguage();
            drawer.closeDrawer(GravityCompat.START);
        });
        tv_customer_chat.setOnClickListener(view -> {
            LoadAdminChat();
            drawer.closeDrawer(GravityCompat.START);
        });

        if (!Constant.DataGetValue(Activity_Home.this, Constant.StoreDetails).equals("empty")) {
            try {
                JSONObject jsonObject = new JSONObject(Constant.DataGetValue(Activity_Home.this, Constant.StoreDetails));
                // Log.e("onCreate: ", jsonObject + "");
                store_info.setText(Html.fromHtml(jsonObject.getString("vendor_name") + "<br>" + jsonObject.getString("email") + "<br>" + jsonObject.getString("mobile")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            appUpdateManager = AppUpdateManagerFactory.create(this);
            appUpdateManager.registerListener(this);
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(appUpdateInfo -> {

                        //UPDATE_AVAILABLE = 2
                        //DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS = 3
                        //UNKNOWN = 0
                        //UPDATE_NOT_AVAILABLE = 1

                        // Log.e("packageName", appUpdateInfo.packageName());
                        //  Log.e("availableVersionCode", "" + appUpdateInfo.availableVersionCode());
                        //  Log.e("installStatus", "" + appUpdateInfo.installStatus());
                        //  Log.e("isUpdateTypeAllowed", "" + appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE));
                        //  Log.e("updateAvailability", "" + appUpdateInfo.updateAvailability());
                        // Log.e("UPDATE_AVAILABLE", "" + UpdateAvailability.UPDATE_AVAILABLE);

                        String s = "packageName=" + appUpdateInfo.packageName() + "\n" +
                                "availableVersionCode=" + appUpdateInfo.availableVersionCode() + "\n" +
                                "installStatus=" + appUpdateInfo.installStatus() + "\n" +
                                "isUpdateTypeAllowed" + appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) + "\n" +
                                "updateAvailability" + appUpdateInfo.updateAvailability() + "\n" +
                                "UPDATE_AVAILABLE" + UpdateAvailability.UPDATE_AVAILABLE;

                        // If the update is downloaded but not installed,
                        // notify the user to complete the update.
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {   //  check for the type of update flow you want
                            requestUpdate(appUpdateInfo);
                            // Log.e("Update", "Available");
                            //  Constant.showToast("Update-Available");
                        }
                    });
        }
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
//                            Log.e("reg token failed", task.getException() + "");
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
//                        Log.e("onComplete: ", token + "");
                    }
                });
    }

    @Override
    public void LoadAdminChat() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Admin_Chat");
        startActivity(intent);
    }

    @Override
    public void LoadUsersList() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "UsersList");
        startActivity(intent);
    }

    private void LoadLanguage() {
        Language language = new Language();
        language.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        language.show(getSupportFragmentManager(), "language");
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().getDecorView().setLayoutDirection(
                "ar".equals(AppLanguageSupport.getLanguage(Activity_Home.this)) ?
                        View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
    }

    public Activity_Home(Application application) {
        this.application = application;
    }

    public Activity_Home() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (resultCode == RESULT_OK) {
                    // Log.e("RESULT_OK: ", "" + resultCode);
                    Constant.loadToastMessage(Activity_Home.this, "Update Completed!");
                } else {

                    // If the update is cancelled or fails,
                    // you can request to start the update again.
                    if (resultCode == RESULT_CANCELED) {
                        // Log.e("RESULT_CANCELED: ", "" + resultCode);
                        Constant.loadToastMessage(Activity_Home.this, "Update Canceled!");
                    } else if (resultCode == RESULT_IN_APP_UPDATE_FAILED) {
                        // Log.e("RESULT_UPDATE_FAILED: ", "" + resultCode);
                        Constant.loadToastMessage(Activity_Home.this, "Update Failed!");
                    } else {
                        // Log.e("Result code other: ", "" + resultCode);
                    }

                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        GetBusyStatus();
        LoadNewOrders();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (appUpdateManager != null) {
                appUpdateManager
                        .getAppUpdateInfo()
                        .addOnCompleteListener(appUpdateInfo -> {
//                              AppFunctions.toastLong(AppHome.this,"Update Completed.");
                        });
                appUpdateManager
                        .getAppUpdateInfo()
                        .addOnFailureListener(appUpdateInfo -> {
                            // AppFunctions.toastLong(AppHome.this, "GPFailedMsg : " + appUpdateInfo.getMessage());
//                            Log.e("onResume: ",appUpdateInfo.getMessage());
                        });

                appUpdateManager
                        .getAppUpdateInfo()
                        .addOnSuccessListener(appUpdateInfo -> {
                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                notifyUser();
                            }
                        });
            }
        }

    }

    private void LoadOptions() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Options");
        startActivity(intent);
    }


    @Override
    public void LoadCoupons() {

        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Coupons");
        startActivity(intent);
    }

    @Override
    public void LoadOrderList() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "OrderList");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        update(menu);
        menu.findItem(R.id.menu_item).setVisible(false);
//        getNotification(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item) {
            LoadUsersList();
        }

        return super.onOptionsItemSelected(item);
    }

    private void update(Menu menu) {
        View view = menu.findItem(R.id.with_notify).getActionView();
        ImageView t1 = view.findViewById(R.id.cart_image_view);
        TextView t2 = view.findViewById(R.id.cart_count_value);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.DataGetValue(getApplicationContext(), "vendor_uid").equals("") ||
                        !Constant.DataGetValue(getApplicationContext(), "vendor_uid").equals("empty")) {
                    Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
                    intent.putExtra("Type", "UsersList");
                    startActivity(intent);
                }
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.DataGetValue(getApplicationContext(), "vendor_uid").equals("") ||
                        !Constant.DataGetValue(getApplicationContext(), "vendor_uid").equals("empty")) {
                    Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
                    intent.putExtra("Type", "UsersList");
                    startActivity(intent);
                }
            }
        });
    }

    private void getNotification(Menu menu) {
        menu.findItem(R.id.menu_item).setVisible(false);
        reference = FirebaseDatabase.getInstance().getReference("messages").child(Constant.DataGetValue(getApplicationContext(), "vendor_uid"));
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getKey() != null) {
                            reference = FirebaseDatabase.getInstance().getReference("users_list").child(dataSnapshot.getKey());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (!Objects.equals(dataSnapshot.getKey(), Constant.DataGetValue(getApplicationContext(), "vendor_uid"))) {
                                            Users users = snapshot.getValue(Users.class);
                                            usersArrayList.add(users);
                                            for (int i = 0; i < usersArrayList.size(); i++) {
                                                if (usersArrayList.get(i).getUid() != null) {
                                                    DatabaseReference referenceChat = FirebaseDatabase.getInstance().getReference("messages").child(Constant.DataGetValue(mActivity, "vendor_uid")).child(usersArrayList.get(i).getUid());
                                                    referenceChat.addValueEventListener(new ValueEventListener() {
                                                        @SuppressLint("NotifyDataSetChanged")
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            int count = 0;
                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                if (snapshot.getValue() != null) {
                                                                    Chat chat = snapshot.getValue(Chat.class);
                                                                    if (chat != null) {
                                                                        if (chat.getSeen() != null) {
                                                                            if (chat.getSeen().equals("false")) {
                                                                                count = count + 1;
                                                                            }
                                                                        }
                                                                    }
                                                                    if (count == 0) {
                                                                        menu.findItem(R.id.menu_item).setVisible(true);
                                                                        menu.findItem(R.id.with_notify).setVisible(false);
                                                                    } else {
                                                                        menu.findItem(R.id.menu_item).setVisible(false);
                                                                        menu.findItem(R.id.with_notify).setVisible(true);
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Constant.loadToastMessage(mActivity, mActivity.getResources().getString(R.string.process_failed_please_try_again));
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Constant.loadToastMessage(mActivity, mActivity.getResources().getString(R.string.process_failed_please_try_again));
            }
        });
    }

    private void showToast(String message) {
        Constant.showToast(message);
    }

    private void logout() {

        JSONObject object = new JSONObject();
        try {
            if (OneSignal.getDeviceState() != null && OneSignal.getDeviceState().getUserId() != null) {
                object.put("push_id", OneSignal.getDeviceState().getUserId());
            } else {
                object.put("push_id", "");
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<String> call = apiInterface.log_out(Constant.DataGetValue(mActivity, Constant.Token), body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (!jsonObject.isNull("success")) {
                                JSONObject object1 = jsonObject.getJSONObject("success");
                                Constant.loadToastMessage(mActivity, object1.getString("message"));
                                Constant.DataRemoveValue(mActivity, "admin_uid");
                                Constant.DataRemoveValue(mActivity, "vendor_uid");
                                Constant.DataRemoveValue(mActivity, "token_notify");
                                Constant.DataRemoveValue(Activity_Home.this.getApplicationContext(), Constant.Token);
                                FirebaseAuth.getInstance().signOut();
                                //Deinit Printer Service
                                SunmiPrintHelper.getInstance().deInitSunmiPrinterService(getApplicationContext());
                                LoadAgain();
                            } else if (!jsonObject.isNull("error")) {
                                JSONObject object1 = jsonObject.getJSONObject("error");
                                Constant.loadToastMessage(mActivity, object1.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Constant.loadToastMessage(mActivity, getResources().getString(R.string.process_failed_please_try_again));
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Constant.loadToastMessage(mActivity, getResources().getString(R.string.process_failed_please_try_again));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Constant.loadToastMessage(mActivity, getResources().getString(R.string.process_failed_please_try_again));
        }

    }

    @Override
    public void LoadAgain() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void LoadDashboard() {
        getSupportActionBar().setTitle(getString(R.string.text_home));
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "AllOrderList");
        startActivity(intent);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void LoadNewOrders() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle("");
            getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.toolbar_background));
        }
        NewOrdersList newOrdersList = new NewOrdersList();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.home_loader, newOrdersList, "newOrdersList").addToBackStack("newOrdersList");
        fragmentTransaction.commit();
    }

    @Override
    public void LoadChangePassword() {
        Fragment_change_password fragment_change_password = new Fragment_change_password();
        fragment_change_password.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        fragment_change_password.show(getSupportFragmentManager(), "change password");
    }

    @Override
    public void LoadEditStore() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "EditAccount");
        startActivity(intent);
    }

    @Override
    public void LoadSections() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Sections");
        startActivity(intent);
    }

    @Override
    public void LoadProducts() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Products");
        startActivity(intent);
    }

    @Override
    public void LoadReportOrderList() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Order_Report");
        startActivity(intent);
    }

    @Override
    public void LoadReportShippingList() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Shipping_Report");
        startActivity(intent);
    }

    @Override
    public void LoadReportCommissionList() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Commission_Report");
        startActivity(intent);
    }

    @Override
    public void LoadReportCouponList() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Coupon_Report");
        startActivity(intent);
    }

    @Override
    public void LoadReportProductsList() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Products_Report");
        startActivity(intent);
    }

    @Override
    public void EarningHistory() {
        Intent intent = new Intent(Activity_Home.this, Activity_BackBtn_Container.class);
        intent.putExtra("Type", "Earning_History");
        startActivity(intent);
    }

    private void mLogE(String title, String msg) {
        // Log.e(title,msg);
    }

    private void mLogD(String title, String msg) {
        // Log.d("*******************","************************");
        // Log.d(title,msg);
        // Log.d("*******************","************************");
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onStateUpdate(InstallState installState) {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            notifyUser();
        }
    }

    private void notifyUser() {
        Snackbar snackbar =
                Snackbar.make(
                        mHomeBodyFullPageContainer,
                        getResources().getString(R.string.an_update_dowmloaded),
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getResources().getString(R.string.restart), view -> {
                    appUpdateManager.completeUpdate();
                    appUpdateManager.unregisterListener(this);
                }
        );
        snackbar.setActionTextColor(
                getResources().getColor(R.color.white));
        snackbar.show();
    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo) {
        if (appUpdateManager != null) {
            try {
                appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE, //  HERE specify the type of update flow you want
                        this,   //  the instance of an activity
                        MY_REQUEST_CODE
                );
            } catch (Exception e) {
                //  Log.e("requestUpdate excep", e.toString());
                Constant.loadToastMessage(Activity_Home.this, "RUException: " + e.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(this);
        }
    }

    public void GetBusyStatus() {
        try {
            if (Constant.isNetworkAvailable()) {
                Call<String> call = apiInterface.get_BusyStatus(Constant.DataGetValue(mActivity, Constant.Token));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (!jsonObject.isNull("busy_status")) {
                                    busyStatus = jsonObject.getString("busy_status");
                                } else {
                                    busyStatus = "";
                                }
                            } catch (JSONException e) {
//                                Log.e("Exception ", e.getMessage());
                            }

                            labeledSwitch.setOn(busyStatus.equals("1"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        //pb_LoaderOrder.setVisibility(View.GONE);
                    }
                });

            } else {
                Constant.LoadNetworkError(getSupportFragmentManager());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void SetBusyStatus(String data) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", data);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

            if (Constant.isNetworkAvailable()) {
                Call<String> call = apiInterface.BusyStatus_update(Constant.DataGetValue(mActivity, Constant.Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (!jsonObject.isNull("success")) {
                                    JSONObject object = jsonObject.getJSONObject("success");
                                    if (!object.isNull("message")) {
                                        Constant.showToast(object.getString("message"));
                                    }
                                }
                            } catch (JSONException e) {
//                                Log.e("Exception ", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        //pb_LoaderOrder.setVisibility(View.GONE);
                    }
                });

            } else {
                Constant.LoadNetworkError(getSupportFragmentManager());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
