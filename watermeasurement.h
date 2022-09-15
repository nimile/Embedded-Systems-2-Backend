#ifndef WATER_MEASUREMENT_H_

#include <Wire.h>  
#include <VL53L0X.h>  

#define DISTANCE_CALIBRATION_VALUE (0.89)
#define AMOUNT_MEASUREMENTS 10


// DO NOT MODIFY THE CODE BELOW

#define SCK_PIN 21
#define SCL_PIN 22


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


VL53L0X water_sensor;

/**
 * @brief Initializes the water measurement
 * 
 * @param retries Amount of retries until the initialization fails, default 10
 * @return When the component is initialized true will be returned.
 */
bool initialize_water_sensor_module(int retries = 10){
    LOGn("[VL53L0X] Initialize water_sensor");
    LOGn("[VL53L0X] Start I2C wire");
    Wire.begin();
    Wire.setPins(SCK_PIN, SCL_PIN);

    water_sensor.setTimeout(500);
    for(int i = 0; i < retries; ++i){
        LOGn("[VL53L0X] Current attempt %i: ", i);
        if(water_sensor.init()){
            LOGn("Successful initialized");
            water_sensor.setTimeout(500);
            water_sensor.setMeasurementTimingBudget(200000);
            water_sensor.setSignalRateLimit(0.5); 
            water_sensor.startContinuous();
            return true;
        }
        LOGn("Failed");
        delay(500);
    }
    
    LOGn("[VL53L0X] Cannot initialize water_sensor");
    return false;
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