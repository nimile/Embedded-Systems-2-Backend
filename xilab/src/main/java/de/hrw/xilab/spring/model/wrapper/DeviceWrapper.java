package de.hrw.xilab.spring.model.wrapper;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "DeviceWrapper")
public class DeviceWrapper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String uuid;
    private int battery;
    private long latitude;
    private long longitude;
    private int max;
    private int min;
    private int current;
}
