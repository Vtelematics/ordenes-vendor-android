package ordenese.vendor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReportDataSet {

    @SerializedName("filter")
    @Expose
    private ArrayList<ReportModelList> filter = null;
    @SerializedName("success")
    @Expose
    private Success success;

    public ArrayList<ReportModelList> getFilter() {
        return filter;
    }

    public void setFilter(ArrayList<ReportModelList> filter) {
        this.filter = filter;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

}
