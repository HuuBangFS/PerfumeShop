package fpt.edu.vn.perfumeshop.models;

public class OrderDetail {
    private long id;
    private long orderId;
    private long perfumeId;
    private double unitPrice;
    private int quantity;

    public OrderDetail(long orderId, long perfumeId, double unitPrice, int quantity) {
        this.orderId = orderId;
        this.perfumeId = perfumeId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public OrderDetail(long id, long orderId, long perfumeId, double unitPrice, int quantity) {
        this.id = id;
        this.orderId = orderId;
        this.perfumeId = perfumeId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getPerfumeId() {
        return perfumeId;
    }

    public void setPerfumeId(long flowerId) {
        this.perfumeId = perfumeId;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
