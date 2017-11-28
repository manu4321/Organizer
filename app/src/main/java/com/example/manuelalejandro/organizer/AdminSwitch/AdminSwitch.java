package com.example.manuelalejandro.organizer.AdminSwitch;

/**
 * Created by mfreites on 2017-07-09.
 */

public class AdminSwitch {
    private int timesClicked = 0;
    private long previousTime = 0;
    private long nextTime = 0;
    private boolean isAdmin = false;
    public AdminSwitch() {
        previousTime = System.nanoTime();
        nextTime = previousTime;
        timesClicked = 0;
    }

    public void setTimesClicked(int timesClicked) {
        this.timesClicked = timesClicked;
    }

    public int getTimesClicked() {
        return timesClicked;
    }

    public long getPreviousTime() {
        return previousTime;
    }

    public void setPreviousTime(long previousTime) {
        this.previousTime = previousTime;
    }

    public long getNextTime() {
        return nextTime;
    }

    public void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
