package ordenese.vendor.fragment.menu;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_change_password extends DialogFragment {

    ApiInterface apiInterface;
    TextView txt_password, txt_conform, error_password, error_conform_password;
    Button btn_save;
    Activity activity;
    ProgressBar progressBar;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password_page, container, false);

        txt_password = view.findViewById(R.id.Change_password);
        txt_conform = view.findViewById(R.id.txt_confirm);
        error_password = view.findViewById(R.id.error_password);
        error_conform_password = view.findViewById(R.id.error_confirm_password);
        btn_save = view.findViewById(R.id.button_save);
        progressBar = view.findViewById(R.id.progressBar);


        btn_save.setOnClickListener(v -> {
            if (txt_password.getText().toString().length() == 0  ) {
                error_password.setVisibility(View.VISIBLE);
                error_password.setText(getString(R.string.error_password));
            } else {
                if (txt_password.getText().toString().length() > 3){
                    error_password.setVisibility(View.GONE);
                }else {
                    error_password.setVisibility(View.VISIBLE);
                    error_password.setText(getString(R.string.error_password));
                }

            }
            if (txt_conform.getText().toString().length() == 0) {
                error_conform_password.setVisibility(View.VISIBLE);
                error_conform_password.setText(getString(R.string.error_confirm_password));
            } else {
                if (txt_password.getText().toString().equals(txt_conform.getText().toString())) {
                    error_password.setVisibility(View.GONE);
                    error_conform_password.setVisibility(View.GONE);
                } else {
                    error_conform_password.setVisibility(View.VISIBLE);
                    error_conform_password.setText(getString(R.string.error_confirm));
                }
            }

            if (txt_password.getText().toString().length() > 3
                    && txt_conform.getText().toString().length() > 0
                    && txt_password.getText().toString().equals(txt_conform.getText().toString())) {

                progressBar.setVisibility(View.VISIBLE);

                String Token = Constant.DataGetValue(activity, Constant.Token);
                String password = txt_password.getText().toString();
                String confirm = txt_conform.getText().toString();

                if (Constant.isNetworkAvailable()) {

                    apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put("password",password);
                        jsonObject.put("confirm_password",confirm);
                        jsonObject.put("language_id",language);

                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                        Call<String> Call = apiInterface.ChangePassword(Token,body);
                        Call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("success");
                                        Toast.makeText(activity, "" + jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    dismiss();
                                } else {
                                    BufferedReader br = new BufferedReader(new InputStreamReader(response.errorBody().byteStream()));
                                    String line;
                                    StringBuilder result = new StringBuilder();
                                    try {
                                        while ((line = br.readLine()) != null) {
                                            result.append(line);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        JSONObject jsonObject = new JSONObject(result.toString());
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("error");
                                        Toast.makeText(activity, jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }








                } else {
                    Constant.LoadNetworkError(getChildFragmentManager());
                }
            }


        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;
        int dialogWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

    }


}
