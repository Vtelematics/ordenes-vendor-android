package ordenese.vendor.fragment.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ordenese.vendor.R;
import ordenese.vendor.activity.Activity_Splash_Screen;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;


public class DialogNetworkError extends DialogFragment {

    private View v_NetworkError;
    private Activity activity;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_NetworkError = inflater.inflate(R.layout.dialog_network_error, container, false);
        load();
        return v_NetworkError;
    }

    private void load() {
        ImageButton ib_CheckConnection = v_NetworkError.findViewById(R.id.btn_retry);
        TextView tv_Title = v_NetworkError.findViewById(R.id.tv_title_no_internet_connection);

        ib_CheckConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.isNetworkAvailable()) {
                    Constant.showToast(getString(R.string._retry));
                } else {
                    Intent intent = new Intent(activity, Activity_Splash_Screen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null)
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


}
