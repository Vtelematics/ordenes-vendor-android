package ordenese.vendor.fragment.option;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.OptionValueTransfer;
import ordenese.vendor.common.instant_transfer.Refresher;
import ordenese.vendor.model.LanguageModel;
import ordenese.vendor.model.Model_OptionDetail;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ordenese.vendor.common.Constant.showToast;


public class Dialog_AddOption extends DialogFragment implements OptionValueTransfer {

    private Activity activity;
    private View v_AddOptionHolder;
    private Refresher refresher;
    private ProgressBar pb_LoaderOption;
    private ApiInterface apiInterface;
    private ArrayList<LanguageModel> languageList = new ArrayList<>();
    private String type = "NEW", mOption_Type, Option_type,OptionType;
    private Spinner s_LanguageLoader;
    private int selectedLanguageId = 1, optionId;
    private ArrayList<Model_OptionDetail> optionDetailList = new ArrayList<>();
    private ArrayList<Model_OptionDetail> deleteOptionDetailList = new ArrayList<>();
    private OptionValueTransfer optionValueTransfer;
    private OptionAdapter optionAdapter;
    private RecyclerView rc_OptionDetailList;
    String[] Type;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.getString(Constant.TYPE) != null) {
                type = bundle.getString(Constant.TYPE);
            }
            if (bundle.getInt(Constant.OPTION_ID) != 0) {
                optionId = bundle.getInt(Constant.OPTION_ID);
            }
        }
    }

    public void setRefresher(Refresher refresher) {
        this.refresher = refresher;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_AddOptionHolder = inflater.inflate(R.layout.dialog_products_add_option, container, false);
        optionValueTransfer = this;

        Type = new String[] {getResources().getString(R.string.single_selection), getResources().getString(R.string.multiple_selection)};

        load();
        return v_AddOptionHolder;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null)
            return;

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }



    private void load() {
        Button btn_Save, btn_Cancel;
        btn_Cancel = v_AddOptionHolder.findViewById(R.id.btn_cancel_option);
        btn_Save = v_AddOptionHolder.findViewById(R.id.btn_save_option);
        rc_OptionDetailList = v_AddOptionHolder.findViewById(R.id.rc_option_detail_list);
        TextView tv_Title = v_AddOptionHolder.findViewById(R.id.tv_option_title);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        s_LanguageLoader = v_AddOptionHolder.findViewById(R.id.s_language_loader);
        pb_LoaderOption = v_AddOptionHolder.findViewById(R.id.pb_loader_add_option);

        rc_OptionDetailList.setLayoutManager(new LinearLayoutManager(activity));

        optionAdapter = new OptionAdapter();

        if (type.equals("NEW")) {
            tv_Title.setText(getString(R.string.product_add_option));
            s_LanguageLoader.setVisibility(View.GONE);
            loadOption();
            rc_OptionDetailList.setAdapter(optionAdapter);
        } else {
            tv_Title.setText(getString(R.string.txt_edit_option));
            s_LanguageLoader.setVisibility(View.VISIBLE);
            getLanguageList();
        }


        btn_Cancel.setOnClickListener(v -> dismiss());

        btn_Save.setOnClickListener(v -> {
            if (checkOption())
                if (type.equals("NEW")) {
                    saveNewOption();
                } else {
                    saveEditOption();
                }
            refresher.refresher();
        });
    }

    private void saveNewOption() {
        if (optionDetailList != null) {
            if (optionDetailList.size() > 0) {
                if (optionDetailList.get(0).getType().equals("OptionName")) {
                    String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", optionDetailList.get(0).getOptionValue());
                        jsonObject.put("type", Option_type);
                        jsonObject.put("sort_order", optionDetailList.get(0).getSortOrder());
                        jsonObject.put("language_id",language);

                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                        pb_LoaderOption.setVisibility(View.VISIBLE);
                        Call<String> call = apiInterface.addOption(Constant.DataGetValue(activity, Constant.Token), body);
                        call.enqueue(new Callback<String>() {

                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                pb_LoaderOption.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    optionDetailList.remove(0);

                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response.body());
                                        if (!jsonObject1.isNull("option_id")) {
                                            optionId = jsonObject1.getInt("option_id");
                                            saveNewOption();
                                        }
                                    } catch (Exception e) {

                                    }
                                }else {
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

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                builder.setMessage(jsonErrorObject.getString("message"));
                                                builder.show();
                                                builder.create();

                                                //  showToast(jsonErrorObject.getString("message"));
                                            } else {
                                                showToast(getString(R.string.error));
                                            }
                                        } else {
                                            showToast(getString(R.string.error));
                                        }

                                    } catch (Exception e) {
                                        showToast(getString(R.string.error));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                pb_LoaderOption.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {
                        saveNewOption();
                    }
                } else if (optionDetailList.get(0).getType().equals("OptionValue")) {
                    String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", optionDetailList.get(0).getOptionValue());
                        jsonObject.put("option_id", optionId);
                        jsonObject.put("language_id", selectedLanguageId);
                        jsonObject.put("sort_order", optionDetailList.get(0).getSortOrder());
                        jsonObject.put("language_id",language);

                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                        pb_LoaderOption.setVisibility(View.VISIBLE);
                        Call<String> call = apiInterface.addOptionValue(Constant.DataGetValue(activity, Constant.Token), body);
                        call.enqueue(new Callback<String>() {

                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                pb_LoaderOption.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    if (optionId != 0) {
                                        optionDetailList.remove(0);
                                        saveNewOption();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                pb_LoaderOption.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {
                        saveNewOption();
                    }
                } else if (optionDetailList.get(0).getType().equals("OptionValueTitle")) {
                    optionDetailList.remove(0);
                    saveNewOption();
                }


            } else {
                refresher.refresher();
                dismiss();
            }
        } else {
            refresher.refresher();
            dismiss();
        }
    }

    private void saveEditOption() {
        if (optionDetailList != null) {
            if (optionDetailList.size() > 0) {
                if (optionDetailList.get(0).getType().equals("OptionName")) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", optionDetailList.get(0).getOptionValue());
                        jsonObject.put("type", Option_type);
                        jsonObject.put("sort_order", optionDetailList.get(0).getSortOrder());
                        jsonObject.put("option_id", optionId);
                        jsonObject.put("language_id", selectedLanguageId);

                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                        pb_LoaderOption.setVisibility(View.VISIBLE);
                        Call<String> call = apiInterface.editOption(Constant.DataGetValue(activity, Constant.Token), body);
                        call.enqueue(new Callback<String>() {

                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                pb_LoaderOption.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    optionDetailList.remove(0);
                                    saveEditOption();
                                }else {

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

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                builder.setMessage(jsonErrorObject.getString("message"));
                                                builder.show();
                                                builder.create();

                                                //  showToast(jsonErrorObject.getString("message"));
                                            } else {
                                                showToast(getString(R.string.error));
                                            }
                                        } else {
                                            showToast(getString(R.string.error));
                                        }

                                    } catch (Exception e) {
                                        showToast(getString(R.string.error));
                                    }

                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                pb_LoaderOption.setVisibility(View.GONE);
                                saveEditOption();
                            }
                        });
                    } catch (Exception e) {
                        saveEditOption();
                    }
                } else if (optionDetailList.get(0).getType().equals("OptionValue")) {

                    try {
                        if (optionDetailList.get(0).getPostType()) {
                            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name", optionDetailList.get(0).getOptionValue());
                                jsonObject.put("option_id", optionId);
                                jsonObject.put("language_id", selectedLanguageId);
                                jsonObject.put("sort_order", optionDetailList.get(0).getSortOrder());
                                jsonObject.put("language_id",language);

                                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                pb_LoaderOption.setVisibility(View.VISIBLE);
                                Call<String> call = apiInterface.addOptionValue(Constant.DataGetValue(activity, Constant.Token), body);
                                call.enqueue(new Callback<String>() {

                                    @Override
                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                        pb_LoaderOption.setVisibility(View.GONE);
                                        if (response.isSuccessful()) {
                                            if (optionId != 0) {
                                                optionDetailList.remove(0);
                                            }
                                        }
                                        saveEditOption();
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                        pb_LoaderOption.setVisibility(View.GONE);
                                        saveEditOption();
                                    }
                                });
                            } catch (Exception e) {
                                saveEditOption();
                            }
                        } else {
                            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", optionDetailList.get(0).getOptionValue());
                            jsonObject.put("option_id", optionId);
                            jsonObject.put("language_id", selectedLanguageId);
                            jsonObject.put("option_value_id", optionDetailList.get(0).getOptionValueId());
                            jsonObject.put("sort_order", optionDetailList.get(0).getSortOrder());
                            jsonObject.put("language_id",language);

                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                            pb_LoaderOption.setVisibility(View.VISIBLE);
                            Call<String> call = apiInterface.editOptionValue(Constant.DataGetValue(activity, Constant.Token), body);
                            call.enqueue(new Callback<String>() {

                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    pb_LoaderOption.setVisibility(View.GONE);
                                    if (response.isSuccessful()) {
                                        optionDetailList.remove(0);
                                    }
                                    saveEditOption();
                                }

                                @Override
                                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                    pb_LoaderOption.setVisibility(View.GONE);
                                    saveEditOption();
                                }
                            });
                        }
                    } catch (Exception e) {
                        saveEditOption();
                    }
                } else if (optionDetailList.get(0).getType().equals("OptionValueTitle")) {
                    optionDetailList.remove(0);
                    saveEditOption();
                }

            } else {
                deleteSavedOption();
            }
        } else {
            deleteSavedOption();
        }
    }

    private void deleteSavedOption() {
        if (deleteOptionDetailList != null) {
            if (deleteOptionDetailList.size() > 0) {
                try {
                    String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("option_id", deleteOptionDetailList.get(0).getOptionId());
                    jsonObject.put("option_value_id", deleteOptionDetailList.get(0).getOptionValueId());
                    jsonObject.put("language_id",language);

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                    pb_LoaderOption.setVisibility(View.VISIBLE);
                    Call<String> call = apiInterface.deleteOptionValue(Constant.DataGetValue(activity, Constant.Token), body);
                    call.enqueue(new Callback<String>() {

                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            pb_LoaderOption.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                deleteOptionDetailList.remove(0);
                            }
                            deleteSavedOption();
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            pb_LoaderOption.setVisibility(View.GONE);
                            deleteSavedOption();
                        }
                    });
                } catch (Exception e) {
                    deleteSavedOption();
                }
            } else {
                dismiss();
            }
        } else {
            dismiss();
        }
    }

    private boolean checkOption() {
        if (optionDetailList != null) {
            if (optionDetailList.size() > 0) {
                if (optionDetailList.size() > 2) {
                    int count = 0;
                    for (int i = 0; i < optionDetailList.size(); i++) {
                        if (optionDetailList.get(i).getType().equals("OptionName")) {
                            if (optionDetailList.get(i).getType().length() == 0) {
                                count++;
                            }
                        }
                    }

                    return count == 0;
                } else {
                    Constant.loadToastMessage(activity, getString(R.string.txt_add_more_option));
                }
            }
        }
        return false;
    }

    private void loadOption() {
        Model_OptionDetail model_optionDetailOptionName = new Model_OptionDetail();
        model_optionDetailOptionName.setType("OptionName");
        optionDetailList.add(model_optionDetailOptionName);

        Model_OptionDetail model_optionDetailOptionValueTitle = new Model_OptionDetail();
        model_optionDetailOptionValueTitle.setType("OptionValueTitle");
        optionDetailList.add(model_optionDetailOptionValueTitle);
    }

    private void getLanguageList() {
        pb_LoaderOption.setVisibility(View.VISIBLE);

        Call<String> call = apiInterface.getLanguage(Constant.current_language_id(), Constant.current_language_code());
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                pb_LoaderOption.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    languageList = ContentJsonParser.getLanguage(response.body());

                    if (languageList != null) {
                        if (languageList.size() > 0) {
                            int selectedPosition = 0;
                            for (int i = 0; i < languageList.size(); i++) {
                                if (languageList.get(i).isDefault()) {
                                    selectedLanguageId = Integer.valueOf(languageList.get(i).getLanguage_id());
                                    selectedPosition = i;
                                }
                            }

                            s_LanguageLoader.setAdapter(new SpinnerLanguageAdapter());

                            s_LanguageLoader.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (languageList != null)
                                        if (languageList.get(position).getName() != null) {

                                            if (optionDetailList != null) {
                                                if (optionDetailList.size() > 0) {
                                                    for (int i = 0; i < optionDetailList.size(); i++) {
                                                        optionDetailList.remove(i);
                                                    }
                                                    optionDetailList = null;
                                                    optionDetailList = new ArrayList<>();
                                                }
                                            }

                                            selectedLanguageId = Integer.valueOf(languageList.get(position).getLanguage_id());
                                            getOptionInfo(selectedLanguageId);
                                        }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            s_LanguageLoader.setSelection(selectedPosition);
                        } else {
                            selectedLanguageId = 0;
                            s_LanguageLoader.setVisibility(View.GONE);
                        }
                    } else {
                        selectedLanguageId = 0;
                        s_LanguageLoader.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                pb_LoaderOption.setVisibility(View.GONE);
            }
        });
    }

    private void getOptionInfo(int languageId) {
        pb_LoaderOption.setVisibility(View.VISIBLE);
        Call<String> call = apiInterface.getOptionInfo(Constant.DataGetValue(activity, Constant.Token), optionId, languageId);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                pb_LoaderOption.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Model_OptionDetail optionDetail = ContentJsonParser.getOptionInfo(response.body());
                    optionDetailList.add(optionDetail);
                    if (optionDetail != null) {
                       OptionType = optionDetail.getOptionType();
                    }


                    Model_OptionDetail model_optionDetailOptionValueTitle = new Model_OptionDetail();
                    model_optionDetailOptionValueTitle.setType("OptionValueTitle");
                    optionDetailList.add(model_optionDetailOptionValueTitle);

                    getOptionValueInfo(languageId);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                pb_LoaderOption.setVisibility(View.GONE);
            }
        });
    }

    private void getOptionValueInfo(int languageId) {
        pb_LoaderOption.setVisibility(View.VISIBLE);
        Call<String> call = apiInterface.getOptionValueList(Constant.DataGetValue(activity, Constant.Token), optionId, languageId);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                pb_LoaderOption.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ArrayList<Model_OptionDetail> tempList = ContentJsonParser.getOptionValueList(response.body());
                    if (tempList != null) {
                        if (tempList.size() > 0) {
                            for (int i = 0; i < tempList.size(); i++) {
                                tempList.get(i).setOptionId(optionId);
                            }

                            optionDetailList.addAll(tempList);
                            optionAdapter.notifyDataSetChanged();
                        }
                    }

                    rc_OptionDetailList.setAdapter(optionAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                pb_LoaderOption.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void transferDetail(String optionValue, int sortOrder, boolean postType, int optionValueId) {
        if (postType) {
            Model_OptionDetail model_optionDetail = new Model_OptionDetail();
            model_optionDetail.setOptionValue(optionValue);
            model_optionDetail.setType("OptionValue");
            model_optionDetail.setPostType(postType);
            model_optionDetail.setSortOrder(sortOrder);
            optionDetailList.add(model_optionDetail);
        } else {
            if (optionDetailList != null) {
                if (optionDetailList.size() > 0) {
                    for (int i = 0; i < optionDetailList.size(); i++) {
                        if (optionDetailList.get(i).getType().equals("OptionValue"))
                            if (optionDetailList.get(i).getOptionValueId() == optionValueId) {
                                optionDetailList.get(i).setOptionValue(optionValue);
                                optionDetailList.get(i).setSortOrder(sortOrder);
                            }
                    }
                }
            }
        }
        optionAdapter.notifyDataSetChanged();
    }

    private class SpinnerLanguageAdapter extends BaseAdapter implements SpinnerAdapter {

        SpinnerLanguageAdapter() {

        }

        @Override
        public int getCount() {
            return languageList.size();
        }

        @Override
        public Object getItem(int position) {
            return languageList.get(position).getName();
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
            txt.setText(languageList.get(position).getName());
            txt.setTextColor(getResources().getColor(R.color.grey_500));
            return txt;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(14);
            txt.setGravity(Gravity.START);
            txt.setText(languageList.get(position).getName());
            return txt;
        }
    }

    private class OptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new OptionNameViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_option_name, parent, false));
            } else if (viewType == 2) {
                return new OptionValueTitleViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_option_value_title, parent, false));
            } else {
                return new OptionValueOptionViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_option_value, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                OptionNameViewHolder optionNameViewHolder = (OptionNameViewHolder) holder;
                if (optionDetailList.get(position).getOptionValue() != null)
                    optionNameViewHolder.et_OptionName.setText(optionDetailList.get(position).getOptionValue());

                if (optionDetailList.get(position).getSortOrder() != null)
                    optionNameViewHolder.et_OptionSortOrder.setText(String.valueOf(optionDetailList.get(position).getSortOrder()));

                optionNameViewHolder.et_OptionName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        optionDetailList.get(position).setOptionValue(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                optionNameViewHolder.et_OptionSortOrder.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0)
                            optionDetailList.get(position).setSortOrder(Integer.valueOf(s.toString()));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                SpinnerTypeAdapter spinnerTypeAdapter = new SpinnerTypeAdapter(Type);
                optionNameViewHolder.sp_type.setAdapter(spinnerTypeAdapter);

                optionNameViewHolder.sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mOption_Type = Type[position];
                        if (mOption_Type.equals(getResources().getString(R.string.single_selection))) {
                            Option_type = "radio";
                        } else {
                            Option_type = "checkbox";
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if(OptionType != null)
                if (OptionType.equals("checkbox")){
                    optionNameViewHolder.sp_type.setSelection(1);
                } else {
                    optionNameViewHolder.sp_type.setSelection(0);
                }


            } else if (holder.getItemViewType() == 2) {
                OptionValueTitleViewHolder optionValueTitleViewHolder = (OptionValueTitleViewHolder) holder;
                optionValueTitleViewHolder.tv_AddOptionValue.setOnClickListener(v -> {
                    Dialog_OptionValue dialog_optionValue = new Dialog_OptionValue();
                    dialog_optionValue.setOptionValue(optionValueTransfer);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.TYPE, "NEW");
                    dialog_optionValue.setArguments(bundle);
                    dialog_optionValue.show(getChildFragmentManager(), "OptionValueAdd");
                });
            } else {
                OptionValueOptionViewHolder optionValueOptionViewHolder = (OptionValueOptionViewHolder) holder;
                optionValueOptionViewHolder.tv_OptionValue.setText(optionDetailList.get(position).getOptionValue());
                String sortOrder = getString(R.string.sorting_title) + " " + optionDetailList.get(position).getSortOrder();
                optionValueOptionViewHolder.tv_SortOrder.setText(sortOrder);

                optionValueOptionViewHolder.ib_Edit.setOnClickListener(v -> {
                    Dialog_OptionValue dialog_optionValue = new Dialog_OptionValue();
                    dialog_optionValue.setOptionValue(optionValueTransfer);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.TYPE, "EDIT");
                    bundle.putString(Constant.NAME, optionDetailList.get(position).getOptionValue());
                    if (optionDetailList.get(position).getSortOrder() != null) {
                        bundle.putInt(Constant.SORT_ORDER, optionDetailList.get(position).getSortOrder());
                    } else {
                        bundle.putInt(Constant.SORT_ORDER, -1);
                    }
                    if (optionDetailList.get(position).getPostType()) {
                        bundle.putInt(Constant.OPTION_ID, 0);
                    } else {
                        bundle.putInt(Constant.OPTION_ID, optionDetailList.get(position).getOptionValueId());
                    }
                    dialog_optionValue.setArguments(bundle);
                    dialog_optionValue.show(getChildFragmentManager(), "OptionValue");
                });


                optionValueOptionViewHolder.ib_Delete.setOnClickListener(v -> {
                    if (!type.equals("NEW")) {
                        deleteOptionDetailList.add(optionDetailList.get(position));
                    }
                    DialogAlertDelete(position);

                });
            }
        }

        private void DialogAlertDelete(int position) {
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                        optionDetailList.remove(position);
                        notifyItemRemoved(position);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                    .create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle(activity.getString(R.string.delete_confirmation_title));
            alertDialog.setMessage(activity.getString(R.string.delete_message));
            alertDialog.show();
        }

        @Override
        public int getItemCount() {
            return optionDetailList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (optionDetailList.get(position).getType().equals("OptionName")) {
                return 1;
            } else if (optionDetailList.get(position).getType().equals("OptionValueTitle")) {
                return 2;
            } else {
                return 3;
            }
        }

        class OptionNameViewHolder extends RecyclerView.ViewHolder {
            EditText et_OptionName, et_OptionSortOrder;
            Spinner sp_type;

            OptionNameViewHolder(View itemView) {
                super(itemView);
                et_OptionName = itemView.findViewById(R.id.et_option_name_value);
                et_OptionSortOrder = itemView.findViewById(R.id.et_sort_order_value);
                sp_type = itemView.findViewById(R.id.sp_type);
            }
        }

        class OptionValueTitleViewHolder extends RecyclerView.ViewHolder {
            TextView tv_AddOptionValue;

            OptionValueTitleViewHolder(View itemView) {
                super(itemView);
                tv_AddOptionValue = itemView.findViewById(R.id.tv_add_new);
            }
        }

        class OptionValueOptionViewHolder extends RecyclerView.ViewHolder {
            TextView tv_OptionValue, tv_SortOrder;
            ImageButton ib_Edit, ib_Delete;

            OptionValueOptionViewHolder(View itemView) {
                super(itemView);
                tv_OptionValue = itemView.findViewById(R.id.tv_option_title);
                tv_SortOrder = itemView.findViewById(R.id.tv_option_sort_order);
                ib_Edit = itemView.findViewById(R.id.ib_option_edit);
                ib_Delete = itemView.findViewById(R.id.ib_option_delete);
            }
        }

        private class SpinnerTypeAdapter extends BaseAdapter implements SpinnerAdapter {

            private final String[] TypeList;

            SpinnerTypeAdapter(String[] type) {
                this.TypeList = type;
            }

            @Override
            public int getCount() {
                return TypeList.length;
            }

            @Override
            public Object getItem(int position) {
                return TypeList[position];
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
                txt.setText(TypeList[position]);
                txt.setTextColor(getResources().getColor(R.color.grey_500));
                return txt;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView txt = new TextView(activity);
                txt.setPadding(16, 16, 16, 16);
                txt.setTextSize(14);
                txt.setGravity(Gravity.START);
                txt.setText(TypeList[position]);
                return txt;
            }
        }
    }

}
