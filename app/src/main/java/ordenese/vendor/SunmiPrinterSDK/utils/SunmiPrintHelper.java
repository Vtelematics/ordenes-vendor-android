package ordenese.vendor.SunmiPrinterSDK.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;

import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.peripheral.printer.WoyouConsts;

public class SunmiPrintHelper {
    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;

    /**
     *  sunmiPrinter means checking the printer connection status
     */
    public int sunmiPrinter = CheckSunmiPrinter;

    private SunmiPrinterService sunmiPrinterService;

    private static SunmiPrintHelper helper = new SunmiPrintHelper();

    private SunmiPrintHelper() {}

    public static SunmiPrintHelper getInstance() {
        return helper;
    }

    private InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
        @Override
        protected void onConnected(SunmiPrinterService service) {
            sunmiPrinterService = service;
            checkSunmiPrinterService(service);
        }

        @Override
        protected void onDisconnected() {
            sunmiPrinterService = null;
            sunmiPrinter = LostSunmiPrinter;
        }
    };

    /**
     * init sunmi print service
     */
    public void initSunmiPrinterService(Context context){
        try {
            boolean ret =  InnerPrinterManager.getInstance().bindService(context,
                    innerPrinterCallback);
            if(!ret){
                sunmiPrinter = NoSunmiPrinter;
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     *  deInit sunmi print service
     */
    public void deInitSunmiPrinterService(Context context){
        try {
            if(sunmiPrinterService != null){
                InnerPrinterManager.getInstance().unBindService(context, innerPrinterCallback);
                sunmiPrinterService = null;
                sunmiPrinter = LostSunmiPrinter;
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the printer connection,
     * like some devices do not have a printer but need to be connected to the cash drawer through a print service
     */
    private void checkSunmiPrinterService(SunmiPrinterService service){
        boolean ret = false;
        try {
            ret = InnerPrinterManager.getInstance().hasPrinter(service);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
        sunmiPrinter = ret?FoundSunmiPrinter:NoSunmiPrinter;
    }

    /**
     *  paper feed three lines
     *  Not disabled when line spacing is set to 0
     */
    public void print3Line(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.lineWrap(3, null);
        } catch (RemoteException e) {

        }
    }

    public void print1Line(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.lineWrap(1, null);
        } catch (RemoteException e) {

        }
    }


    /**
     * print text
     * setPrinterStyle:Api require V4.2.22 or later, So use esc cmd instead when not supported
     *  More settings reference documentation {@link WoyouConsts}
     * printTextWithFont:
     *  Custom fonts require V4.14.0 or later!
     *  You can put the custom font in the 'assets' directory and Specify the font name parameters
     *  in the Api.
     */
    public void printText(String content, float size, boolean isBold, boolean isUnderLine,
                          String typeface) {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, isBold?
                        WoyouConsts.ENABLE:WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                if (isBold) {
                    sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
                } else {
                    sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
                }
            }
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_UNDERLINE, isUnderLine?
                        WoyouConsts.ENABLE:WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                if (isUnderLine) {
                    sunmiPrinterService.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null);
                } else {
                    sunmiPrinterService.sendRAWData(ESCUtil.underlineOff(), null);
                }
            }
            sunmiPrinterService.printTextWithFont(content, typeface, size, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     *  Due to the distance between the paper hatch and the print head,
     *  the paper needs to be fed out automatically
     *  But if the Api does not support it, it will be replaced by printing three lines
     */
    public void feedPaper(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.autoOutPaper(null);
        } catch (RemoteException e) {
            print3Line();
        }
    }

    public void printNewLine(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.autoOutPaper(null);
        } catch (RemoteException e) {
            print1Line();
        }
    }

    /**
     *  Printer cuts paper and throws exception on machines without a cutter
     */
    public void cutpaper(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.cutPaper(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Print pictures and text in the specified orde
     *  After the picture is printed,
     *  the line feed output needs to be called,
     *  otherwise it will be saved in the cache
     *  In this example, the image will be printed because the print text content is added
     */
    public void printBitmap(Bitmap bitmap) {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.printBitmap(bitmap, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
