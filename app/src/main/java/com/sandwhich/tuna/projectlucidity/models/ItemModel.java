package com.sandwhich.tuna.projectlucidity.models;

public class ItemModel {
    String itemName;
    Double itemPrice;
    int drawableResource;

    public String getItemName() {
        return itemName;
    }

    public ItemModel(String itemName, Double itemPrice, int drawableResource) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.drawableResource = drawableResource;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getDrawableResource() {
        return drawableResource;
    }

    public void setDrawableResource(int drawableResource) {
        this.drawableResource = drawableResource;
    }
}
