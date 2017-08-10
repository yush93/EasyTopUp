package com.aayush.scanandtopup.extras;

/**
 * Created by aayus on 8/10/2017.
 */

public class HistoryData {

    private String date;
    private String carrier;
    private String pin;

    public HistoryData(String date, String carrier, String pin) {
        this.date = date;
        this.carrier = carrier;
        this.pin = pin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
