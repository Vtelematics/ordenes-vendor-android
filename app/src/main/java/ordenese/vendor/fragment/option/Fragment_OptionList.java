package ordenese.vendor.fragment.option;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ordenese.vendor.Api.ApiClient;
import ordenese.vendor.Api.ApiInterface;
import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.ContentJsonParser;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.OnLoadMoreListener;
import ordenese.vendor.common.instant_transfer.Refresher;
import ordenese.vendor.model.Model_Option;

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


public class Fragment_OptionList extends Fragment implements Refresher {

    private View v_OptionHolder;
    private Activity activity;
    private TextView tv_Sorting, tv_Filter;
    private RecyclerView rc_OptionLister;
    private ProgressBar pb_LoaderOptionList;
    private LinearLayout ll_Sorting, ll_Filter;
    private String url;
    private int page = 1, totalCount = 1, currentType = 0;
    private ArrayList<Model_Option> optionList = new ArrayList<>();
    private ApiInterface apiInterface;
    private Refresher refresher;
    private EditText et_OptionName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_OptionHolder = inflater.inflate(R.layout.fragment_products_option_list, container, false);
        load();
        refresher = this;
        return v_OptionHolder;
    }

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

    private void load() {
        Button btn_Apply, btn_Cancel;
        btn_Apply = v_OptionHolder.findViewById(R.id.btn_apply_options);
        btn_Cancel = v_OptionHolder.findViewById(R.id.btn_cancel_options);
        TextView tv_OptionNameASC, tv_OptionNameDESC, tv_SortOrderASC, tv_SortOrderDESC;
        tv_Sorting = v_OptionHolder.findViewById(R.id.tv_sorting);
        tv_Filter = v_OptionHolder.findViewById(R.id.tv_filter);
        tv_OptionNameASC = v_OptionHolder.findViewById(R.id.tv_option_name_asc_sorting);
        tv_OptionNameDESC = v_OptionHolder.findViewById(R.id.tv_option_name_desc_sorting);
        tv_SortOrderASC = v_OptionHolder.findViewById(R.id.tv_sort_order_asc_sorting);
        tv_SortOrderDESC = v_OptionHolder.findViewById(R.id.tv_sort_order_desc_sorting);
        et_OptionName = v_OptionHolder.findViewById(R.id.et_option_name);
        rc_OptionLister = v_OptionHolder.findViewById(R.id.rc_option_list);
        pb_LoaderOptionList = v_OptionHolder.findViewById(R.id.pb_loader_option);
        ll_Sorting = v_OptionHolder.findViewById(R.id.ll_sorting_option_bg);
        ll_Filter = v_OptionHolder.findViewById(R.id.ll_filter_option_bg);
        Button btn_AddNew = v_OptionHolder.findViewById(R.id.btn_add_new);

        rc_OptionLister.setLayoutManager(new LinearLayoutManager(activity));

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        urlSetup(0);
        LoadOptionList();

        tv_Sorting.setOnClickListener(v -> {
            hideSortingHandler(true);
        });

        ll_Sorting.setOnClickListener(v -> {
            hideSortingHandler(false);
        });

        tv_Filter.setOnClickListener(v -> {
            hideFilterHandler(true);
        });

        ll_Filter.setOnClickListener(v -> hideFilterHandler(false));

        tv_OptionNameASC.setOnClickListener(v -> {
            page = 1;
            urlSetup(1);
            hideSortingHandler(false);
            LoadOptionList();
        });

        tv_OptionNameDESC.setOnClickListener(v -> {
            page = 1;
            urlSetup(2);
            hideSortingHandler(false);
            LoadOptionList();
        });

        tv_SortOrderASC.setOnClickListener(v -> {
            page = 1;
            urlSetup(3);
            hideSortingHandler(false);
            LoadOptionList();
        });
        tv_SortOrderDESC.setOnClickListener(v -> {
            page = 1;
            urlSetup(4);
            hideSortingHandler(false);
            LoadOptionList();
        });

        btn_Cancel.setOnClickListener(v -> {
            hideKeyboardFrom(activity, v_OptionHolder);
            tv_Filter.setVisibility(View.VISIBLE);
            emptyTextSetup();
            ll_Filter.setVisibility(View.GONE);
        });

        btn_Apply.setOnClickListener(v -> {
            if (et_OptionName.getText().toString().length() > 0) {
                urlSetup(5);
                LoadOptionList();
                emptyTextSetup();
                tv_Filter.setVisibility(View.VISIBLE);
                ll_Filter.setVisibility(View.GONE);
            } else {
                Constant.loadToastMessage(activity, getString(R.string.txt_filter_enter_value));
            }
        });

        btn_AddNew.setOnClickListener(v -> addNewOption());
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void emptyTextSetup() {
        et_OptionName.setHint(getString(R.string.txt_filter_option_name));
    }

    private void hideSortingHandler(boolean type) {
        if (type) {
            tv_Sorting.setVisibility(View.GONE);
            ll_Sorting.setVisibility(View.VISIBLE);
        } else {
            tv_Sorting.setVisibility(View.VISIBLE);
            ll_Sorting.setVisibility(View.GONE);
        }
    }

    private void hideFilterHandler(boolean type) {
        if (type) {
            tv_Filter.setVisibility(View.GONE);
            ll_Filter.setVisibility(View.VISIBLE);
        } else {
            tv_Filter.setVisibility(View.VISIBLE);
            ll_Filter.setVisibility(View.GONE);
        }
    }

    private void urlSetup(int type) {

        url = Constant.OPTION + page + "&language_id=" + Constant.current_language_id();

        switch (type) {
            case 1:
                url = url + Constant.OPTION_SORTING_NAME_ASC;
                break;
            case 2:
                url = url + Constant.OPTION_SORTING_NAME_DESC;
                break;
            case 3:
                url = url + Constant.OPTION_SORTING_SORT_ORDER_ASC;
                break;
            case 4:
                url = url + Constant.OPTION_SORTING_SORT_ORDER_DESC;
                break;
            case 5:
                if (currentType == 1) {
                    url = url + Constant.OPTION_SORTING_NAME_ASC;
                } else if (currentType == 2) {
                    url = url + Constant.OPTION_SORTING_NAME_DESC;
                } else if (currentType == 3) {
                    url = url + Constant.OPTION_SORTING_SORT_ORDER_ASC;
                } else if (currentType == 4) {
                    url = url + Constant.OPTION_SORTING_SORT_ORDER_DESC;
                }
                url = url + Constant.OPTION_FILTER + et_OptionName.getText().toString();
                break;
            default:
                break;
        }

        currentType = type;
    }

    private void LoadOptionList() {
        pb_LoaderOptionList.setVisibility(View.VISIBLE);
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        Call<String> call = apiInterface.getOptionListReport(Constant.DataGetValue(activity, Constant.Token), url, language);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                pb_LoaderOptionList.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    optionList = ContentJsonParser.getOptionList(response.body());
                    totalCount = ContentJsonParser.getTotalCount(response.body());
                    OptionListAdapter optionListAdapter = new OptionListAdapter();
                    rc_OptionLister.setAdapter(optionListAdapter);

                    if (totalCount > 1) {
                        optionListAdapter.setOnLoadMoreListener(() -> {
                            if (page <= ((totalCount / 10) + 1)) {
                                page++;
                                urlSetup(currentType);
                                String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                                Call<String> call1 = apiInterface.getOptionListReport(Constant.DataGetValue(activity, Constant.Token), url, language);
                                call1.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(@NonNull Call<String> call1, @NonNull Response<String> responsePaging) {
                                        pb_LoaderOptionList.setVisibility(View.GONE);
                                        if (responsePaging.isSuccessful()) {
                                            ArrayList<Model_Option> temp_option_list = ContentJsonParser.getOptionList(responsePaging.body());
                                            if (temp_option_list != null) {
                                                if (temp_option_list.size() > 0) {
                                                    optionList.addAll(temp_option_list);
                                                    optionListAdapter.notifyDataSetChanged();
                                                    optionListAdapter.setLoaded();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<String> call1, @NonNull Throwable t) {
                                        pb_LoaderOptionList.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                pb_LoaderOptionList.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void refresher() {
        page = 1;
        urlSetup(0);
        LoadOptionList();
    }

    class OptionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int visibleThreshold = 10;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean loading;

        OptionListAdapter() {
            if (rc_OptionLister.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rc_OptionLister.getLayoutManager();
                rc_OptionLister.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }
                });
            }
        }

        void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        void setLoaded() {
            loading = false;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new OptionViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_row_products_option, parent, false));
            } else {
                return new OptionEmptyViewHolder(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                OptionViewHolder optionViewHolder = (OptionViewHolder) holder;
                optionViewHolder.tv_CategoryTitle.setText(optionList.get(position).getOptionName());
                String sortOrder = getString(R.string.product_sort_order) + " : " + optionList.get(position).getSortOrder();
                optionViewHolder.tv_CategorySortOrder.setText(sortOrder);
                optionViewHolder.ib_CategoryEdit.setOnClickListener(v -> {
                    editOption(optionList.get(position).getOptionId());
                });
                optionViewHolder.ib_CategoryDelete.setOnClickListener(v -> {
                    DialogAlertDelete(position);
                });
            } else {
                OptionEmptyViewHolder categoryEmptyViewHolder = (OptionEmptyViewHolder) holder;
                categoryEmptyViewHolder.tv_Empty.setText(getString(R.string.option_empty));
            }
        }

        void DialogAlertDelete(int position) {
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setPositiveButton(R.string.btn_ok, (dialogInterface, i) -> {
                        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("option_id", optionList.get(position).getOptionId());
                            jsonObject.put("language_id", language);

                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                            Call<String> callDelete = apiInterface.deleteOption(Constant.DataGetValue(activity, Constant.Token), body);
                            callDelete.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    if (response.isSuccessful()) {
                                        optionList.remove(position);
                                        notifyItemRemoved(position);
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
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                                    builder.setMessage(jsonErrorObject.getString("message"));
                                                    builder.setPositiveButton(activity.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    builder.create();
                                                    builder.show();
                                                    // showToast(jsonErrorObject.getJSONObject("message").toString());
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

                                }
                            });
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle(activity.getString(R.string.delete_confirmation_title));
            alertDialog.setMessage(activity.getString(R.string.delete_message));
            alertDialog.show();
        }

        @Override
        public int getItemCount() {
            if (optionList != null) {
                if (optionList.size() > 0) {
                    return optionList.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (optionList != null) {
                if (optionList.size() > 0) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                return 2;
            }
        }

        class OptionViewHolder extends RecyclerView.ViewHolder {
            TextView tv_CategoryTitle, tv_CategorySortOrder;
            ImageButton ib_CategoryEdit, ib_CategoryDelete;

            OptionViewHolder(View itemView) {
                super(itemView);
                tv_CategoryTitle = itemView.findViewById(R.id.tv_category_title);
                tv_CategorySortOrder = itemView.findViewById(R.id.tv_category_sort_order);
                ib_CategoryEdit = itemView.findViewById(R.id.ib_category_edit);
                ib_CategoryDelete = itemView.findViewById(R.id.ib_category_delete);
            }
        }

        class OptionEmptyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_Empty;

            OptionEmptyViewHolder(View itemView) {
                super(itemView);
                tv_Empty = itemView.findViewById(R.id.tv_empty);
            }
        }
    }

    private void addNewOption() {
        Dialog_AddOption dialog_addOption = new Dialog_AddOption();
        dialog_addOption.setRefresher(refresher);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TYPE, "NEW");
        dialog_addOption.setArguments(bundle);
        dialog_addOption.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog_addOption.show(getChildFragmentManager(), "NewOption");
    }

    private void editOption(int OptionId) {
        Dialog_AddOption dialog_addOption = new Dialog_AddOption();
        dialog_addOption.setRefresher(refresher);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TYPE, "EDIT");
        bundle.putInt(Constant.OPTION_ID, OptionId);
        dialog_addOption.setArguments(bundle);
        dialog_addOption.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog_addOption.show(getChildFragmentManager(), "EditOption");
    }
}
