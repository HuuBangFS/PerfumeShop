package fpt.edu.vn.perfumeshop.models;

import java.io.Serializable;

public class Perfume implements Serializable {
    private long id;
    private String perfumeName;
    private String description;
    private String imageUrl;
    private double unitPrice;
    private int unitInStock;

    public Perfume(){
    }

    public Perfume(String perfumeName, String description, String imageUrl, double unitPrice, int unitInStock) {
        this.perfumeName = perfumeName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.unitInStock = unitInStock;
    }

    public Perfume(long id, String perfumeName, String description, String imageUrl, double unitPrice, int unitInStock) {
        this.id = id;
        this.perfumeName = perfumeName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.unitInStock = unitInStock;
    }

    public long getId() {
        return id;
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

    public int getUnitInStock() {
        return unitInStock;
    }

    public void setUnitInStock(int unitInStock) {
        this.unitInStock = unitInStock;
    }
}
