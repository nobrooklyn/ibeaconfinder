package local.sandbox.beacon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class DataEntity {
    private Date updatedAt;
    private int state;
    private String uuid;
    private String major;
    private String minor;
    private double distance;
    private int rssi;

    DataEntity(int state, String uuid, String major, String minor, double distance, int rssi) {
        this.updatedAt = new Date();
        this.state = state;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.distance = distance;
        this.rssi = rssi;
    }

    int getState() {
        return state;
    }

    void setState(int state) {
        this.state = state;
    }

    Date getUpdatedAt() {
        return updatedAt;
    }

    String getUpdatedAtStr() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        return df.format(updatedAt);
    }

    String getUuid() {
        return uuid;
    }

    String getMajor() {
        return major;
    }

    String getMinor() {
        return minor;
    }

    double getDistance() {
        return distance;
    }

    String getDistanceStr() {
        return String.format("%.2f", distance);
    }

    int getRssi() {
        return rssi;
    }

    String getRssiStr() {
        return Integer.toString(rssi);
    }

    String getPlace() {
        return "Meeting Room : 2115";
    }
}
