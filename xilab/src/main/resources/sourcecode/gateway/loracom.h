#ifndef LORA_COM_H_
#define LORA_COM_H_

#include "utils.h"
#include <SPI.h>
#include <LoRa.h>

namespace xilab{
    namespace network{
        namespace lora{
            typedef void (*lora_callback)(const char* data, int size);
          
            enum loraResults{
                OK = 0,
                NOT_INITIALIZED = 201,
            };
            class LoRaModule{
                public:
                    static LoRaModule& getInstance(){
                        static LoRaModule instance;
                        return instance;
                    }
                private:
                    bool initialized_m = false;
                    network::lora::lora_callback dataReceivedCallback_m = nullptr;
                public:    
                    int init(lora_callback callback = nullptr){
                        LOGn("[LORA MODULE ] Initialize lora module.");

                        dataReceivedCallback_m = callback;

                        LoRa.setPins(configuration::lora::PIN_SS, configuration::lora::PIN_RST, configuration::lora::PIN_DIO0);

                        bool loraStarted = false;
                        for(int i = 0; i < configuration::retries::LORA && !loraStarted; i++){
                            loraStarted = LoRa.begin(configuration::lora::REGION);
                        }
                        if(!loraStarted){
                            return loraResults::NOT_INITIALIZED;
                        }

                        LoRa.onReceive(&getInstance().onDataReceived);
                        initialized_m = true;
                        LOGn("[LORA MODULE ] Lora module initialized.");
                        return loraResults::OK;
                    }


                    bool isInitialized(){
                        return initialized_m;
                    }

                    static void onDataReceived(int packetSize){
                        LOGn("[LORA MODULE ] Received data from %s.", LoRa.packetRssi());

                        char* data = (char*) calloc((packetSize + 1), sizeof(char));
                        for(int i = 0; i < packetSize; i++){
                            data[i] = (char)LoRa.read();
                        }
                        data[packetSize] = 0;
                        if(nullptr != getInstance().dataReceivedCallback_m){
                            getInstance().dataReceivedCallback_m(data, packetSize);
                        }
                    }
            };
        } // namespace lora
    } // namespace network
} // namespace xilab

#endif // LORA_COM_H_