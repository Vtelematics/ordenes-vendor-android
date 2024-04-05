package ordenese.vendor.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.onesignal.OneSignal;

import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Activity_Splash_Screen extends AppCompatActivity {

    ApiInterface apiInterface;
    private MediaPlayer mMediaPlayer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__splash__screen);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        loadHome();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppLanguageSupport.onAttach(base));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(
                    "ar".equals(AppLanguageSupport.getLanguage(Activity_Splash_Screen.this)) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
    }

    private void loadHome() {

        if (LanguageDetailsDB.getInstance(this).get_language_id().equals("")) {
            LanguageDetailsDB.getInstance(this).insert_language_detail("1");
        }

        new Handler().postDelayed(() -> {
            if (!Constant.DataGetValue(Activity_Splash_Screen.this, Constant.Token).equals("empty")) {
                startActivity(new Intent(Activity_Splash_Screen.this, Activity_Home.class));
            } else {
                startActivity(new Intent(Activity_Splash_Screen.this, Activity_BackBtn_Container.class));
            }
            finish();
        }, 3000);
    }

    private void LoadLanguage() {

        LanguageDetailsDB.getInstance(this).insert_language_detail("1");

        String language = LanguageDetailsDB.getInstance(this).get_language_id();

        Call<String> call = apiInterface.getLanguage(Constant.current_language_id(), language);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    ContentJsonParser.getLanguage(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }


}
