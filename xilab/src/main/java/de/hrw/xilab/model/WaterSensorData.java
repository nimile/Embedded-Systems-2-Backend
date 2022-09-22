package de.hrw.xilab.model;

import lombok.Data;

@Data
public class WaterSensorData {
    private int max;
    private int min;
    private int current;
}
