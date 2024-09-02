

package com.greenario;

public class DroneData {
    
    private String data;

    // Constructor
    public DroneData() {
    }

    // Getter and Setter
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    // Example method
    public String processData() {
        // Process the data and return a result
        return "Processed: " + data;
    }
}
