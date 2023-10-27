package com.example.barterbuddy.models;

public class TradeCard {
    private String offeringImage;
    private String posterImage;
    private String poster;
    private String offerer;
    private float money;

    public TradeCard(String offeringImage, String posterImage, String poster, String offerer, float money) {
        this.offeringImage = offeringImage;
        this.posterImage = posterImage;
        this.poster = poster;
        this.offerer = offerer;
        this.money = money;
    }

    public String getOfferingImage() {
        return offeringImage;
    }

    public void setOfferingImage(String offeringImage) {
        this.offeringImage = offeringImage;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOfferer() {
        return offerer;
    }

    public void setOfferer(String offerer) {
        this.offerer = offerer;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }
}
