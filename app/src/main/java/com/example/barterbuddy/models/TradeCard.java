package com.example.barterbuddy.models;

public class TradeCard {
    private String offeringImageUrl;
    private String posterImageUrl;
    private String poster;
    private String offerer;
    private float money;

    public TradeCard(String offeringImage, String posterImage, String poster, String offerer, float money) {
        this.offeringImageUrl = offeringImage;
        this.posterImageUrl = posterImage;
        this.poster = poster;
        this.offerer = offerer;
        this.money = money;
    }

    public String getOfferingImageUrl() {
        return offeringImageUrl;
    }

    public void setOfferingImageUrl(String offeringImageUrl) {
        this.offeringImageUrl = offeringImageUrl;
    }

    public String getPosterImageUrl() {
        return posterImageUrl;
    }

    public void setPosterImageUrl(String posterImageUrl) {
        this.posterImageUrl = posterImageUrl;
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
