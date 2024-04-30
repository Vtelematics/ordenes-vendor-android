package ordenese.vendor.fragment.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.instant_transfer.SectionPageHandler;
import ordenese.vendor.model.Section;

import java.util.ArrayList;


public class Dialog_section_list extends DialogFragment {
    RecyclerView recyclerView;
    Activity activity;
    Button btn_cancel, btn_ok;
    TextView addNewCategory;
    private StringBuilder Section_id,Section_Name;
    private SectionPageHandler SectionHandler;
    private ArrayList<Section> Section_Lists;

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

    public void setSectionHandler(SectionPageHandler sectionPageHandler){
        this.SectionHandler = sectionPageHandler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            Section_Lists  = (ArrayList<Section>) getArguments().getSerializable("sections_lists");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_section_list, container, false);

        btn_cancel = view.findViewById(R.id.button_cancel);
        btn_ok = view.findViewById(R.id.button_ok);
        recyclerView = view.findViewById(R.id.rc_section_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        addNewCategory = view.findViewById(R.id.tv_add_new_category);

        if (Section_Lists != null) {

            RecyclerViewListAdapter recyclerViewListAdapter = new RecyclerViewListAdapter();
            recyclerView.setAdapter(recyclerViewListAdapter);
        }

        addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SectionHandler.LoadSectionListPage();
                dismiss();
            }
        });

        btn_cancel.setOnClickListener(v -> dismiss());
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Section_Lists != null){
                    if (Section_Lists.size() > 0){
                        Section_id = new StringBuilder();
                        Section_Name = new StringBuilder();
                        for (int i = 0; i < Section_Lists.size(); i++) {

                            if (Section_Lists.get(i).isSelected()){
                                if (Section_id.length() > 0){
                                    Section_id.append(",").append(Section_Lists.get(i).getCategory_id());
                                    Section_Name.append(",").append(Section_Lists.get(i).getName());
                                }else {
                                    Section_id.append(Section_Lists.get(i).getCategory_id());
                                    Section_Name.append(Section_Lists.get(i).getName());
                                }

                            }

                        }
                        if (Section_id != null) {
                            if (Section_id.length() > 0){
                                SectionHandler.LoadAddSection(Section_id.toString(),Section_Name.toString());
                                dismiss();
                            }else {
                                Constant.showToast(getResources().getString(R.string.please_select_checkbox));
                            }
                        } else {
                            Constant.showToast(getResources().getString(R.string.please_select_checkbox));
                        }

                    }
                }
            }
        });
        return view;
    }

    private class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListAdapter.ViewHolder> {
        private boolean isFromView = false;


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.coupon_product_list_adapter,parent,false);
            return  new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.mCheckBox.setText(Section_Lists.get(position).getName());

            // To check weather checked event fire from getview() or user input
            isFromView = true;
            holder.mCheckBox.setChecked(Section_Lists.get(position).isSelected());
            isFromView = false;

            holder.mCheckBox.setTag(position);
            holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    Section_Lists.get(getPosition).setSelected(isChecked);
                }
            });
        }



        @Override
        public int getItemCount() {
            return Section_Lists.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private CheckBox mCheckBox;
            ViewHolder(View itemView) {
                super(itemView);

                mCheckBox = itemView
                        .findViewById(R.id.checkBox);
            }
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
}
