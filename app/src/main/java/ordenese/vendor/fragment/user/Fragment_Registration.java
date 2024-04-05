package ordenese.vendor.fragment.user;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.bumptech.glide.Glide;
import com.github.angads25.toggle.LabeledSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.LoginPageHandler;
import ordenese.vendor.fragment.ScrollMaps;
import ordenese.vendor.model.Payment_method;
import ordenese.vendor.model.Register_fields;
import ordenese.vendor.model.Restaurant_Info;
import ordenese.vendor.model.Store_Cuisine;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Registration extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private View v_RegistrationHolder;
    //private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Activity activity;
    private TextView tv_EnglishNameError, /*tv_ArabicNameError,*/
            tv_ownerNameError, tv_EmailError, tv_MobileNoError, tv_PasswordError,
            tv_ConfirmPasswordError, tv_address_value, tv_sun_from, tv_mon_from, tv_tue_from, tv_wed_from, tv_thu_from, tv_fri_from, tv_sat_from,
            tv_sun_to, tv_mon_to, tv_tue_to, tv_wed_to, tv_thu_to, tv_fri_to, tv_sat_to, tv_password, tv_confirm;
    private EditText et_EnglishName, /*et_ArabicName,*/
            et_OwnerName, et_Email, et_MobileNo, et_Password, et_ConfirmPassword, et_preparing_time_value;
    private Button btn_Submit;
    ProgressBar progressBar, progress_image;
    ApiInterface apiInterface;
    LoginPageHandler loginPageHandler;
    LocationListener mLocationListener;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient;
    private Geocoder geocoder;
    private GoogleMap mGoogleMap;
    private LocationRequest mLocationRequest;
    ScrollView mscrollView;
    ScrollMaps mapFragment;
    private Double Latitude, Longitude;
    private String mLocation;
    ProgressDialog progressDialog;
    static AlertDialog alert;
    private int USER_LOCATION_PERMISSION_CODE = 41;
    String[] Status;
    Spinner sp_status;
    private String Status_id;
    private int status_id;
    private Restaurant_Info mRestaurant_Info;
    private RecyclerView rc_cuisine_list/*, rc_payment_method*/;
    private CalendarView cv_Custom;
    private Register_fields mRegisterFields;
    ImageButton ib_image, ib_logo;
    ImageView iv_image, iv_logo;
    private final static int IMAGE = 1, LOGO = 2;
    private String PROFILE_IMAGE, LOGO_IMAGE;
    private String PROFILE_PATH = "", LOGO_PATH = "";
    private StringBuilder Cuisine_id, Payment_id;
    List<Calendar> selectedDates;
    LabeledSwitch switch_pickup, switch_delivery, switch_sunday, switch_monday, switch_tuesday, switch_wednesday, switch_thursday, switch_friday, switch_saturday;
    private int pickup = 0, delivery = 0, sunday = 0, monday = 0, thuesday = 0, wednesday = 0, thursday = 0, friday = 0, saturday = 0;
    private String EditType;
    private ArrayList<Integer> Select_cuisine_id = new ArrayList<>();
    private ArrayList<Integer> Select_payment_id = new ArrayList<>();
    private ArrayList<Store_Cuisine> CuisineList = new ArrayList<>();
    private ArrayList<Payment_method> PaymentList = new ArrayList<>();
    private String Restaurant_Infos;

    private EditText pan_card_ed, acc_no_ed, ifsc_code_ed;
    private RadioButton store_type_veg, store_type_non_veg, store_type_both;

    String time = "", vendor_uid = "";
    FirebaseAuth auth;
    DatabaseReference regReference;


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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            EditType = getArguments().getString("type");
            Restaurant_Infos = getArguments().getString("Restaurant_info");

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_RegistrationHolder = inflater.inflate(R.layout.fragment_registration, container, false);

        Status = new String[]{getResources().getString(R.string.enabled), getResources().getString(R.string.disabled)};
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        pan_card_ed = v_RegistrationHolder.findViewById(R.id.pan_card_ed);
        acc_no_ed = v_RegistrationHolder.findViewById(R.id.acc_no_ed);
        ifsc_code_ed = v_RegistrationHolder.findViewById(R.id.ifsc_code_ed);

        store_type_veg = v_RegistrationHolder.findViewById(R.id.store_type_veg);
        store_type_non_veg = v_RegistrationHolder.findViewById(R.id.store_type_non_veg);
        store_type_both = v_RegistrationHolder.findViewById(R.id.store_type_both);
        store_type_both.setChecked(true);

        rc_cuisine_list = v_RegistrationHolder.findViewById(R.id.rc_cuisine_list);
        // rc_payment_method = v_RegistrationHolder.findViewById(R.id.rc_payment_method);
        cv_Custom = v_RegistrationHolder.findViewById(R.id.cv_custom);
//        handler = new Handler();
        mapFragment = (ScrollMaps) getChildFragmentManager()
                .findFragmentById(R.id.map_view_address);
        mscrollView = v_RegistrationHolder.findViewById(R.id.scrollView);
        ((ScrollMaps) getChildFragmentManager().findFragmentById(R.id.map_view_address)).setListener(() -> mscrollView.requestDisallowInterceptTouchEvent(true));
        mapFragment.onCreate(savedInstanceState);
        geocoder = new Geocoder(getActivity());
        if (Constant.isNetworkAvailable()) {
            loadRestaurantLocationMap();
        } else {
            Constant.LoadNetworkError(getChildFragmentManager());
        }
        LoadStatus();
        LoadSetting();
        LoadRegisterFields();
//          LoadDate();

        if (EditType.equals("Edit")) {
//            LoadStoreInfo();

            storeInfo(Restaurant_Infos);
        } else {
            LoadDate();
        }

        return v_RegistrationHolder;
    }


    private void LoadRegisterFields() {
        progressBar.setVisibility(View.VISIBLE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<String> call = apiInterface.getRegisterInfo();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    mRegisterFields = ContentJsonParser.getRegisterFields(response.body());
                    LodCuisine(mRegisterFields.getStore_cuisines());
                    //  LoadPaymentMethods(mRegisterFields.getPayment_methods());
                }
            }


            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void LoadStatus() {
        sp_status = v_RegistrationHolder.findViewById(R.id.sp_status);
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

    private void LoadDate() {
        cv_Custom = v_RegistrationHolder.findViewById(R.id.cv_custom);
        Calendar min = Calendar.getInstance();
        min.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE) - 1);
        cv_Custom.setMinimumDate(min);

    }


    private void LodCuisine(ArrayList<Store_Cuisine> store_cuisines) {

        if (mRegisterFields != null) {
            CuisineList = store_cuisines;
            rc_cuisine_list.setLayoutManager(new LinearLayoutManager(activity));
            CuisineListAdapter cuisineListAdapter = new CuisineListAdapter();
            rc_cuisine_list.setAdapter(cuisineListAdapter);
        }

    }

   /* private void LoadPaymentMethods(ArrayList<Payment_method> payment_methods) {
        if (mRegisterFields != null) {
            PaymentList = payment_methods;
            rc_payment_method.setLayoutManager(new LinearLayoutManager(activity));
            Payment_methodListAdapter payment_methodListAdapter = new Payment_methodListAdapter();
            rc_payment_method.setAdapter(payment_methodListAdapter);
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        CheckGpsConnection();
    }


    private void CheckGpsConnection() {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            createLocationServiceError(activity);
        } else {
            loadRestaurantLocationMap();

        }
    }


    public void createLocationServiceError(final Activity activityObj) {

        // show alert dialog if Internet is not connected
        AlertDialog.Builder builder = new AlertDialog.Builder(activityObj);

        builder.setMessage(getResources().getString(R.string.gps_access_permission))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.settings),
                        (dialog, id) -> {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activityObj.startActivity(intent);
                            dialog.dismiss();
                        })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        (dialog, id) -> dialog.dismiss());
        alert = builder.create();
        alert.show();
    }


    private void LoadSetting() {

        et_EnglishName = v_RegistrationHolder.findViewById(R.id.et_english_name_value);
        //  et_ArabicName = v_RegistrationHolder.findViewById(R.id.et_arabic_name_value);
        et_OwnerName = v_RegistrationHolder.findViewById(R.id.et_owner_name_value);
        et_Email = v_RegistrationHolder.findViewById(R.id.et_email_value);
        et_MobileNo = v_RegistrationHolder.findViewById(R.id.et_mobile_no_value);
        et_Password = v_RegistrationHolder.findViewById(R.id.et_pwd_value);
        et_ConfirmPassword = v_RegistrationHolder.findViewById(R.id.et_confirm_pwd_value);
        et_preparing_time_value = v_RegistrationHolder.findViewById(R.id.et_preparing_time_value);
        tv_password = v_RegistrationHolder.findViewById(R.id.tv_pwd_title);
        tv_confirm = v_RegistrationHolder.findViewById(R.id.tv_confirm_pwd_title);

        tv_EnglishNameError = v_RegistrationHolder.findViewById(R.id.tv_english_name_error);
        //tv_ArabicNameError = v_RegistrationHolder.findViewById(R.id.tv_arabic_name_error);
        tv_ownerNameError = v_RegistrationHolder.findViewById(R.id.tv_owner_name_error);
        tv_EmailError = v_RegistrationHolder.findViewById(R.id.tv_email_error);
        tv_MobileNoError = v_RegistrationHolder.findViewById(R.id.tv_mobile_no_error);
        tv_PasswordError = v_RegistrationHolder.findViewById(R.id.tv_pwd_error);
        tv_ConfirmPasswordError = v_RegistrationHolder.findViewById(R.id.tv_confirm_pwd_error);
        tv_address_value = v_RegistrationHolder.findViewById(R.id.tv_address_value);
        progressBar = v_RegistrationHolder.findViewById(R.id.progressBar);
        progress_image = v_RegistrationHolder.findViewById(R.id.progress_image);

        //Text view time picker sunday to saturday

        tv_sun_from = v_RegistrationHolder.findViewById(R.id.time_from_sun);
        tv_mon_from = v_RegistrationHolder.findViewById(R.id.time_from_mon);
        tv_tue_from = v_RegistrationHolder.findViewById(R.id.time_from_tue);
        tv_wed_from = v_RegistrationHolder.findViewById(R.id.time_from_wed);
        tv_thu_from = v_RegistrationHolder.findViewById(R.id.time_from_thu);
        tv_fri_from = v_RegistrationHolder.findViewById(R.id.time_from_fri);
        tv_sat_from = v_RegistrationHolder.findViewById(R.id.time_from_sat);

        tv_sun_to = v_RegistrationHolder.findViewById(R.id.time_to_sun);
        tv_mon_to = v_RegistrationHolder.findViewById(R.id.time_to_mon);
        tv_tue_to = v_RegistrationHolder.findViewById(R.id.time_to_tue);
        tv_wed_to = v_RegistrationHolder.findViewById(R.id.time_to_wed);
        tv_thu_to = v_RegistrationHolder.findViewById(R.id.time_to_thu);
        tv_fri_to = v_RegistrationHolder.findViewById(R.id.time_to_fri);
        tv_sat_to = v_RegistrationHolder.findViewById(R.id.time_to_sat);

        iv_image = v_RegistrationHolder.findViewById(R.id.im_image);
        iv_logo = v_RegistrationHolder.findViewById(R.id.im_logo);
        ib_image = v_RegistrationHolder.findViewById(R.id.ib_image);
        ib_logo = v_RegistrationHolder.findViewById(R.id.ib_logo);

        ib_image.setOnClickListener(v -> {
            if (checkPermission()) {
                showFileChooser(IMAGE);
            } else {
                requestPermission(IMAGE);
            }
        });

        ib_logo.setOnClickListener(v -> {
            if (checkPermission()) {
                showFileChooser(LOGO);
            } else {
                requestPermission(LOGO);
            }
        });

        if (EditType.equals("Edit")) {
            et_Password.setVisibility(View.GONE);
            et_ConfirmPassword.setVisibility(View.GONE);
            tv_password.setVisibility(View.GONE);
            tv_confirm.setVisibility(View.GONE);
            tv_PasswordError.setVisibility(View.GONE);
            tv_ConfirmPasswordError.setVisibility(View.GONE);
        } else {
            et_Password.setVisibility(View.VISIBLE);
            et_ConfirmPassword.setVisibility(View.VISIBLE);
            tv_password.setVisibility(View.VISIBLE);
            tv_confirm.setVisibility(View.VISIBLE);

        }

        //switch button
        switch_pickup = v_RegistrationHolder.findViewById(R.id.switch_pickup);
        switch_delivery = v_RegistrationHolder.findViewById(R.id.switch_delivery);

        switch_sunday = v_RegistrationHolder.findViewById(R.id.switch_sunday);
        switch_monday = v_RegistrationHolder.findViewById(R.id.switch_monday);
        switch_tuesday = v_RegistrationHolder.findViewById(R.id.switch_tuesday);
        switch_wednesday = v_RegistrationHolder.findViewById(R.id.switch_wednesday);
        switch_thursday = v_RegistrationHolder.findViewById(R.id.switch_thursday);
        switch_friday = v_RegistrationHolder.findViewById(R.id.switch_friday);
        switch_saturday = v_RegistrationHolder.findViewById(R.id.switch_saturday);

        switch_pickup.setOnToggledListener((labeledSwitch, isOn) -> {
            if (isOn) {
                pickup = 1;
            } else {
                pickup = 0;
            }
        });

        switch_delivery.setOnToggledListener((labeledSwitch, isOn) -> {
            if (isOn) {
                delivery = 1;
            } else {
                delivery = 0;
            }
        });

        switch_sunday.setOnToggledListener((labeledSwitch, isOn) -> {
            if (isOn) {
                sunday = 1;
            } else {
                sunday = 0;
            }
        });
        switch_monday.setOnToggledListener((labeledSwitch, isOn) -> {
            if (isOn) {
                monday = 1;
            } else {
                monday = 0;
            }
        });
        switch_tuesday.setOnToggledListener((labeledSwitch, isOn) -> {
            if (isOn) {
                thuesday = 1;
            } else {
                thuesday = 0;
            }
        });
        switch_wednesday.setOnToggledListener((labeledSwitch, isOn) -> {
            if (isOn) {
                wednesday = 1;
            } else {
                wednesday = 0;
            }
        });
        switch_thursday.setOnToggledListener((labeledSwitch, isOn) -> {
            if (isOn) {
                thursday = 1;
            } else {
                thursday = 0;
            }
        });
        switch_friday.setOnToggledListener((labeledSwitch, isOn) -> {
            if (isOn) {
                friday = 1;
            } else {
                friday = 0;
            }
        });
        switch_saturday.setOnToggledListener((labeledSwitch, isOn) -> {
            if (isOn) {
                saturday = 1;
            } else {
                saturday = 0;
            }
        });


        tv_sun_from.setOnClickListener(v -> LoadDialogTime(1, tv_sun_from.getText().toString()));
        tv_mon_from.setOnClickListener(v -> LoadDialogTime(2, tv_mon_from.getText().toString()));
        tv_tue_from.setOnClickListener(v -> LoadDialogTime(3, tv_tue_from.getText().toString()));
        tv_wed_from.setOnClickListener(v -> LoadDialogTime(4, tv_wed_from.getText().toString()));
        tv_thu_from.setOnClickListener(v -> LoadDialogTime(5, tv_thu_from.getText().toString()));
        tv_fri_from.setOnClickListener(v -> LoadDialogTime(6, tv_fri_from.getText().toString()));
        tv_sat_from.setOnClickListener(v -> LoadDialogTime(7, tv_sat_from.getText().toString()));

        tv_sun_to.setOnClickListener(v -> LoadDialogTime(8, tv_sun_to.getText().toString()));
        tv_mon_to.setOnClickListener(v -> LoadDialogTime(9, tv_mon_to.getText().toString()));
        tv_tue_to.setOnClickListener(v -> LoadDialogTime(10, tv_tue_to.getText().toString()));
        tv_wed_to.setOnClickListener(v -> LoadDialogTime(11, tv_wed_to.getText().toString()));
        tv_thu_to.setOnClickListener(v -> LoadDialogTime(12, tv_thu_to.getText().toString()));
        tv_fri_to.setOnClickListener(v -> LoadDialogTime(13, tv_fri_to.getText().toString()));
        tv_sat_to.setOnClickListener(v -> LoadDialogTime(14, tv_sat_to.getText().toString()));

        btn_Submit = v_RegistrationHolder.findViewById(R.id.btn_submit);
        btn_Submit.setOnClickListener(v -> {
            if (!et_Email.getText().toString().isEmpty()) {
                auth.createUserWithEmailAndPassword(et_Email.getText().toString(), et_Password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        vendor_uid = firebaseUser.getUid();
                                    }
                                    time = String.valueOf(Calendar.getInstance().getTime());
                                    regReference = FirebaseDatabase.getInstance().getReference("users_list").child(vendor_uid);

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("uid", vendor_uid);
                                    hashMap.put("user_name", et_OwnerName.getText().toString());
                                    hashMap.put("email", et_Email.getText().toString());
                                    hashMap.put("time", time);
                                    hashMap.put("type", "2");
                                    regReference.setValue(hashMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (vendor_uid != null && !vendor_uid.isEmpty()) {
                                                            register(vendor_uid);
                                                        } else {
                                                            Constant.showToast(getString(R.string.error));
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Constant.showToast(e.getMessage() +"");
                    }
                });
            }
        });

    }

    private void checkGpsLocationEnable() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                task.getResult(ApiException.class);
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(activity, 100);
                        } catch (IntentSender.SendIntentException e) {
                            //   Log.d("test", e.getMessage());
                        } catch (ClassCastException e) {
                            // Log.d("test", e.getMessage());
                        }
                        break;
                }
            }
        });

    }

    private void register(String vendor_uid) {

        if (et_EnglishName.getText().toString().length() > 2) {
            tv_EnglishNameError.setVisibility(View.GONE);

            // if (et_ArabicName.getText().toString().length() > 2) {
            // tv_ArabicNameError.setVisibility(View.GONE);

            if (et_OwnerName.getText().toString().length() > 2) {
                tv_ownerNameError.setVisibility(View.GONE);

                if (et_Email.getText().toString().length() > 0) {
                    //if (et_Email.getText().toString().matches(emailPattern)) {
                    tv_EmailError.setVisibility(View.GONE);

                    if (et_MobileNo.getText().toString().length() > 0) {
                        tv_MobileNoError.setVisibility(View.GONE);

                        if (!EditType.equals("Edit")) {
                            if (et_Password.getText().toString().length() > 3) {
                                tv_PasswordError.setVisibility(View.GONE);

                                if (et_ConfirmPassword.getText().toString().length() > 0) {
                                    if (et_Password.getText().toString().length() > 0
                                            && et_ConfirmPassword.getText().toString().equals(et_Password.getText().toString())) {
                                        tv_ConfirmPasswordError.setVisibility(View.GONE);
                                    } else {
                                        tv_ConfirmPasswordError.setVisibility(View.VISIBLE);
                                        et_ConfirmPassword.requestFocus();
                                        et_ConfirmPassword.setCursorVisible(true);
                                        dialog_box();
                                    }
                                } else {
                                    tv_ConfirmPasswordError.setVisibility(View.VISIBLE);
                                    et_ConfirmPassword.requestFocus();
                                    et_ConfirmPassword.setCursorVisible(true);
                                    dialog_box();
                                }
                            } else {
                                tv_PasswordError.setVisibility(View.VISIBLE);
                                et_Password.requestFocus();
                                et_Password.setCursorVisible(true);
                                dialog_box();
                            }
                        }

                    } else {
                        tv_MobileNoError.setVisibility(View.VISIBLE);
                        et_MobileNo.requestFocus();
                        et_MobileNo.setCursorVisible(true);
                        dialog_box();
                    }

//                        } else {
//                            tv_EmailError.setVisibility(View.VISIBLE);
//                            et_Email.requestFocus();
//                            et_Email.setCursorVisible(true);
//                            dialog_box();
//                        }
                } else {
                    tv_EmailError.setVisibility(View.VISIBLE);
                    et_Email.requestFocus();
                    et_Email.setCursorVisible(true);
                    dialog_box();
                }

            } else {
                tv_ownerNameError.setVisibility(View.VISIBLE);
                et_OwnerName.requestFocus();
                et_OwnerName.setCursorVisible(true);
                dialog_box();
            }

                /*} else {
                    tv_ArabicNameError.setVisibility(View.VISIBLE);
                    et_ArabicName.requestFocus();
                    et_ArabicName.setCursorVisible(true);
                    dialog_box();
                }*/

        } else {
            tv_EnglishNameError.setVisibility(View.VISIBLE);
            et_EnglishName.requestFocus();
            et_EnglishName.setCursorVisible(true);
            dialog_box();
        }

        if (mRegisterFields.getStore_cuisines() != null) {
            if (mRegisterFields.getStore_cuisines().size() > 0) {
                Cuisine_id = new StringBuilder();
                for (int i = 0; i < mRegisterFields.getStore_cuisines().size(); i++) {

                    if (mRegisterFields.getStore_cuisines().get(i).isSelected()) {
                        if (Cuisine_id.length() > 0) {
                            Cuisine_id.append(",").append(mRegisterFields.getStore_cuisines().get(i).getCuisine_id());

                        } else {
                            Cuisine_id.append(mRegisterFields.getStore_cuisines().get(i).getCuisine_id());
                        }

                    }

                }


            }
        }

        if (mRegisterFields.getPayment_methods() != null) {
            if (mRegisterFields.getPayment_methods().size() > 0) {
                Payment_id = new StringBuilder();
                for (int i = 0; i < mRegisterFields.getPayment_methods().size(); i++) {

                    if (mRegisterFields.getPayment_methods().get(i).isSelected()) {
                        if (Payment_id.length() > 0) {
                            Payment_id.append(",").append(mRegisterFields.getPayment_methods().get(i).getPayment_method_id());

                        } else {
                            Payment_id.append(mRegisterFields.getPayment_methods().get(i).getPayment_method_id());
                        }

                    }

                }


            }
        }

        JSONObject Cuisine = new JSONObject();
        if (Cuisine_id != null /*&& Cuisine_id.toString() != null*/) {
            String[] Cuisines_Ids = Cuisine_id.toString().split(",");

            if (Cuisines_Ids != null) {
                if (Cuisines_Ids.length > 0) {
                    for (int i = 0; i < Cuisines_Ids.length; i++) {
                        if (Cuisines_Ids[i] != null) {
                            if (Cuisines_Ids[i].length() > 0) {
                                try {
                                    Cuisine.put(String.valueOf(i), Cuisines_Ids[i]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
            }
        }

        JSONObject Payment = new JSONObject();
        if (Payment_id.toString() != null) {
            String[] Payment_Ids = Payment_id.toString().split(",");

            if (Payment_Ids != null) {
                if (Payment_Ids.length > 0) {
                    for (int i = 0; i < Payment_Ids.length; i++) {
                        if (Payment_Ids[i] != null) {
                            if (Payment_Ids[i].length() > 0) {
                                try {
                                    Payment.put(String.valueOf(i), Payment_Ids[i]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
            }


        }

        selectedDates = cv_Custom.getSelectedDates();
        StringBuilder selectdate = new StringBuilder();
        for (int i = 0; i < selectedDates.size(); i++) {
            int month = selectedDates.get(i).get(Calendar.MONTH);
            if (selectdate.length() > 0) {
                selectdate.append(",").append(selectedDates.get(i).get(Calendar.YEAR)).append("-").append(month + 1).append("-").append(selectedDates.get(i).get(Calendar.DAY_OF_MONTH));
            } else {
                selectdate.append(selectedDates.get(i).get(Calendar.YEAR)).append("-").append(month + 1).append("-").append(selectedDates.get(i).get(Calendar.DAY_OF_MONTH));
            }


        }

        JSONObject Working_days, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;

        Sunday = new JSONObject();
        Monday = new JSONObject();
        Tuesday = new JSONObject();
        Wednesday = new JSONObject();
        Thursday = new JSONObject();
        Friday = new JSONObject();
        Saturday = new JSONObject();
        Working_days = new JSONObject();
        try {
            Sunday.put("working", sunday);
            Sunday.put("start_time", tv_sun_from.getText().toString());
            Sunday.put("end_time", tv_sun_to.getText().toString());
            Working_days.put("0", Sunday);
            Monday.put("working", monday);
            Monday.put("start_time", tv_mon_from.getText().toString());
            Monday.put("end_time", tv_mon_to.getText().toString());
            Working_days.put("1", Monday);
            Tuesday.put("working", thuesday);
            Tuesday.put("start_time", tv_tue_from.getText().toString());
            Tuesday.put("end_time", tv_tue_to.getText().toString());
            Working_days.put("2", Tuesday);
            Wednesday.put("working", wednesday);
            Wednesday.put("start_time", tv_wed_from.getText().toString());
            Wednesday.put("end_time", tv_wed_to.getText().toString());
            Working_days.put("3", Wednesday);
            Thursday.put("working", thursday);
            Thursday.put("start_time", tv_thu_from.getText().toString());
            Thursday.put("end_time", tv_thu_to.getText().toString());
            Working_days.put("4", Thursday);
            Friday.put("working", friday);
            Friday.put("start_time", tv_fri_from.getText().toString());
            Friday.put("end_time", tv_fri_to.getText().toString());
            Working_days.put("5", Friday);
            Saturday.put("working", saturday);
            Saturday.put("start_time", tv_sat_from.getText().toString());
            Saturday.put("end_time", tv_sat_to.getText().toString());
            Working_days.put("6", Saturday);
            //  Log.e("LoadSetting: ", "" + Working_days);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String user_id = "";
        if (user_id != null && !user_id.equals("")) {
            String pan_card = pan_card_ed.getText().toString();
            String acc_no = acc_no_ed.getText().toString();
            String ifsc_code = ifsc_code_ed.getText().toString();

            String store_type;
            if (store_type_veg.isChecked()) {
                store_type = "1";
            } else if (store_type_non_veg.isChecked()) {
                store_type = "2";
            } else if (store_type_both.isChecked()) {
                store_type = "3";
            } else {
                store_type = " ";
            }

            if (!EditType.equals("Edit")) {

                if (et_EnglishName.getText().toString().length() > 2
                        // && et_ArabicName.getText().toString().length() > 2
                        && et_OwnerName.getText().toString().length() > 2
                        && et_Email.getText().toString().length() > 0
                        //&& et_Email.getText().toString().matches(emailPattern)
                        && et_MobileNo.getText().toString().length() > 0
                        && et_Password.getText().toString().length() > 3
                        && et_ConfirmPassword.getText().toString().length() > 0
                        && (et_Password.getText().toString().length() > 0
                        && et_ConfirmPassword.getText().toString().equals(et_Password.getText().toString()))
                ) {


                    JSONObject jsonObject = new JSONObject();
                    try {

                        String Ename = et_EnglishName.getText().toString();
                        // String Aname = et_ArabicName.getText().toString();
                        String Oname = et_OwnerName.getText().toString();
                        String Email = et_Email.getText().toString();
                        String Mobile = et_MobileNo.getText().toString();
                        String Password = et_Password.getText().toString();
                        String Confirm = et_ConfirmPassword.getText().toString();
                        String Preparing_time = et_preparing_time_value.getText().toString();
                        String language = LanguageDetailsDB.getInstance(activity).get_language_id();

                        jsonObject.put("name", Ename);
                        jsonObject.put("name_arabic", Ename);
                        jsonObject.put("owner", Oname);
                        jsonObject.put("email", Email);
                        jsonObject.put("telephone", Mobile);
                        jsonObject.put("password", Password);
                        jsonObject.put("confirm", Confirm);
                        jsonObject.put("status", status_id);
                        jsonObject.put("latitude", Latitude);
                        jsonObject.put("longitude", Longitude);
                        jsonObject.put("address", mLocation);
                        //  String geocode = Latitude + "," + Longitude;
                        //  jsonObject.put("geocode", geocode);
                        jsonObject.put("image", PROFILE_PATH);
                        jsonObject.put("logo", LOGO_PATH);
                        jsonObject.put("preparing_time", Preparing_time);
                        jsonObject.put("store_cuisine", Cuisine);
                        JSONObject emptyPayment = new JSONObject();
                        jsonObject.put("store_payment_method", emptyPayment);
                        jsonObject.put("non_available_date", selectdate);
                        jsonObject.put("pickup", pickup);
                        jsonObject.put("delivery", delivery);
                        jsonObject.put("working_hours", Working_days);
                        jsonObject.put("working_status_id", "open");
                        jsonObject.put("push_id", user_id);
                        jsonObject.put("device_type", "1");
                        jsonObject.put("store_type", store_type);
                        jsonObject.put("payment_detail", pan_card);
//                        jsonObject.put("acc_no", acc_no);
//                        jsonObject.put("ifsc_code", ifsc_code);
                        jsonObject.put("language_id", language);

                        if (!vendor_uid.isEmpty()) {
                            JSONObject object = new JSONObject();
                            object.put("type", "2");
                            object.put("user_id", vendor_uid);
                            jsonObject.put("firebase_data", object);
                        }
                        progressDialog.show();

                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                        apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        //  Call<String> call = apiInterface.UserRegistration(Fname,Lname,Email,Mobile,Password,Confirm,status_id,Latitude,Longitude,mLocation,"");
                        Call<String> call = apiInterface.UserRegistration(body);

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful()) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response.body());
                                        JSONObject jsonObject2 = jsonObject1.getJSONObject("success");
                                        Constant.showToast(jsonObject2.getString("message"));
                                        loginPageHandler.BackPressed();
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
                                                progressDialog.dismiss();
                                                Constant.showToast(jsonErrorObject.getString("message"));
                                            } else {
                                                progressDialog.dismiss();
                                                Constant.showToast(getString(R.string.error));
                                            }
                                        } else {
                                            progressDialog.dismiss();
                                            Constant.showToast(getString(R.string.error));
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        progressDialog.dismiss();
                                        Constant.showToast(getString(R.string.error));
                                    }
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
                }
            }
        }

    }

    private void loadRestaurantLocationMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)        // 5 seconds, in milliseconds
                .setFastestInterval(5000); // 5 second, in milliseconds


        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mGoogleApiClient != null)
                    if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient.connect();
                    } else if (!mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                    }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        // initialCameraPosition();
        restaurantLocationMapInitializerAndConnect();
    }

    private void restaurantLocationMapInitializerAndConnect(/*MapView mapView*/) {


        if (mapFragment != null) {

            mapFragment.onResume(); // needed to get the map to display immediately

            try {
                MapsInitializer.initialize(activity.getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }


            mapFragment.getMapAsync(mMap -> {
                mGoogleMap = mMap;
                //    initialCameraPosition(mGoogleMap.getCameraPosition().target);


                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    askPermission();
                } else {
                    mMap.setMyLocationEnabled(true);
                    getDeviceLocation();


                }


            });

        }


        if (mGoogleApiClient != null) {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }

    }

    private void initialCameraPosition(Location location) {
        if (location != null)
            if (!EditType.equals("Edit")) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.00f));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            }

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        mGoogleMap.setOnMarkerClickListener(marker -> false);

        mGoogleMap.setOnCameraMoveStartedListener(i -> {
            progressBar.setVisibility(View.VISIBLE);
            // Clear all markers :-
            if (mGoogleMap != null) {
                mGoogleMap.clear();
            }
        });


        mGoogleMap.setOnCameraIdleListener(() -> getAddress(mGoogleMap.getCameraPosition().target));

        mGoogleMap.setOnMapClickListener(latLng -> {

        });

        // }


    }

    private void getAddress(LatLng ll) {
        try {
            List<Address> addresses;
            addresses = geocoder.getFromLocation(ll.latitude, ll.longitude, 1);
            //  Log.e("latitude: ", ll.latitude + "");
            //  Log.e("longitude: ", ll.longitude + "");
            Latitude = ll.latitude;
            Longitude = ll.longitude;
            if (addresses.size() > 0) {

                // Log.d("*********************","*****************");

                // String mAdss = addresses.get(0).getAddressLine(0)+"--"+addresses.get(0).getLocality()+"--"+addresses.get(0).getAdminArea();
//                String mAdss = addresses.get(0).getAddressLine(0);
//                tv_address_value.setText(mAdss);
                if (addresses.get(0).getFeatureName() != null && addresses.get(0).getThoroughfare() != null) {
                    String street = addresses.get(0).getFeatureName() + "," + addresses.get(0).getThoroughfare();
                    tv_address_value.setText(street);
                } else if (addresses.get(0).getFeatureName() != null && addresses.get(0).getThoroughfare() == null) {
                    tv_address_value.setText(addresses.get(0).getFeatureName());
                } else if (addresses.get(0).getFeatureName() == null && addresses.get(0).getThoroughfare() != null) {
                    tv_address_value.setText(addresses.get(0).getThoroughfare());
                }
                mLocation = addresses.get(0).getLocality() + "," + addresses.get(0).getAdminArea();
                progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            //   e.printStackTrace();
        }
    }

    private void getDeviceLocation() {

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        //  Log.e("onComplete: ", task.getResult() + "");
                        initialCameraPosition(task.getResult());
                    } else {

                    }
                }
            });

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void askPermission() {

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(activity)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.please_allow_the_device_location_access)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    USER_LOCATION_PERMISSION_CODE);

                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        USER_LOCATION_PERMISSION_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 41:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadRestaurantLocationMap();

                } else {
                    askPermission();
                }
                break;

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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

    class CuisineListAdapter extends RecyclerView.Adapter<CuisineListAdapter.ViewHolderCuisine> {

        private boolean isFromView = false;

        @NonNull
        @Override
        public ViewHolderCuisine onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.fragment_cuisine_list_adapter, parent, false);
            return new ViewHolderCuisine(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderCuisine holder, int position) {
            holder.cb_cuisine.setText(CuisineList.get(position).getName());
            isFromView = true;
            holder.cb_cuisine.setChecked(CuisineList.get(position).isSelected());
            isFromView = false;
            holder.cb_cuisine.setTag(position);

            holder.cb_cuisine.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    CuisineList.get(getPosition).setSelected(isChecked);
                }
            });
        }


        @Override
        public int getItemCount() {
            return CuisineList.size();
        }

        class ViewHolderCuisine extends RecyclerView.ViewHolder {
            CheckBox cb_cuisine;

            ViewHolderCuisine(View itemView) {
                super(itemView);
                cb_cuisine = itemView.findViewById(R.id.cb_cuisine);
            }
        }
    }

    class Payment_methodListAdapter extends RecyclerView.Adapter<Payment_methodListAdapter.ViewHolderPayment> {

        private boolean isFromView = false;


        @NonNull
        @Override
        public ViewHolderPayment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.fragment_payment_method_adapter, parent, false);
            return new ViewHolderPayment(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderPayment holder, int position) {
            holder.cb_payment.setText(PaymentList.get(position).getName());
            isFromView = true;
            holder.cb_payment.setChecked(PaymentList.get(position).isSelected());
            isFromView = false;
            holder.cb_payment.setTag(position);

            holder.cb_payment.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    PaymentList.get(getPosition).setSelected(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return PaymentList.size();
        }

        class ViewHolderPayment extends RecyclerView.ViewHolder {
            CheckBox cb_payment;

            ViewHolderPayment(View itemView) {
                super(itemView);
                cb_payment = itemView.findViewById(R.id.cb_payment);
            }
        }
    }

    private void showFileChooser(int Type) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), Type);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String encodedString;
        File file;
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), filePath);

                if (requestCode == IMAGE) {
                    //  Log.e("onActivityResult: ", "testing");
                    Bitmap profile_drawable = Constant.getResizedBitmap(bitmap, 400);
                    iv_image.setImageBitmap(profile_drawable);
                } else {
                    Bitmap logo_drawable = Constant.getResizedBitmap(bitmap, 400);
                    iv_logo.setImageBitmap(logo_drawable);
                }

                file = new File("" + filePath);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 20, stream);
                byte[] byte_arr = stream.toByteArray();

                encodedString = Base64.encodeToString(byte_arr, 0);

                uploader(encodedString, file, requestCode);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void uploader(String data, File file, int requestCode) {
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        if (requestCode == IMAGE) {
            try {

                JSONObject OptionPost = new JSONObject();
                OptionPost.put("filename", file.getName().replaceAll("[^a-zA-Z0-9]", "") + ".png");
                OptionPost.put("file", data);
                OptionPost.put("language_id", language);

                PROFILE_IMAGE = OptionPost.toString();
                // Log.e("uploader: ", OptionPost + "");
                loadUploadImage(PROFILE_IMAGE, requestCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {

                JSONObject OptionPost = new JSONObject();
                OptionPost.put("filename", file.getName().replaceAll("[^a-zA-Z0-9]", "") + ".png");
                OptionPost.put("file", data);
                OptionPost.put("language_id", language);

                LOGO_IMAGE = OptionPost.toString();
                // Log.e("uploader: ", OptionPost + "");
                loadUploadImage(LOGO_IMAGE, requestCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void loadUploadImage(String Data, int requestCode) {
        progress_image.setVisibility(View.VISIBLE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), Data);
        Call<String> call = apiInterface.ImageUpload(body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if (response.isSuccessful()) {
                    progress_image.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response.body());
                        if (requestCode == IMAGE) {
                            PROFILE_PATH = object.getString("filepath");
                        } else {
                            LOGO_PATH = object.getString("filepath");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progress_image.setVisibility(View.GONE);
            }
        });


    }

    private void requestPermission(int Type) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Type);
    }

    private boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void LoadDialogTime(int type, String date) {
        int hour = 0;
        int minute = 0;
        if (date.length() > 0) {

            String list[] = date.split(":");
            hour = Integer.parseInt(list[0]);
            minute = Integer.parseInt(list[1]);
        } else {
            Calendar mcurrentTime = Calendar.getInstance();
            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            minute = mcurrentTime.get(Calendar.MINUTE);
        }

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                String AM_PM;
//                if (selectedHour < 12) {
//                    AM_PM = "AM";
//                } else {
//                    AM_PM = "PM";
//                }

                if (type == 1) {
                    tv_sun_from.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 2) {
                    tv_mon_from.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 3) {
                    tv_tue_from.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 4) {
                    tv_wed_from.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 5) {
                    tv_thu_from.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 6) {
                    tv_fri_from.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 7) {
                    tv_sat_from.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 8) {
                    tv_sun_to.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 9) {
                    tv_mon_to.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 10) {
                    tv_tue_to.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 11) {
                    tv_wed_to.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 12) {
                    tv_thu_to.setText(selectedHour + ":" + selectedMinute);
                } else if (type == 13) {
                    tv_fri_to.setText(selectedHour + ":" + selectedMinute);
                } else {
                    tv_sat_to.setText(selectedHour + ":" + selectedMinute);
                }

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.show();
    }

//    private void LoadStoreInfo() {
//        progressBar.setVisibility(View.VISIBLE);
//        apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<String> call = apiInterface.getStoreInfo(Constant.DataGetValue(activity, Constant.Token));
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                progressBar.setVisibility(View.GONE);
//                if (response.isSuccessful()) {
//                    mRestaurant_Info = ContentJsonParser.getStoreInfo(response.body());
//                    if (mRestaurant_Info != null) {
//                        et_EnglishName.setText(mRestaurant_Info.getStoreInfo().getName());
//                        et_ArabicName.setText(mRestaurant_Info.getStoreInfo().getName_arabic());
//                        et_OwnerName.setText(mRestaurant_Info.getStoreInfo().getOwner());
//                        et_Email.setText(mRestaurant_Info.getStoreInfo().getEmail());
//                        et_MobileNo.setText(mRestaurant_Info.getStoreInfo().getTelephone());
//                        et_preparing_time_value.setText(mRestaurant_Info.getStoreInfo().getPreparing_time());
//                        String sectionStatus = mRestaurant_Info.getStoreInfo().getStatus();
//                        if (Status != null) {
//                            if (Status.length > 0) {
//                                for (int i = 0; i < Status.length; i++) {
//                                    if (sectionStatus.equals(Status[i])) {
//                                        sp_status.setSelection(i);
//
//                                    }
//
//                                }
//                            }
//                        }
//                        if (mRestaurant_Info.getStore_cuisines() != null) {
//                            for (int i = 0; i < mRestaurant_Info.getStore_cuisines().size(); i++) {
//                                Select_cuisine_id.add(Integer.valueOf(mRestaurant_Info.getStore_cuisines().get(i).getCuisine_id()));
//                            }
//                        }
//                        if (mRestaurant_Info.getPayment_methods() != null) {
//                            for (int i = 0; i < mRestaurant_Info.getPayment_methods().size(); i++) {
//                                Select_payment_id.add(Integer.valueOf(mRestaurant_Info.getPayment_methods().get(i).getPayment_method_id()));
//                            }
//                        }
//
//                        if (Select_cuisine_id != null) {
//                            if (Select_cuisine_id.size() > 0) {
//                                for (int i = 0; i < CuisineList.size(); i++) {
//                                    for (int j = 0; j < Select_cuisine_id.size(); j++) {
//                                        if (CuisineList.get(i).getCuisine_id().equals(String.valueOf(Select_cuisine_id.get(j)))) {
//                                            CuisineList.get(i).setSelected(true);
//                                        }
//
//                                    }
//                                }
//                            }
//                        }
//
//                        if (Select_payment_id != null) {
//                            if (Select_payment_id.size() > 0) {
//                                for (int i = 0; i < PaymentList.size(); i++) {
//                                    for (int j = 0; j < Select_payment_id.size(); j++) {
//                                        if (PaymentList.get(i).getPayment_method_id().equals(String.valueOf(Select_payment_id.get(j)))) {
//                                            PaymentList.get(i).setSelected(true);
//                                        }
//
//                                    }
//                                }
//                            }
//                        }
//
//                        LodCuisine(CuisineList);
//                        LoadPaymentMethods(PaymentList);
//
//                        if (mRestaurant_Info.getStoreInfo().getPickup() == 1) {
//                            switch_pickup.setOn(true);
//                        } else {
//                            switch_pickup.setOn(false);
//                        }
//
//                        if (mRestaurant_Info.getStoreInfo().getDelivery() == 1) {
//                            switch_delivery.setOn(true);
//                        } else {
//                            switch_delivery.setOn(false);
//                        }
//                        Double latitude = mRestaurant_Info.getStoreInfo().getLatitude();
//                        Double longitude = mRestaurant_Info.getStoreInfo().getLongitude();
//
//                        if (latitude != null && longitude != null) {
//
//                            progressBar.setVisibility(View.GONE);
//                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.00f));
//                            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
//                        } else {
//                            getDeviceLocation();
//                        }
//                        PROFILE_PATH = mRestaurant_Info.getStoreInfo().getImage_path();
//                        LOGO_PATH = mRestaurant_Info.getStoreInfo().getLogo_path();
//                        Glide.with(activity).load(mRestaurant_Info.getStoreInfo().getImage()).into(iv_image);
//                        Glide.with(activity).load(mRestaurant_Info.getStoreInfo().getLogo()).into(iv_logo);
//                        LoadSelectDates();
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(response.body());
//                            JSONObject workingHours = jsonObject.getJSONObject("working_hours");
//                            if (workingHours != null) {
//                                JSONObject Sunday = workingHours.getJSONObject("sunday");
//                                tv_sun_from.setText(Sunday.getString("start_time"));
//                                tv_sun_to.setText(Sunday.getString("end_time"));
//                                if (Sunday.getString("working").equals("open")) {
//                                    switch_sunday.setOn(true);
//                                }
//                                JSONObject Monday = workingHours.getJSONObject("monday");
//                                tv_mon_from.setText(Monday.getString("start_time"));
//                                tv_mon_to.setText(Monday.getString("end_time"));
//                                if (Monday.getString("working").equals("open")) {
//                                    switch_monday.setOn(true);
//                                }
//                                JSONObject Tuesday = workingHours.getJSONObject("tuesday");
//                                tv_tue_from.setText(Tuesday.getString("start_time"));
//                                tv_tue_to.setText(Tuesday.getString("end_time"));
//                                if (Tuesday.getString("working").equals("open")) {
//                                    switch_tuesday.setOn(true);
//                                }
//                                JSONObject Wednesday = workingHours.getJSONObject("wednesday");
//                                tv_wed_from.setText(Wednesday.getString("start_time"));
//                                tv_wed_to.setText(Wednesday.getString("end_time"));
//                                if (Wednesday.getString("working").equals("open")) {
//                                    switch_wednesday.setOn(true);
//                                }
//                                JSONObject Thursday = workingHours.getJSONObject("thursday");
//                                tv_thu_from.setText(Thursday.getString("start_time"));
//                                tv_thu_to.setText(Thursday.getString("end_time"));
//                                if (Thursday.getString("working").equals("open")) {
//                                    switch_thursday.setOn(true);
//                                }
//                                JSONObject Friday = workingHours.getJSONObject("friday");
//                                tv_fri_from.setText(Friday.getString("start_time"));
//                                tv_fri_to.setText(Friday.getString("end_time"));
//                                if (Friday.getString("working").equals("open")) {
//                                    switch_friday.setOn(true);
//                                }
//                                JSONObject Saturday = workingHours.getJSONObject("saturday");
//                                tv_sat_from.setText(Saturday.getString("start_time"));
//                                tv_sat_to.setText(Saturday.getString("end_time"));
//                                if (Saturday.getString("working").equals("open")) {
//                                    switch_saturday.setOn(true);
//                                }
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                }
//            }
//
//
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//    }

    private void storeInfo(String mRestaurant_info) {
        // Log.e( "storeInfo: ",mRestaurant_info+" " );
        mRestaurant_Info = ContentJsonParser.getStoreInfo(mRestaurant_info);

        if (mRestaurant_Info != null) {
            et_EnglishName.setText(mRestaurant_Info.getStoreInfo().getName());
            // et_ArabicName.setText(mRestaurant_Info.getStoreInfo().getName_arabic());
            et_OwnerName.setText(mRestaurant_Info.getStoreInfo().getOwner());
            et_Email.setText(mRestaurant_Info.getStoreInfo().getEmail());
            et_MobileNo.setText(mRestaurant_Info.getStoreInfo().getTelephone());
            et_preparing_time_value.setText(mRestaurant_Info.getStoreInfo().getPreparing_time());
            String sectionStatus = mRestaurant_Info.getStoreInfo().getStatus();
            if (Status != null) {
                if (Status.length > 0) {
                    for (int i = 0; i < Status.length; i++) {
                        if (sectionStatus.equals(Status[i])) {
                            sp_status.setSelection(i);

                        }

                    }
                }
            }

            if (mRestaurant_Info.getStore_cuisines() != null) {
                for (int i = 0; i < mRestaurant_Info.getStore_cuisines().size(); i++) {
                    Select_cuisine_id.add(Integer.valueOf(mRestaurant_Info.getStore_cuisines().get(i).getCuisine_id()));
                }
            }
            if (mRestaurant_Info.getPayment_methods() != null) {
                for (int i = 0; i < mRestaurant_Info.getPayment_methods().size(); i++) {
                    Select_payment_id.add(Integer.valueOf(mRestaurant_Info.getPayment_methods().get(i).getPayment_method_id()));
                }
            }

            if (Select_cuisine_id != null) {
                if (Select_cuisine_id.size() > 0) {
                    for (int i = 0; i < CuisineList.size(); i++) {
                        for (int j = 0; j < Select_cuisine_id.size(); j++) {
                            if (CuisineList.get(i).getCuisine_id().equals(String.valueOf(Select_cuisine_id.get(j)))) {
                                CuisineList.get(i).setSelected(true);
                            }

                        }
                    }
                }
            }

            if (Select_payment_id != null) {
                if (Select_payment_id.size() > 0) {
                    for (int i = 0; i < PaymentList.size(); i++) {
                        for (int j = 0; j < Select_payment_id.size(); j++) {
                            if (PaymentList.get(i).getPayment_method_id().equals(String.valueOf(Select_payment_id.get(j)))) {
                                PaymentList.get(i).setSelected(true);
                            }

                        }
                    }
                }
            }

            LodCuisine(CuisineList);
            // LoadPaymentMethods(PaymentList);

            if (mRestaurant_Info.getStoreInfo().getPickup() == 1) {
                switch_pickup.setOn(true);
            } else {
                switch_pickup.setOn(false);
            }

            if (mRestaurant_Info.getStoreInfo().getDelivery() == 1) {
                switch_delivery.setOn(true);
            } else {
                switch_delivery.setOn(false);
            }
            Double latitude = mRestaurant_Info.getStoreInfo().getLatitude();
            Double longitude = mRestaurant_Info.getStoreInfo().getLongitude();

            if (latitude != null && longitude != null) {

                progressBar.setVisibility(View.GONE);
//                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.00f));
//                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            } else {
                getDeviceLocation();
            }
            PROFILE_PATH = mRestaurant_Info.getStoreInfo().getImage_path();
            LOGO_PATH = mRestaurant_Info.getStoreInfo().getLogo_path();
            Glide.with(activity).load(mRestaurant_Info.getStoreInfo().getImage()).into(iv_image);
            Glide.with(activity).load(mRestaurant_Info.getStoreInfo().getLogo()).into(iv_logo);
            LoadSelectDates();

//            try {
//                JSONObject jsonObject = new JSONObject(response.body());
//                JSONObject workingHours = jsonObject.getJSONObject("working_hours");
//                if (workingHours != null) {
//                    JSONObject Sunday = workingHours.getJSONObject("sunday");
//                    tv_sun_from.setText(Sunday.getString("start_time"));
//                    tv_sun_to.setText(Sunday.getString("end_time"));
//                    if (Sunday.getString("working").equals("open")) {
//                        switch_sunday.setOn(true);
//                    }
//                    JSONObject Monday = workingHours.getJSONObject("monday");
//                    tv_mon_from.setText(Monday.getString("start_time"));
//                    tv_mon_to.setText(Monday.getString("end_time"));
//                    if (Monday.getString("working").equals("open")) {
//                        switch_monday.setOn(true);
//                    }
//                    JSONObject Tuesday = workingHours.getJSONObject("tuesday");
//                    tv_tue_from.setText(Tuesday.getString("start_time"));
//                    tv_tue_to.setText(Tuesday.getString("end_time"));
//                    if (Tuesday.getString("working").equals("open")) {
//                        switch_tuesday.setOn(true);
//                    }
//                    JSONObject Wednesday = workingHours.getJSONObject("wednesday");
//                    tv_wed_from.setText(Wednesday.getString("start_time"));
//                    tv_wed_to.setText(Wednesday.getString("end_time"));
//                    if (Wednesday.getString("working").equals("open")) {
//                        switch_wednesday.setOn(true);
//                    }
//                    JSONObject Thursday = workingHours.getJSONObject("thursday");
//                    tv_thu_from.setText(Thursday.getString("start_time"));
//                    tv_thu_to.setText(Thursday.getString("end_time"));
//                    if (Thursday.getString("working").equals("open")) {
//                        switch_thursday.setOn(true);
//                    }
//                    JSONObject Friday = workingHours.getJSONObject("friday");
//                    tv_fri_from.setText(Friday.getString("start_time"));
//                    tv_fri_to.setText(Friday.getString("end_time"));
//                    if (Friday.getString("working").equals("open")) {
//                        switch_friday.setOn(true);
//                    }
//                    JSONObject Saturday = workingHours.getJSONObject("saturday");
//                    tv_sat_from.setText(Saturday.getString("start_time"));
//                    tv_sat_to.setText(Saturday.getString("end_time"));
//                    if (Saturday.getString("working").equals("open")) {
//                        switch_saturday.setOn(true);
//                    }
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

        }
    }

    private void dialog_box() {

        AlertDialog.Builder alart = new AlertDialog.Builder(getContext());
        alart.setMessage(getResources().getString(R.string.required_fields_missing));
        alart.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alart.create();
        alart.show();

    }

    private void LoadSelectDates() {
//        cv_Custom = v_RegistrationHolder.findViewById(R.id.cv_custom);


        List<Calendar> calendars = new ArrayList<>();

        if (mRestaurant_Info.getNon_available_date() != null) {

            String[] Select_Date = mRestaurant_Info.getNon_available_date().split(",");
            // Log.e("Select_Date", mRestaurant_Info.getNon_available_date());

            for (int i = 0; i < Select_Date.length; i++) {
                String list[] = Select_Date[i].split("-");

                // Log.e("list", Select_Date[i]);

                if (list.length > 0) {
                    Calendar calendar1 = DateUtils.getCalendar();
                    int year = Integer.valueOf(list[0]), month = Integer.valueOf(list[1]) - 1, date = Integer.valueOf(list[2]);
                    calendar1.set(year, month, date);

                    calendars.add(calendar1);
                }

            }
            cv_Custom.setSelectedDates(calendars);
        }


    }
}
