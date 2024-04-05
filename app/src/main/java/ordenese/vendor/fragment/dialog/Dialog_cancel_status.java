package ordenese.vendor.fragment.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiClientGson;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.LoginPageHandler;
import ordenese.vendor.model.DelayDataSet;
import ordenese.vendor.model.DelayList;
import ordenese.vendor.model.OrderCancelResason;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dialog_cancel_status extends DialogFragment {

    private RecyclerView reason_rec_list;
    private Activity activity;
    private String title, Order_id;
    private ApiInterface apiInterface;
    private String reason_txt = "", delay_mins = "";
    private TextView btn_cancel, btn_save;
    private LoginPageHandler loginPageHandler;
    private ProgressBar progressBar;

    private ArrayList<OrderCancelResason> orderCancelResasonsList;

    public Dialog_cancel_status() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            Order_id = getArguments().getString("Order_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_cancel_status, container, false);

        reason_rec_list = view.findViewById(R.id.reason_rec_list);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_save = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progressBar);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (title != null) {
                    if (title.equals(getString(R.string.title_1))) {

                        if (delay_mins.equals("") || delay_mins.isEmpty()) {
                            Constant.showToast(getString(R.string.delay_min));
                        } else {
                            load_update_delay();
                        }

                    } else {
                        if (reason_txt.equals("") || reason_txt.isEmpty()) {
                            Constant.showToast(getString(R.string.cancellation_reason));
                        } else {
                            LoadUpdateStatus();
                        }
                    }
                }
            }
        });
        btn_cancel.setOnClickListener(v -> dismiss());

        if (title != null) {
            if (title.equals(getString(R.string.title_1))) {
                load_delay();
            } else {
                load();
            }
        }

        return view;
    }

    private void load_delay() {

        if (Constant.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            apiInterface = ApiClientGson.getClient().create(ApiInterface.class);

            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
            JSONObject object = new JSONObject();
            try {
                object.put("language_id", language);

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                Call<DelayDataSet> call = apiInterface.delay_list(Constant.DataGetValue(activity, Constant.Token), body);
                call.enqueue(new Callback<DelayDataSet>() {
                    @Override
                    public void onResponse(@NonNull Call<DelayDataSet> call, @NonNull Response<DelayDataSet> response) {
                        if (response.isSuccessful()) {
                            DelayDataSet dataSet = response.body();
                            if (dataSet != null) {
                                reason_rec_list.setLayoutManager(new LinearLayoutManager(activity));
                                reason_rec_list.setAdapter(new DelayAdapter(dataSet.getDelayList()));
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DelayDataSet> call, @NonNull Throwable t) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Constant.LoadNetworkError(getChildFragmentManager());
        }


    }

    private void load() {

        if (Constant.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            apiInterface = ApiClient.getClient().create(ApiInterface.class);

            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
            JSONObject object = new JSONObject();
            try {
                object.put("language_id", language);

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                Call<String> call = apiInterface.getOrderCancel(Constant.DataGetValue(activity, Constant.Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            orderCancelResasonsList = ContentJsonParser.orderCancelResasonArrayList(response.body());

                            reason_rec_list.setLayoutManager(new LinearLayoutManager(activity));
                            reason_rec_list.setAdapter(new OrderStatusAdapter(orderCancelResasonsList));

                            progressBar.setVisibility(View.GONE);
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
            Constant.LoadNetworkError(getChildFragmentManager());
        }
    }

    private void load_update_delay() {

        if (Constant.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            apiInterface = ApiClient.getClient().create(ApiInterface.class);

            try {

                String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("delay", delay_mins);
                jsonObject.put("order_id", Order_id);
                jsonObject.put("language_id", language);

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Call<String> call = apiInterface.delay_update(Constant.DataGetValue(activity, Constant.Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            dismiss();
                            //Constant.showToast(getString(R.string.delay_min));
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
            Constant.LoadNetworkError(getChildFragmentManager());
        }
    }

    private void LoadUpdateStatus() {

        if (Constant.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("order_id", Order_id);
                jsonObject.put("order_status_id", "4");
                jsonObject.put("comment", reason_txt);
                jsonObject.put("notify", "true");
                jsonObject.put("language_id", language);

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Call<String> call = apiInterface.UpdateOrderStatus(Constant.DataGetValue(activity, Constant.Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            loginPageHandler.LoadOrderInfo(Order_id);
                            dismiss();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        loginPageHandler = (LoginPageHandler) context;
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
        getDialog().setTitle(title);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);

        if (getDialog() == null)
            return;

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.ViewHolderStatus> {

        private final ArrayList<OrderCancelResason> Order_list;
        TextView temp;


        OrderStatusAdapter(ArrayList<OrderCancelResason> order_status_histories) {
            this.Order_list = order_status_histories;
        }

        @NonNull
        @Override
        public ViewHolderStatus onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.cancel_reason_list, parent, false);
            return new ViewHolderStatus(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderStatus holder, int position) {

            holder.reason_text.setText(Order_list.get(position).getReason());

        }

        @Override
        public int getItemCount() {
            return Order_list.size();
        }

        class ViewHolderStatus extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView reason_text;

            ViewHolderStatus(View itemView) {
                super(itemView);
                reason_text = itemView.findViewById(R.id.reason_text);
                reason_text.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int id = view.getId();

                if (id == R.id.reason_text) {

                    if (temp == null) {
                        reason_text.setBackgroundColor(activity.getResources().getColor(R.color.grey_400));
                        temp = reason_text;
                    } else {
                        temp.setBackgroundColor(activity.getResources().getColor(R.color.white));
                        reason_text.setBackgroundColor(activity.getResources().getColor(R.color.grey_400));
                        temp = reason_text;
                    }

                    reason_txt = Order_list.get(getAdapterPosition()).getReason();
//                    delay_mins = Order_list.get(getAdapterPosition()).getReason();
                }

            }
        }
    }

    public class DelayAdapter extends RecyclerView.Adapter<DelayAdapter.ViewHolderStatus> {

        private final ArrayList<DelayList> Order_list;
        TextView temp;


        DelayAdapter(ArrayList<DelayList> order_status_histories) {
            this.Order_list = order_status_histories;
        }

        @NonNull
        @Override
        public ViewHolderStatus onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.cancel_reason_list, parent, false);
            return new ViewHolderStatus(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderStatus holder, int position) {
            holder.reason_text.setText(Order_list.get(position).getValue());
        }

        @Override
        public int getItemCount() {
            return Order_list.size();
        }

        class ViewHolderStatus extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView reason_text;

            ViewHolderStatus(View itemView) {
                super(itemView);
                reason_text = itemView.findViewById(R.id.reason_text);
                reason_text.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int id = view.getId();

                if (id == R.id.reason_text) {

                    if (temp == null) {
                        reason_text.setBackgroundColor(activity.getResources().getColor(R.color.grey_400));
                        temp = reason_text;
                    } else {
                        temp.setBackgroundColor(activity.getResources().getColor(R.color.white));
                        reason_text.setBackgroundColor(activity.getResources().getColor(R.color.grey_400));
                        temp = reason_text;
                    }
                    delay_mins = Order_list.get(getBindingAdapterPosition()).getKey();
                }

            }
        }
    }

}
