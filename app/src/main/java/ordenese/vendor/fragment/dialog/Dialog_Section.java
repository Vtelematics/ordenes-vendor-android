package ordenese.vendor.fragment.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.LoginPageHandler;
import ordenese.vendor.common.instant_transfer.SectionPageHandler;
import ordenese.vendor.model.LanguageModel;
import ordenese.vendor.model.Section_Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dialog_Section extends DialogFragment {

    Spinner sp_status;
    Activity activity;
    String[] Status;
    ApiInterface apiInterface;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    private ArrayList<LanguageModel> mLanguageList;
    private SectionPageHandler SectionPageHandler;
    private String Section_id;
    private String Section_type;
    TextView tv_section_title, tv_name, btn_cancel, btn_save;
    private String Status_id;
    private int status_id;
    EditText ed, ed_sort_order_value;
    List<EditText> allEds = new ArrayList<EditText>();
    LoginPageHandler loginPageHandler;
    View view;
    private Section_Info mSectionInfo;

    @Override
    public void onAttach(Context context) {

        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        this.loginPageHandler = (LoginPageHandler) context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getActivity() != null) {

                getActivity().getWindow().getDecorView().setLayoutDirection(
                        "ar".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                                View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

            }
        }

    }

    public void setAddSectionInterface(SectionPageHandler sectionPageHandler) {
        this.SectionPageHandler = sectionPageHandler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Section_id = getArguments().getString("section_id");
            Section_type = getArguments().getString("section_type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_section, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Status = new String[]{getResources().getString(R.string.enabled), getResources().getString(R.string.disabled)};


        linearLayout = view.findViewById(R.id.section_layout);
        sp_status = view.findViewById(R.id.sp_status);
        progressBar = view.findViewById(R.id.progressBar);
        tv_section_title = view.findViewById(R.id.tv_section_title);
        ed_sort_order_value = view.findViewById(R.id.ed_sort_order_value);
        tv_name = view.findViewById(R.id.tv_name);
        tv_name.setText(R.string.Section_Name);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_save = view.findViewById(R.id.btn_save);
        if (Section_type.equals("Edit")) {
            tv_section_title.setText(R.string.txt_edit_section);
            LoadSectionInfo(Section_id);
        } else {
            tv_section_title.setText(R.string.txt_add_section);
            LoadLanguage();
        }

        LoadStatus();
        btn_cancel.setOnClickListener(v -> dismiss());

        btn_save.setOnClickListener(v -> {
            String[] strings = new String[allEds.size()];

            for (int i = 0; i < allEds.size(); i++) {
                strings[i] = allEds.get(i).getText().toString();
            }

            Boolean isEmpty = false;
            for (String string : strings) {
                if (string.isEmpty()) {
                    isEmpty = true;
                    break;
                }
            }
            if (isEmpty) {
                Toast.makeText(activity, getResources().getString(R.string.please_fill_all_required), Toast.LENGTH_SHORT).show();
            } else {
                String sort_order = ed_sort_order_value.getText().toString();
                String language = LanguageDetailsDB.getInstance(activity).get_language_id();

                if (!Section_type.equals("Edit")) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        JSONObject section_name = new JSONObject();
                        for (int i = 0; i < strings.length; i++) {
                            section_name.put(String.valueOf(i + 1), strings[i]);
                        }
                        jsonObject.put("category_description", section_name);
                        jsonObject.put("status", status_id);
                        jsonObject.put("sort_order", sort_order);
                        jsonObject.put("language_id",language);
                        jsonObject.put("store_id",Constant.DataGetValue(activity, Constant.StoreId));

                        progressBar.setVisibility(View.VISIBLE);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                        Call<String> call = apiInterface.CreateCategory(Constant.DataGetValue(activity, Constant.Token), body);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response.body());
                                        JSONObject jsonObject2 = jsonObject1.getJSONObject("success");
                                        Constant.showToast(jsonObject2.getString("message"));
                                        SectionPageHandler.refresher();
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
                                                Constant.showToast(jsonErrorObject.getString("message"));
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                Constant.showToast(getString(R.string.error));
                                            }
                                        } else {
                                            Constant.showToast(getString(R.string.error));
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Constant.showToast(getString(R.string.error));
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
                    try {
                        JSONObject jsonObject = new JSONObject();
                        JSONObject section_name = new JSONObject();
                        for (int i = 0; i < strings.length; i++) {
                            section_name.put(String.valueOf(i + 1), strings[i]);
                        }
                        jsonObject.put("category_description", section_name);
                        jsonObject.put("status", status_id);
                        jsonObject.put("sort_order", sort_order);
                        jsonObject.put("section_id", Section_id);
                        jsonObject.put("language_id",language);

                        progressBar.setVisibility(View.VISIBLE);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                        Call<String> call = apiInterface.EditSection(Constant.DataGetValue(activity, Constant.Token), body);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response.body());
                                        JSONObject jsonObject2 = jsonObject1.getJSONObject("success");
                                        Constant.showToast(jsonObject2.getString("message"));
                                        SectionPageHandler.refresher();
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
                                                Constant.showToast(jsonErrorObject.getString("message"));
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                Constant.showToast(getString(R.string.error));
                                            }
                                        } else {
                                            Constant.showToast(getString(R.string.error));
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Constant.showToast(getString(R.string.error));
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
                }


            }
        });

        return view;
    }


    private void LoadLanguage() {
        Call<String> call = apiInterface.getLanguage(Constant.current_language_id(), Constant.current_language_code());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    mLanguageList = ContentJsonParser.getLanguage(response.body());
                    if (mLanguageList != null) {
                        if (mLanguageList.size() > 0) {

                            for (int i = 0; i < mLanguageList.size(); i++) {

                                ed = new EditText(activity);
                                allEds.add(ed);
                                ed.setId(i);
                               // ed.setHint("name (" + mLanguageList.get(i).getName() + ")");
                                if(getActivity() != null){
                                    ed.setHint(getActivity().getResources().getString(R.string.name));
                                }
                                linearLayout.addView(ed);
                                if (Section_type.equals("Edit")) {
                                    if (mSectionInfo != null) {
                                        for (int j = 0; j < mSectionInfo.getSection_descriptions().size(); j++) {
                                            if (mLanguageList.get(i).getLanguage_id().equals(mSectionInfo.getSection_descriptions().get(j).getLanguage_id())) {
                                                ed.setText(mSectionInfo.getSection_descriptions().get(j).getName());
                                            }

                                        }
                                    }
                                }


                            }


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

    private void LoadSectionInfo(String section_id) {
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getSectionInfo(Constant.DataGetValue(activity, Constant.Token), section_id,language);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {

                    mSectionInfo = ContentJsonParser.getSectionInfo(response.body());
                    LoadLanguage();
                    if (mSectionInfo != null) {
                        ed_sort_order_value.setText(mSectionInfo.getSort_order());
                        String sectionStatus = mSectionInfo.getStatus();
                        if (Status != null) {
                            if (Status.length > 0) {
                                for (int i = 0; i < Status.length; i++) {
                                    if (sectionStatus.equals(Status[i])) {
                                        sp_status.setSelection(i);

                                    }

                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }
}
