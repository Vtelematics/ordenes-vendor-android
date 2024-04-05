package ordenese.vendor.model;

import android.graphics.Bitmap;

public class Product_Image_DataSet {

    private String ServerImagePath;
    private String SortOrder;
    private Boolean isEmpty;
    private String LoadImagePath;
    //private Bitmap Localimage;


    public String getSortOrder() {
        return SortOrder;
    }

    public void setSortOrder(String sortOrder) {
        SortOrder = sortOrder;
    }

    public Boolean getEmpty() {
        return isEmpty;
    }

    public void setEmpty(Boolean empty) {
        isEmpty = empty;
    }

//    public Bitmap getLocalimage() {
//        return Localimage;
//    }
//
//    public void setLocalimage(Bitmap localimage) {
//        Localimage = localimage;
//    }


    public String getServerImagePath() {
        return ServerImagePath;
    }

    public void setServerImagePath(String serverImagePath) {
        ServerImagePath = serverImagePath;
    }

    public String getLoadImagePath() {
        return LoadImagePath;
    }

    public void setLoadImagePath(String loadImagePath) {
        LoadImagePath = loadImagePath;
    }
}
