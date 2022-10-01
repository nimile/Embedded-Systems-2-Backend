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
    
    int result = xilab::utils::JsonUtil::getInstance().parse(json);  
    if(result == xilab::utils::JsonResults::OK){
      int charge = xilab::utils::JsonUtil::getInstance().extractInteger(data, "device", "charge");
      String uuid = xilab::utils::JsonUtil::getInstance().extractString(data, "device", "uuid");
      int value = xilab::utils::JsonUtil::getInstance().extractInteger(data, "water", "value");
      double lonitude = xilab::utils::JsonUtil::getInstance().extractInteger(data, "coordinates", "lonitude");
      double latitude = xilab::utils::JsonUtil::getInstance().extractInteger(data, "coordiantes", "latitude");
      backend.send(uuid, value, charge, lonitude, latitude);
    }else{
      LOGn("[JSON PARSE  ] Parsing yields %i for document \"%s\"", result, json);
    }
  }

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
