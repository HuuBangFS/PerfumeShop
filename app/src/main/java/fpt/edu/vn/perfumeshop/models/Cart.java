package fpt.edu.vn.perfumeshop.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Cart {
    @PrimaryKey
    private long id;
    private long idPerfume;
    private long idCustomer;
    @ColumnInfo(name = "perfume_name")
    private String perfumeName;
    private String description;
    @ColumnInfo(name = "image_url")
    private String imageUrl;
    @ColumnInfo(name = "unit_price")
    private double unitPrice;
    private int quantity;

    public Cart(long id, String perfumeName, String description, String imageUrl, double unitPrice, int quantity, long idPerfume, long idCustomer) {
        this.id = id;
        this.perfumeName = perfumeName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.idPerfume = idPerfume;
        this.idCustomer = idCustomer;
    }

    public long getIdPerfume() {
        return idPerfume;
    }

    public long getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(long idCustomer) {
        this.idCustomer = idCustomer;
    }

    public void setIdPerfume(long idPerfume) {
        this.idPerfume = idPerfume;
    }

    public long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPerfumeName() {
        return perfumeName;
    }

    public void setPerfumeName(String perfumeName) {
        this.perfumeName = perfumeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
