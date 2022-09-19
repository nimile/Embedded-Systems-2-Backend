#define DEBUG
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <stdio.h>


#include "watermeasurement.h"
#include "loracom.h"

#include <HaevnNotifier.h>

#define BATTERY_PIN 34
#define MAX_VOLTAGE (4.2)

// The following definitions are used as QOL to create
// a better interaction with time values
#define SECOND (1000)
#define MINUTE (60 * SECOND)
#define HOUR (60 * MINUTE)
#define DAY (24 * HOUR)

// Controls when the device should measure and notify the server
// For production remove line 24 - 25 and uncomment line 26
//#define ACTIVITY_THRESHOLD (5 * MINUTE) 
#define ACTIVITY_THRESHOLD (500)


/**
 * @brief This is a helper method
 * It provides a printf style serial printing
 * @param fmt Format
 * @param ... arguments
 */
/*void LOGn(const char *fmt, ...) {
#ifdef DEBUG
    char buff[1024];
    va_list pargs;
    va_start(pargs, fmt);
    vsnprintf(buff, 1024, fmt, pargs);
    va_end(pargs);
    Serial.println(buff);
#endif
}
*/


/**
 * @brief This struct contains information about the device
 */
typedef struct device_data_t{
    /// Name of the device
	const char* name;

    /// UUID of the device
	const char* id;

    /// Current battery voltage in percent
    unsigned short battery;
} device_data;

/**
 * @brief This struct contains the location of the device
 */
typedef struct location_data_t{
    /// Longitude of the location
    long longitude;

    /// Langitude of the location
    long latitude;
}location_data;


water_sensor_data water_data_m;
device_data device_data_m;
location_data location_data_m;

void setup() { 
    // Device name e.g. SouthWest001
    device_data_m.name = "Test Device 0001";
    // UUID e.g. 688b7fb2-92d1-49bb-93bc-2212979ad6ca
    device_data_m.id = "688b7fb2-92d1-49bb-93bc-2212979ad6ca";

    // GPS Coordinates
    location_data_m.latitude = 0;
    location_data_m.longitude = 0;

    // Waterlevel, e.g. watertank boundaries
    water_data_m.max = 15 * 10;
    water_data_m.min = 0;


    Serial.begin(115200);

    initialize_lora_module();
    initialize_water_sensor_module();
    LOGn("[SETUP  ] Device Configuration");
    LOGn("[SETUP  ] Device name: %s", device_data_m.name);
    LOGn("[SETUP  ] Device UUID: %s", device_data_m.id);
    LOGn("[SETUP  ] longitude  : %d", location_data_m.longitude);
    LOGn("[SETUP  ] latitude   : %d", location_data_m.latitude);
    LOGn("[SETUP  ] water min  : %d", water_data_m.min);
    LOGn("[SETUP  ] water max  : %d", water_data_m.max);


    // TODO 
    // REMOVE WHEN PRODUCTION
    setupAndStartWebServer([](AsyncWebServerRequest* request){
        char buff[1024];
        //sprintf(buff, "<html><head><meta http-equiv=\"refresh\" content=\"1\"></head><body><h1 style=\"text-align: center;\">Current water value: %i<h1><h1 style=\"text-align: center;\">Current voltage value: %i<h1></body></html>", water_data_m.current, device_data_m.battery);
        sprintf(buff, "<html><head><meta http-equiv=\"refresh\" content=\"1\"><style>.center {margin: auto;border: 3px solid #73AD21;padding: 10px;}</style></head><body><div class=\"center\"><h1>Device</h1><p>Name: %s</p><p>UUID: %s</p><p>Battery: %d</p></div><div class=\"center\"><h1>Water</h1><p>Max: %d</p><p>Min: %d</p><p>Current: %d</p></div><div class=\"center\"><h1>Location</h1><p>Lat: %ld</p><p>Long: %ld</p></div></body></html>",
            device_data_m.name, device_data_m.id, device_data_m.battery,
            water_data_m.max, water_data_m.min, water_data_m.current,
            location_data_m.longitude, location_data_m.latitude);
        request->send(200, "text/html", buff);
    });
}

/**
 * @brief Measures the voltage supplied by the voltage divider.
 * After the measurement the measured value is mapped to the battery capacity
 * Finally the value is transformed into a percentage space and returned.
 * 
 */
int measure_battery(){
    LOGn("[BATTERY] Read battery");
    float current_battery_voltage = analogRead(BATTERY_PIN);
    float mapped_voltage = map(current_battery_voltage, 0, 4096, 0, 210);
    float actual_volt = (mapped_voltage / 100) * 2;
    float voltagePercentage = (actual_volt / MAX_VOLTAGE) * 100; 
    
    return voltagePercentage;  
}

/**
 * @brief Sends a json document to the LoRa receiver.
 * The JSON object contains of the following elements
 * - device: Device information
 *      - name: Name of the device
 *      - id: UUID of the device
 *      - battery: current battery value
 * - water: Information about the water value
 *      - min: Minimum of water 
 *      - max: Maximum water capacity
 *      - current: Current water level 
 * - location: GPS location of this device
 *      - long: Longitude
 *      - lat: Latitude
 */
void send_data(){
    LOGn("[NOTIFY] Sending data to master device");
    char buffer[255] = {0};
    sprintf(buffer, "{\"device\":{\"name\":\"%s\",\"id\":\"%s\",\"battery\":%d},\"water\":{\"max\":%d,\"min\":%d,\"current\":%d},\"location\":{\"long\":%ld,\"lat\":%ld}}",
    device_data_m.name, device_data_m.id, device_data_m.battery,
    water_data_m.max, water_data_m.min, water_data_m.current,
    location_data_m.longitude, location_data_m.latitude);
    broadcast_data(buffer);
}

/**
 * @brief Broadcast an error code
 *  
 * @param code Code associated with the message, ranging from 0 to 255.
 */
void send_code(int code){
    char buffer[255] = {0};
    sprintf(buffer, "{\"error_code\":%i,\"device\":{\"name\":\"%s\",\"id\":\"%s\",\"battery\":%d},\"location\":{\"long\":%ld,\"lat\":%ld}}",
    code, message,
    device_data_m.name, device_data_m.id, device_data_m.battery,
    location_data_m.longitude, location_data_m.latitude);
    broadcast_data(buffer);
}

void loop() {
    
    device_data_m.battery = measure_battery();
    water_data_m.current = read_water_level();
    send_data();
    
    
    // To ensure an under-voltage protection the esp will go into the deepsleep
    // The master device is notified 10 times with an urgent message
    if(false && device_data_m.battery  <= 3.3){ 
        device_data_m.battery = 0;
        for(int i = 0; i < 10; ++i){
            send_code(0xDE);
            delay(1000);
        }
        esp_deep_sleep_start();
    }
    delay(ACTIVITY_THRESHOLD);    
}