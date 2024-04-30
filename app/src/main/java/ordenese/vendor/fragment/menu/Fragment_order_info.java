package ordenese.vendor.fragment.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import ordenese.vendor.SunmiPrinterSDK.dataset.Printer_DataSet;
import ordenese.vendor.SunmiPrinterSDK.threadHelp.ThreadPoolManager;
import ordenese.vendor.SunmiPrinterSDK.utils.SunmiPrintHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.LoginPageHandler;
import ordenese.vendor.fragment.dialog.Dialog_Order_Status;
import ordenese.vendor.fragment.dialog.Dialog_cancel_status;
import ordenese.vendor.model.Delivery_product_details;
import ordenese.vendor.model.OptionValueModel;
import ordenese.vendor.model.Order_Info;
import ordenese.vendor.model.Order_Status;
import ordenese.vendor.model.Order_status_histories;
import ordenese.vendor.model.Order_total_list;


public class Fragment_order_info extends Fragment {

    private TextView tv_name, tv_phone, tv_customer_address, tv_customer_flat, tv_email, tv_order_id, tv_status, tv_order_date, tv_time,
            tv_order_type, tv_delivery_date_time, tv_delivery_type, tv_payment_type, tv_comment;
    LinearLayout linear_delivery_time, schedule_linear;
    ApiInterface apiInterface;
    Activity activity;
    private String Order_id, order_status_id;
    private Order_Info mOrder_Info;
    private RecyclerView rc_previous_order_status, rc_order_info_total_list, rc_order_product;
    private CardView OrderHistorycardView, OrderProductcardView;
    private ArrayList<Order_Status> mOrderstatusList;
    private ProgressBar progressBar;
    private LoginPageHandler loginPageHandler;

    private String Preparing_id = "", order_completed = "", order_ready_to_pick_up = "", order_picked = "";
    private TextView Preparing_tv, delay_tv, order_completed_tv, cancel_tv, ready_to_pick_up_tv, picked_tv, accept_tv, tv_schedule, restaurant_accept_;

    Button print_btn, connect_print_btn;
    String print_str = "";
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    private Boolean mIsPreparingStatusProcessing = false, mIsCompletedStatusProcessing = false, mIsDelayStatusProcessing = false, mIsCancelStatusProcessing = false,
            mIsReadyToPickUpStatusProcessing = false, mIsPickedStatusProcessing = false, mIsAcceptStatusProcessing = false;
    Bitmap bitmap_img_icon_banner;
    //mIsPreparingStatusProcessing,mIsCompletedStatusProcessing,mIsDelayStatusProcessing,
    //mIsCancelStatusProcessing,mIsReadyToPickUpStatusProcessing,mIsPickedStatusProcessing

    @Override
    public void onAttach(Context context) {

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Order_id = getArguments().getString("order_id");
            // Log.e("onCreate: ", Order_id + "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_info, container, false);

        tv_name = view.findViewById(R.id.tv_name);
        tv_email = view.findViewById(R.id.tv_email);
        tv_phone = view.findViewById(R.id.tv_phone);
        tv_schedule = view.findViewById(R.id.tv_schedule);
        schedule_linear = view.findViewById(R.id.schedule_linear);
        tv_customer_address = view.findViewById(R.id.tv_customer_address);
        tv_order_id = view.findViewById(R.id.tv_order_id);
        tv_status = view.findViewById(R.id.tv_status);
        tv_order_date = view.findViewById(R.id.tv_order_date);
        tv_time = view.findViewById(R.id.tv_time);
        tv_order_type = view.findViewById(R.id.tv_order_type);
        tv_delivery_type = view.findViewById(R.id.tv_delivery_type);
        tv_payment_type = view.findViewById(R.id.tv_payment_type);
        tv_comment = view.findViewById(R.id.tv_comment);
        tv_customer_flat = view.findViewById(R.id.tv_customer_flat);
        tv_delivery_date_time = view.findViewById(R.id.tv_delivery_date_time);
        linear_delivery_time = view.findViewById(R.id.linear_delivery_time);
        restaurant_accept_ = view.findViewById(R.id.order_status_accept_);

        ready_to_pick_up_tv = view.findViewById(R.id.tv_order_status_ready_to_pickup);
        //ready_to_pick_up_tv.setVisibility(View.GONE);
        picked_tv = view.findViewById(R.id.add_order_status_picked);
        picked_tv.setVisibility(View.GONE);

        Preparing_tv = view.findViewById(R.id.add_order_status_preparing);
        delay_tv = view.findViewById(R.id.tv_order_delay);
        order_completed_tv = view.findViewById(R.id.tv_order_completed_status);
        order_completed_tv.setVisibility(View.GONE);
        cancel_tv = view.findViewById(R.id.tv_order_cancel);
        accept_tv = view.findViewById(R.id.order_status_accept);

        print_btn = view.findViewById(R.id.print_btn);
        connect_print_btn = view.findViewById(R.id.connect_print_btn);

        progressBar = view.findViewById(R.id.ld_status);
        OrderHistorycardView = view.findViewById(R.id.container);
        OrderProductcardView = view.findViewById(R.id.product_container);

        //mIsPreparingStatusProcessing,mIsCompletedStatusProcessing,mIsDelayStatusProcessing,
        //mIsCancelStatusProcessing,mIsReadyToPickUpStatusProcessing,mIsPickedStatusProcessing

        ready_to_pick_up_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //To avoid same button press at continuously in so many times ! :-
                if (!mIsReadyToPickUpStatusProcessing) {
                    mIsReadyToPickUpStatusProcessing = true;
//                    order_ready_to_pick_up = "5";
                    LoadUpdateStatus("5");
                }
            }
        });
        picked_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //To avoid same button press at continuously in so many times ! :-
                if (!mIsPickedStatusProcessing) {
                    mIsPickedStatusProcessing = true;
//                    order_picked = "8";
                    LoadUpdateStatus("8");
                }


            }
        });


        Preparing_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //To avoid same button press at continuously in so many times ! :-
                if (!mIsPreparingStatusProcessing) {
                    mIsPreparingStatusProcessing = true;
//                    Preparing_id = "3";
                    LoadUpdateStatus("3");
                }

            }
        });

        restaurant_accept_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsAcceptStatusProcessing) {
                    mIsAcceptStatusProcessing = true;
//                    accept_id = "2";
                    LoadUpdateStatus("2");
                }
            }
        });

        accept_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadUpdateStatus("2");
                accept_tv.setEnabled(false);
            }
        });

        order_completed_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To avoid same button press at continuously in so many times ! :-
                if (!mIsCompletedStatusProcessing) {
                    mIsCompletedStatusProcessing = true;
                    order_completed = "9";
                    LoadUpdateStatus("9");
                }
            }
        });

        delay_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel_status(getString(R.string.title_1));
            }
        });

        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancel_status(getString(R.string.title_2));
            }
        });

        rc_previous_order_status = view.findViewById(R.id.rc_previous_order_status);
        rc_order_info_total_list = view.findViewById(R.id.rc_order_info_total_list);
        rc_order_product = view.findViewById(R.id.rc_order_product);

        connect_print_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open bluetooth connection
                try {
                    findBT();
                    openBT();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // send data typed by the user to be printed
        print_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // bluetooth printer
                /* try {
                    sendData();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/

//                ArrayList<Printer_DataSet> dataList = test_temp(mOrder_Info);
                ArrayList<Printer_DataSet> dataList = PrepareSendData(mOrder_Info);
                try {
                    if (mOrder_Info != null) {
                        if (dataList != null) {
                            ThreadPoolManager.getInstance().executeTask(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i <= dataList.size() - 1; i++) {
                                        if (dataList.get(i).getNewline()) {
                                            PrintNewLine();
                                        } else if (dataList.get(i).getImageExist()) {
                                            PrintImage(dataList.get(i).getmBitmapImage());
//                                            PrintBitmapImage(dataList.get(i).getmBitmapImage());
                                        } else {
                                            PrintText(dataList.get(i).getPrint_Content(), dataList.get(i).getFontSize(), dataList.get(i).isBold, dataList.get(i).getUnderLine(), dataList.get(i).getFontName());
                                        }
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            break;
                                        }
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        LoadInfo();

    }

    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {

            // the text typed by the user
            String msg = print_str;
            msg += "\n";

            mmOutputStream.write(msg.getBytes());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(msg + "Printed successfully.");
            builder.create();
            builder.show();

            // tell the user data were sent
//            myLabel.setText("Data sent.");

        } catch (Exception e) {
            Constant.showToast(e.getMessage());
            e.printStackTrace();
        }
    }

    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
//                myLabel.setText("No bluetooth adapter available");
                Constant.showToast("No bluetooth adapter available");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
//                    if (device.getName().equals("RPP300")) {
                    if (device.getName().equals("00:11:22:33:44:55")) {
                        mmDevice = device;
                        Constant.showToast("Bluetooth device found.");
                        break;
                    }
                }
            }

//            myLabel.setText("Bluetooth device found.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            if (mmDevice != null) {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();

                beginListenForData();
                Constant.showToast("Bluetooth Opened");
            }
//            myLabel.setText("Bluetooth Opened");

        } catch (Exception e) {
            Constant.showToast(e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
//                                                myLabel.setText(data);
                                                Log.e("run: ", data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadInfo() {

        if (Constant.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            try {
                JSONObject object = new JSONObject();
                String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                object.put("order_id", Order_id);
                object.put("language_id", language);
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
                Call<String> call = apiInterface.getOrderInfo(Constant.DataGetValue(activity, Constant.Token), body);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            try {
                                JSONObject obj = new JSONObject(response.body());
                                if (!obj.isNull("error")) {
                                    JSONObject jsonObject = obj.getJSONObject("error");
                                    if (!jsonObject.isNull("message")) {
                                        Constant.showToast(jsonObject.getString("message"));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mOrder_Info = ContentJsonParser.getOrderInfo(response.body());
                            if (mOrder_Info != null) {

                                if (mOrder_Info.getVendor_type_id().equals("2")) {
                                    accept_tv.setVisibility(View.VISIBLE);
                                    Preparing_tv.setVisibility(View.GONE);
                                    restaurant_accept_.setVisibility(View.GONE);
                                    if (mOrder_Info.getOrder_status_id().equals("1")) {
                                        accept_tv.setBackgroundColor(getResources().getColor(R.color.colorAccent_orange));
                                        accept_tv.setEnabled(true);
                                    } else {
                                        accept_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                        accept_tv.setEnabled(false);
                                    }
                                } else {
                                    accept_tv.setVisibility(View.GONE);
                                    Preparing_tv.setVisibility(View.VISIBLE);
                                    restaurant_accept_.setVisibility(View.VISIBLE);
                                }

                                ready_to_pick_up_tv.setVisibility(View.VISIBLE);
                                delay_tv.setVisibility(View.VISIBLE);

                                if (!mOrder_Info.getOrder_type().isEmpty()) {
                                    if (mOrder_Info.getOrder_type().equals("2")) {
                                        //order_type_id = 2  is pickup.
                                        //Its pick up order type :-
                                        order_completed_tv.setVisibility(View.VISIBLE);
                                    } else {
                                        //order_type_id = 1  is delivery.
                                        //Its delivery order type :-
                                        //Because its handled by delivery app.
                                        order_completed_tv.setVisibility(View.GONE);
                                    }
                                } else {
                                    order_completed_tv.setVisibility(View.GONE);
                                }

                                if (mOrder_Info.getOrder_status_id().equals("2")) {
                                    // 2 - Accepted
                                    restaurant_accept_.setBackgroundColor(getResources().getColor(R.color.disable));
                                    restaurant_accept_.setEnabled(false);

                                    Preparing_tv.setBackgroundColor(getResources().getColor(R.color.colorAccent_orange));
                                    Preparing_tv.setEnabled(true);
                                }

                                if (mOrder_Info.getOrder_status_id().equals("3") || mOrder_Info.getOrder_status_id().equals("6")) {
                                    // 3 - Preparing , 6 - Assigned

                                    restaurant_accept_.setBackgroundColor(getResources().getColor(R.color.disable));
                                    restaurant_accept_.setEnabled(false);

                                    Preparing_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    Preparing_tv.setEnabled(false);

                                }
                                if (mOrder_Info.getOrder_status_id().equals("5")) {
                                    // 5 - Ready to pickup

                                    restaurant_accept_.setBackgroundColor(getResources().getColor(R.color.disable));
                                    restaurant_accept_.setEnabled(false);

                                    Preparing_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    Preparing_tv.setEnabled(false);

                                    ready_to_pick_up_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    ready_to_pick_up_tv.setEnabled(false);
                                }
                                if (mOrder_Info.getOrder_status_id().equals("8")) {
                                    //8 - Picked

                                    restaurant_accept_.setBackgroundColor(getResources().getColor(R.color.disable));
                                    restaurant_accept_.setEnabled(false);

                                    Preparing_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    Preparing_tv.setEnabled(false);

                                    ready_to_pick_up_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    ready_to_pick_up_tv.setEnabled(false);

                                    picked_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    picked_tv.setEnabled(false);

                                } else if (mOrder_Info.getOrder_status_id().equals("6")) {

                                    // 6 - accept ny driver

                                    restaurant_accept_.setBackgroundColor(getResources().getColor(R.color.disable));
                                    restaurant_accept_.setEnabled(false);

                                    Preparing_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    Preparing_tv.setEnabled(false);

                                    ready_to_pick_up_tv.setBackgroundColor(getResources().getColor(R.color.colorAccent_orange));
                                    ready_to_pick_up_tv.setEnabled(true);

                                    picked_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    picked_tv.setEnabled(false);

                                } else if (mOrder_Info.getOrder_status_id().equals("9") || mOrder_Info.getOrder_status_id().equals("4")) {

                                    // 9 - Completed , 4 - Cancelled

                                    restaurant_accept_.setBackgroundColor(getResources().getColor(R.color.disable));
                                    restaurant_accept_.setEnabled(false);

                                    Preparing_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    Preparing_tv.setEnabled(false);

                                    ready_to_pick_up_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    ready_to_pick_up_tv.setEnabled(false);

                                    picked_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    picked_tv.setEnabled(false);

                                    order_completed_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    order_completed_tv.setEnabled(false);

                                    delay_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    delay_tv.setEnabled(false);

                                    cancel_tv.setBackgroundColor(getResources().getColor(R.color.disable));
                                    cancel_tv.setEnabled(false);
                                } else if (mOrder_Info.getOrder_status_id().equals("1")) {
                                    // 1 - New
                                    restaurant_accept_.setBackgroundColor(getResources().getColor(R.color.colorAccent_orange));
                                    restaurant_accept_.setEnabled(true);
                                }

                                mIsReadyToPickUpStatusProcessing = false;
                                mIsPickedStatusProcessing = false;
                                mIsPreparingStatusProcessing = false;
                                mIsCompletedStatusProcessing = false;
                                mIsAcceptStatusProcessing = false;

                                // Customer details
                                tv_name.setText(" : " + mOrder_Info.getFirstname() + " " + mOrder_Info.getLastname());
                                tv_email.setText(activity.getResources().getString(R.string.order_email) + "  : " + mOrder_Info.getEmail());
                                tv_phone.setText(activity.getResources().getString(R.string.order_phone) + " : " + mOrder_Info.getPhone());
                                tv_customer_address.setText(activity.getResources().getString(R.string.reg_address) + " : " + mOrder_Info.getAddress());
                                tv_customer_flat.setText(activity.getResources().getString(R.string.flat_on) + " : " + mOrder_Info.getFlat_no());

                                if (mOrder_Info.getSchedule_status() != null && mOrder_Info.getSchedule_status().equals("1")) {
                                    schedule_linear.setVisibility(View.VISIBLE);
                                    tv_schedule.setText(" : " + mOrder_Info.getSchedule_date() + " " + mOrder_Info.getSchedule_time());
                                } else {
                                    schedule_linear.setVisibility(View.GONE);
                                }

                                order_status_id = mOrder_Info.getOrder_status_id();
                                print_str = mOrder_Info.getOrder_id();

                                // Customer details
                                tv_order_id.setText(" : " + mOrder_Info.getOrder_id());
                                tv_status.setText(" : " + mOrder_Info.getStatus());
                                tv_order_date.setText(" : " + mOrder_Info.getDate_added());
                                tv_time.setText(mOrder_Info.getTime_added());

                                if (mOrder_Info.getOrder_type().equals("2")) {
                                    tv_order_type.setText(" : " + getActivity().getResources().getString(R.string.reg_pickup));
                                } else {
                                    tv_order_type.setText(" : " + getActivity().getResources().getString(R.string.reg_delivery));
                                }

                                tv_delivery_type.setText(" : " + mOrder_Info.getDelivery_type());
                                tv_payment_type.setText(" : " + mOrder_Info.getPayment_type());

                                if (mOrder_Info.getDelivery_time().isEmpty()) {
                                    linear_delivery_time.setVisibility(View.GONE);
                                } else {
                                    if (mOrder_Info.getDelivery_type().equals(activity.getResources().getString(R.string.now))) {
                                        linear_delivery_time.setVisibility(View.GONE);
                                    } else {
                                        linear_delivery_time.setVisibility(View.VISIBLE);
                                        tv_delivery_date_time.setText(" : " + mOrder_Info.getDelivery_time());
                                    }
                                }
                                if (mOrder_Info.getComment() != null) {
                                    tv_comment.setText(" : " + mOrder_Info.getComment());
                                }

                                // total details
                                if (mOrder_Info.getOrder_total_lists() != null) {
                                    if (mOrder_Info.getOrder_total_lists().size() > 0) {
                                        rc_order_info_total_list.setLayoutManager(new LinearLayoutManager(activity));
                                        OrderTotalAdapter orderTotalAdapter = new OrderTotalAdapter(mOrder_Info.getOrder_total_lists());
                                        rc_order_info_total_list.setAdapter(orderTotalAdapter);
                                    }
                                }

                                //Product details
                                if (mOrder_Info.getDelivery_product_details() != null) {
                                    if (mOrder_Info.getDelivery_product_details().size() > 0) {
                                        OrderProductcardView.setVisibility(View.VISIBLE);
                                        rc_order_product.setLayoutManager(new LinearLayoutManager(activity));
                                        RecyclerViewDeliveryProduct recyclerViewDeliveryProduct = new RecyclerViewDeliveryProduct(mOrder_Info.getDelivery_product_details());
                                        rc_order_product.setAdapter(recyclerViewDeliveryProduct);
                                    } else {
                                        OrderProductcardView.setVisibility(View.GONE);
                                    }
                                } else {
                                    OrderProductcardView.setVisibility(View.GONE);
                                }

                                // history details
                                if (mOrder_Info.getOrder_status_histories() != null) {
                                    if (mOrder_Info.getOrder_status_histories().size() > 0) {
                                        OrderHistorycardView.setVisibility(View.VISIBLE);
                                        rc_previous_order_status.setLayoutManager(new LinearLayoutManager(activity));
                                        OrderStatusAdapter orderStatusAdapter = new OrderStatusAdapter(mOrder_Info.getOrder_status_histories());
                                        rc_previous_order_status.setAdapter(orderStatusAdapter);
                                    } else {
                                        OrderHistorycardView.setVisibility(View.GONE);
                                    }
                                } else {
                                    OrderHistorycardView.setVisibility(View.GONE);
                                }

                            } else {
                                mIsReadyToPickUpStatusProcessing = false;
                                mIsPickedStatusProcessing = false;
                                mIsPreparingStatusProcessing = false;
                                mIsAcceptStatusProcessing = false;
                                mIsCompletedStatusProcessing = false;
                            }
                        } else {
                            mIsReadyToPickUpStatusProcessing = false;
                            mIsPickedStatusProcessing = false;
                            mIsPreparingStatusProcessing = false;
                            mIsAcceptStatusProcessing = false;
                            mIsCompletedStatusProcessing = false;

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {


                        progressBar.setVisibility(View.GONE);

                        mIsReadyToPickUpStatusProcessing = false;
                        mIsPickedStatusProcessing = false;
                        mIsPreparingStatusProcessing = false;
                        mIsAcceptStatusProcessing = false;
                        mIsCompletedStatusProcessing = false;

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            progressBar.setVisibility(View.GONE);

            mIsReadyToPickUpStatusProcessing = false;
            mIsPickedStatusProcessing = false;
            mIsAcceptStatusProcessing = false;
            mIsPreparingStatusProcessing = false;
            mIsCompletedStatusProcessing = false;

            Constant.LoadNetworkError(getChildFragmentManager());
        }

    }

    class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.ViewHolderStatus> {
        private final ArrayList<Order_status_histories> Order_list;

        OrderStatusAdapter(ArrayList<Order_status_histories> order_status_histories) {
            this.Order_list = order_status_histories;
        }

        @NonNull
        @Override
        public ViewHolderStatus onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.order_status_list_adapter, parent, false);
            return new ViewHolderStatus(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderStatus holder, int position) {
            //holder.tv_comment.setText(Order_list.get(position).getComment());
            holder.tv_status.setText(Order_list.get(position).getStatus());
            holder.tv_date_added.setText(Order_list.get(position).getDate_added());

        }


        @Override
        public int getItemCount() {
            return Order_list.size();
        }

        class ViewHolderStatus extends RecyclerView.ViewHolder {
            TextView tv_date_added, tv_comment, tv_status;

            ViewHolderStatus(View itemView) {
                super(itemView);
                tv_date_added = itemView.findViewById(R.id.tv_date_added);
                tv_status = itemView.findViewById(R.id.tv_status);
                tv_comment = itemView.findViewById(R.id.tv_comment);
            }
        }
    }

    private void OrderStatus(ArrayList<Order_Status> mOrderStatusList) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("Order_Status", mOrderStatusList);
        bundle.putString("Order_id", Order_id);
        bundle.putString("order_status_id", order_status_id);
        Dialog_Order_Status dialog_order_status = new Dialog_Order_Status();
        dialog_order_status.setArguments(bundle);
        dialog_order_status.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog_order_status.show(getChildFragmentManager(), "Order_Status");

    }

    private void cancel_status(String title) {

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("Order_id", Order_id);
        Dialog_cancel_status dialog_order_status = new Dialog_cancel_status();
        dialog_order_status.setArguments(bundle);
        dialog_order_status.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog_order_status.show(getChildFragmentManager(), "Order_Status");
    }

    private void LoadUpdateStatus(String order_status_id) {

        if (Constant.isNetworkAvailable()) {

            progressBar.setVisibility(View.VISIBLE);

            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("order_id", Order_id);
                jsonObject.put("order_status_id", order_status_id);
                jsonObject.put("comment", "");
                jsonObject.put("notify", "true");
                jsonObject.put("language_id", language);

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Call<String> call = apiInterface.UpdateOrderStatus(Constant.DataGetValue(activity, Constant.Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {

                            try {
                                JSONObject obj = new JSONObject(response.body());
                                if (!obj.isNull("error")) {
                                    JSONObject jsonObject = obj.getJSONObject("error");
                                    if (!jsonObject.isNull("message")) {
                                        Constant.showToast(jsonObject.getString("message"));
                                        mIsReadyToPickUpStatusProcessing = false;
                                        mIsPickedStatusProcessing = false;
                                        mIsPreparingStatusProcessing = false;
                                        mIsAcceptStatusProcessing = false;
                                        mIsCompletedStatusProcessing = false;
                                        accept_tv.setEnabled(true);
                                    }
                                } else {
                                    LoadInfo();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressBar.setVisibility(View.GONE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            mIsReadyToPickUpStatusProcessing = false;
                            mIsPickedStatusProcessing = false;
                            mIsAcceptStatusProcessing = false;
                            mIsPreparingStatusProcessing = false;
                            mIsCompletedStatusProcessing = false;
                            accept_tv.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        mIsReadyToPickUpStatusProcessing = false;
                        mIsPickedStatusProcessing = false;
                        mIsPreparingStatusProcessing = false;
                        mIsCompletedStatusProcessing = false;
                        mIsAcceptStatusProcessing = false;
                        accept_tv.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (JSONException e) {
                mIsReadyToPickUpStatusProcessing = false;
                mIsPickedStatusProcessing = false;
                mIsPreparingStatusProcessing = false;
                mIsCompletedStatusProcessing = false;
                mIsAcceptStatusProcessing = false;
                accept_tv.setEnabled(true);
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }


        } else {
            mIsReadyToPickUpStatusProcessing = false;
            mIsPickedStatusProcessing = false;
            mIsPreparingStatusProcessing = false;
            mIsCompletedStatusProcessing = false;
            mIsAcceptStatusProcessing = false;
            accept_tv.setEnabled(true);
            Constant.LoadNetworkError(getChildFragmentManager());
        }
    }

    class OrderTotalAdapter extends RecyclerView.Adapter<OrderTotalAdapter.ViewHolderTotal> {
        private final ArrayList<Order_total_list> OrderTotalList;

        OrderTotalAdapter(ArrayList<Order_total_list> order_total_lists) {
            this.OrderTotalList = order_total_lists;
        }

        @NonNull
        @Override
        public ViewHolderTotal onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.fragment_order_total_list_adapter, parent, false);
            return new ViewHolderTotal(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderTotal holder, int position) {
            holder.title.setText(OrderTotalList.get(position).getTitle());
            holder.value.setText(" : " + OrderTotalList.get(position).getText());
        }


        @Override
        public int getItemCount() {
            return OrderTotalList.size();
        }

        class ViewHolderTotal extends RecyclerView.ViewHolder {
            TextView title, value;
            ViewHolderTotal(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.total_title);
                value = itemView.findViewById(R.id.total_value);
            }
        }
    }
    class RecyclerViewDeliveryProduct extends RecyclerView.Adapter<RecyclerViewDeliveryProduct.ViewHolder> {
        private final ArrayList<Delivery_product_details> Product_list;
        private ArrayList<OptionValueModel> optionValueModels;
        String options = "";
        RecyclerViewDeliveryProduct(ArrayList<Delivery_product_details> deliveryProductDetails) {
            this.Product_list = deliveryProductDetails;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.fragment_order_product_adapter, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (Product_list.get(position).getImage() != null) {
                if (Product_list.get(position).getImage().length() > 0) {
                    Picasso.with(activity).load(Product_list.get(position).getImage()).into(holder.imageView);
                } else {
                    Picasso.with(activity).load(R.drawable.no_image).into(holder.imageView);
                }
            } else {
                Picasso.with(activity).load(R.drawable.no_image).into(holder.imageView);
            }
            optionValueModels = Product_list.get(position).getOptionValueModels();
            options = "";
            if (optionValueModels != null && optionValueModels.size() > 0) {
                for (int i = 0; i < optionValueModels.size(); i++) {
                    if (i == 0) {
                        options = optionValueModels.get(i).getValue();
                    } else {
                        options = options + " ," + optionValueModels.get(i).getValue();
                    }
                }
                Spanned product = Html.fromHtml(Product_list.get(position).getName().toUpperCase() + "<br/>" + options);

                holder.product.setText(String.valueOf(product));
            } else {
                Spanned product = Html.fromHtml(Product_list.get(position).getName());
                holder.product.setText(String.valueOf(product));
            }
            holder.quantity.setText(Product_list.get(position).getQuantity() + " x ");
            holder.total.setText(Product_list.get(position).getTotal());
        }

        @Override
        public int getItemCount() {
            return Product_list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CircleImageView imageView;
            TextView product, quantity, total;
            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_details);
                product = itemView.findViewById(R.id.tv_product);
                quantity = itemView.findViewById(R.id.tv_quantity);
                total = itemView.findViewById(R.id.tv_total);
            }
        }
    }
    public Bitmap createBitmap() {

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setAntiAlias(true);
        p.setFilterBitmap(true);
        p.setDither(true);
        p.setColor(Color.WHITE);

        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
//        Bitmap bitmap = Bitmap.createBitmap(rectImage.width() * 2,
//                rectImage.height() * 2, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
//      c.drawColor(Color.RED);
        c.drawRect(30, 80, 30, 80, p);
//        c.drawRect(rectImage.left, rectImage.top, rectImage.right,
//                rectImage.bottom, p);
        return bitmap;


    }

    public ArrayList<Printer_DataSet> test_temp(Order_Info order_info) {
        Printer_DataSet imageDataSet = new Printer_DataSet();
        ArrayList<Printer_DataSet> data_List = new ArrayList<>();

        String vendor_name = "";
        try {
            JSONObject jsonObject = new JSONObject(Constant.DataGetValue(activity, Constant.StoreDetails));
            // Log.e("onCreate: ", jsonObject + "");
//            store_info.setText(Html.fromHtml(jsonObject.getString("vendor_name") + "<br>" + jsonObject.getString("email") + "<br>" + jsonObject.getString("mobile")));
            vendor_name = jsonObject.getString("vendor_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String fontName = "Sunmi monospace";
        //Initialize new line
        Printer_DataSet newLineDataSet = new Printer_DataSet();
        newLineDataSet.setNewline(true);
        data_List.add(newLineDataSet);
        //Set HeadingImage

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;
        if (bitmap_img_icon_banner == null) {
            bitmap_img_icon_banner = BitmapFactory.decodeResource(getResources(), R.drawable.logo_printer, options);
        }
//        bitmap_img_icon_banner = createBitmap();
        imageDataSet.setImageExist(true);
        imageDataSet.setmBitmapImage(bitmap_img_icon_banner);
        data_List.add(imageDataSet);

        Printer_DataSet newLineDataSet1 = new Printer_DataSet();
        newLineDataSet1.setNewline(true);
        data_List.add(newLineDataSet1);

        if (vendor_name != null && !vendor_name.isEmpty()) {
            data_List.add(getPackData("        " + vendor_name + "        " + "\n", "26", true, false, fontName));
        } else {
            data_List.add(getPackData("           " + activity.getResources().getString(R.string.app_name_in_print_receipt) + "            " + "\n", "26", true, false, fontName));
        }
        data_List.add(getPackData("         " + mOrder_Info.getDate_added() + "         " + "\n", "24", true, false, fontName));


        SunmiPrintHelper.getInstance().print1Line();
        SunmiPrintHelper.getInstance().cutpaper();

        return data_List;
    }

    public ArrayList<Printer_DataSet> PrepareSendData(Order_Info order_info) {

        String vendor_name = "";

        try {
            JSONObject jsonObject = new JSONObject(Constant.DataGetValue(activity, Constant.StoreDetails));
            // Log.e("onCreate: ", jsonObject + "");
//            store_info.setText(Html.fromHtml(jsonObject.getString("vendor_name") + "<br>" + jsonObject.getString("email") + "<br>" + jsonObject.getString("mobile")));
            vendor_name = jsonObject.getString("vendor_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String fontName = "Sunmi monospace";
        String fontSizeContent = "18";
        String fontSizeHeading = "21";

        Printer_DataSet imageDataSet = new Printer_DataSet();
        ArrayList<Printer_DataSet> data_List = new ArrayList<>();

        //Set HeadingImage

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;
        if (bitmap_img_icon_banner == null) {
            bitmap_img_icon_banner = BitmapFactory.decodeResource(getResources(), R.drawable.logo_printer, options);
        }
        imageDataSet.setImageExist(true);
        imageDataSet.setmBitmapImage(bitmap_img_icon_banner);
        data_List.add(imageDataSet);

        //Initialize new line
        Printer_DataSet newLineDataSet = new Printer_DataSet();
        newLineDataSet.setNewline(true);
        data_List.add(newLineDataSet);

        //Set Header Text
        //Printer Accept 41 character in single line
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = Calendar.getInstance().getTime();

        if (vendor_name != null && !vendor_name.isEmpty()) {
            data_List.add(getPackData(getCenterAlignPadding(vendor_name + "\n", 30), "26", true, false, fontName));
        } else {
            data_List.add(getPackData(getCenterAlignPadding(activity.getResources().getString(R.string.app_name_in_print_receipt) + "\n", 31), "26", true, false, fontName));
        }
        data_List.add(getPackData(getCenterAlignPadding(dateFormat.format(today) + "\n", 31), "24", true, false, fontName));

        data_List.add(getPackData("  ---------------------------------------\n", fontSizeContent, false, false, fontName));
        data_List.add(getPackData(getCenterAlignPadding(activity.getResources().getString(R.string.txt_Customer) + ":" + "\n", 30), "26", true, false, fontName));
        data_List.add(getPackData(getCenterAlignPadding(mOrder_Info.getFirstname() + "\n", 29), "26", true, false, fontName));
        data_List.add(getPackData(getCenterAlignPadding(mOrder_Info.getPhone() + "\n", 29), "26", true, false, fontName));

//        if (vendor_name != null && !vendor_name.isEmpty()) {
//            data_List.add(getPackData("        " + vendor_name + "        " + "\n", "26", true, false, fontName));
//        } else {
//            data_List.add(getPackData("           " + activity.getResources().getString(R.string.app_name_in_print_receipt) + "            " + "\n", "26", true, false, fontName));
//        }
//        data_List.add(getPackData("       " + mOrder_Info.getDate_added() + "         " + "\n", "24", true, false, fontName));
//
//        data_List.add(getPackData("   " + "---------------------------------------\n", fontSizeContent, false, false, fontName));
//        data_List.add(getPackData("           " + activity.getResources().getString(R.string.txt_Customer) + ":" + "           " + "\n", "26", true, false, fontName));
//        data_List.add(getPackData("           " + mOrder_Info.getFirstname() + "           " + "\n", "26", true, false, fontName));
//        data_List.add(getPackData("           " + mOrder_Info.getPhone() + "           " + "\n", "26", true, false, fontName));

        //Order Details Heading
        data_List.add(getPackData("  ----------------------------------------\n", fontSizeContent, false, false, fontName));
        data_List.add(getPackData("  " + activity.getResources().getString(R.string.order_details) + "\n", fontSizeHeading, true, false, fontName));
        data_List.add(getPackData("  ----------------------------------------", fontSizeContent, false, false, fontName));

        //ForNew Line
        data_List.add(newLineDataSet);

        //Order Details Content
//        data_List.add(getPackData("  " + getPadding(activity.getResources().getString(R.string.report_order_id), 15) + " : " + getCenterAlignPadding(mOrder_Info.getOrder_id() + "\n", 23), fontSizeContent, false, false, fontName));
        data_List.add(getPackData("  " + getPadding(activity.getResources().getString(R.string.report_order_id), 15) + " : " + getCenterAlignPadding(mOrder_Info.getOrder_id() + "\n", 20), fontSizeContent, false, false, fontName));
        data_List.add(getPackData("  " + getPadding(activity.getResources().getString(R.string.order_status), 15) + " : " + getCenterAlignPadding(mOrder_Info.getStatus() + "\n", 20), fontSizeContent, false, false, fontName));
        data_List.add(getPackData("  " + getPadding(activity.getResources().getString(R.string.order_date), 15) + " : " + getCenterAlignPadding(mOrder_Info.getDate_added() + "\n", 20), fontSizeContent, false, false, fontName));
        data_List.add(getPackData("  " + getPadding(activity.getResources().getString(R.string.order_order_type), 15) + " : " + getCenterAlignPadding(mOrder_Info.getOrder_type() + "\n", 20), fontSizeContent, false, false, fontName));
        data_List.add(getPackData("  " + getPadding(activity.getResources().getString(R.string.order_type), 15) + " : " + getCenterAlignPadding(mOrder_Info.getDelivery_type() + "\n", 20), fontSizeContent, false, false, fontName));
        data_List.add(getPackData("  " + getPadding(activity.getResources().getString(R.string.order_payment_type), 15) + " : " + getCenterAlignPadding(mOrder_Info.getPayment_type() + "\n", 20), fontSizeContent, false, false, fontName));
        data_List.add(getPackData("  " + getPadding(activity.getResources().getString(R.string.order_cmt), 15) + " : " + getCenterAlignPadding(mOrder_Info.getComment() + "", 20), fontSizeContent, false, false, fontName));


        //ForNew Line
        data_List.add(newLineDataSet);

        //Bill Details Heading
        data_List.add(getPackData("  " + "----------------------------------------\n", fontSizeContent, false, false, fontName));
        data_List.add(getPackData("  " + activity.getResources().getString(R.string.txt_bill_details) + "       " + activity.getResources().getString(R.string.text_product_qty) + "     " + activity.getResources().getString(R.string.order_total) + "\n", fontSizeHeading, true, false, fontName));
        data_List.add(getPackData("  " + "----------------------------------------", fontSizeContent, false, false, fontName));

        //NewLine
        data_List.add(newLineDataSet);

        //Bill Details Content
        for (int i = 0; i <= order_info.getDelivery_product_details().size() - 1; i++) {
            data_List.add(getPackData("  " + getBilldetailwithAlign(order_info.getDelivery_product_details().get(i).getName(), order_info.getDelivery_product_details().get(i).getQuantity(), order_info.getDelivery_product_details().get(i).getTotal()) + "", fontSizeContent, true, false, fontName));
            data_List.add(getPackData("  " + getBilldetailwithAlign("Toppings", "", order_info.getDelivery_product_details().get(i).getOptionValueModels().get(i).getValue()) + "", fontSizeContent, true, false, fontName));
        }
        for (int j =0; j< order_info.getDelivery_product_details().size();j++){
            for(int k =0; k<order_info.getDelivery_product_details().get(j).getOptionValueModels().size(); k++){
                Log.e(j+"toppings", "PrepareSendData: "+order_info.getDelivery_product_details().get(j).getOptionValueModels().get(k).getValue()+j+k+order_info.getDelivery_product_details().size());
            }
        }
        //NewLine
        //data_List.add(newLineDataSet);

        //Total Heading
        data_List.add(getPackData("  " + "----------------------------------------\n", fontSizeContent, false, false, fontName));
        data_List.add(getPackData("  " + activity.getResources().getString(R.string.order_total) + "                    " + activity.getResources().getString(R.string.order_price) + "\n", fontSizeHeading, true, false, fontName));
        data_List.add(getPackData("  " + "----------------------------------------", fontSizeContent, false, false, fontName));

        //ForNew Line
        data_List.add(newLineDataSet);

        //Total Content
        for (int i = 0; i <= order_info.getOrder_total_lists().size() - 1; i++) {
            if (i == order_info.getOrder_total_lists().size() - 1) {
                data_List.add(getPackData("  " + getPadding(order_info.getOrder_total_lists().get(i).getTitle(), 17) + " : " + getCenterAlignPadding(order_info.getOrder_total_lists().get(i).getText() + "\n", 17), "20", true, false, fontName));
            } else {
                data_List.add(getPackData("  " + getPadding(order_info.getOrder_total_lists().get(i).getTitle(), 17) + " : " + getCenterAlignPadding(order_info.getOrder_total_lists().get(i).getText() + "\n", 16), "20", true, false, fontName));
            }
        }
//        for (int i = 0; i <= order_info.getOrder_total_lists().size() - 1; i++) {
//            if (i == order_info.getOrder_total_lists().size() - 1) {
//                data_List.add(getPackData("  " + getPadding(order_info.getOrder_total_lists().get(i).getTitle(), 19) + " : " + getCenterAlignPadding(order_info.getOrder_total_lists().get(i).getText() + "\n", 18), fontSizeContent, true, false, fontName));
//            } else {
//                data_List.add(getPackData("  " + getPadding(order_info.getOrder_total_lists().get(i).getTitle(), 19) + " : " + getCenterAlignPadding(order_info.getOrder_total_lists().get(i).getText() + "\n", 18), fontSizeContent, false, false, fontName));
//            }
//
//        }

        //NewLine
        data_List.add(newLineDataSet);

        data_List.add(getPackData("                  " + activity.getResources().getString(R.string.printer_text_thank_you) + "               " + "", fontSizeContent, true, false, fontName));

        //NewLine
        data_List.add(newLineDataSet);

        data_List.add(getPackData("   " + "---------------------------------------\n", fontSizeContent, false, false, fontName));

        //NewLine
        data_List.add(newLineDataSet);
        data_List.add(newLineDataSet);

        SunmiPrintHelper.getInstance().print1Line();
        SunmiPrintHelper.getInstance().cutpaper();

        return data_List;

    }

    public ArrayList<Printer_DataSet> PrepareSendDataTemp(Order_Info order_info) {

        String vendor_name = "";

        try {
            JSONObject jsonObject = new JSONObject(Constant.DataGetValue(activity, Constant.StoreDetails));
            // Log.e("onCreate: ", jsonObject + "");
//            store_info.setText(Html.fromHtml(jsonObject.getString("vendor_name") + "<br>" + jsonObject.getString("email") + "<br>" + jsonObject.getString("mobile")));
            vendor_name = jsonObject.getString("vendor_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String fontName = "Sunmi monospace";
        String fontSizeContent = "18";
        String fontSizeHeading = "21";

        Printer_DataSet imageDataSet = new Printer_DataSet();
        ArrayList<Printer_DataSet> data_List = new ArrayList<>();

        //Set HeadingImage

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;
        if (bitmap_img_icon_banner == null) {
            bitmap_img_icon_banner = BitmapFactory.decodeResource(getResources(), R.drawable.logo_printer, options);
        }
        imageDataSet.setImageExist(true);
        imageDataSet.setmBitmapImage(bitmap_img_icon_banner);
        data_List.add(imageDataSet);

        //Initialize new line
        Printer_DataSet newLineDataSet = new Printer_DataSet();
        newLineDataSet.setNewline(true);
        data_List.add(newLineDataSet);

        for (int i = 0; i <= order_info.getOrder_total_lists().size() - 1; i++) {
            if (i == order_info.getOrder_total_lists().size() - 1) {
                data_List.add(getPackData("  " + getPadding(order_info.getOrder_total_lists().get(i).getTitle(), 17) + " : " + getCenterAlignPadding(order_info.getOrder_total_lists().get(i).getText() + "\n", 17), "20", true, false, fontName));
            } else {
                data_List.add(getPackData("  " + getPadding(order_info.getOrder_total_lists().get(i).getTitle(), 17) + " : " + getCenterAlignPadding(order_info.getOrder_total_lists().get(i).getText() + "\n", 16), "20", true, false, fontName));
            }
        }
        //ForNew Line
        data_List.add(newLineDataSet);

        //NewLine
        data_List.add(newLineDataSet);

        SunmiPrintHelper.getInstance().print1Line();
        SunmiPrintHelper.getInstance().cutpaper();

        return data_List;
    }

    public Printer_DataSet getPackData(String content, String fontSize, Boolean isBold, Boolean isUnderLine, String fontName) {

        Printer_DataSet printerDataSet = new Printer_DataSet();
        printerDataSet.setPrint_Content(content);
        printerDataSet.setFontSize(fontSize);
        printerDataSet.setBold(isBold);
        printerDataSet.setUnderLine(isUnderLine);
        printerDataSet.setFontName(fontName);

        return printerDataSet;
    }

    public String getPadding(String data, int paddingsize) {
        if (data.length() >= paddingsize) {
            return data;
        }
        for (int i = data.length() + 1; i <= paddingsize; i++) {
            data = data + " ";
        }
        return data;
    }

    public String getCenterAlignPadding(String data, int paddingsize) {
        String data0;
        if (data.length() >= paddingsize) {
            return data;
        }
        data0 = data;
        if (data.contains("\n")) {
            data = data.replace("\n", "");
        }
        int remaining_padding;
        remaining_padding = paddingsize - data.length();
        for (int i = 0; i < remaining_padding / 2; i++) {
            data = " " + data;
        }
        for (int i = 0; i < remaining_padding / 2; i++) {
            data = data + " ";
        }

        if (data0.contains("\n")) {
            return data + "\n";
        } else {
            return data;
        }
    }

    public String getBilldetailwithAlign(String productname, String qty, String total) {

        String compineddata = "";
        String additionaldata = "";
        String extra_length_productname = "", extra_length_qty = "", extra_length_total = "";

        if (productname.length() <= 22) {
            compineddata = compineddata + getPadding(productname, 22) + " ";
        } else {
            if (productname.length() > 22) {
                compineddata = compineddata + getPadding(productname.substring(0, 22), 22) + " ";

                additionaldata = additionaldata + getPadding(productname.substring(22, productname.length()), 22);
            }
        }

        if (qty.length() <= 3) {
            compineddata = compineddata + getCenterAlignPadding(qty, 3);
        } else {
            if (qty.length() > 3) {
                compineddata = compineddata + getCenterAlignPadding(qty.substring(0, 3), 3);

                additionaldata = additionaldata + getCenterAlignPadding(qty.substring(3, qty.length()), 3);
            }
        }

        if (total.length() <= 14) {
            compineddata = compineddata + " " + getCenterAlignPadding(total, 14);
        } else {
            if (total.length() > 14) {
                compineddata = compineddata + " " + getCenterAlignPadding(total.substring(0, 14), 14);

                additionaldata = additionaldata + getCenterAlignPadding(total.substring(14, total.length()), 14);
            }
        }

        compineddata = compineddata + "\n" + additionaldata;

        return compineddata;
    }

    public void PrintText(String content, String fontSize, Boolean isBold, Boolean isUnderLine, String fontname) {
        try {
            float size = Integer.parseInt(fontSize);
            SunmiPrintHelper.getInstance().printText(content, size, isBold, isUnderLine, fontname);
            //Log.e("Test",""+content);
        } catch (Exception e) {
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void PrintImage(Bitmap bitmap) {
        try {
            SunmiPrintHelper.getInstance().printBitmap(bitmap);
            //Log.e("Test",""+content);
        } catch (Exception e) {
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void PrintNewLine() {
        try {
            //SunmiPrintHelper.getInstance().printNewLine();
            PrintText("\n", "14", false, false, "Sunmi monospace");
        } catch (Exception e) {
            Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
