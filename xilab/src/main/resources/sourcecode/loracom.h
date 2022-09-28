#ifndef LORA_COM_H_
#define LORA_COM_H_

#include <SPI.h>
#include <LoRa.h>

// LoRa region of this device
// Europe 866E6
// NA     915E6
// Asia   433E6
#define LORA_REGION 866E6

// LoRa Sync word for this device
#define LORA_SYNC_WORD 0xF3

// DO NOT MODIFY THE FOLLOWING LINES

// LoRa Pin layout
#define LORA_PIN_SS 5
#define LORA_PIN_RST 14
#define LORA_PIN_DIO 2

extern void LOGn(const char *fmt, ...);


/**
 * @brief Initializes the LoRa component
 * This method sets all lora pins and the syncword too.
 * After setting all attributes the LoRa module is started.
 * @param retries Amount of retries until the initializiation fails, default 10
 */
bool initialize_lora_module(int retries = 10){
    bool result = false;
    LOGn("[LoRa   ] Initialize module");
    LoRa.setPins(LORA_PIN_SS, LORA_PIN_RST, LORA_PIN_DIO);
    LoRa.setSyncWord(LORA_SYNC_WORD);

    LOGn("[LoRa   ] Module settings");
    LOGn("[LoRa   ] Pin SS:   %i", LORA_PIN_SS);
    LOGn("[LoRa   ] Pin RST:  %i", LORA_PIN_RST);
    LOGn("[LoRa   ] Pin DI0:  %i", LORA_PIN_DIO);
    LOGn("[LoRa   ] Region:   %i", LORA_REGION);
    LOGn("[LoRa   ] Syncword: %i", LORA_SYNC_WORD);


    LOGn("[LoRa   ] Start initialization, max retries are %i", retries);
    for(int i = 0; i < retries; ++i){
        LOGn("[LoRa   ] Current attempt: %i", i);
        delay(500);
        if(LoRa.begin(LORA_REGION)){
            result = true;
        }
    }

    if(result){
        LOGn("[LoRa   ] Module initialized");
    }else{
        LOGn("[LoRa   ] Module initialization failed");
    }
    return result;
}

/**
 * @brief Broadcast a message via lora
 */
void broadcast_data(const char* data){
    LOGn("[NOTIFY ] Sending data to master device");
    size_t bufferSize = strlen(data);
    LoRa.beginPacket();
    LoRa.print(data);
    LoRa.endPacket();
}

#endif // LORA_COM_H_