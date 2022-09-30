#ifndef _XILAB_BACKEND_GLUE_H_
#define _XILAB_BACKEND_GLUE_H_

#include <ArduinoHttpClient.h>
#include <WiFi101.h>
#include "credentials.h"

namespace xilab{
    namespace server{
        static constexpr const char* SERVER_ADDRESS = "192.168.2.104";
        static constexpr const int PORT  = 8080;
    } // namespace server
    
    enum BackendResults{
        OK = 0,
        NOT_INITIALIZED = 1
    };

    class BackendGlue{
        private:
            bool initialized_m = false;

            WiFiClient wifi;
            HttpClient client = HttpClient(wifi, xilab::server::SERVER_ADDRESS, xilab::server::PORT);
            int status = WL_IDLE_STATUS;
        public:

            int init(){
                  for(int i = 0; i < 10 && status != WL_CONNECTED; i++) {
                    status = WiFi.begin(xilab::credentials::wlan::SSID, xilab::credentials::wlan::PASSWORD);
                }
                initialized_m = true;
                int statusCode = refreshToken();
                return statusCode;
            }

            int send(const char* uuid, int current, int charge, double longitude, double latitude){
                if(!initialized_m){
                    return BackendResults::NOT_INITIALIZED;
                }

                client.beginRequest();
                client.get("/secure");
                client.sendBasicAuth("username", "password"); // send the username and password for authentication
                client.endRequest();

                int statusCode = client.responseStatusCode();
                String response = client.responseBody();
                return (statusCode >= 200 && statusCode < 300) ? BackendResults::OK : statusCode;
            }

            int refreshToken(){
                if(!initialized_m){
                    return BackendResults::NOT_INITIALIZED;
                }

                client.beginRequest();
                client.get("/token/create");  
            
                client.sendBasicAuth(xilab::credentials::server::API_USER_NAME, xilab::credentials::server::API_USER_PASSWORD);
                client.endRequest();

                int statusCode = client.responseStatusCode();
                String response = client.responseBody();
                return (statusCode >= 200 && statusCode < 300) ? BackendResults::OK : statusCode;
            }

            bool isInitialized(){
                return initialized_m;
            }
    }

} // namespace xilab

#endif // _XILAB_BACKEND_GLUE_H_