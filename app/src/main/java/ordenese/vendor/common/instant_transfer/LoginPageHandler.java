package ordenese.vendor.common.instant_transfer;

/**
 * Created by user on 7/3/2018.
 * Home Page Handler
 */

public interface LoginPageHandler {

    void LoadSection();

    void LoadOptions();

    void LoadSignIn();

    void LoadSignUp();

    void loadRefreshMenu();

    void CloseActivity();

    void LoadForgot();

    void BackPressed();

    void OrderList();

    void LoadOrderInfo(String Order_id);

    void LoadStore(String mRestaurant_Info);

    void LoadCoupon();

    void LoadProducts();

    void LoadReportOrder();

    void LoadReportShipping();

    void LoadReportCommission();

    void LoadReportCoupon();

    void LoadReportProducts();

    void LoadUsersList();

    void Admin_Chat();

    void EarningHistory();

    void change_language();

}
