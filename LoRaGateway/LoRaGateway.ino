#define DEBUG
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <stdio.h>

#include "utils.h"
#include "backendglue.h"
#include "loracom.h"

xilab::network::http::BackendGlue backend;
xilab::network::lora::LoRaModule lora;

void LOGn(const char *fmt, ...);

void lora_data_received(const char* data, int size){
    String json(data);
    
    int charge = utils::json::extractInteger(data, "charge");
    int value = utils::json::extractInteger(data, "value");
    double lonitude = utils::json::extractInteger(data, "lonitude");
    double latitude = utils::json::extractInteger(data, "latitude");
    String uuid = utils::json::extractString(data, "uuid");
    backend.send(uuid, value, charge, lonitude, latitude);}

void setup() {
  Serial.begin(115200);
  LOGn("[SETUP DEVICE] Start setup.");
  backend.init();
  lora.init(&lora_data_received);
  LOGn("[SETUP DEVICE] Setup done.");
}

void loop(){
  LOGn("[SETUP DEVICE] ...");
}
