package ordenese.vendor.Api;

import java.net.URI;

import okhttp3.RequestBody;
import ordenese.vendor.model.Category;
import ordenese.vendor.model.CategoryList;
import ordenese.vendor.model.DelayDataSet;
import ordenese.vendor.model.GroceryCategoryDataSet;
import ordenese.vendor.model.GroceryProducts;
import ordenese.vendor.model.OrderSummaryDataSet;
import ordenese.vendor.model.ReportDataSet;
import ordenese.vendor.model.ReportOrdersDataset;
import ordenese.vendor.model.SubCategoryDataSet;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {


    /*POST Order list - Home*/
    @POST("api/vendor/order-list")
    Call<String> getOrderList(@Header("Vendor-Authorization") String Token, @Body RequestBody requestBody);

    //Order Status list - Report
    @POST("api/vendor/order-status-list")
    Call<String> getOrderStatusListReport(@Header("Vendor-Authorization") String Token, @Query("language_id") String language_id);

    @POST("api/vendor/login")
    Call<String> UserLogin(@Body RequestBody jsonObject);

    /*Busy Status*/
    @POST("api/vendor/busy-status")
    Call<String> BusyStatus_update(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /*Restaurant Busy Status*/
    @POST("api/vendor/busy")
    Call<String> get_BusyStatus(@Header("Vendor-Authorization") String Token);

    /*Order Info*/
    @POST("api/vendor/order-info")
    Call<String> getOrderInfo(@Header("Vendor-Authorization") String Token, @Body RequestBody requestBody);

    /*  Update order status */
    @POST("api/vendor/order-status-update")
    Call<String> UpdateOrderStatus(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    @POST("api/vendor/sub-category-list")
    Call<SubCategoryDataSet> subcategory(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    @POST("api/vendor/category-list")
    Call<CategoryList> category_list(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    @POST("api/vendor/product-list")
    Call<GroceryProducts> product(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    @POST("api/vendor/product-search")
    Call<GroceryProducts> product_search(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    @POST("api/vendor/product/status-update")
    Call<String> product_status_update(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /*Order cancel*/
    @POST("api/vendor/cancel/reason-list")
    Call<String> getOrderCancel(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /*Change Password*/
    @POST("api/vendor/change-password")
    Call<String> ChangePassword(@Header("Vendor-Authorization") String Token, @Body RequestBody body);

    /* forget password */
    @POST("api/vendor/forget-password")
    Call<String> UserForgetPassword(@Body RequestBody jsonObject);

    @POST("api/vendor/report-filter")
    Call<ReportDataSet> report_filter(@Header("Vendor-Authorization") String Token, @Body RequestBody body);

    @POST("api/vendor/report-list")
    Call<ReportOrdersDataset> report_order_list(@Header("Vendor-Authorization") String Token, @Body RequestBody body);

    @POST("api/vendor/delay-list")
    Call<DelayDataSet> delay_list(@Header("Vendor-Authorization") String Token, @Body RequestBody body);

    @POST("api/vendor/delay-update")
    Call<String> delay_update(@Header("Vendor-Authorization") String Token, @Body RequestBody body);

    @POST("api/vendor/logout")
    Call<String> log_out (@Header("Vendor-Authorization") String Token, @Body RequestBody body);

    @POST("api/vendor/report-summery")
    Call<OrderSummaryDataSet> report_summary_list(@Header("Vendor-Authorization") String Token,@Body RequestBody body);





    /*Get Language*/
    @GET("index.php?route=store/local/language")
    Call<String> getLanguage(@Query("language_id") Integer Language_id, @Query("language_code") String language_code);

    /*Get Dashboard*/
    @GET("index.php?route=store/account/dashboard")
    Call<String> getDashboard(@Header("Vendor-Authorization") String Token, @Query("language_id") String language_id);

    /*Get Store Info*/
    @GET("index.php?route=store/account/info")
    Call<String> getStoreInfo(@Header("Vendor-Authorization") String Token, @Query("language_id") String language_id);

    /*Get Coupon Coupon_Products*/
    @GET("index.php?route=store/coupon/products")
    Call<String> getProducts(@Header("Vendor-Authorization") String Token, @Query("language_id") String language_id);

    /*Get Register Info*/
    @GET("index.php?route=store/store/register_fields")
    Call<String> getRegisterInfo();

    /*Get Coupon list*/
    @GET("index.php?route=store/coupon")
    Call<String> getCouponList(@Header("Vendor-Authorization") String Token, @Query("page") int page,
                               @Query("limit") int limit, @Query("language_id") String language_id);

    /*Get Coupon Info*/
    @GET("index.php?route=store/coupon/info")
    Call<String> getCouponInfo(@Header("Vendor-Authorization") String Token,
                               @Query("coupon_id") String coupon_id, @Query("language_id") String language_id);

    /*Delete Coupon*/
    @GET("index.php?route=store/coupon/delete")
    Call<String> DeleteCoupon(@Header("Vendor-Authorization") String Token, @Query("coupon_id") String coupon_id
            , @Query("language_id") String language_id);

    /*Get Section list*/
    @GET("index.php?route=store/section")
    Call<String> getSectionList(@Header("Vendor-Authorization") String Token, @Query("page") int page,
                                @Query("limit") int limit, @Query("language_id") String language_id);

    @GET("index.php?route=store/product/getCategories")
    Call<String> getSectionList_(@Header("Vendor-Authorization") String Token, @Query("page") int page,
                                 @Query("limit") int limit, @Query("language_id") String language_id);

    /*Get Section Info*/
    @GET("index.php?route=store/section/info")
    Call<String> getSectionInfo(@Header("Vendor-Authorization") String Token, @Query("section_id") String section_id,
                                @Query("language_id") String language_id);

    /*Delete Section*/
    @GET("index.php?route=store/section/delete")
    Call<String> DeleteSection(@Header("Vendor-Authorization") String Token, @Query("section_id") String section_id
            , @Query("language_id") String language_id);

    //Get Order list - Report
    @GET("index.php?route=store/report/orders")
    Call<String> getOrderListReport(@Header("Vendor-Authorization") String Token, @Query("filter_start_date") String filter_start_date,
                                    @Query("filter_end_date") String filter_end_date,
                                    @Query("filter_date_delivery") String filter_date_delivery,
                                    @Query("filter_order_status") String filter_order_status_id,
                                    @Query("filter_order_id") String filter_order_id,
                                    @Query("filter_customer") String filter_customer,
                                    @Query("filter_total") String filter_total
            , @Query("language_id") String language_id);

    //Get Shipping list - Report
    @GET("index.php?route=store/report/shipping")
    Call<String> getShippingListReport(@Header("Vendor-Authorization") String Token, @Query("filter_start_date") String filter_start_date,
                                       @Query("filter_end_date") String filter_end_date,
                                       @Query("filter_order_status") String filter_order_status_id,
                                       @Query("filter_group") String filter_group,
                                       @Query("language_id") String language_id);

    // Get Commission list - Report
    @GET("index.php?route=store/report/commission")
    Call<String> getCommissionListReport(@Header("Vendor-Authorization") String Token, @Query("filter_start_date") String filter_start_date,
                                         @Query("filter_end_date") String filter_end_date, @Query("language_id") String language_id);

    // Get Coupon list - Report
    @GET("index.php?route=store/report/coupons")
    Call<String> getCouponListReport(@Header("Vendor-Authorization") String Token, @Query("filter_date_start") String filter_date_start,
                                     @Query("filter_date_end") String filter_date_end, @Query("language_id") String language_id);

    //Get Products list - Report
    @GET("index.php?route=store/report/products")
    Call<String> getProductsListReport(@Header("Vendor-Authorization") String Token, @Query("filter_start_date") String filter_start_date,
                                       @Query("filter_end_date") String filter_end_date,
                                       @Query("filter_order_status_id") String filter_order_status_id
            , @Query("language_id") String language_id);

    //Get Products list
    @GET("index.php?route=store/product/products")
    Call<String> getProductsList(@Header("Vendor-Authorization") String Token, @Query("page") int page, @Query("language_id") String language_id);

    /*Get Status list*/
    @POST("index.php?route=store/order/order_statuses")
    Call<String> getOrderStatus(@Header("Vendor-Authorization") String Token, @Query("language_id") String language_id);

    /*Product info*/
    @POST("index.php?route=store/product/info")
    Call<String> ProductInfo(@Header("Vendor-Authorization") String Token, @Query("product_id") String product_id,
                             @Query("language_id") String language_id);

    //Get Option list
    @GET
    Call<String> getOptionListReport(@Header("Vendor-Authorization") String Token, @Url String url, @Query("language_id") String languageId);

    //Get Option Detail list
    @GET("index.php?route=store/option/info")
    Call<String> getOptionInfo(@Header("Vendor-Authorization") String Token, @Query("option_id") int optionId,
                               @Query("language_id") int languageId);

    //Get Option Value list
    @GET("index.php?route=store/ovalue/ovalues")
    Call<String> getOptionValueList(@Header("Vendor-Authorization") String Token, @Query("option_id") int optionId,
                                    @Query("language_id") int languageId);

    /*Post*/

    /*  Register */
    // @FormUrlEncoded
    @POST("index.php?route=store/store/registration")
    //  Call<String> UserRegistration(@Field("firstname") String firstname,@Field("lastname") String lastname,@Field("email") String email,@Field("telephone") String telephone,@Field("password") String password,@Field("confirm") String confirm,@Field("status") int status,@Field("latitude") Double latitude  ,@Field("longitude") Double longitude,@Field("address") String address,@Field("geocode") String geocode);
    Call<String> UserRegistration(@Body RequestBody jsonObject);

    //Edit Store
    @POST("index.php?route=store/account/edit")
    Call<String> UserEditAccount(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /* CreateCategory*/
    @POST("index.php?route=store/product/createCategory")
    Call<String> CreateCategory(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /* Edit Section*/
    @POST("index.php?route=store/section/edit")
    Call<String> EditSection(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /* Add Coupon*/
    @POST("index.php?route=store/coupon/add")
    Call<String> AddCoupon(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /* Edit coupon*/
    @POST("index.php?route=store/coupon/edit")
    Call<String> EditCoupon(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /* Image upload*/
    @POST("index.php?route=store/account/upload")
    Call<String> ImageUpload(@Body RequestBody jsonObject);

    /*Image upload for product*/
    @POST("index.php?route=store/product/upload")
    Call<String> ImageUploadForProduct(@Body RequestBody jsonObject);

    /*Add Product*/
    @POST("index.php?route=store/product/add")
    Call<String> AddProduct(@Header("Vendor-Authorization") String Token, @Body RequestBody body);

    /*Edit Product*/
    @POST("index.php?route=store/product/edit")
    Call<String> EditProduct(@Header("Vendor-Authorization") String Token, @Body RequestBody body);

    /*Delete Product*/
    @POST("index.php?route=store/product/delete")
    Call<String> DeleteProduct(@Header("Vendor-Authorization") String Token, @Body RequestBody body);

    /*Option Add*/
    @POST("index.php?route=store/option/add")
    Call<String> addOption(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /*Option Edit*/
    @POST("index.php?route=store/option/edit")
    Call<String> editOption(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /*Option Delete*/
    @POST("index.php?route=store/option/delete")
    Call<String> deleteOption(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /*Option Value Add*/
    @POST("index.php?route=store/ovalue/add")
    Call<String> addOptionValue(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /*Option Value Edit*/
    @POST("index.php?route=store/ovalue/edit")
    Call<String> editOptionValue(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);

    /*Option Value Delete*/
    @POST("index.php?route=store/ovalue/delete")
    Call<String> deleteOptionValue(@Header("Vendor-Authorization") String Token, @Body RequestBody jsonObject);
    /*Post*/

}
