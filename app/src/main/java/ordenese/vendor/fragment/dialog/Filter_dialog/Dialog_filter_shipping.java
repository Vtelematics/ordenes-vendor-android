package ordenese.vendor.fragment.dialog.Filter_dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.Filter_Shipping;
import ordenese.vendor.model.Order_Status;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dialog_filter_shipping extends DialogFragment {
    private Activity activity;
    private String Start_date, End_date;
    TextView start_date, end_date;
    Button btn_cancel, btn_save;
    String key = "";
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    ProgressDialog progressDialog;
    private Filter_Shipping filter_shipping_handler;
    Spinner sp_status, sp_group;
    private ApiInterface apiInterface;
    private String Order_status_id, Order_Status, Group, Group_id;
    private ArrayList<ordenese.vendor.model.Order_Status> OrderStatus = new ArrayList<>();

    String[] Groups;

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

    public void AssignShippinghandler(Filter_Shipping filter_shipping) {
        this.filter_shipping_handler = filter_shipping;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            Groups = new String[]{getResources().getString(R.string.day), getResources().getString(R.string.week),
                    getResources().getString(R.string.month), getResources().getString(R.string.year)};

            Start_date = getArguments().getString("start_date");
            End_date = getArguments().getString("end_date");
            Order_Status = getArguments().getString("order_status");
            Group = getArguments().getString("group");
            // Log.e("onCreate: ", ""+ Group);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter_shipping, container, false);
        start_date = view.findViewById(R.id.tv_start_date);
        end_date = view.findViewById(R.id.tv_end_date);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_save = view.findViewById(R.id.btn_save);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        if (Start_date != null || End_date != null) {
            start_date.setText(Start_date);
            end_date.setText(End_date);
        }
//        if (End_date != null){
//
//        }
        sp_status = view.findViewById(R.id.sp_status);
        LoadOrderStatus();
        sp_group = view.findViewById(R.id.sp_group);
        LoadGroup();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        start_date.setOnClickListener(v -> {
            loadDate();
            int Year = 0, Month = 0, Date = 0;
            String Start_Date = start_date.getText().toString();
            if (Start_Date.length() > 0) {
                String list[] = Start_Date.split("-");
                if (list.length > 0 && list.length == 3) {
                    Year = Integer.valueOf(list[0]);
                    Month = Integer.valueOf(list[1]) - 1;
                    Date = Integer.valueOf(list[2]);
                }
            } else {
                Year = myCalendar.get(Calendar.YEAR);
                Month = myCalendar.get(Calendar.MONTH);
                Date = myCalendar.get(Calendar.DAY_OF_MONTH);
            }

            DatePickerDialog dialog = new DatePickerDialog(activity, date, Year, Month, Date);
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
            int Year = 0, Month = 0, Date = 0;
            String End_Date = end_date.getText().toString();
            if (End_Date.length() > 0) {
                String list[] = End_Date.split("-");
                if (list.length > 0 && list.length == 3) {
                    Year = Integer.valueOf(list[0]);
                    Month = Integer.valueOf(list[1]) - 1;
                    Date = Integer.valueOf(list[2]);
                }
            } else {
                Year = myCalendar.get(Calendar.YEAR);
                Month = myCalendar.get(Calendar.MONTH);
                Date = myCalendar.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog dialog = new DatePickerDialog(activity, date, Year, Month, Date);
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

            filter_shipping_handler.LoadFilterShipping(Start_Date, End_Date, Order_status_id, Group_id);
            progressDialog.dismiss();
            dismiss();
        });
        return view;
    }

    private void LoadGroup() {
        if (Constant.isNetworkAvailable()) {
            SpinnerOrderGroup spinnerOrderGroup = new SpinnerOrderGroup(Groups);
            sp_group.setAdapter(spinnerOrderGroup);

            sp_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Group_id = Groups[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } else {
            Constant.LoadNetworkError(getChildFragmentManager());
        }
    }

    private void LoadOrderStatus() {

        if (Constant.isNetworkAvailable()) {
            progressDialog.show();
            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
            Call<String> call = apiInterface.getOrderStatus(Constant.DataGetValue(activity, Constant.Token), language);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful()) {
                        progressDialog.dismiss();
                        OrderStatus = ContentJsonParser.getOrderStatus(response.body());
                        SpinnerOrderStatus spinnerOrderStatus = new SpinnerOrderStatus(OrderStatus);
                        sp_status.setAdapter(spinnerOrderStatus);
                        if (Order_Status != null) {
                            if (Order_Status.length() > 0) {
                                for (int i = 0; i < OrderStatus.size(); i++) {

                                    if (Order_Status.equals(OrderStatus.get(i).getOrder_ststus_id())) {
                                        sp_status.setSelection(i);
                                    }
                                }
                            }
                        }

                        sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Order_status_id = OrderStatus.get(position).getOrder_ststus_id();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                }
            });


        } else {
            Constant.LoadNetworkError(getChildFragmentManager());
        }

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
            //  Log.e("onCreateView: ", " " + year + " - " + ((monthOfYear)+1) + " - " + dayOfMonth);
            updateDate(year, ((monthOfYear) + 1), dayOfMonth, key);


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

    private class SpinnerOrderStatus extends BaseAdapter implements SpinnerAdapter {
        private final ArrayList<Order_Status> OrderStatus;

        SpinnerOrderStatus(ArrayList<Order_Status> order_status_list) {
            this.OrderStatus = order_status_list;
        }

        @Override
        public int getCount() {
            return OrderStatus.size();
        }

        @Override
        public Object getItem(int position) {
            return OrderStatus.get(position);
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
            txt.setText(OrderStatus.get(position).getName());
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(OrderStatus.get(position).getName());
            return txt;
        }
    }

    private class SpinnerOrderGroup extends BaseAdapter implements SpinnerAdapter {

        private final String[] GroupList;

        SpinnerOrderGroup(String[] groups) {
            this.GroupList = groups;
        }

        @Override
        public int getCount() {
            return GroupList.length;
        }

        @Override
        public Object getItem(int position) {
            return GroupList[position];
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
            txt.setText(GroupList[position]);
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(GroupList[position]);
            return txt;
        }
    }
}
