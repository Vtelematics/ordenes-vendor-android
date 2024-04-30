package ordenese.vendor.common;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.datatransport.cct.internal.LogEvent;

import ordenese.vendor.model.Coupon;
import ordenese.vendor.model.Coupon_Products;
import ordenese.vendor.model.Delivery_product_details;
import ordenese.vendor.model.Home_page;
import ordenese.vendor.model.LanguageModel;
import ordenese.vendor.model.Model_Option;
import ordenese.vendor.model.Model_OptionDetail;
import ordenese.vendor.model.Model_ReportCommission;
import ordenese.vendor.model.Model_ReportCoupon;
import ordenese.vendor.model.Model_ReportOrderHolder;
import ordenese.vendor.model.Model_ReportProducts;
import ordenese.vendor.model.Model_ReportShipping;
import ordenese.vendor.model.Model_ReportSingleOrder;
import ordenese.vendor.model.Model_Status;
import ordenese.vendor.model.OptionValueModel;
import ordenese.vendor.model.OrderCancelResason;
import ordenese.vendor.model.Order_Info;
import ordenese.vendor.model.Order_List;
import ordenese.vendor.model.Order_Status;
import ordenese.vendor.model.Order_status_histories;
import ordenese.vendor.model.Order_total_list;
import ordenese.vendor.model.Payment_method;
import ordenese.vendor.model.Product_List;
import ordenese.vendor.model.Register_fields;
import ordenese.vendor.model.Restaurant_Info;
import ordenese.vendor.model.Section;
import ordenese.vendor.model.Section_Info;
import ordenese.vendor.model.StoreInfo;
import ordenese.vendor.model.Store_Cuisine;
import ordenese.vendor.model.Store_info;
import ordenese.vendor.model.section_description;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ContentJsonParser {
    public static ArrayList<Order_List> getOrderLIst(String response) {

        try {
            ArrayList<Order_List> order_lists = new ArrayList<>();
            JSONObject jsonOrderList = new JSONObject(response);
            if (!jsonOrderList.isNull("order")) {
                JSONArray jsonArray = jsonOrderList.getJSONArray("order");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonOrderDetails = jsonArray.getJSONObject(i);

                        Order_List order_list = new Order_List();
                        if (!jsonOrderDetails.isNull("order_id")) {
                            order_list.setOrder_id(jsonOrderDetails.getString("order_id"));
                        } else {
                            order_list.setOrder_id("");
                        }
                        if (!jsonOrderDetails.isNull("name")) {
                            order_list.setName(jsonOrderDetails.getString("name"));
                        } else {
                            order_list.setName("");
                        }
                        if (!jsonOrderDetails.isNull("telephone")) {
                            order_list.setTelephone(jsonOrderDetails.getString("telephone"));
                        } else {
                            order_list.setTelephone("");
                        }
                        if (!jsonOrderDetails.isNull("status")) {
                            order_list.setStatus(jsonOrderDetails.getString("status"));
                        } else {
                            order_list.setStatus("");
                        }
                        if (!jsonOrderDetails.isNull("order_date")) {
                            order_list.setDate(jsonOrderDetails.getString("order_date"));
                        } else {
                            order_list.setDate("");
                        }
                        if (!jsonOrderDetails.isNull("order_time")) {
                            order_list.setOrder_time(jsonOrderDetails.getString("order_time"));
                        } else {
                            order_list.setOrder_time("");
                        }
                        if (!jsonOrderDetails.isNull("total")) {
                            order_list.setTotal(jsonOrderDetails.getString("total"));
                        } else {
                            order_list.setTotal("");
                        }
                        if (!jsonOrderDetails.isNull("delivery_type")) {
                            order_list.setDelivery_type(jsonOrderDetails.getString("delivery_type"));
                        } else {
                            order_list.setDelivery_type("");
                        }
                        if (!jsonOrderDetails.isNull("order_type")) {
                            order_list.setOrder_type(jsonOrderDetails.getString("order_type"));
                        } else {
                            order_list.setOrder_type("");
                        }
                        if (!jsonOrderDetails.isNull("schedule_status")) {
                            order_list.setSchedule_status(jsonOrderDetails.getString("schedule_status"));
                        } else {
                            order_list.setSchedule_status("");
                        }
                        if (!jsonOrderDetails.isNull("schedule_time")) {
                            order_list.setSchedule_time(jsonOrderDetails.getString("schedule_time"));
                        } else {
                            order_list.setSchedule_time("");
                        }
                        if (!jsonOrderDetails.isNull("schedule_date")) {
                            order_list.setSchedule_date(jsonOrderDetails.getString("schedule_date"));
                        } else {
                            order_list.setSchedule_date("");
                        }

                        order_lists.add(order_list);
                    }
                }

            }
            return order_lists;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getOrderTotalCount(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (!jsonObject.isNull("total")) {
                return jsonObject.getInt("total");
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static Home_page getHomePage(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Home_page home_page = new Home_page();
            JSONObject store_info = jsonObject.getJSONObject("store_info");
            home_page.setHost_name(store_info.getString("host_name"));
            home_page.setImage(store_info.getString("image"));
            home_page.setTotal_sales(jsonObject.getString("total_sales"));
            home_page.setTotal_complete_sales(jsonObject.getString("total_completed_sales"));
            home_page.setTotal_orders(jsonObject.getString("total_orders"));
            home_page.setTotal_products(jsonObject.getString("total_products"));

            return home_page;
        } catch (JSONException e) {
            // e.printStackTrace();
            return null;
        }
    }

    public static Order_Info getOrderInfo(String response) {
        Order_Info order_info = new Order_Info();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject Order_Info = jsonObject.getJSONObject("info");
            if (!Order_Info.isNull("firstname")) {
                order_info.setFirstname(Order_Info.getString("firstname"));
            } else {
                order_info.setFirstname("");
            }
            if (!Order_Info.isNull("lastname")) {
                order_info.setLastname(Order_Info.getString("lastname"));
            } else {
                order_info.setLastname("");
            }
            if (!Order_Info.isNull("email")) {
                order_info.setEmail(Order_Info.getString("email"));
            } else {
                order_info.setEmail("");
            }
            if (!Order_Info.isNull("comment")) {
                order_info.setComment(Order_Info.getString("comment"));
            } else {
                order_info.setComment("");
            }
            if (!Order_Info.isNull("telephone")) {
                order_info.setPhone(Order_Info.getString("telephone"));
            } else {
                order_info.setPhone("");
            }

            if (!Order_Info.isNull("payment_address")) {
                order_info.setAddress(Order_Info.getString("payment_address"));
            } else {
                order_info.setAddress("");
            }
            if (!Order_Info.isNull("house_number")) {
                order_info.setFlat_no(Order_Info.getString("house_number"));
            } else {
                order_info.setFlat_no("");
            }

            if (!Order_Info.isNull("order_status_id")) {
                order_info.setOrder_status_id(Order_Info.getString("order_status_id"));
            } else {
                order_info.setOrder_status_id("");
            }
            if (!jsonObject.isNull("order_id")) {
                order_info.setOrder_id(jsonObject.getString("order_id"));
            } else {
                order_info.setOrder_id("");
            }

            if (!Order_Info.isNull("date_added")) {
                order_info.setDate_added(Order_Info.getString("date_added"));
            } else {
                order_info.setDate_added("");
            }
            if (!Order_Info.isNull("time_added")) {
                order_info.setTime_added(Order_Info.getString("time_added"));
            } else {
                order_info.setTime_added("");
            }
            if (!Order_Info.isNull("status")) {
                order_info.setStatus(Order_Info.getString("status"));
            } else {
                order_info.setStatus("");
            }
            if (!Order_Info.isNull("delivery_type")) {
                order_info.setDelivery_type(Order_Info.getString("delivery_type"));
            } else {
                order_info.setDelivery_type("");
            }

            if (!Order_Info.isNull("payment_method")) {
                order_info.setPayment_type(Order_Info.getString("payment_method"));
            } else {
                order_info.setPayment_type("");
            }

            if (!Order_Info.isNull("date_delivery")) {
                order_info.setDelivery_time(Order_Info.getString("date_delivery"));
            } else {
                order_info.setDelivery_time("");
            }
            if (!Order_Info.isNull("order_type")) {
                order_info.setOrder_type(Order_Info.getString("order_type"));
            } else {
                order_info.setOrder_type("");
            }

            if (!Order_Info.isNull("vendor_type")) {
                order_info.setVendor_type_id(Order_Info.getString("vendor_type"));
            } else {
                order_info.setVendor_type_id("");
            }
            if (!Order_Info.isNull("schedule_status")) {
                order_info.setSchedule_status(Order_Info.getString("schedule_status"));
            } else {
                order_info.setSchedule_status("");
            }
            if (!Order_Info.isNull("schedule_time")) {
                order_info.setSchedule_time(Order_Info.getString("schedule_time"));
            } else {
                order_info.setSchedule_time("");
            }
            if (!Order_Info.isNull("schedule_date")) {
                order_info.setSchedule_date(Order_Info.getString("schedule_date"));
            } else {
                order_info.setSchedule_date("");
            }

            JSONArray Order_Product = Order_Info.getJSONArray("products");
            ArrayList<Delivery_product_details> delivery_product_details = new ArrayList<>();
            for (int i = 0; i < Order_Product.length(); i++) {
                JSONObject jsonOrderProduct = Order_Product.getJSONObject(i);

                Delivery_product_details delivery_product_detail = new Delivery_product_details();
                if (!jsonOrderProduct.isNull("name")) {
                    delivery_product_detail.setName(jsonOrderProduct.getString("name"));
                } else {
                    delivery_product_detail.setName("");

                }
                if (!jsonOrderProduct.isNull("quantity")) {
                    delivery_product_detail.setQuantity(jsonOrderProduct.getString("quantity"));
                } else {
                    delivery_product_detail.setQuantity("");

                }
                if (!jsonOrderProduct.isNull("image")) {
                    delivery_product_detail.setImage(jsonOrderProduct.getString("image"));
                } else {
                    delivery_product_detail.setImage("");

                }
                if (!jsonOrderProduct.isNull("total")) {
                    delivery_product_detail.setTotal(jsonOrderProduct.getString("total"));
                } else {
                    delivery_product_detail.setTotal("");
                }

                if (!jsonOrderProduct.isNull("option")) {

                    JSONArray array = jsonOrderProduct.getJSONArray("option");
                    ArrayList<OptionValueModel> optionValueModels = new ArrayList<>();

                    if (array.length() > 0) {

                        for (int j = 0; j < array.length(); j++) {

                            JSONObject object = array.getJSONObject(j);
                            OptionValueModel optionValueModel = new OptionValueModel();

                            if (!object.isNull("option_name")) {
                                optionValueModel.setName(object.getString("option_name"));
                            } else {
                                optionValueModel.setName(object.getString(""));
                            }

                            if (!object.isNull("option_value")) {
                                optionValueModel.setValue(object.getString("option_value"));
                            } else {
                                optionValueModel.setValue(object.getString(""));
                            }
                            optionValueModels.add(optionValueModel);
                            delivery_product_detail.setOptionValueModels(optionValueModels);
                        }
                    }
                }

                delivery_product_details.add(delivery_product_detail);
            }
            order_info.setDelivery_product_details(delivery_product_details);
            JSONArray Order_total = Order_Info.getJSONArray("totals");
            ArrayList<Order_total_list> orderTotalLists = new ArrayList<>();
            for (int i = 0; i < Order_total.length(); i++) {
                Order_total_list order_total = new Order_total_list();
                JSONObject jsonOrderTotal = Order_total.getJSONObject(i);
                if (!jsonOrderTotal.isNull("text")) {
                    order_total.setText(jsonOrderTotal.getString("text"));
                } else {
                    order_total.setText("");

                }
                if (!jsonOrderTotal.isNull("title")) {
                    order_total.setTitle(jsonOrderTotal.getString("title"));
                } else {
                    order_total.setTitle("");

                }
                orderTotalLists.add(order_total);

            }
            order_info.setOrder_total_lists(orderTotalLists);

            JSONArray jsonArray = Order_Info.getJSONArray("histories");
            ArrayList<Order_status_histories> Order_status_histories = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Order_status_histories order_status_histories = new Order_status_histories();
                if (!jsonObject1.isNull("date_added")) {
                    order_status_histories.setDate_added(jsonObject1.getString("date_added"));
                } else {
                    order_status_histories.setDate_added("");
                }
                if (!jsonObject1.isNull("status")) {
                    order_status_histories.setStatus(jsonObject1.getString("status"));
                } else {
                    order_status_histories.setStatus("");
                }
                if (!jsonObject1.isNull("comment")) {
                    order_status_histories.setComment(jsonObject1.getString("comment"));
                } else {
                    order_status_histories.setComment("");
                }
                Order_status_histories.add(order_status_histories);
            }
            order_info.setOrder_status_histories(Order_status_histories);

            return order_info;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<OrderCancelResason> orderCancelResasonArrayList(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray Order_status = jsonObject.getJSONArray("cancel_reason");
            ArrayList<OrderCancelResason> orderCancelResasonArrayList = new ArrayList<>();
            for (int i = 0; i < Order_status.length(); i++) {
                OrderCancelResason order_status = new OrderCancelResason();
                JSONObject jsonOrderStatus = Order_status.getJSONObject(i);
                if (!jsonOrderStatus.isNull("reason")) {
                    order_status.setReason(jsonOrderStatus.getString("reason"));
                } else {
                    order_status.setReason("");
                }
                if (!jsonOrderStatus.isNull("reason_id")) {
                    order_status.setId(jsonOrderStatus.getString("reason_id"));
                } else {
                    order_status.setId("");
                }
                orderCancelResasonArrayList.add(order_status);
            }
            return orderCancelResasonArrayList;
        } catch (JSONException e) {
            // e.printStackTrace();
            return null;
        }

    }

    public static Restaurant_Info getStoreInfo(String response) {

        Restaurant_Info restaurant_info = new Restaurant_Info();

        try {
            JSONObject jsonObject = new JSONObject(response);
            restaurant_info.setNon_available_date(jsonObject.getString("non_available_date"));
            JSONObject StoreInfo = jsonObject.getJSONObject("store_info");
            StoreInfo storeInfo = new StoreInfo();
            if (!StoreInfo.isNull("name")) {
                storeInfo.setName(StoreInfo.getString("name"));
            } else {
                storeInfo.setName("");

            }
            if (!StoreInfo.isNull("store_type")) {
                storeInfo.setStore_type(StoreInfo.getString("store_type"));
            } else {
                storeInfo.setStore_type("");
            }
            if (!StoreInfo.isNull("payment_detail")) {
                storeInfo.setPan_card_details(StoreInfo.getString("payment_detail"));
            } else {
                storeInfo.setPan_card_details("");
            }
            if (!StoreInfo.isNull("ifsc_code")) {
                storeInfo.setIfsc_code(StoreInfo.getString("ifsc_code"));
            } else {
                storeInfo.setIfsc_code("");
            }
            if (!StoreInfo.isNull("acc_no")) {
                storeInfo.setAccount_num(StoreInfo.getString("acc_no"));
            } else {
                storeInfo.setAccount_num("");
            }
            if (!StoreInfo.isNull("name_arabic")) {
                storeInfo.setName_arabic(StoreInfo.getString("name_arabic"));
            } else {
                storeInfo.setName_arabic("");

            }
            if (!StoreInfo.isNull("owner")) {
                storeInfo.setOwner(StoreInfo.getString("owner"));
            } else {
                storeInfo.setOwner("");

            }
            if (!StoreInfo.isNull("email")) {
                storeInfo.setEmail(StoreInfo.getString("email"));
            } else {
                storeInfo.setEmail("");

            }
            if (!StoreInfo.isNull("telephone")) {
                storeInfo.setTelephone(StoreInfo.getString("telephone"));
            } else {
                storeInfo.setTelephone("");

            }
            if (!StoreInfo.isNull("status")) {
                storeInfo.setStatus(StoreInfo.getString("status"));
            } else {
                storeInfo.setStatus("");

            }
            if (!StoreInfo.isNull("preparing_time")) {
                storeInfo.setPreparing_time(StoreInfo.getString("preparing_time"));
            } else {
                storeInfo.setPreparing_time("");

            }

            if (!StoreInfo.isNull("latitude")) {
                storeInfo.setLatitude(StoreInfo.getDouble("latitude"));
            } else {
                storeInfo.setLatitude(null);

            }
            if (!StoreInfo.isNull("longitude")) {
                storeInfo.setLongitude(StoreInfo.getDouble("longitude"));
            } else {
                storeInfo.setLongitude(null);

            }
            if (!StoreInfo.isNull("image")) {
                storeInfo.setImage(StoreInfo.getString("image"));
            } else {
                storeInfo.setImage("");

            }
            if (!StoreInfo.isNull("logo")) {
                storeInfo.setLogo(StoreInfo.getString("logo"));
            } else {
                storeInfo.setLogo("");

            }
            if (!StoreInfo.isNull("image_path")) {
                storeInfo.setImage_path(StoreInfo.getString("image_path"));
            } else {
                storeInfo.setImage_path("");

            }
            if (!StoreInfo.isNull("logo_path")) {
                storeInfo.setLogo_path(StoreInfo.getString("logo_path"));
            } else {
                storeInfo.setLogo_path("");

            }
            if (!StoreInfo.isNull("pickup")) {
                storeInfo.setPickup(StoreInfo.getInt("pickup"));
            } else {
                storeInfo.setPickup(0);

            }
            if (!StoreInfo.isNull("delivery")) {
                storeInfo.setDelivery(StoreInfo.getInt("delivery"));
            } else {
                storeInfo.setDelivery(0);

            }
            restaurant_info.setStoreInfo(storeInfo);

            JSONArray jsonCuisine = jsonObject.getJSONArray("store_cuisine");
            ArrayList<Store_Cuisine> store_cuisines = new ArrayList<>();
            for (int i = 0; i < jsonCuisine.length(); i++) {
                JSONObject jsonObject1 = jsonCuisine.getJSONObject(i);
                Store_Cuisine storeCuisine = new Store_Cuisine();
                if (!jsonObject1.isNull("cuisine_id")) {
                    storeCuisine.setCuisine_id(jsonObject1.getString("cuisine_id"));
                } else {
                    storeCuisine.setCuisine_id("");
                }
                store_cuisines.add(storeCuisine);
            }
            restaurant_info.setStore_cuisines(store_cuisines);

            JSONArray jsonPayment = jsonObject.getJSONArray("payment_methods");
            ArrayList<Payment_method> paymentMethods = new ArrayList<>();
            for (int i = 0; i < jsonPayment.length(); i++) {
                JSONObject jsonObject1 = jsonPayment.getJSONObject(i);
                Payment_method paymentMethod = new Payment_method();
                if (!jsonObject1.isNull("payment_method_id")) {
                    paymentMethod.setPayment_method_id(jsonObject1.getString("payment_method_id"));
                } else {
                    paymentMethod.setPayment_method_id("");
                }
                if (!jsonObject1.isNull("name")) {
                    paymentMethod.setName(jsonObject1.getString("name"));
                } else {
                    paymentMethod.setName("");
                }
                paymentMethods.add(paymentMethod);
            }
            restaurant_info.setPayment_methods(paymentMethods);

            return restaurant_info;
        } catch (Exception e) {
            // e.printStackTrace();

            return null;
        }


    }

    public static Register_fields getRegisterFields(String response) {

        Register_fields register_fields = new Register_fields();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonCuisine = jsonObject.getJSONArray("cuisines");
            ArrayList<Store_Cuisine> store_cuisines = new ArrayList<>();
            for (int i = 0; i < jsonCuisine.length(); i++) {
                JSONObject jsonObject1 = jsonCuisine.getJSONObject(i);
                Store_Cuisine storeCuisine = new Store_Cuisine();
                if (!jsonObject1.isNull("cuisine_id")) {
                    storeCuisine.setCuisine_id(jsonObject1.getString("cuisine_id"));
                } else {
                    storeCuisine.setCuisine_id("");
                }
                if (!jsonObject1.isNull("name")) {
                    storeCuisine.setName(jsonObject1.getString("name"));
                } else {
                    storeCuisine.setName("");
                }
                storeCuisine.setSelected(false);
                store_cuisines.add(storeCuisine);
            }
            register_fields.setStore_cuisines(store_cuisines);

            JSONArray jsonPayment = jsonObject.getJSONArray("payment_methods");
            ArrayList<Payment_method> paymentMethods = new ArrayList<>();
            for (int i = 0; i < jsonPayment.length(); i++) {
                JSONObject jsonObject1 = jsonPayment.getJSONObject(i);
                Payment_method paymentMethod = new Payment_method();
                if (!jsonObject1.isNull("payment_method_id")) {
                    paymentMethod.setPayment_method_id(jsonObject1.getString("payment_method_id"));
                } else {
                    paymentMethod.setPayment_method_id("");
                }
                if (!jsonObject1.isNull("name")) {
                    paymentMethod.setName(jsonObject1.getString("name"));
                } else {
                    paymentMethod.setName("");
                }
                paymentMethod.setSelected(false);
                paymentMethods.add(paymentMethod);
            }
            register_fields.setPayment_methods(paymentMethods);
            return register_fields;
        } catch (JSONException e) {
            // e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Coupon> getCouponList(String response) {


        try {
            ArrayList<Coupon> coupons = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("coupons");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Coupon coupon = new Coupon();
                if (!jsonObject1.isNull("coupon_id")) {
                    coupon.setCoupon_id(jsonObject1.getString("coupon_id"));
                } else {
                    coupon.setCoupon_id("");

                }
                if (!jsonObject1.isNull("name")) {
                    coupon.setName(jsonObject1.getString("name"));
                } else {
                    coupon.setName("");

                }
                if (!jsonObject1.isNull("code")) {
                    coupon.setCode(jsonObject1.getString("code"));
                } else {
                    coupon.setCode("");

                }
                if (!jsonObject1.isNull("discount")) {
                    coupon.setDiscount(jsonObject1.getString("discount"));
                } else {
                    coupon.setDiscount("");

                }
                if (!jsonObject1.isNull("status")) {
                    coupon.setStatus(jsonObject1.getString("status"));
                } else {
                    coupon.setStatus("");

                }
                coupons.add(coupon);

            }
            return coupons;
        } catch (JSONException e) {
            //  e.printStackTrace();
            return null;
        }
    }

    public static int getCouponTotalCount(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (!jsonObject.isNull("total_coupons")) {
                return jsonObject.getInt("total_coupons");
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static ArrayList<LanguageModel> getLanguage(String response) {

        ArrayList<LanguageModel> languages = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("languages");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                LanguageModel language = new LanguageModel();
                language.setLanguage_id(jsonObject1.getString("language_id"));
                language.setName(jsonObject1.getString("name"));
                language.setCode(jsonObject1.getString("code"));
                if (!jsonObject1.isNull("default")) {
                    language.setDefault(jsonObject1.getBoolean("default"));
                } else {
                    language.setDefault(false);
                }
                languages.add(language);

            }
            return languages;
        } catch (JSONException e) {
            //  e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Section> getSectionList(String response) {


        try {
            ArrayList<Section> sections = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("sections");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Section section = new Section();

                if (!jsonObject1.isNull("name")) {
                    section.setName(jsonObject1.getString("name"));
                } else {
                    section.setName("");

                }
                if (!jsonObject1.isNull("category_id")) {
                    section.setCategory_id(jsonObject1.getString("category_id"));
                } else {
                    section.setCategory_id("");

                }

                if (!jsonObject1.isNull("status")) {
                    section.setStatus(jsonObject1.getString("status"));
                } else {
                    section.setStatus("");

                }
                sections.add(section);

            }
            return sections;
        } catch (JSONException e) {
            //  e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Section> getCategoryList(String response) {


        try {
            ArrayList<Section> sections = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("categories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Section section = new Section();

                if (!jsonObject1.isNull("name")) {
                    section.setName(jsonObject1.getString("name"));
                } else {
                    section.setName("");

                }
                if (!jsonObject1.isNull("category_id")) {
                    section.setCategory_id(jsonObject1.getString("category_id"));
                } else {
                    section.setCategory_id("");

                }

                /*if (!jsonObject1.isNull("status")) {
                    section.setStatus(jsonObject1.getString("status"));
                } else {
                    section.setStatus("");
                }*/
                sections.add(section);

            }
            return sections;
        } catch (JSONException e) {
            //  e.printStackTrace();
            return null;
        }
    }

    public static int getSectionTotalCount(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (!jsonObject.isNull("total_sections")) {
                return jsonObject.getInt("total_sections");
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static Section_Info getSectionInfo(String response) {
        Section_Info section_info = new Section_Info();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonSection = jsonObject.getJSONObject("section_info");
            if (!jsonSection.isNull("status")) {
                section_info.setStatus(jsonSection.getString("status"));
            } else {
                section_info.setStatus("");

            }
            if (!jsonSection.isNull("sort_order")) {
                section_info.setSort_order(jsonSection.getString("sort_order"));
            } else {
                section_info.setSort_order("");

            }
            ArrayList<section_description> section_descriptions = new ArrayList<>();
            JSONArray jsonArray = jsonSection.getJSONArray("section_description");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                section_description section_description = new section_description();
                if (!jsonObject1.isNull("name")) {
                    section_description.setName(jsonObject1.getString("name"));
                } else {
                    section_description.setName("");

                }
                if (!jsonObject1.isNull("language_id")) {
                    section_description.setLanguage_id(jsonObject1.getString("language_id"));
                } else {
                    section_description.setLanguage_id("");

                }
                section_descriptions.add(section_description);
            }
            section_info.setSection_descriptions(section_descriptions);
            return section_info;
        } catch (JSONException e) {
            // e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Coupon_Products> getCouponProducts(String response) {
        ArrayList<Coupon_Products> couponProductsArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("products");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Coupon_Products coupon_products = new Coupon_Products();
                if (!jsonObject1.isNull("product_id")) {
                    coupon_products.setProduct_id(jsonObject1.getString("product_id"));
                } else {
                    coupon_products.setProduct_id("");
                }
                if (!jsonObject1.isNull("name")) {
                    coupon_products.setName(jsonObject1.getString("name"));
                } else {
                    coupon_products.setName("");
                }
                couponProductsArrayList.add(coupon_products);
            }
            return couponProductsArrayList;
        } catch (JSONException e) {
            //  e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ArrayList<Model_ReportOrderHolder> getReportOrderList(String response) {

        try {
            ArrayList<Model_ReportOrderHolder> reportList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);

            Model_ReportOrderHolder model_reportTotalHolder = new Model_ReportOrderHolder();

            model_reportTotalHolder.setType(Constant.REPORT_TYPE_TOTAL);

            if (!jsonObject.isNull("total")) {
                model_reportTotalHolder.setTotal_order(jsonObject.getInt("total"));
            } else {
                model_reportTotalHolder.setTotal_order(0);

            }
            if (!jsonObject.isNull("total_order_amount")) {
                model_reportTotalHolder.setTotal_price(jsonObject.getString("total_order_amount"));
            } else {
                model_reportTotalHolder.setTotal_price("");

            }
            if (!jsonObject.isNull("total_order_products")) {
                model_reportTotalHolder.setTotal_products(jsonObject.getInt("total_order_products"));
            } else {
                model_reportTotalHolder.setTotal_products(0);
            }

            reportList.add(model_reportTotalHolder);

            if (!jsonObject.isNull("orders")) {
                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Model_ReportOrderHolder model_reportOrderHolder = new Model_ReportOrderHolder();

                    model_reportOrderHolder.setType(Constant.REPORT_TYPE_ORDER);

                    Model_ReportSingleOrder model_reportSingleOrder = new Model_ReportSingleOrder();

                    if (!jsonObject1.isNull("order_id")) {
                        model_reportSingleOrder.setOrder_id(jsonObject1.getString("order_id"));
                    } else {
                        model_reportSingleOrder.setOrder_id("");
                    }
                    if (!jsonObject1.isNull("customer")) {
                        model_reportSingleOrder.setCustomerName(jsonObject1.getString("customer"));
                    } else {
                        model_reportSingleOrder.setCustomerName("");
                    }
                    if (!jsonObject1.isNull("status")) {
                        model_reportSingleOrder.setStatus(jsonObject1.getString("status"));
                    } else {
                        model_reportSingleOrder.setStatus("");
                    }
                    if (!jsonObject1.isNull("products")) {
                        model_reportSingleOrder.setProducts(jsonObject1.getString("products"));
                    } else {
                        model_reportSingleOrder.setProducts("");
                    }
                    if (!jsonObject1.isNull("total")) {
                        model_reportSingleOrder.setTotal(jsonObject1.getString("total"));
                    } else {
                        model_reportSingleOrder.setTotal("");
                    }
                    if (!jsonObject1.isNull("date_added")) {
                        model_reportSingleOrder.setDate_added(jsonObject1.getString("date_added"));
                    } else {
                        model_reportSingleOrder.setDate_added("");
                    }
                    if (!jsonObject1.isNull("date_modified")) {
                        model_reportSingleOrder.setDate_modified(jsonObject1.getString("date_modified"));
                    } else {
                        model_reportSingleOrder.setDate_modified("");
                    }
                    if (!jsonObject1.isNull("date_delivery")) {
                        model_reportSingleOrder.setDate_delivery(jsonObject1.getString("date_delivery"));
                    } else {
                        model_reportSingleOrder.setDate_delivery("");
                    }
                    if (!jsonObject1.isNull("shipping_code")) {
                        model_reportSingleOrder.setShipping_code(jsonObject1.getString("shipping_code"));
                    } else {
                        model_reportSingleOrder.setShipping_code("");
                    }
                    if (!jsonObject1.isNull("payment_method")) {
                        model_reportSingleOrder.setPayment_method(jsonObject1.getString("payment_method"));
                    } else {
                        model_reportSingleOrder.setPayment_method("");
                    }
                    if (!jsonObject1.isNull("delivery_type")) {
                        model_reportSingleOrder.setDelivery_type(jsonObject1.getString("delivery_type"));
                    } else {
                        model_reportSingleOrder.setDelivery_type("");
                    }
                    if (!jsonObject1.isNull("order_type")) {
                        model_reportSingleOrder.setOrder_type(jsonObject1.getString("order_type"));
                    } else {
                        model_reportSingleOrder.setOrder_type("");
                    }
                    if (!jsonObject1.isNull("restaurant")) {
                        model_reportSingleOrder.setRestaurant(jsonObject1.getString("restaurant"));
                    } else {
                        model_reportSingleOrder.setRestaurant("");
                    }

                    model_reportOrderHolder.setOrderDetail(model_reportSingleOrder);

                    reportList.add(model_reportOrderHolder);

                }

                if (!jsonObject.isNull("today_total")) {
                    model_reportTotalHolder.setTodayTotal(jsonObject.getString("today_total"));
                } else {
                    model_reportTotalHolder.setTodayTotal("0.00");
                }
                if (!jsonObject.isNull("today_sale")) {
                    model_reportTotalHolder.setTodayOrders(jsonObject.getString("today_sale"));
                } else {
                    model_reportTotalHolder.setTodayOrders("0");
                }
                if (!jsonObject.isNull("weekly_total")) {
                    model_reportTotalHolder.setWeekTotal(jsonObject.getString("weekly_total"));
                } else {
                    model_reportTotalHolder.setWeekTotal("0.00");
                }
                if (!jsonObject.isNull("weekly_sale")) {
                    model_reportTotalHolder.setWeekOrders(jsonObject.getString("weekly_sale"));
                } else {
                    model_reportTotalHolder.setWeekOrders("0");
                }
                if (!jsonObject.isNull("monthly_total")) {
                    model_reportTotalHolder.setMonthTotal(jsonObject.getString("monthly_total"));
                } else {
                    model_reportTotalHolder.setMonthTotal("0.00");
                }
                if (!jsonObject.isNull("monthly_sale")) {
                    model_reportTotalHolder.setMonthOrders(jsonObject.getString("monthly_sale"));
                } else {
                    model_reportTotalHolder.setMonthOrders("0");
                }

            }

            return reportList;
        } catch (Exception e) {
            //  e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ArrayList<Model_ReportShipping> getReportShippingList(String response) {

        try {
            ArrayList<Model_ReportShipping> reportList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("orders")) {

                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Model_ReportShipping model_reportShipping = new Model_ReportShipping();

                    if (!jsonObject1.isNull("date_start")) {
                        model_reportShipping.setDateStart(jsonObject1.getString("date_start"));
                    } else {
                        model_reportShipping.setDateStart("");
                    }
                    if (!jsonObject1.isNull("date_end")) {
                        model_reportShipping.setDateEnd(jsonObject1.getString("date_end"));
                    } else {
                        model_reportShipping.setDateEnd("");
                    }
                    if (!jsonObject1.isNull("title")) {
                        model_reportShipping.setTitle(jsonObject1.getString("title"));
                    } else {
                        model_reportShipping.setTitle("");
                    }
                    if (!jsonObject1.isNull("orders")) {
                        model_reportShipping.setOrders(jsonObject1.getString("orders"));
                    } else {
                        model_reportShipping.setOrders("");
                    }
                    if (!jsonObject1.isNull("total")) {
                        model_reportShipping.setTotal(jsonObject1.getString("total"));
                    } else {
                        model_reportShipping.setTotal("");
                    }

                    reportList.add(model_reportShipping);

                }
            }
            return reportList;
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ArrayList<Model_ReportCommission> getReportCommissionList(String response) {

        try {
            ArrayList<Model_ReportCommission> reportList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("orders")) {

                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Model_ReportCommission model_reportCommission = new Model_ReportCommission();
                    if (!jsonObject.isNull("commission_total")) {
                        model_reportCommission.setTotalCommission(jsonObject.getString("commission_total"));
                    } else {
                        model_reportCommission.setTotalCommission("0");
                    }
                    if (!jsonObject1.isNull("order_id")) {
                        model_reportCommission.setOrder_id(jsonObject1.getString("order_id"));
                    } else {
                        model_reportCommission.setOrder_id("");
                    }
                    if (!jsonObject1.isNull("restaurant")) {
                        model_reportCommission.setRestaurant(jsonObject1.getString("restaurant"));
                    } else {
                        model_reportCommission.setRestaurant("");
                    }
                    if (!jsonObject1.isNull("commission")) {
                        model_reportCommission.setCommission(jsonObject1.getString("commission"));
                    } else {
                        model_reportCommission.setCommission("");
                    }
                    if (!jsonObject1.isNull("balance")) {
                        model_reportCommission.setBalance(jsonObject1.getString("balance"));
                    } else {
                        model_reportCommission.setBalance("");
                    }
                    if (!jsonObject1.isNull("total")) {
                        model_reportCommission.setTotal(jsonObject1.getString("total"));
                    } else {
                        model_reportCommission.setTotal("");
                    }

                    reportList.add(model_reportCommission);

                }
            }
            return reportList;
        } catch (Exception e) {
            //  e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ArrayList<Model_ReportCoupon> getReportCouponList(String response) {

        try {
            ArrayList<Model_ReportCoupon> reportList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("coupons")) {

                JSONArray jsonArray = jsonObject.getJSONArray("coupons");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Model_ReportCoupon model_reportCoupon = new Model_ReportCoupon();

                    if (!jsonObject1.isNull("name")) {
                        model_reportCoupon.setCoupon_name(jsonObject1.getString("name"));
                    } else {
                        model_reportCoupon.setCoupon_name("");
                    }
                    if (!jsonObject1.isNull("code")) {
                        model_reportCoupon.setCode(jsonObject1.getString("code"));
                    } else {
                        model_reportCoupon.setCode("");
                    }
                    if (!jsonObject1.isNull("orders")) {
                        model_reportCoupon.setOrders(jsonObject1.getString("orders"));
                    } else {
                        model_reportCoupon.setOrders("");
                    }
                    if (!jsonObject1.isNull("total")) {
                        model_reportCoupon.setTotal(jsonObject1.getString("total"));
                    } else {
                        model_reportCoupon.setTotal("");
                    }

                    reportList.add(model_reportCoupon);

                }
            }
            return reportList;
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ArrayList<Model_ReportProducts> getReportProductsList(String response) {

        try {
            ArrayList<Model_ReportProducts> reportList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("products")) {

                JSONArray jsonArray = jsonObject.getJSONArray("products");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Model_ReportProducts model_reportProducts = new Model_ReportProducts();

                    if (!jsonObject1.isNull("name")) {
                        model_reportProducts.setProduct_name(jsonObject1.getString("name"));
                    } else {
                        model_reportProducts.setProduct_name("");
                    }
                    if (!jsonObject1.isNull("quantity")) {
                        model_reportProducts.setQuantity(jsonObject1.getString("quantity"));
                    } else {
                        model_reportProducts.setQuantity("");
                    }
                    if (!jsonObject1.isNull("total")) {
                        model_reportProducts.setTotal(jsonObject1.getString("total"));
                    } else {
                        model_reportProducts.setTotal("");
                    }

                    reportList.add(model_reportProducts);

                }
            }
            return reportList;
        } catch (Exception e) {
            //  e.printStackTrace();
            return null;
        }
    }

    public static int getTotalCount(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("total")) {
                return jsonObject.getInt("total");
            } else {
                return 0;
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return 0;
        }
    }

    public static ArrayList<Product_List> getProductList(String response) {

        try {
            ArrayList<Product_List> product_lists = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("products");
            if (jsonArray != null) {
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Product_List product_list = new Product_List();
                        if (!jsonObject1.isNull("product_id")) {
                            product_list.setProduct_id(jsonObject1.getString("product_id"));
                        } else {
                            product_list.setProduct_id("");
                        }
                        if (!jsonObject1.isNull("image")) {
                            product_list.setImage(jsonObject1.getString("image"));
                        } else {
                            product_list.setImage("");
                        }
                        if (!jsonObject1.isNull("name")) {
                            product_list.setName(jsonObject1.getString("name"));
                        } else {
                            product_list.setName("");
                        }
                        if (!jsonObject1.isNull("price")) {
                            product_list.setPrice(jsonObject1.getString("price"));
                        } else {
                            product_list.setPrice("");
                        }
                        if (!jsonObject1.isNull("quantity")) {
                            product_list.setQuantity(jsonObject1.getString("quantity"));
                        } else {
                            product_list.setQuantity("");
                        }
                        if (!jsonObject1.isNull("status")) {
                            product_list.setStatus(jsonObject1.getString("status"));
                        } else {
                            product_list.setStatus("");
                        }
                        product_lists.add(product_list);
                    }
                }
            }

            return product_lists;
        } catch (Exception e) {
            //  e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Order_Status> getOrderStatus(String response) {

        ArrayList<Order_Status> order_statuses = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray Order_status = jsonObject.getJSONArray("order_statuses");

            Order_Status order_status1 = new Order_Status();
            order_status1.setOrder_ststus_id("");
            order_status1.setName("Please Select");
            order_statuses.add(order_status1);

            for (int i = 0; i < Order_status.length(); i++) {
                Order_Status order_status = new Order_Status();
                JSONObject jsonOrderStatus = Order_status.getJSONObject(i);

                if (!jsonOrderStatus.isNull("vendor_status_id")) {
                    order_status.setOrder_ststus_id(jsonOrderStatus.getString("vendor_status_id"));
                } else {
                    order_status.setOrder_ststus_id("");
                }
                if (!jsonOrderStatus.isNull("name")) {
                    order_status.setName(jsonOrderStatus.getString("name"));
                } else {
                    order_status.setName("");
                }
                order_statuses.add(order_status);

            }
            return order_statuses;
        } catch (JSONException e) {
            // e.printStackTrace();
            return null;
        }

    }

    @Nullable
    public static ArrayList<Model_Option> getOptionList(String response) {
        try {
            ArrayList<Model_Option> categoryList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("options")) {

                JSONArray jsonArray = jsonObject.getJSONArray("options");
                if (jsonArray != null) {
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Model_Option model_option = new Model_Option();

                            if (!jsonObject1.isNull("option_id")) {
                                model_option.setOptionId(jsonObject1.getInt("option_id"));
                            } else {
                                model_option.setOptionId(0);
                            }
                            if (!jsonObject1.isNull("name")) {
                                model_option.setOptionName(jsonObject1.getString("name"));
                            } else {
                                model_option.setOptionName("");
                            }
                            if (!jsonObject1.isNull("sort_order")) {
                                model_option.setSortOrder(jsonObject1.getInt("sort_order"));
                            } else {
                                model_option.setSortOrder(0);
                            }
                            if (!jsonObject1.isNull("option_value")) {
                                model_option.setOptionValue(jsonObject1.getInt("option_value"));
                            } else {
                                model_option.setOptionValue(0);
                            }

                            categoryList.add(model_option);
                        }
                    }
                }
            }
            return categoryList;
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static Model_OptionDetail getOptionInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("option_info")) {

                JSONObject jsonCategoryInfo = jsonObject.getJSONObject("option_info");
                if (jsonCategoryInfo != null) {
                    Model_OptionDetail model_OptionInfo = new Model_OptionDetail();

                    if (!jsonCategoryInfo.isNull("option_id")) {
                        model_OptionInfo.setOptionId(jsonCategoryInfo.getInt("option_id"));
                    } else {
                        model_OptionInfo.setOptionId(0);
                    }
                    if (!jsonCategoryInfo.isNull("sort_order")) {
                        model_OptionInfo.setSortOrder(jsonCategoryInfo.getInt("sort_order"));
                    } else {
                        model_OptionInfo.setSortOrder(0);
                    }
                    if (!jsonCategoryInfo.isNull("type")) {
                        model_OptionInfo.setOptionType(jsonCategoryInfo.getString("type"));
                    } else {
                        model_OptionInfo.setOptionType("");
                    }
                    if (!jsonCategoryInfo.isNull("name")) {
                        model_OptionInfo.setOptionValue(jsonCategoryInfo.getString("name"));
                    } else {
                        model_OptionInfo.setOptionValue("");
                    }
                    model_OptionInfo.setType("OptionName");
                    model_OptionInfo.setPostType(false);
                    return model_OptionInfo;

                }
            }
            return null;
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ArrayList<Model_OptionDetail> getOptionValueList(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            ArrayList<Model_OptionDetail> optionDetailList = new ArrayList<>();
            if (!jsonObject.isNull("option_values")) {

                JSONArray jsonArray = jsonObject.getJSONArray("option_values");
                if (jsonArray != null) {
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonOptionInfo = jsonArray.getJSONObject(i);
                            if (jsonOptionInfo != null) {
                                Model_OptionDetail model_optionDetail = new Model_OptionDetail();

                                if (!jsonOptionInfo.isNull("name")) {
                                    model_optionDetail.setOptionValue(jsonOptionInfo.getString("name"));
                                } else {
                                    model_optionDetail.setOptionValue("");
                                }
                                if (!jsonOptionInfo.isNull("image")) {
                                    model_optionDetail.setType(jsonOptionInfo.getString("image"));
                                } else {
                                    model_optionDetail.setImage("");
                                }
                                if (!jsonOptionInfo.isNull("option_value_id")) {
                                    model_optionDetail.setOptionValueId(jsonOptionInfo.getInt("option_value_id"));
                                } else {
                                    model_optionDetail.setOptionValueId(0);
                                }
                                if (!jsonOptionInfo.isNull("sort_order")) {
                                    model_optionDetail.setSortOrder(jsonOptionInfo.getInt("sort_order"));
                                } else {
                                    model_optionDetail.setSortOrder(0);
                                }
                                model_optionDetail.setType("OptionValue");
                                model_optionDetail.setPostType(false);
                                optionDetailList.add(model_optionDetail);
                            }
                        }
                    }
                }
            }
            return optionDetailList;
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ArrayList<Model_Status> getOrderStatusListHome(Activity activity, String response) {

        try {
            ArrayList<Model_Status> groupByList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(response);

            if (!jsonObject.isNull("order_statuses")) {

                JSONArray jsonArray = jsonObject.getJSONArray("order_statuses");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Model_Status model_status = new Model_Status();

                    if (!jsonObject1.isNull("vendor_status_id")) {
                        if (!jsonObject1.isNull("name")) {
                            String name = jsonObject1.getString("name");
                            if (name.length() > 0) {
                                if (name.toLowerCase().equals("received")) {
                                    model_status.setName("New");
                                } else {
                                    model_status.setName(name);
                                }
                            } else {
                                model_status.setName(name);
                            }
                        } else {
                            model_status.setName("");
                        }
                        if (!jsonObject1.isNull("vendor_status_id")) {
                            model_status.setValue(jsonObject1.getString("vendor_status_id"));
                        } else {
                            model_status.setValue("");
                        }
                        if (Constant.DataGetValue(activity, "vendor_type").equals("2")) {
                            if (!jsonObject1.getString("vendor_status_id").equals("3")) { //preparing
                                groupByList.add(model_status);
                            }
                        } else {
                            groupByList.add(model_status);
                        }
                    }
                }
            }
            return groupByList;
        } catch (Exception e) {
            //  e.printStackTrace();
            return null;
        }
    }

}


