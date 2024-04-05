package ordenese.vendor.SunmiPrinterSDK.dataset;

import android.graphics.Bitmap;

public class Printer_DataSet {

    public String print_Content;
    public String fontSize;
    public Boolean isBold;
    public Boolean isUnderLine;
    public String fontName;
    public Boolean isNewline=false;
    public Boolean isImageExist=false;
    public Bitmap mBitmapImage;

    public String getPrint_Content() {
        return print_Content;
    }

    public void setPrint_Content(String print_Content) {
        this.print_Content = print_Content;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public Boolean getBold() {
        return isBold;
    }

    public void setBold(Boolean bold) {
        isBold = bold;
    }

    public Boolean getUnderLine() {
        return isUnderLine;
    }

    public void setUnderLine(Boolean underLine) {
        isUnderLine = underLine;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Boolean getNewline() {
        return isNewline;
    }

    public void setNewline(Boolean newline) {
        isNewline = newline;
    }

    public Boolean getImageExist() {
        return isImageExist;
    }

    public void setImageExist(Boolean imageExist) {
        isImageExist = imageExist;
    }

    public Bitmap getmBitmapImage() {
        return mBitmapImage;
    }

    public void setmBitmapImage(Bitmap mBitmapImage) {
        this.mBitmapImage = mBitmapImage;
    }
}
