#ifndef _XILAB_WLAN_H_
#define _XILAB_WLAN_H_

#include <SPI.h>
#include <HttpClient.h>
#include <Ethernet.h>
#include <EthernetClient.h>

#include "utils.h"

void LOGn(const char *fmt, ...);

namespace xilab{
    namespace server{
        static constexpr const char* SERVER_URL = "192.168.2.104";
        static constexpr const int PORT = 8080;
        static constexpr const char* API_USER_NAME = "admin";
        static constexpr const char* API_USER_PASSWORD = "admin";
    }

    namespace credentials{
        static constexpr const char* SSID = "";
        static constexpr const char* PASSWORD;
    } 
    
    class BackendGlue{
        private:
            bool connected = false;
            uint8_t mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };

            String token;
        public:
            BackendGlue(){}

            void init(){
                if (Ethernet.begin(mac) != 1) {
                    LOGn("[WiFi ] Cannot connect to wifi, please use the init method at a later point");
                }else{
                    refreshToken();
                    connected = true;
                }
            }

            int update(int current, int charge, double longitude, double latitude, const char* uuid){
                // Send update
                if(!connected){
                    LOGn("Device is not connected to any network");
                    return;
                }
                // Build params
                String currentString;
                if(current > 0){
                    currentString = String("waterlevel=") + String(current);
                }else {
                    currentString = "";
                }
                
                String chargeString;
                if(charge > 0){
                    chargeString = String("battery=") + String(charge);
                }else {
                    chargeString = "";
                }
                
                String longitudeString;
                if(longitude > 0){
                    longitudeString = String("longitude=") + String(longitude);
                }else {
                    longitudeString = "";
                }
                
                String latitudeString;
                if(latitude > 0){
                    latitudeString = String("latitude=") + String(latitude);
                }else {
                    latitudeString = "";
                }

                // Build query
                String queryString = currentString + "&" + chargeString + "&" + longitudeString + "&" + latitudeString;
                

                
                EthernetClient c;
                HttpClient http(c);

                // Starting new request
                http.beginRequest();
                http.sendHeader("Authorization", String("Bearer ") + token);
                int result = http.put(server::SERVER_URL, server::PORT, queryString);
                http.endRequest();
                int response = http.responseStatusCode();
                // Not authorized => refresh token
                if(response == 401){
                    refreshToken();
                }

                http.stop();
                return response;
            }

            void refreshToken(){
              
                if(!connected){
                    LOGn("Device is not connected to any network");
                    return;
                }

                EthernetClient c;
                HttpClient http(c);

                http.beginRequest();
                http.sendBasicAuth(server::API_USER_NAME, server::API_USER_PASSWORD);
                int result = http.get(server::SERVER_URL, server::PORT, "/token/create");
                int response = http.responseStatusCode();
                http.endRequest();    

                if(response == 200){
                    // todo
                    // Update token 
                }

                http.stop();
            }
    }

    // the server's disconnected, stop the client:
    client.stop();
    Serial.println();
    Serial.println("disconnected");
  } else {// if not connected:
    Serial.println("connection failed");
  }
}
            }            


    }
}// namespace xilab







#endif // _XILAB_WLAN_H_