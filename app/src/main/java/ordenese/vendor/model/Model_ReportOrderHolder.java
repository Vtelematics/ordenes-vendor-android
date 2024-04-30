package ordenese.vendor.model;

/**
 * Created by user on 12/5/2018.
 * Order Report type holder
 */

public class Model_ReportOrderHolder {

    private String type, total_price,todayTotal, weekTotal, monthTotal, todayOrders, weekOrders, monthOrders;
    private int total_order, total_products;
    private Model_ReportSingleOrder orderDetail;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public int getTotal_order() {
        return total_order;
    }

    public void setTotal_order(int total_order) {
        this.total_order = total_order;
    }

    public int getTotal_products() {
        return total_products;
    }

    public void setTotal_products(int total_products) {
        this.total_products = total_products;
    }

    public Model_ReportSingleOrder getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(Model_ReportSingleOrder orderDetail) {
        this.orderDetail = orderDetail;
    }

    public String getTodayTotal() {
        return todayTotal;
    }

    public void setTodayTotal(String todayTotal) {
        this.todayTotal = todayTotal;
    }

    public String getWeekTotal() {
        return weekTotal;
    }

    public void setWeekTotal(String weekTotal) {
        this.weekTotal = weekTotal;
    }

    public String getMonthTotal() {
        return monthTotal;
    }

    public void setMonthTotal(String monthTotal) {
        this.monthTotal = monthTotal;
    }

    public String getTodayOrders() {
        return todayOrders;
    }

    public void setTodayOrders(String todayOrders) {
        this.todayOrders = todayOrders;
    }

    public String getWeekOrders() {
        return weekOrders;
    }

    public void setWeekOrders(String weekOrders) {
        this.weekOrders = weekOrders;
    }

    public String getMonthOrders() {
        return monthOrders;
    }

    public void setMonthOrders(String monthOrders) {
        this.monthOrders = monthOrders;
    }
}
