package ordenese.vendor.fragment.option;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.instant_transfer.OptionValueTransfer;


public class Dialog_OptionValue extends DialogFragment {

    private View v_OptionValue;
    private OptionValueTransfer optionValueTransfer;
    private String loadType, optionValueName;
    private int OptionId = 0, sortOrder;

    @Override
    public void onAttach(Context context) {

        super.onAttach(AppLanguageSupport.onAttach(context));
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
        Bundle bundle = getArguments();
        if (bundle != null) {
            loadType = bundle.getString(Constant.TYPE);

            if (!loadType.equals("NEW")) {
                optionValueName = bundle.getString(Constant.NAME);
                OptionId = bundle.getInt(Constant.OPTION_ID);
                sortOrder = bundle.getInt(Constant.SORT_ORDER);
            }

        } else {
            loadType = "NEW";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_OptionValue = inflater.inflate(R.layout.dialog_option_value_add, container, false);
        load();
        return v_OptionValue;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null)
            return;

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setOptionValue(OptionValueTransfer optionValue) {
        this.optionValueTransfer = optionValue;
    }

    private void load() {
        EditText et_OptionValue, et_SortOrder;
        Button btn_Save, btn_Cancel;

        et_OptionValue = v_OptionValue.findViewById(R.id.et_option_value_value);
        et_SortOrder = v_OptionValue.findViewById(R.id.et_sort_order_value);
        btn_Cancel = v_OptionValue.findViewById(R.id.btn_cancel_option);
        btn_Save = v_OptionValue.findViewById(R.id.btn_save_option);

        if (optionValueName != null) {
            if (optionValueName.length() > 0) {
                et_OptionValue.setText(optionValueName);
            }
        }
        if (sortOrder != -1)
            et_SortOrder.setText(String.valueOf(sortOrder));

        btn_Cancel.setOnClickListener(v -> dismiss());

        btn_Save.setOnClickListener(v -> {
            if (et_OptionValue.getText().toString().length() > 0) {
                int sortOrder;
                if (et_SortOrder.getText().toString().length() > 0) {
                    sortOrder = Integer.valueOf(et_SortOrder.getText().toString());
                } else {
                    sortOrder = 0;
                }
                optionValueTransfer.transferDetail(et_OptionValue.getText().toString(), sortOrder, loadType.equals("NEW"), OptionId);
                dismiss();
            }else {
                Constant.loadToastMessage(getActivity(), getString(R.string.txt_filter_enter_value));
            }
        });
    }
}
