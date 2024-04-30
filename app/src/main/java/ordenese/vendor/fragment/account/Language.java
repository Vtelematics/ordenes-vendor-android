package ordenese.vendor.fragment.account;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.activity.Activity_Home;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.model.LanguageDataSet;
import ordenese.vendor.model.LanguageModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Language extends DialogFragment implements View.OnClickListener {

    private View mLanguageView;
    ApiInterface apiInterface;
    private LinearLayout mClose, mLanguageBody, mProgressBarContainer;
    private ArrayList<LanguageModel> mLanguageList;
    private Button mChangeLanguage;
    private RecyclerView mLanguageRecycler;
    private RecyclerView.LayoutManager mLanguageRecyclerLayoutMgr;
    private LanguageDataSet mCurrentLanguage;
    ProgressBar progressBar;
    RadioGroup radioGroup;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mLanguageView = inflater.inflate(R.layout.language, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        mClose = (LinearLayout) mLanguageView.findViewById(R.id.layout_restaurant_language_close);
        mLanguageBody = (LinearLayout) mLanguageView.findViewById(R.id.layout_restaurant_language_body);
       // mLanguageBody.setVisibility(View.GONE);
        mClose.setOnClickListener(this);
        progressBar = mLanguageView.findViewById(R.id.progressBar);
        radioGroup = mLanguageView.findViewById(R.id.language_group);
        mLanguageRecycler = (RecyclerView) mLanguageView.findViewById(R.id.layout_restaurant_language_list);

        mChangeLanguage = (Button) mLanguageView.findViewById(R.id.btn_restaurant_language_change);
       // mChangeLanguage.setVisibility(View.GONE);
        mChangeLanguage.setOnClickListener(this);

        getLanguage();

        return mLanguageView;
    }

    private void getLanguage() {

        Call<String> call = apiInterface.getLanguage(Constant.current_language_id(), Constant.current_language_code());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                   // Log.e("onResponse: ", response.body());
                    mLanguageList = ContentJsonParser.getLanguage(response.body());
                    RadioButton radioButton ;

                    if(mLanguageList != null) {

                        for (int i = 0; i < mLanguageList.size(); i++) {
                            radioButton = new RadioButton(getActivity());
                            radioButton.setText(mLanguageList.get(i).getName());
                            radioButton.setId(Integer.parseInt(mLanguageList.get(i).getLanguage_id()));
                            radioGroup.addView(radioButton);

                            RadioButton finalRadioButton = radioButton;
                            radioButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    for (int i = 0 ; i < mLanguageList.size(); i++){

                                        if (mLanguageList.get(i).getLanguage_id().equals(String.valueOf(finalRadioButton.getId()))){
                                            mCurrentLanguage = new LanguageDataSet();
                                            mCurrentLanguage.setLanguageId(mLanguageList.get(i).getLanguage_id());
                                            mCurrentLanguage.setCode(mLanguageList.get(i).getCode().toLowerCase());
                                            mCurrentLanguage.setName(mLanguageList.get(i).getName());
                                        }

                                    }
                                  //  Log.e( "onClick: ", String.valueOf(finalRadioButton.getId()));

                                }
                            });

                        }


                    }else {
                        Constant.showToast(getString(R.string.process_failed_please_try_again));
                    }
                    // languageList(mLanguageList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

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
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;
        int dialogWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

    }

    @Override
    public void onClick(View v) {
        int mCancelId = v.getId();
        if (mCancelId == R.id.layout_restaurant_language_close) {
            dismiss();
        } else if (mCancelId == R.id.btn_restaurant_language_change) {
            //
            if (mCurrentLanguage != null) {
                String languageId = LanguageDetailsDB.getInstance(getActivity()).get_language_id();
                if (mCurrentLanguage.getLanguageId().equals(languageId)) {

                   // Log.e("same",mCurrentLanguage.getLanguageId());

                    dismiss();
                } else {

                    //*******************************************************************************

                    if (mCurrentLanguage.getCode().equalsIgnoreCase("ar")) {

                        AppLanguageSupport.setLocale(getActivity(), mCurrentLanguage.getCode().toLowerCase());

                        if (LanguageDetailsDB.getInstance(getActivity()).check_language_selected()) {
                            LanguageDetailsDB.getInstance(getActivity()).delete_language_detail();
                            LanguageDetailsDB.getInstance(getActivity()).insert_language_detail(mCurrentLanguage.getLanguageId());
                            // mCurrentLanguage.getLanguageId() where arabic language id might be "2" most time.
                        } else {
                            LanguageDetailsDB.getInstance(getActivity()).insert_language_detail(mCurrentLanguage.getLanguageId());
                        }

                        applyLanguageForApp();

                    } else {

                        AppLanguageSupport.setLocale(getActivity(), mCurrentLanguage.getCode().toLowerCase());

                        if (LanguageDetailsDB.getInstance(getActivity()).check_language_selected()) {
                            LanguageDetailsDB.getInstance(getActivity()).delete_language_detail();
                            LanguageDetailsDB.getInstance(getActivity()).insert_language_detail(mCurrentLanguage.getLanguageId());
                        } else {
                            LanguageDetailsDB.getInstance(getActivity()).insert_language_detail(mCurrentLanguage.getLanguageId());
                        }

                        applyLanguageForApp();


                    }

                   // Log.e("differ",mCurrentLanguage.getLanguageId());
                    //*******************************************************************************

                }
            }

        }
    }

    private void applyLanguageForApp() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), Activity_Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void changeLanguage(String languageCode) {

        // AppFunctions.toastLong(getActivity(),languageCode);
        AppLanguageSupport.setLocale(getActivity().getBaseContext(), languageCode);

        Intent intent = new Intent(getActivity().getApplicationContext(), Activity_Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
        getActivity().finish();

    }

   /* private void languageList(ArrayList<LanguageModel> languageList){
        mLanguageRecyclerLayoutMgr = new LinearLayoutManager(getActivity());
        mLanguageRecycler.setLayoutManager(mLanguageRecyclerLayoutMgr);
        mLanguageAdapter = new LanguageAdapter(languageList);
        mLanguageRecycler.setAdapter(mLanguageAdapter);
    }*/

//        if (from.equals(DefaultNames.Store_Language)) {
//
//            if (result != null && result.length > 0) {
//
//                //  Log.e("Language Response", result[0]);
//
//                ResponseDataSet mResponse = ApiMethods.apiResponse(result[0]);
//
//                if (mResponse != null) {
//
//                    if (!mResponse.getResponseEmpty()) {
//                        if (mResponse.getIsSuccess()) {
//
//                            mLanguageList = ApiMethods.languageList(result[0]);
//                            if (mLanguageList != null) {
//
//                                languageList(mLanguageList);
//                                mLanguageBody.setVisibility(View.VISIBLE);
//                                mChangeLanguage.setVisibility(View.VISIBLE);
//
//                            } else {
//                                mLanguageBody.setVisibility(View.GONE);
//                                mChangeLanguage.setVisibility(View.GONE);
//                                Constant.showToast(getString(R.string.process_failed_please_try_again));
//                            }
//
//
//                        } else {
//                            mLanguageBody.setVisibility(View.GONE);
//                            mChangeLanguage.setVisibility(View.GONE);
//                            AppFunctions.toastShort(getActivity(), mResponse.getMessage());
//                        }
//                    } else {
//                        mLanguageBody.setVisibility(View.GONE);
//                        mChangeLanguage.setVisibility(View.GONE);
//                        Constant.showToast(getString(R.string.process_failed_please_try_again));
//                    }
//
//
//                } else {
//                    mLanguageBody.setVisibility(View.GONE);
//                    mChangeLanguage.setVisibility(View.GONE);
//                    Constant.showToast(getString(R.string.process_failed_please_try_again));
//                }
//
//                mProgressBarContainer.setVisibility(View.GONE);
//            }else {
//                mLanguageBody.setVisibility(View.GONE);
//                mProgressBarContainer.setVisibility(View.GONE);
//                mChangeLanguage.setVisibility(View.GONE);
//                Constant.showToast(getString(R.string.process_failed_please_try_again));
//            }
//        } else {
//            mLanguageBody.setVisibility(View.GONE);
//            mProgressBarContainer.setVisibility(View.GONE);
//            mChangeLanguage.setVisibility(View.GONE);
//            Constant.showToast(getString(R.string.process_failed_please_try_again));
//        }

}
