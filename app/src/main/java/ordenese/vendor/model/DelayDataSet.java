package ordenese.vendor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DelayDataSet {

    @SerializedName("delay_list")
    @Expose
    private ArrayList<DelayList> delayList = new ArrayList<>();
    @SerializedName("success")
    @Expose
    private Success success;

    public ArrayList<DelayList> getDelayList() {
        return delayList;
    }

    public void setDelayList(ArrayList<DelayList> delayList) {
        this.delayList = delayList;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

}
