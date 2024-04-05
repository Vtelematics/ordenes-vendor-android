package ordenese.vendor.fragment.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.activity.Activity_Home;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.LoginPageHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentSignIn extends Fragment {

    EditText et_Email, et_Password;
    Button btn_login;
    TextView Error_email, Error_password, Signup, btn_forgot;
    LoginPageHandler loginPageHandler;
    private Activity context;
    ApiInterface apiInterface;
    ProgressBar loadProgress;
    FirebaseAuth authLogin;
    Activity activity;
    String token = "";

    public FragmentSignIn() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.context = (Activity) context;
        loginPageHandler = (LoginPageHandler) context;
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
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        activity = getActivity();
        authLogin = FirebaseAuth.getInstance();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        Constant.DataStoreValue(activity, "token_notify", token);
                    }
                });

        et_Email = view.findViewById(R.id.text_email);
        et_Password = view.findViewById(R.id.text_password);
        btn_login = view.findViewById(R.id.btn_login);
        btn_forgot = view.findViewById(R.id.text_forget);
        Error_email = view.findViewById(R.id.error_email);
        Error_password = view.findViewById(R.id.error_password);
        Signup = view.findViewById(R.id.text_sign_up);
        loadProgress = view.findViewById(R.id.progressBar);
        CheckBox cb_ShowPassword = view.findViewById(R.id.cb_show_pwd);
        Signup.setOnClickListener(v -> loginPageHandler.LoadSignUp());
        btn_forgot.setOnClickListener(v -> loginPageHandler.LoadForgot());

        cb_ShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                et_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                et_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
        btn_login.setOnClickListener(v -> {

            if (et_Email.getText().length() == 0) {
                Error_email.setVisibility(View.VISIBLE);
                Error_email.setText(getResources().getString(R.string.error_email));
            } else {
                Error_email.setVisibility(View.GONE);
            }
            if (et_Password.getText().length() == 0) {
                Error_password.setVisibility(View.VISIBLE);
                Error_password.setText(getResources().getString(R.string.error_password));
            } else {
                if (et_Password.getText().length() > 3) {
                    Error_password.setVisibility(View.GONE);
                } else {
                    Error_password.setVisibility(View.VISIBLE);
                    Error_password.setText(getResources().getString(R.string.error_password));
                }

            }
            if (et_Email.getText().length() > 0 && et_Password.getText().length() > 3) {
                String email = et_Email.getText().toString();
                String password = et_Password.getText().toString();
                String user_id ="";
                try {
                   user_id = OneSignal.getDeviceState().getUserId();
                }catch (Exception e){
                    Log.e("user_id", "onCreateView: "+e.getMessage());
                    Constant.showToast(e.getMessage());
                }

//                String user_id ="b747ce43-5f15-4555-8766-c901dba9960e";
                if (Constant.isNetworkAvailable()) {

                    if (user_id != null && !user_id.equals("")) {

                        JSONObject jsonObject = new JSONObject();
                        try {
//                            if (token != null && !token.equals("")) {
                                String language = LanguageDetailsDB.getInstance(context).get_language_id();

                                jsonObject.put("language_id", Constant.current_language_id());
                                jsonObject.put("language_code", Constant.current_language_code());
                                jsonObject.put("email", email);
                                jsonObject.put("password", password);
                                jsonObject.put("push_id", user_id);
                                jsonObject.put("device_type", "1");
                                jsonObject.put("language_id", language);

                                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                                apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                Call<String> Call = apiInterface.UserLogin(body);

                                loadProgress.setVisibility(View.VISIBLE);
                                Call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                        loadProgress.setVisibility(View.GONE);
                                        if (response.isSuccessful()) {
//                                    login();
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.body());
                                                if (!jsonObject.isNull("success")) {
                                                    JSONObject jsonObject1 = jsonObject.getJSONObject("success");
                                                    JSONObject jsonObject2 = jsonObject.getJSONObject("vendor_info");
                                                    String Token = jsonObject2.getString("secret_key");

                                                    Constant.DataStoreValue(context, Constant.Token, Token);
                                                    Constant.DataStoreValue(context, Constant.StoreDetails, jsonObject2.toString());
                                                    Constant.DataStoreValue(context, Constant.StoreId, jsonObject2.getString("id"));

                                                    if (!jsonObject.isNull("vendor_info")) {
                                                        JSONObject object = jsonObject.getJSONObject("vendor_info");
                                                        if (!object.isNull("vendor_type")) {  // 1-food, 2-grocery
                                                            Constant.DataStoreValue(context, "vendor_type", object.getString("vendor_type"));
                                                        } else {
                                                            Constant.DataStoreValue(context, "vendor_type", "1");
                                                        }
                                                    } else {
                                                        Constant.DataStoreValue(context, "vendor_type", "1");
                                                    }

                                                    if (!jsonObject2.isNull("admin_uid")) {
                                                        Constant.DataStoreValue(context, "admin_uid", jsonObject2.getString("admin_uid"));
                                                    }
                                                    if (!jsonObject2.isNull("firebase_data")) {
                                                        JSONObject object = jsonObject2.getJSONObject("firebase_data");
                                                        if (!object.isNull("user_id") && !object.getString("user_id").isEmpty()) {
                                                            Constant.DataStoreValue(context, "vendor_uid", object.getString("user_id"));
                                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                                            HashMap<String, Object> hashMap1 = new HashMap<>();
                                                            hashMap1.put("token", Constant.DataGetValue(activity, "token_notify"));
                                                            reference.child("users_list").child(object.getString("user_id")).updateChildren(hashMap1);
                                                        } else {
                                                            login();
                                                        }
                                                    }

                                                    Constant.DataStoreValue(context, Constant.StoreId, jsonObject2.getString("id"));
                                                    showToast(jsonObject1.getString("message"));
                                                    loginPageHandler.loadRefreshMenu();
                                                    loginPageHandler.CloseActivity();

                                                    Intent intent = new Intent(context, Activity_Home.class);
                                                    startActivity(intent);
                                                    context.finish();
                                                } else if (!jsonObject.isNull("error")) {
                                                    JSONObject error = jsonObject.getJSONObject("error");
                                                    if (!error.isNull("message")) {
                                                        showToast(error.getString("message"));
                                                    }
                                                } else {
                                                    Constant.showToast(getString(R.string.error)+"1");
                                                }
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
                                                        loadProgress.setVisibility(View.GONE);
                                                    } else {
                                                        Constant.showToast(getString(R.string.error)+"2");
                                                    }
                                                } else {
                                                    Constant.showToast(getString(R.string.error)+"3");
                                                }


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Constant.showToast(getString(R.string.error)+"4");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                        loadProgress.setVisibility(View.GONE);
                                    }
                                });
//                            } else {
//                                Constant.showToast(getString(R.string.error));
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Constant.showToast(getString(R.string.error)+"5");
                    }

                } else {
                    Constant.LoadNetworkError(getChildFragmentManager());
                }

            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        Constant.DataStoreValue(activity, "token_notify", token);
                    }
                });

        return view;
    }

    private void login() {

        authLogin.signInWithEmailAndPassword(et_Email.getText().toString(), et_Password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getUser() != null) {
                                Constant.DataStoreValue(context, "vendor_uid", task.getResult().getUser().getUid());

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                HashMap<String, Object> hashMap1 = new HashMap<>();
                                hashMap1.put("token", Constant.DataGetValue(activity, "token_notify"));
                                reference.child("users_list").child(task.getResult().getUser().getUid()).updateChildren(hashMap1);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToast(String message) {
        Constant.showToast(message);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }
}
