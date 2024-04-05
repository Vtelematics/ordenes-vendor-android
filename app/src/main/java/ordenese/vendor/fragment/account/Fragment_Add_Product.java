package ordenese.vendor.fragment.account;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.LoginPageHandler;
import ordenese.vendor.common.instant_transfer.Refresher;
import ordenese.vendor.common.instant_transfer.SectionPageHandler;
import ordenese.vendor.fragment.dialog.Dialog_ProductOptionWebView;
import ordenese.vendor.fragment.dialog.Dialog_Section;
import ordenese.vendor.fragment.dialog.Dialog_section_list;
import ordenese.vendor.model.LanguageModel;
import ordenese.vendor.model.Product_Image_DataSet;
import ordenese.vendor.model.Section;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Fragment_Add_Product extends DialogFragment implements SectionPageHandler {


    Activity activity;
    ApiInterface apiInterface;
    private ArrayList<LanguageModel> LanguageList;
    Spinner sp_status, sp_price, sp_quantity, sp_language;
    private String Status_id, Price_type_id, Quantity_type;
    private int status_id, price_type, quantity_type;
    String[] Status;
    String[] Price_Type;
    ScrollView scrollView;
    Button btn_submit;
    EditText price, discount_price, sort_order, ed_product_name, ed_description, ed_delivery_note, ed_item_note, tv_quantity;
    TextView tv_start_date, tv_end_date, error_product_name, error_price, add_section;
    private String key = "", section_Ids, mProduct_id, mType, Select_language_id;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    private final static int IMAGE = 1;
    private String PROFILE_IMAGE;
    private String PROFILE_PATH;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    ProgressDialog progressDialog;
    private ArrayList<Section> mSectionList = new ArrayList<>();
    SectionPageHandler sectionPageHandler;
    LoginPageHandler loginPageHandler;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private ArrayList<Integer> Select_Section_id = new ArrayList<>();
    private Refresher refresher;

    RadioButton product_type_veg, product_type_non_veg;

    private RecyclerView recycler_product_images;
    private RecyclerView.LayoutManager mProductImageListLayoutMgr;
    private ProductImageListAdapter productImageListAdapter;

    //ImageView im_image;
    //ImageButton ib_image;
    private ImageView IvProductImage;

    private Button BtnAdd;
    public ArrayList<Product_Image_DataSet> Product_image_list=new ArrayList<>();
    public int ImagePosition=-1;


    public Fragment_Add_Product(){

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        this.sectionPageHandler = this;
        this.loginPageHandler = (LoginPageHandler) context;
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
            mProduct_id = getArguments().getString("product_id");
            mType = getArguments().getString("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);


        Status = new String[]{getResources().getString(R.string.disabled), getResources().getString(R.string.enabled)};
        Price_Type = new String[]{getResources().getString(R.string.no), getResources().getString(R.string.yes)};

        scrollView = view.findViewById(R.id.scrollView);
        sp_status = view.findViewById(R.id.sp_status);
        sp_price = view.findViewById(R.id.sp_price);
        sp_quantity = view.findViewById(R.id.sp_quantity);
        sp_language = view.findViewById(R.id.sp_language);
        price = view.findViewById(R.id.ed_price);
        discount_price = view.findViewById(R.id.ed_discount_price);
        tv_start_date = view.findViewById(R.id.tv_start_date);
        tv_end_date = view.findViewById(R.id.tv_end_date);
        tv_quantity = view.findViewById(R.id.tv_quantity);
        sort_order = view.findViewById(R.id.ed_sort_order);
        ed_product_name = view.findViewById(R.id.ed_product_name);
        ed_description = view.findViewById(R.id.ed_description);
        ed_delivery_note = view.findViewById(R.id.ed_delivery_note);
        ed_item_note = view.findViewById(R.id.ed_item_note);
        error_product_name = view.findViewById(R.id.error_product_name);
        error_price = view.findViewById(R.id.error_price);
        //im_image = view.findViewById(R.id.im_image);
        //ib_image = view.findViewById(R.id.ib_image);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        recycler_product_images=view.findViewById(R.id.recycler_product_images);

        product_type_veg = view.findViewById(R.id.product_type_veg);
        product_type_non_veg = view.findViewById(R.id.product_type_non_veg);

        if (Constant.DataGetValue(activity, Constant.StoreType) != null) {

            if (Constant.DataGetValue(activity, Constant.StoreType).equals("3")) {
                product_type_veg.setVisibility(View.VISIBLE);
                product_type_non_veg.setVisibility(View.VISIBLE);
                product_type_veg.setChecked(true);
            } else if (Constant.DataGetValue(activity, Constant.StoreType).equals("2")) {
                product_type_veg.setVisibility(View.GONE);
                product_type_non_veg.setVisibility(View.VISIBLE);
                product_type_non_veg.setChecked(true);
            } else if (Constant.DataGetValue(activity, Constant.StoreType).equals("1")) {
                product_type_veg.setVisibility(View.VISIBLE);
                product_type_non_veg.setVisibility(View.GONE);
                product_type_veg.setChecked(true);
            } else {
                product_type_veg.setVisibility(View.GONE);
                product_type_non_veg.setVisibility(View.GONE);
            }
        }

        BtnAdd=view.findViewById(R.id.btn_add_image);
        BtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Product_Image_DataSet product_image_dataSet=new Product_Image_DataSet();
                product_image_dataSet.setLoadImagePath("https://www.e-order.ae/image/cache/no_image-150x150.png");
                product_image_dataSet.setEmpty(true);
                Product_image_list.add(product_image_dataSet);

                mProductImageListLayoutMgr = new LinearLayoutManager(getActivity());
                recycler_product_images.setLayoutManager(mProductImageListLayoutMgr);
                productImageListAdapter = new ProductImageListAdapter(Product_image_list);
                recycler_product_images.setAdapter(productImageListAdapter);

            }
        });

        LoadStatus();
        LoadPriceSelection();
        LoadQuantity();
        btn_submit = view.findViewById(R.id.btn_submit);
        LoadSectionList();
        add_section = view.findViewById(R.id.add_section);
        add_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSectionList != null) {

                    LoadDialogSection(mSectionList);
                } else {
                    Toast.makeText(activity, getResources().getString(R.string.no_section_list), Toast.LENGTH_SHORT).show();
                }

            }
        });
        tv_start_date.setOnClickListener(v -> {


            loadDate();
            int Year = 0, Month = 0, Date = 0;
            String Start_Date = tv_start_date.getText().toString();
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
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
//            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    if (which == DialogInterface.BUTTON_NEGATIVE) {
//                        tv_start_date.setText("");
//                    }
//                }
//            });
            key = "Start_Date";




        });




        tv_end_date.setOnClickListener((View v) -> {
            loadDate();
            int Year = 0, Month = 0, Date = 0;
            int y = 0, m = 0, d = 0;
            String End_Date = tv_end_date.getText().toString();
            if (End_Date.length() > 0) {
                String list[] = End_Date.split("-");
                if (list.length > 0 && list.length == 3) {
                    Year = Integer.valueOf(list[0]);
                    Month = Integer.valueOf(list[1]) - 1;
                    Date = Integer.valueOf(list[2]);
                }
            } else {

                String StartDate = tv_start_date.getText().toString();

                if (StartDate.length() == 0) {
                    Year = myCalendar.get(Calendar.YEAR);
                    Month = myCalendar.get(Calendar.MONTH);
                    Date = myCalendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    String list[] = StartDate.split("-");
                    if (list.length > 0 && list.length == 3) {
                        y = Integer.valueOf(list[0]);
                        m = Integer.valueOf(list[1]) - 1;
                        d = Integer.valueOf(list[2]);
                    } else {
                        y = myCalendar.get(Calendar.YEAR);
                        m = myCalendar.get(Calendar.MONTH);
                        d = myCalendar.get(Calendar.DAY_OF_MONTH);
                    }
                }

                /*Year = myCalendar.get(Calendar.YEAR);
                Month = myCalendar.get(Calendar.MONTH);
                Date = myCalendar.get(Calendar.DAY_OF_MONTH);*/
            }
            DatePickerDialog dialog = new DatePickerDialog(activity, date, Year, Month, Date);
            Calendar c = Calendar.getInstance();
            c.set(y, m, d);
            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
            dialog.show();
//            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    if (which == DialogInterface.BUTTON_NEGATIVE) {
//                        tv_end_date.setText("");
//                    }
//                }
//            });
            key = "End_Date";
        });
//        ib_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (hasPermissions(activity, PERMISSIONS)) {
//                    selectImage();
//                } else {
//                    requestPermission(IMAGE);
//                }
//            }
//        });

        if (mType.equals("Edit")) {
            LoadLanguage();
        }else{
            Product_Image_DataSet product_image_dataSet=new Product_Image_DataSet();
            product_image_dataSet.setLoadImagePath("https://www.e-order.ae/image/cache/no_image-150x150.png");
            product_image_dataSet.setEmpty(true);
            Product_image_list.add(product_image_dataSet);

            mProductImageListLayoutMgr = new LinearLayoutManager(getActivity());
            recycler_product_images.setLayoutManager(mProductImageListLayoutMgr);
            productImageListAdapter = new ProductImageListAdapter(Product_image_list);
            recycler_product_images.setAdapter(productImageListAdapter);
        }

        btn_submit.setOnClickListener(v -> {

            if (ed_product_name.getText().length() == 0) {
                ed_product_name.requestFocus();
                ed_product_name.setCursorVisible(true);
                error_product_name.setVisibility(View.VISIBLE);
                dialog_box();

            } else {
                if (ed_product_name.getText().length() == 2) {
                    ed_product_name.requestFocus();
                    ed_product_name.setCursorVisible(true);
                    error_product_name.setVisibility(View.VISIBLE);
                    dialog_box();
                } else {
                    error_product_name.setVisibility(View.GONE);

                    if (price.isEnabled()) {
                        if (price.getText().length() == 0) {
                            price.requestFocus();
                            price.setCursorVisible(true);
                            error_price.setVisibility(View.VISIBLE);
                            dialog_box();
                        } else {
                            error_price.setVisibility(View.GONE);

                            if (ed_product_name.getText().length() > 2) {
                                if (price.isEnabled()) {
                                    if (price.getText().length() > 0) {
                                        AddProduct();
                                    }
                                } else {
                                    AddProduct();
                                }

                            }
                        }
                    } else {
                        error_price.setVisibility(View.GONE);

                        if (ed_product_name.getText().length() > 2) {
                            if (price.isEnabled()) {
                                if (price.getText().length() > 0) {
                                    AddProduct();
                                }
                            } else {
                                AddProduct();
                            }

                        }

                    }

                }
            }

        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


//        mProductImageListLayoutMgr = new LinearLayoutManager(getActivity());
//        recycler_product_images.setLayoutManager(mProductImageListLayoutMgr);
//        productImageListAdapter = new ProductImageListAdapter(Product_image_list);
//        recycler_product_images.setAdapter(productImageListAdapter);

    }

    private void dialog_box() {

        AlertDialog.Builder alart = new AlertDialog.Builder(getContext());
        alart.setMessage(getResources().getString(R.string.required_fields_missing));
        alart.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alart.create();
        alart.show();

    }

    private void LoadSectionList() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getSectionList_(Constant.DataGetValue(activity, Constant.Token), 0, 0, language);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    mSectionList = ContentJsonParser.getCategoryList(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    private void LoadDialogSection(ArrayList<Section> mSectionList) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("sections_lists", mSectionList);
        Dialog_section_list dialog_section_list = new Dialog_section_list();
        dialog_section_list.setStyle(STYLE_NO_TITLE, 0);
        dialog_section_list.setSectionHandler((sectionPageHandler));
        dialog_section_list.setArguments(bundle);
        dialog_section_list.show(getChildFragmentManager(), "sections_lists");
    }

    private void AddProduct() {
        String p_name = ed_product_name.getText().toString();
        String p_description = ed_description.getText().toString();
        //  String p_delivery_note = ed_delivery_note.getText().toString();
        String p_item_note = ed_item_note.getText().toString();

        String start_date = tv_start_date.getText().toString();
        String end_date = tv_end_date.getText().toString();

        String p_price = price.getText().toString();
        String p_discount_price = discount_price.getText().toString();
        String p_sort_order = sort_order.getText().toString();

        String quantity = tv_quantity.getText().toString();

        String product_type;

        if (product_type_veg.isChecked()) {
            product_type = "1";
        } else if (product_type_non_veg.isChecked()) {
            product_type = "2";
        } else {
            product_type = "0";
        }

        JSONObject selectSectionIds = new JSONObject();
        if (section_Ids != null) {
            String[] Section_Ids = section_Ids.split(",");

            if (Section_Ids.length > 0) {
                for (int i = 0; i < Section_Ids.length; i++) {
                    try {
                        selectSectionIds.put(String.valueOf(i), Section_Ids[i]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        String StoreId = Constant.DataGetValue(activity, Constant.StoreId);
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();

        if (!mType.equals("Edit")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", p_name);
                jsonObject.put("description", p_description);
                //  jsonObject.put("delivery_note", p_delivery_note);
                jsonObject.put("item_note", p_item_note);
                jsonObject.put("price_selection", price_type);
                jsonObject.put("price", p_price);
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("price", p_discount_price);
                jsonObject1.put("date_start", start_date);
                jsonObject1.put("date_end", end_date);
                jsonObject.put("product_special", jsonObject1);
                jsonObject.put("quantity_required", quantity_type);
                jsonObject.put("quantity", quantity);
                jsonObject.put("status", status_id);
                jsonObject.put("sort_order", p_sort_order);


                //jsonObject.put("image", PROFILE_PATH);


                if(Product_image_list!=null){
                    JSONArray imageArray = new JSONArray();
                    for(int i=0;i<Product_image_list.size();i++){

                        if(Product_image_list.get(i).getEmpty()){
                            continue;
                        }

                        JSONObject image = new JSONObject();
                        try {
                            image.put("image", Product_image_list.get(i).getServerImagePath());
                            image.put("sort_order", Product_image_list.get(i).getSortOrder());

                            imageArray.put(image);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    jsonObject.put("images", imageArray);
                }


                jsonObject.put("product_category", selectSectionIds);
                jsonObject.put("language_id", language);
                jsonObject.put("product_type", product_type);
                // Log.e("onCreateView: ", "" + jsonObject.toString());

                progressDialog.show();
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Call<String> call = apiInterface.AddProduct(Constant.DataGetValue(activity, Constant.Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject1 = new JSONObject(response.body());
//                                JSONObject jsonObject2 = jsonObject1.getJSONObject("success");
//                                Constant.showToast(jsonObject2.getString("message"));
//                                loginPageHandler.LoadProducts();

                                Dialog_ProductOptionWebView dialog_productOptionWebView = new Dialog_ProductOptionWebView();
                                Bundle bundle = new Bundle();
                                bundle.putInt("PRODUCT_ID", Integer.parseInt(jsonObject1.getString("product_id")));
                                bundle.putInt("price_selection", price_type);
                                bundle.putString("Store_id", StoreId);
                                dialog_productOptionWebView.setArguments(bundle);
                                dialog_productOptionWebView.setProductRefresher(refresher);
                                dialog_productOptionWebView.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                                dialog_productOptionWebView.show(activity.getFragmentManager(), "EditProduct");
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
                                        progressDialog.dismiss();
                                        Constant.showToast(jsonErrorObject.getString("message"));
                                    } else {
                                        progressDialog.dismiss();
                                        Constant.showToast(getString(R.string.error));
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Constant.showToast(getString(R.string.error));
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Constant.showToast(getString(R.string.error));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressDialog.dismiss();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("product_id", mProduct_id);
                jsonObject.put("name", p_name);
                jsonObject.put("description", p_description);
                //  jsonObject.put("delivery_note", p_delivery_note);
                jsonObject.put("item_note", p_item_note);
                jsonObject.put("price_selection", price_type);
                jsonObject.put("price", p_price);
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("price", p_discount_price);
                jsonObject1.put("date_start", start_date);
                jsonObject1.put("date_end", end_date);
                jsonObject.put("product_special", jsonObject1);
                jsonObject.put("quantity_required", quantity_type);
                jsonObject.put("quantity", quantity);
                jsonObject.put("status", status_id);
                jsonObject.put("sort_order", p_sort_order);



                //jsonObject.put("image", PROFILE_PATH);

                if(Product_image_list!=null){
                    JSONArray imageArray = new JSONArray();
                    for(int i=0;i<Product_image_list.size();i++){

                        if(Product_image_list.get(i).getEmpty()){
                            continue;
                        }

                        JSONObject image = new JSONObject();
                        try {
                            if(Product_image_list.get(i).getServerImagePath()!=null){
                                image.put("image", Product_image_list.get(i).getServerImagePath());
                            }else{
                                String str[]=Product_image_list.get(i).getLoadImagePath().split("/");
                                if(str.length>0){
                                    image.put("image",str[str.length-2]+"/"+str[str.length-1]);
                                }
                            }
                            image.put("sort_order", Product_image_list.get(i).getSortOrder());

                            imageArray.put(image);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    jsonObject.put("images", imageArray);
                }



                jsonObject.put("product_category", selectSectionIds);
                jsonObject.put("language_id", language);
                jsonObject.put("product_type", product_type);

                // Log.e("onCreateView: ", "" + jsonObject.toString());

                progressDialog.show();
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Call<String> call = apiInterface.EditProduct(Constant.DataGetValue(activity, Constant.Token), body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();

//                                JSONObject jsonObject1 = new JSONObject(response.body());
//                                JSONObject jsonObject2 = jsonObject1.getJSONObject("success");
//                                Constant.showToast(jsonObject2.getString("message"));
                            Dialog_ProductOptionWebView dialog_productOptionWebView = new Dialog_ProductOptionWebView();
                            Bundle bundle = new Bundle();
                            bundle.putInt("PRODUCT_ID", Integer.parseInt(mProduct_id));
                            bundle.putInt("price_selection", price_type);
                            bundle.putString("Store_id", StoreId);
                            dialog_productOptionWebView.setArguments(bundle);
                            dialog_productOptionWebView.setProductRefresher(refresher);
                            dialog_productOptionWebView.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                            dialog_productOptionWebView.show(activity.getFragmentManager(), "EditProduct");
                            dismiss();

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
                                        progressDialog.dismiss();
                                        Constant.showToast(jsonErrorObject.getString("message"));
                                    } else {
                                        progressDialog.dismiss();
                                        Constant.showToast(getString(R.string.error));
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Constant.showToast(getString(R.string.error));
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Constant.showToast(getString(R.string.error));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressDialog.dismiss();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    private void LoadLanguage() {

        Call<String> call = apiInterface.getLanguage(Constant.current_language_id(), Constant.current_language_code());
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    LanguageList = ContentJsonParser.getLanguage(response.body());
                    if (LanguageList != null) {
                        if (LanguageList.size() > 0) {
                            LanguageAdapter languageAdapter = new LanguageAdapter(LanguageList);
                            sp_language.setAdapter(languageAdapter);
                            sp_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Select_language_id = LanguageList.get(position).getLanguage_id();
                                    LoadProductInfo(Select_language_id);

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });


                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }


    private void LoadStatus() {
        SpinnerStatusAdapter spinnerStatusAdapter = new SpinnerStatusAdapter(Status);
        sp_status.setAdapter(spinnerStatusAdapter);

        sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Status_id = Status[position];
                if (Status_id.equals(getResources().getString(R.string.enabled))) {
                    status_id = 1;
                } else {
                    status_id = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void LoadAddSection(String id, String type) {
        add_section.setText(type);
        section_Ids = id;

    }

    @Override
    public void refresher() {
        LoadSectionList();
    }

    @Override
    public void LoadSectionListPage() {
        if (mSectionList != null) {

            Dialog_Section dialog_section = new Dialog_Section();
            dialog_section.setStyle(STYLE_NO_TITLE, 0);
            if (sectionPageHandler != null) {
                dialog_section.setAddSectionInterface(sectionPageHandler);
            }
            Bundle bundle = new Bundle();
            bundle.putString("section_id", "");
            bundle.putString("section_type", "");
            dialog_section.setArguments(bundle);
            dialog_section.show(getChildFragmentManager(), "Add Section");

        }
    }

    private class SpinnerStatusAdapter extends BaseAdapter implements SpinnerAdapter {

        private final String[] Status;

        SpinnerStatusAdapter(String[] status) {
            this.Status = status;
        }

        @Override
        public int getCount() {
            return Status.length;
        }

        @Override
        public Object getItem(int position) {
            return Status[position];
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
            txt.setText(Status[position]);
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(Status[position]);
            return txt;
        }
    }

    private void LoadPriceSelection() {
        PriceSelectionAdapter priceSelectionAdapter = new PriceSelectionAdapter(Price_Type);
        sp_price.setAdapter(priceSelectionAdapter);

        sp_price.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Price_type_id = Price_Type[position];
                if (Price_type_id.equals(getResources().getString(R.string.yes))) {
                    price_type = 1;
                    price.setText("");
                    price.setEnabled(false);
                    discount_price.setText("");
                    discount_price.setEnabled(false);
                    tv_start_date.setEnabled(false);
                    tv_start_date.setText("");
                    tv_start_date.setBackgroundColor(getResources().getColor(R.color.grey_400));
                    tv_end_date.setText("");
                    tv_end_date.setEnabled(false);
                    tv_end_date.setBackgroundColor(getResources().getColor(R.color.grey_400));
                } else {
                    price_type = 0;
                    price.setEnabled(true);
                    discount_price.setEnabled(true);
                    tv_start_date.setEnabled(true);
                    tv_end_date.setEnabled(true);
                    tv_start_date.setBackground(getResources().getDrawable(R.drawable.draw_spinner_bg));
                    tv_end_date.setBackground(getResources().getDrawable(R.drawable.draw_spinner_bg));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private class PriceSelectionAdapter extends BaseAdapter implements SpinnerAdapter {


        private final String[] Price_Type;

        PriceSelectionAdapter(String[] price_type) {
            this.Price_Type = price_type;
        }

        @Override
        public int getCount() {
            return Price_Type.length;
        }

        @Override
        public Object getItem(int position) {
            return Price_Type[position];
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
            txt.setText(Price_Type[position]);
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(Price_Type[position]);
            return txt;
        }
    }

    private void LoadQuantity() {
        QuantitySelectionAdapter quantitySelectionAdapter = new QuantitySelectionAdapter(Price_Type);
        sp_quantity.setAdapter(quantitySelectionAdapter);

        sp_quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Quantity_type = Price_Type[position];
                if (Quantity_type.equals(getResources().getString(R.string.yes))) {
                    quantity_type = 1;
                    tv_quantity.setEnabled(true);
                    tv_quantity.setBackgroundColor(getResources().getColor(android.R.color.white));
                    tv_quantity.setBackground(getResources().getDrawable(R.drawable.draw_spinner_bg));
                } else {
                    quantity_type = 0;
                    tv_quantity.setEnabled(false);
                    tv_quantity.setBackgroundColor(getResources().getColor(R.color.grey_400));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private class QuantitySelectionAdapter extends BaseAdapter implements SpinnerAdapter {


        private final String[] Price_Type;

        QuantitySelectionAdapter(String[] price_type) {
            this.Price_Type = price_type;
        }

        @Override
        public int getCount() {
            return Price_Type.length;
        }

        @Override
        public Object getItem(int position) {
            return Price_Type[position];
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
            txt.setText(Price_Type[position]);
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(Price_Type[position]);
            return txt;
        }
    }

    private void LoadProductInfo(String select_language_id) {

        progressDialog.show();
        Call<String> call = apiInterface.ProductInfo(Constant.DataGetValue(activity, Constant.Token), mProduct_id, select_language_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject product_details = jsonObject.getJSONObject("product");
                        ed_product_name.setText(product_details.getString("name"));
                        ed_description.setText(product_details.getString("description"));
                        //  ed_delivery_note.setText(product_details.getString("delivery_note"));
                        ed_item_note.setText(product_details.getString("item_note"));

                        String Price = product_details.getString("price_selection");
                        if (Price.equals("0")) {
                            price.setText(product_details.getString("price"));

                            Object json = new JSONTokener(product_details.get("product_special").toString()).nextValue();
                            if (json instanceof JSONObject) {

                                JSONObject product_special = product_details.getJSONObject("product_special");
                                discount_price.setText(product_special.getString("price"));
                                tv_start_date.setText(product_special.getString("date_start"));
                                tv_end_date.setText(product_special.getString("date_end"));

                            } else if (json instanceof JSONArray) {

                            }
                        }
                        String sectionStatus = product_details.getString("status");
                        String Quantity = product_details.getString("quantity_required");

                        if (product_details.getString("product_type") != null) {

                            if (product_details.getString("product_type").equals("1")) {
                                product_type_veg.setChecked(true);
                            } else if (product_details.getString("product_type").equals("2")) {
                                product_type_non_veg.setChecked(true);
                            }
                        }

                        if (sectionStatus.equals("1")) {
                            sp_status.setSelection(1);
                        } else {
                            sp_status.setSelection(0);
                        }

                        if (Price.equals("1")) {
                            sp_price.setSelection(1);
                        } else {
                            sp_price.setSelection(0);
                        }

                        if (Quantity.equals("1")) {
                            sp_quantity.setSelection(1);
                        } else {
                            sp_quantity.setSelection(0);
                        }

                        tv_quantity.setText(product_details.getString("quantity"));

                        PROFILE_PATH = product_details.getString("image");

                        Product_image_list=new ArrayList<>();

                        JSONArray images_array = product_details.getJSONArray("images");
                        for (int i=0; i<images_array.length(); i++) {
                            JSONObject image = images_array.getJSONObject(i);
                            Product_Image_DataSet product_image_dataSet=new Product_Image_DataSet();
                            product_image_dataSet.setLoadImagePath(image.getString("image"));
                            product_image_dataSet.setSortOrder(image.getString("sort_order"));
                            product_image_dataSet.setEmpty(false);
                            Product_image_list.add(product_image_dataSet);
                        }


                        mProductImageListLayoutMgr = new LinearLayoutManager(getActivity());
                        recycler_product_images.setLayoutManager(mProductImageListLayoutMgr);
                        productImageListAdapter = new ProductImageListAdapter(Product_image_list);
                        recycler_product_images.setAdapter(productImageListAdapter);


                        sort_order.setText(product_details.getString("sort_order"));

                        JSONArray product_section = product_details.getJSONArray("product_category");
                        if (product_section != null) {
                            if (product_section.length() > 0) {
                                for (int i = 0; i < product_section.length(); i++) {
                                    JSONObject product_section_ids = product_section.getJSONObject(i);
                                    Select_Section_id.add(Integer.valueOf(product_section_ids.getString("category_id")));
                                }
                            }
                        }

                        if (Select_Section_id != null) {
                            if (Select_Section_id.size() > 0) {
                                StringBuilder S_name = new StringBuilder();
                                StringBuilder S_ids = new StringBuilder();
                                for (int i = 0; i < mSectionList.size(); i++) {
                                    for (int j = 0; j < Select_Section_id.size(); j++) {
                                        if (mSectionList.get(i).getCategory_id().equals(String.valueOf(Select_Section_id.get(j)))) {
                                            mSectionList.get(i).setSelected(true);
                                            if (S_name.length() > 0) {
                                                S_name.append(",").append(mSectionList.get(i).getName());
                                                S_ids.append(",").append(mSectionList.get(i).getCategory_id());
                                            } else {
                                                S_name.append(mSectionList.get(i).getName());
                                                S_ids.append(mSectionList.get(i).getCategory_id());
                                            }

                                        }

                                    }
                                }
                                add_section.setText(S_name);
                                section_Ids = String.valueOf(S_ids);

                            }
                        }

//                        ArrayList<Product_Image_DataSet> product_image_list=new ArrayList<>();
//
//                        Product_Image_DataSet product_image_dataSet=new Product_Image_DataSet();
//                        product_image_dataSet.setImagePath(product_details.getString("image_thumb"));
//                        product_image_list.add(product_image_dataSet);
//
//                        Product_Image_DataSet product_image_dataSet0=new Product_Image_DataSet();
//                        product_image_dataSet0.setImagePath(product_details.getString("image_thumb"));
//                        product_image_list.add(product_image_dataSet0);
//
//
//                        //if(product_image_list)
//
//
//                        mProductImageListLayoutMgr = new LinearLayoutManager(getActivity());
//                        recycler_product_images.setLayoutManager(mProductImageListLayoutMgr);
//                        productImageListAdapter = new ProductImageListAdapter(product_image_list);
//                        recycler_product_images.setAdapter(productImageListAdapter);




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });

    }


    private void loadDate() {

        myCalendar = Calendar.getInstance();

        date = (view1, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            // Log.e("onCreateView: ", " " + year + " - " + ((monthOfYear) + 1) + " - " + dayOfMonth);
            updateDate(year, ((monthOfYear) + 1), dayOfMonth, key);


        };

    }

    private void updateDate(int year, int monthOfYear, int dayOfMonth, String key) {
        switch (key) {
            case "Start_Date":
                tv_start_date.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                break;
            case "End_Date":
                tv_end_date.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                break;

        }

    }

    private void requestPermission(int Type) {

        if (!hasPermissions(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, Type);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // Select image from camera and gallery
    private void selectImage() {
        try {

            PackageManager pm = activity.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, activity.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {getResources().getString(R.string.take_photo), getResources().getString(R.string.choose_from_gallery),
                        getResources().getString(R.string.cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(getResources().getString(R.string.select_option));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals(getResources().getString(R.string.take_photo))) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals(getResources().getString(R.string.choose_from_gallery))) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals(getResources().getString(R.string.cancel))) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                showAlert();
        } catch (Exception e) {
            Toast.makeText(activity, getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String encodedString;
        File file;

        if (requestCode == PICK_IMAGE_GALLERY) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {

                Uri filePath = data.getData();
                try {
                    //Getting the Bitmap from Gallery
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), filePath);
                    //  Log.e("Activity", "Pick from Gallery::>>> ");

                    Bitmap profile_drawable = Constant.getResizedBitmap(bitmap, 400);
                    IvProductImage.setImageBitmap(profile_drawable);

                    file = new File("" + filePath);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                    byte[] byte_arr = stream.toByteArray();

                    encodedString = Base64.encodeToString(byte_arr, 0);

                    uploader(encodedString, file, requestCode,bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {

            if (requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap photo = (Bitmap) extras.get("data");
                IvProductImage.setImageBitmap(photo);
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(activity, photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 70, stream);
                byte[] byte_arr = stream.toByteArray();

                encodedString = Base64.encodeToString(byte_arr, 0);
                uploader(encodedString, finalFile, requestCode,photo);
            }


        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void uploader(String data, File file, int requestCode,Bitmap bitmap) {
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        if (requestCode == PICK_IMAGE_GALLERY) {
            try {

                JSONObject OptionPost = new JSONObject();
                OptionPost.put("filename", file.getName().replaceAll("[^a-zA-Z0-9]", "") + ".png");
                OptionPost.put("file", data);
                OptionPost.put("language_id", language);

                PROFILE_IMAGE = OptionPost.toString();
                // Log.e("uploader: ", OptionPost + "");
                loadUploadImage(PROFILE_IMAGE,bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject OptionPost = new JSONObject();
                OptionPost.put("filename", file.getName().replaceAll("[^a-zA-Z0-9]", "") + ".png");
                OptionPost.put("file", data);
                OptionPost.put("language_id", language);

                PROFILE_IMAGE = OptionPost.toString();
                // Log.e("uploader: ", OptionPost + "");
                loadUploadImage(PROFILE_IMAGE,bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void loadUploadImage(String Data,Bitmap bitmap) {
        progressDialog.show();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), Data);
        Call<String> call = apiInterface.ImageUploadForProduct(body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        JSONObject object = new JSONObject(response.body());
                        PROFILE_PATH = object.getString("filepath");

                        //File Uploaded Data Updated in DataSet for ImageListAdapter's
                        Product_Image_DataSet product_image_dataSet=new Product_Image_DataSet();
                        product_image_dataSet.setServerImagePath(PROFILE_PATH);
                        product_image_dataSet.setLoadImagePath(Constant.base_url+"image/"+PROFILE_PATH);
                        product_image_dataSet.setSortOrder(String.valueOf(ImagePosition));
                        product_image_dataSet.setEmpty(false);
                        Product_image_list.set(ImagePosition,product_image_dataSet);

                        mProductImageListLayoutMgr = new LinearLayoutManager(getActivity());
                        recycler_product_images.setLayoutManager(mProductImageListLayoutMgr);
                        productImageListAdapter = new ProductImageListAdapter(Product_image_list);
                        recycler_product_images.setAdapter(productImageListAdapter);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void setRefresher(Refresher refresher) {
        this.refresher = refresher;
    }

    private class LanguageAdapter extends BaseAdapter implements SpinnerAdapter {

        private final ArrayList<LanguageModel> Language_list;

        LanguageAdapter(ArrayList<LanguageModel> languageList) {
            this.Language_list = languageList;
        }

        @Override
        public int getCount() {
            return Language_list.size();
        }

        @Override
        public Object getItem(int position) {
            return Language_list.get(position);
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
            txt.setText(Language_list.get(position).getName());
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(Language_list.get(position).getName());
            return txt;
        }
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(getResources().getString(R.string.alert));
        alertDialog.setMessage(getResources().getString(R.string.app_needs_to_access_the_Camera));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.deny),
                (dialog, which) -> dialog.dismiss());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.allow),
                (dialog, which) -> {
                    dialog.dismiss();
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA},
                            PICK_IMAGE_CAMERA);


                });
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null)
            return;

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

//    private void showSettingsAlert() {
//        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
//        alertDialog.setTitle("Alert");
//        alertDialog.setMessage("App needs to access the Camera.");
//        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        //finish();
//                    }
//                });
//        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//
//                    }
//                });
//        alertDialog.show();
//    }


    public class ProductImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ArrayList<Product_Image_DataSet> mProductImageList;

        public ProductImageListAdapter(ArrayList<Product_Image_DataSet> productImageList) {
            this.mProductImageList=productImageList;

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ProdutImageListViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.rc_row_product_image_list, parent, false));
        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


            ProdutImageListViewHolder productImageViewHolder = (ProdutImageListViewHolder) holder;
            IvProductImage=productImageViewHolder.Iv_product_image;

            if(mProductImageList.get(position).getEmpty()){
                Glide.with(activity).load(mProductImageList.get(position).getLoadImagePath()).into(productImageViewHolder.Iv_product_image);
            }else{
//                if(mProductImageList.get(position).getLocalimage()!=null){
//                    productImageViewHolder.Iv_product_image.setImageBitmap(mProductImageList.get(position).getLocalimage());
//                }

                    Glide.with(activity).load(mProductImageList.get(position).getLoadImagePath()).into(productImageViewHolder.Iv_product_image);
//                }else{
//                    Glide.with(activity).load(mProductImageList.get(position).getImagePath()).into(productImageViewHolder.Iv_product_image);
//                }
            }

            productImageViewHolder.layout_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mProductImageList.remove(position);
                    notifyDataSetChanged();
                }
            });

            productImageViewHolder.Ib_product_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (hasPermissions(activity, PERMISSIONS)) {
                        ImagePosition=position;
                        selectImage();
                    } else {
                        requestPermission(IMAGE);
                    }
                }
            });


        }


//        @Override
//        public long getItemId(int position) {
//            return super.getItemId(position);
//        }


        @Override
        public int getItemCount() {
            return mProductImageList.size();
        }

//        @Override
//        public int getItemViewType(int position) {
//
//
//            if (mAParentWholeList.get(position) instanceof Boolean) {
//                return 1;
//            } else if (mAParentWholeList.get(position) instanceof ArrayList) {
//                return 2;
//            } else
//                return 3;
//        }


    }

    public class ProdutImageListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView Iv_product_image;
        ImageButton Ib_product_add;
        LinearLayout layout_remove;


        public ProdutImageListViewHolder(View itemView) {
            super(itemView);

            Iv_product_image=itemView.findViewById(R.id.iv_product_image);
            Ib_product_add=itemView.findViewById(R.id.ib_product_add);
            layout_remove=itemView.findViewById(R.id.layout_remove);

            Iv_product_image.setOnClickListener(this);
            Ib_product_add.setOnClickListener(this);
            layout_remove.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int mId = v.getId();
        }
    }


}
