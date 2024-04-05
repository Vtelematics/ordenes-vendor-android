package ordenese.vendor.fragment.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;
import ordenese.vendor.common.LanguageDetailsDB;
import ordenese.vendor.common.instant_transfer.Refresher;


public class Dialog_ProductOptionWebView extends DialogFragment {

    private View v_ProductOptionHolder;
    private Activity activity;
    private int productId = 0, storeId = 0, PriceSelectionId = 0;
    private Refresher refresher;
    private String Store_id;

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

    public void setProductRefresher(Refresher refresher) {
        this.refresher = refresher;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            productId = bundle.getInt("PRODUCT_ID");
            PriceSelectionId = bundle.getInt("price_selection");
            Store_id = bundle.getString("Store_id");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v_ProductOptionHolder = inflater.inflate(R.layout.dialog_product_option, container, false);
        load();
        return v_ProductOptionHolder;
    }

    ///@SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @SuppressLint({"SetJavaScriptEnabled"})
    private void load() {

        WebView w_ProductOptionLoader = v_ProductOptionHolder.findViewById(R.id.w_product_option_loader);
        String language = LanguageDetailsDB.getInstance(activity).get_language_id();
        String url = Constant.PRODUCT_OPTION + productId + "&store_id=" + Store_id + "&price_selection=" + PriceSelectionId + "&language=" + language;

           Log.e("url",url);

       /* @SuppressWarnings("unused")
        class ProductOptionJSInterface {
            @JavascriptInterface
            public void processHTML(String html) {
                // process the html as needed by the app
                if (html.replace("<html><head></head><body style=\"color: #FFF\">", "")
                        .replace("</body></html>", "")
                        .contains("1")) {
                  success();
                }
            }

        }*/

        w_ProductOptionLoader.getSettings().setJavaScriptEnabled(true);
        w_ProductOptionLoader.clearCache(true);
        //  w_ProductOptionLoader.addJavascriptInterface(new ProductOptionJSInterface(), "HTMLOUT");
        w_ProductOptionLoader.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //  Log.e( "onPageStarted: ",""+url );
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(w_ProductOptionLoader, url);
                if (url.contains("restaurant/product_option/success")) {
                    // w_ProductOptionLoader.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    success();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
        });

        w_ProductOptionLoader.loadUrl(url);
    }

    private void success() {
//        activity.runOnUiThread(() -> {
//
//        });
        refresher.refresher();
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);

        if (getDialog() == null)
            return;

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }


}
