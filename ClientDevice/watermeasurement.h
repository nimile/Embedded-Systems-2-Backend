#ifndef WATER_MEASUREMENT_H_

#include <Wire.h>  
#include <VL53L0X.h>  

#define DISTANCE_CALIBRATION_VALUE (0.89)
#define AMOUNT_MEASUREMENTS 10


// DO NOT MODIFY THE CODE BELOW

#define SCK_PIN 21
#define SCL_PIN 22
#define WATER_READ_TIMEOUT 500
#define WATER_MEASUREMENT_TIMING_BUDGET 200000
#define WATER_SIGNAL_RATE_LIMIT 0.5
 

extern void LOGn(const char *fmt, ...);

/**
 * @brief This struct contains information about the water measurement
 */
typedef struct water_sensor_data_t{
    /// Maximum capacity
    int max;

    /// Minimum capacity
    int min;

    /// Current value
    int current;
} water_sensor_data;

typedef water_sensor_data WaterSensorData;

VL53L0X water_sensor;

/**
 * @brief Initializes the water measurement
 * 
 * @param retries Amount of retries until the initialization fails, default 10
 * @return When the component is initialized true will be returned.
 */
bool initialize_water_sensor_module(int retries = 10){
    bool result = false;
    LOGn("[VL53L0X] Initialize module");

    LOGn("[VL53L0X] Start I2C wire");
    Wire.begin(SCK_PIN, SCL_PIN);

    LOGn("[VL53L0X] Module settings");
    LOGn("[VL53L0X] Pin SCK:             %i", SCK_PIN);
    LOGn("[VL53L0X] Pin SCL:             %i", SCL_PIN);
    LOGn("[VL53L0X] Read timeout:        %i", WATER_READ_TIMEOUT);
    LOGn("[VL53L0X] timing budget:       %i", WATER_MEASUREMENT_TIMING_BUDGET);
    LOGn("[VL53L0X] signal rate limit:   %i", WATER_SIGNAL_RATE_LIMIT);
    LOGn("[VL53L0X] Calibration:         %i", DISTANCE_CALIBRATION_VALUE);
    LOGn("[VL53L0X] amount measurements: %i", AMOUNT_MEASUREMENTS);

    
    LOGn("[VL53L0X] Start initialization, max retries are %i", retries);
    water_sensor.setTimeout(500);
    for(int i = 0; i < retries; ++i){
        LOGn("[VL53L0X] Current attempt %i: ", i);
        delay(500);
        if(water_sensor.init()){
            water_sensor.setTimeout(WATER_READ_TIMEOUT);
            water_sensor.setMeasurementTimingBudget(WATER_MEASUREMENT_TIMING_BUDGET);
            water_sensor.setSignalRateLimit(WATER_SIGNAL_RATE_LIMIT); 
            water_sensor.startContinuous();
            result = true;
        }
    }
    
    if(result){
        LOGn("[VL53L0X] Module initialized");
    }else{
        LOGn("[VL53L0X] Module initialization failed");
    }
    return result;
}

/**
 * @brief Reads a water level and stores the value inside the water_data_m attribute
 * A range test is used for the measurement, the VL53L0X starts a ranging test.
 * After each test the value is added to a temporary variable. Before assigning
 * a value to the water_data_m attribute the temporary sum is divided by the 
 * amount of measurements. 
 * NOTE 
 * - The AMOUNT_MEASUREMENTS definition controls how many measurements are done.
 * - When an invalid sum is yield the VL530X will be reinitialized and the
 *      measurement is done again.
 */
int read_water_level(){
    LOGn("[VL53L0X]Reading water level");

    int sum = 0;
    for(int i = 0; i < AMOUNT_MEASUREMENTS; ++i){
        if (!water_sensor.timeoutOccurred()) { 
            sum += water_sensor.readRangeContinuousMillimeters() * DISTANCE_CALIBRATION_VALUE;
        }
    }

    return sum / AMOUNT_MEASUREMENTS;
}

#endif // WATER_MEASUREMENT_H_