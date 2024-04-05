package ordenese.vendor.fragment.menu;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ordenese.vendor.common.Constant.emailPattern;

public class ForgotPassword extends DialogFragment {
    View mForgetView;
    EditText ed_mail;
    Button btn_save, btn_cancel;
    private Activity activity;
    TextView error_email;
    ProgressBar progressBar;
    private ApiInterface apiInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mForgetView = inflater.inflate(R.layout.fragment_forget_password, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        ed_mail = mForgetView.findViewById(R.id.text_email);
        btn_save = mForgetView.findViewById(R.id.btn_save);
        btn_cancel = mForgetView.findViewById(R.id.btn_cancel);
        error_email = mForgetView.findViewById(R.id.error_email);
        progressBar = mForgetView.findViewById(R.id.progressBar);
        btn_cancel.setOnClickListener(v -> dismiss());

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_mail.getText().length() == 0) {
                    error_email.setVisibility(View.VISIBLE);
                    error_email.setText(getString(R.string.error_email));
                } else {
//                    if (!ed_mail.getText().toString().matches(emailPattern)) {
                    error_email.setVisibility(View.VISIBLE);
                    error_email.setText(getString(R.string.error_invalid_email));
//                    } else {
//                        error_email.setVisibility(View.GONE);
//                    }

                }
                if (ed_mail.getText().length() > 0) {
                    String E_mail = ed_mail.getText().toString();
                    String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                    if (Constant.isNetworkAvailable()) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("language_id", "1");
                            jsonObject.put("language_code", "en");
                            jsonObject.put("email", E_mail);
                            jsonObject.put("language_id", language);

                            progressBar.setVisibility(View.VISIBLE);
                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                            Call<String> call = apiInterface.UserForgetPassword(body);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    progressBar.setVisibility(View.GONE);
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONObject jsonObject1 = new JSONObject(response.body());
                                            JSONObject jsonObject2 = jsonObject1.getJSONObject("success");
                                            Constant.showToast(jsonObject2.getString("message"));
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
                                                    progressBar.setVisibility(View.GONE);
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

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Constant.LoadNetworkError(getFragmentManager());
                    }

                }
            }
        });
        return mForgetView;
    }

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

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;
        int dialogWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().setCancelable(false);

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

    }
}
