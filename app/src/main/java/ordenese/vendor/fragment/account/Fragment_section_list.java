package ordenese.vendor.fragment.account;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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
import ordenese.vendor.common.instant_transfer.SectionPageHandler;
import ordenese.vendor.fragment.dialog.Dialog_Section;
import ordenese.vendor.fragment.dialog.Dialog_section_list;
import ordenese.vendor.model.Section;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;


public class Fragment_section_list extends Fragment implements SectionPageHandler {

    ApiInterface apiInterface;
    Activity activity;
    RecyclerView rc_section_list;
    private int page = 1, total = 0;
    ProgressBar progressBar;
    Button btn_add_new;
    private SectionListAdapter sectionListAdapter;
    private ArrayList<Section> mSectionList;
    SectionPageHandler sectionPageHandler;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_section_list, container, false);
        rc_section_list = view.findViewById(R.id.rc_section_list);
        progressBar = view.findViewById(R.id.progressBar);
        btn_add_new = view.findViewById(R.id.btn_add_new);
        btn_add_new.setOnClickListener(v -> LoadAddSection("", ""));
        LoadSectionList();
        sectionPageHandler = this;
        return view;
    }

    private void LoadSectionList() {
        if (mSectionList != null) {
            if (mSectionList.size() > 0) {
                page = 1;

                for (int i = 0; i < mSectionList.size(); i++) {
                    mSectionList.remove(i);
                }
            }
        }
        if (Constant.isNetworkAvailable()) {
            apiInterface = ApiClient.getClient().create(ApiInterface.class);
            String language = LanguageDetailsDB.getInstance(activity).get_language_id();
            Call<String> call = apiInterface.getSectionList(Constant.DataGetValue(activity, Constant.Token), page, 5,language);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        total = ContentJsonParser.getSectionTotalCount(response.body());
                        mSectionList = ContentJsonParser.getSectionList(response.body());
                        rc_section_list.setLayoutManager(new LinearLayoutManager(activity));
                        sectionListAdapter = new SectionListAdapter();
                        rc_section_list.setAdapter(sectionListAdapter);
                        sectionListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {
                                progressBar.setVisibility(View.VISIBLE);
                                if (page <= ((total / 10) + 1)) {
                                    if (Constant.isNetworkAvailable()) {
                                        page++;
                                        apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                                        Call<String> call = apiInterface.getSectionList(Constant.DataGetValue(activity, Constant.Token), page, 5,language);
                                        call.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (response.isSuccessful()) {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (response.body() != null) {
                                                        ArrayList<Section> temp_section_list = ContentJsonParser.getSectionList(response.body());

                                                        if (temp_section_list != null) {
                                                            if (temp_section_list.size() > 0) {
                                                                mSectionList.addAll(temp_section_list);
                                                            }

                                                        }
                                                        sectionListAdapter.notifyDataSetChanged();
                                                        sectionListAdapter.setLoaded();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });

                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        } else {
            Constant.LoadNetworkError(getChildFragmentManager());
        }
    }

    @Override
    public void LoadAddSection(String id, String type) {

        Dialog_Section dialog_section = new Dialog_Section();
        //add_menu_item.setDialogInterface(alert_dialog_handler);
        dialog_section.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        if (sectionPageHandler != null) {
            dialog_section.setAddSectionInterface(sectionPageHandler);
        }
        Bundle bundle = new Bundle();
        bundle.putString("section_id", id);
        bundle.putString("section_type", type);
        dialog_section.setArguments(bundle);
        dialog_section.show(getChildFragmentManager(), "Add Section");
    }

    @Override
    public void refresher() {
       page = 1;
       LoadSectionList();
    }

    @Override
    public void LoadSectionListPage() {
        if(mSectionList != null && mSectionList.size() > 0){
            LoadDialogSection(mSectionList);
        }
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

    private class SectionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean loading;

        SectionListAdapter() {
            if (rc_section_list.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rc_section_list.getLayoutManager();
                rc_section_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        @Override
        public int getItemCount() {
            if (mSectionList != null) {
                if (mSectionList.size() > 0) {
                    return mSectionList.size();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mSectionList != null) {
                if (mSectionList.size() > 0) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 2) {
                return new ViewHolderSection(LayoutInflater.from(activity).inflate(R.layout.fragment_section_list_adapter, parent, false));
            } else {
                return new ViewHolderEmpty(LayoutInflater.from(activity).inflate(R.layout.rc_empty_row, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 2) {
                ViewHolderSection viewHolderSection = (ViewHolderSection) holder;
                viewHolderSection.section_name.setText(" : " + mSectionList.get(position).getName());
                viewHolderSection.section_status.setText(" : " + mSectionList.get(position).getStatus());


                viewHolderSection.iv_section_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogAlertDelete(activity, mSectionList.get(position).getCategory_id());
                    }
                });
                viewHolderSection.iv_section_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadAddSection(mSectionList.get(position).getCategory_id(), "Edit");
                    }
                });
            } else {
                ViewHolderEmpty viewHolderEmpty = (ViewHolderEmpty) holder;
                viewHolderEmpty.tv_sectionListEmpty.setText(getString(R.string.section_empty));
            }
        }

        //
        void DialogAlertDelete(Context mContext, String id) {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                        LoadMenuDelete(id);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                    .create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle(mContext.getString(R.string.delete_confirmation_title));
            alertDialog.setMessage(mContext.getString(R.string.delete_message));
            alertDialog.show();
        }


        private void LoadMenuDelete(String id) {
            if (Constant.isNetworkAvailable()) {
                progressBar.setVisibility(View.VISIBLE);
                String language = LanguageDetailsDB.getInstance(activity).get_language_id();
                Call<String> call = apiInterface.DeleteSection(Constant.DataGetValue(activity, Constant.Token), id,language);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject1 = new JSONObject(response.body());
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("success");
                                Constant.showToast(jsonObject2.getString("message"));
                                LoadSectionList();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });


            } else {
                Constant.LoadNetworkError(getChildFragmentManager());
            }

        }


        private class ViewHolderSection extends RecyclerView.ViewHolder {
            TextView section_name, section_status;
            ImageView iv_section_edit, iv_section_delete;
            CardView Card_view;

            ViewHolderSection(View view) {
                super(view);
                Card_view = view.findViewById(R.id.Card_view);
                section_name = view.findViewById(R.id.tv_section_value);
                section_status = view.findViewById(R.id.tv_status_value);

                iv_section_edit = view.findViewById(R.id.iv_section_edit);
                iv_section_delete = view.findViewById(R.id.iv_section_delete);
            }
        }

        private class ViewHolderEmpty extends RecyclerView.ViewHolder {
            TextView tv_sectionListEmpty;

            ViewHolderEmpty(View view) {
                super(view);
                tv_sectionListEmpty = view.findViewById(R.id.tv_empty);
            }
        }
    }


}
