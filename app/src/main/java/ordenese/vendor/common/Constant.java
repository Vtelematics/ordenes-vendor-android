package ordenese.vendor.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import ordenese.vendor.R;
import ordenese.vendor.fragment.dialog.DialogNetworkError;

/**
 * Created by user on 7/20/2018.
 * Constant
 */

public class Constant {

    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String Token = "Token";
    public static String StoreDetails = "Store_details";
    public static String StoreId = "Store_id";
    public static String StoreType = "";
    public static String REPORT_TYPE_TOTAL = "report_order_total";
    public static String REPORT_TYPE_ORDER = "report_single_order";
    public static String OPTION_ID = "option_id";
    public static String TYPE = "type";
    public static String SORT_ORDER = "sort_order";
    public static String NAME = "name";

    public static String base_url = "https://www.ordenesdelivery.com/";

    public static String PRODUCT_OPTION = "https://www.ordenesdelivery.com/restaurant/product_option&product_id=";

    /*Report*/
    private static String PAGE = "&page=";
    private static String LIMIT = "&limit=6" + PAGE;
    public static String ORDER_ID = "&filter_order_id=";
    public static String CUSTOMER = "&filter_customer=";
    public static String ORDER_STATUS = "&filter_order_status=";
    public static String TOTAL = "&filter_total=";
    public static String DATE_DELIVERY = "&filter_date_delivery=";
    private static String BASE_URL_2 = "index.php?route=";
    public static String REPORT_ORDER = BASE_URL_2 + "appstore/report/orders" + LIMIT;

    /*Sorting*/
    private static String SORTING_ASCEND = "ASC";
    private static String SORTING_DESCENDING = "DESC";
    private static String SORTING_SORT = "&sort=";
    private static String SORTING_ORDER = "&order=";
    private static String SORTING_NAME = SORTING_SORT + "name" + SORTING_ORDER;
    private static String SORTING_SORT_ORDER = SORTING_SORT + "sort_order" + SORTING_ORDER;

     /*Option*/
    public static String OPTION = BASE_URL_2 + "store/option/options" + LIMIT;
    private static String SORTING_OPTION_NAME = SORTING_SORT + "od.name" + SORTING_ORDER;
    private static String SORTING_OPTION_SORT_ORDER = SORTING_SORT + "o.sort_order" + SORTING_ORDER;
    public static String OPTION_SORTING_NAME_ASC = SORTING_OPTION_NAME + SORTING_ASCEND;
    public static String OPTION_SORTING_NAME_DESC = SORTING_OPTION_NAME + SORTING_DESCENDING;
    public static String OPTION_SORTING_SORT_ORDER_ASC = SORTING_OPTION_SORT_ORDER + SORTING_ASCEND;
    public static String OPTION_SORTING_SORT_ORDER_DESC = SORTING_OPTION_SORT_ORDER + SORTING_DESCENDING;
    public static String OPTION_FILTER = "&filter_name=";

     /*Order*/
    public static String MY_ORDER = BASE_URL_2 + "store/order/mine" + LIMIT;
    public static String ADD_DATE = "&filter_date_added=";
    public static String ORDER_STATUS_TITLE = "order_status";

    /*Report*/
    public static void DataStoreValue(Context context, String Key, String Value) {
        DataRemoveValue(context, Key);
        SharedPreferences sharedPreferences = context.getSharedPreferences("My_Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public static String DataGetValue(Context context, String Key) {
        SharedPreferences preferences = context.getSharedPreferences("My_Pref", Context.MODE_PRIVATE);
        return preferences.getString(Key, "empty");
    }

    public static void DataRemoveValue(Context context, String Key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("My_Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Key);
        editor.apply();
    }

    public static void showToast(String message) {
        Toast.makeText(ApplicationContext.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static void glide_image_loader(String url, final ImageView imageView) {
        Glide.with(ApplicationContext.getInstance()).load(url).apply(getOption("Default")).into(imageView);
    }

    public static void glide_image_loader_fixed_size(String url, final ImageView imageView) {
        Glide.with(ApplicationContext.getInstance()).load(url).apply(getOption("fixed")).into(imageView);
    }

    public static void glide_image_loader_banner(String url, final ImageView imageView) {
        Glide.with(ApplicationContext.getInstance()).load(url).apply(getOption("Default")).into(imageView);
    }

    private static RequestOptions getOption(String which) {

        RequestOptions options;
        switch (which) {
            case "fixed":
                options = new RequestOptions()
                        .error(R.drawable.error_logo_3)
                        .override(300, 300)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE);
                break;
            case "Category":
                options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE);
                break;
            default:
                options = new RequestOptions()
                        .error(R.drawable.error_logo_3)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE);
                break;
        }
        return options;

    }

    public static int current_language_id() {

        /*if (!Constant.DataGetValue(ApplicationContext.getInstance(), Constant.CurrentLanguage).equals("empty")) {
            if (Constant.DataGetValue(ApplicationContext.getInstance(), Constant.CurrentLanguage).equals("true")) {
                return 1;
            } else {
                return 3;
            }
        } else {
            return 1;
        }*/

        if(LanguageDetailsDB.getInstance(ApplicationContext.getInstance()).check_language_selected()){
            String languageId = LanguageDetailsDB.getInstance(ApplicationContext.getInstance()).get_language_id();
            if(languageId.equals("3")){
                //arabic :-
                return 3;
            }else {
                //english
                return 1;
            }
        }else {
            //english
            return 1;
        }


    }

    public static String current_language_code() {

        /*if (!Constant.DataGetValue(ApplicationContext.getInstance(), Constant.CurrentLanguage).equals("empty")) {
            if (Constant.DataGetValue(ApplicationContext.getInstance(), Constant.CurrentLanguage).equals("true")) {
                return "en";
            } else {
                return "ar";
            }
        } else {
            return "en";
        }*/

        if(LanguageDetailsDB.getInstance(ApplicationContext.getInstance()).check_language_selected()){
            String languageId = LanguageDetailsDB.getInstance(ApplicationContext.getInstance()).get_language_id();
            if(languageId.equals("3")){
                //arabic :-
                return "ar";
            }else {
                //english
                return "en";
            }
        }else {
            //english
            return "en";
        }

    }

    public static String current_language_checkout() {

        if(LanguageDetailsDB.getInstance(ApplicationContext.getInstance()).check_language_selected()){
            String languageId = LanguageDetailsDB.getInstance(ApplicationContext.getInstance()).get_language_id();
            if(languageId.equals("3")){
                //arabic :-
                return "ar";
            }else {
                //english
                return "en";
            }
        }else {
            //english
            return "en";
        }

    }

    @NonNull
    public static Boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationContext.getInstance()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connectivityManager != null;
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else {
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                            return false;
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return false;
                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            return false;
                        default:
                            return true;
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static void loadToastMessage(Activity activity, String Message) {
        Toast.makeText(activity, Message, Toast.LENGTH_SHORT).show();
    }

    public static void LoadNetworkError(FragmentManager fragmentManager) {
        DialogNetworkError networkError = new DialogNetworkError();
        networkError.setCancelable(false);
        networkError.show(fragmentManager, "NetworkError");
    }


    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


}
