package ordenese.vendor.fragment.dialog.Filter_dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.instant_transfer.Filter_Coupon;

import java.util.Calendar;


public class Dialog_filter_coupon extends DialogFragment{

    private Activity activity;
    private Filter_Coupon filter_coupon_handler;
    private String Start_date,End_date;
    TextView start_date, end_date;
    Button btn_cancel, btn_save;
    String key = "";
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    ProgressDialog progressDialog;

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
    public void AssignCouponhandler(Filter_Coupon filter_coupon) {
        this.filter_coupon_handler = filter_coupon;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){

            Start_date = getArguments().getString("start_date");
            End_date = getArguments().getString("end_date");

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter_coupon,container,false);
        start_date = view.findViewById(R.id.tv_start_date);
        end_date = view.findViewById(R.id.tv_end_date);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_save = view.findViewById(R.id.btn_save);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        if (Start_date != null || End_date != null){
            start_date.setText(Start_date);
            end_date.setText(End_date);
        }
//        if (End_date != null){
//
//        }
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        start_date.setOnClickListener(v -> {
            loadDate();
            int Year = 0,Month = 0,Date = 0;
            String Start_Date = start_date.getText().toString();
            if (Start_Date.length() > 0){
                String list[] = Start_Date.split("-");
                if (list.length > 0 && list.length == 3) {
                    Year = Integer.valueOf(list[0]);
                    Month = Integer.valueOf(list[1]) - 1 ;
                    Date = Integer.valueOf(list[2]);
                }
            }else {
                Year =  myCalendar.get(Calendar.YEAR);
                Month = myCalendar.get(Calendar.MONTH);
                Date =  myCalendar.get(Calendar.DAY_OF_MONTH);
            }

            DatePickerDialog dialog = new DatePickerDialog(activity, date, Year, Month,Date);
            dialog.show();
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        start_date.setText("");
                    }
                }
            });
            key = "Start_Date";
        });
        end_date.setOnClickListener((View v) -> {
            loadDate();
            int Year = 0,Month = 0,Date = 0;
            String End_Date = end_date.getText().toString();
            if (End_Date.length() > 0){
                String list[] = End_Date.split("-");
                if (list.length > 0 && list.length == 3) {
                    Year = Integer.valueOf(list[0]);
                    Month = Integer.valueOf(list[1]) - 1 ;
                    Date = Integer.valueOf(list[2]);
                }
            }else {
                Year =  myCalendar.get(Calendar.YEAR);
                Month = myCalendar.get(Calendar.MONTH);
                Date =  myCalendar.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog dialog = new DatePickerDialog(activity, date, Year, Month,Date);
            dialog.show();
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        end_date.setText("");
                    }
                }
            });
            key = "End_Date";
        });
        btn_save.setOnClickListener(v -> {
            progressDialog.show();

            String Start_Date = start_date.getText().toString();
            String End_Date = end_date.getText().toString();

            filter_coupon_handler.LoadFilterCoupon(Start_Date,End_Date);
            progressDialog.dismiss();
            dismiss();
        });
        return view;
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

    private void loadDate() {

        myCalendar = Calendar.getInstance();

        date = (view1, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
           // Log.e("onCreateView: ", " " + year + " - " + ((monthOfYear)+1) + " - " + dayOfMonth);
            updateDate(year, ((monthOfYear)+1), dayOfMonth, key);


        };

    }

    private void updateDate(int year, int monthOfYear, int dayOfMonth, String key) {
        switch (key) {
            case "Start_Date":
                start_date.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                break;
            case "End_Date":
                end_date.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                break;

        }

    }
}
