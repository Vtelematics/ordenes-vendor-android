package ordenese.vendor.model;

import java.util.ArrayList;

/**
 * Created by happy on 11/23/2018.
 */

public class Section_Info {
    private String status,sort_order;
    private ArrayList<section_description> section_descriptions;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public ArrayList<section_description> getSection_descriptions() {
        return section_descriptions;
    }

    public void setSection_descriptions(ArrayList<section_description> section_descriptions) {
        this.section_descriptions = section_descriptions;
    }
}
