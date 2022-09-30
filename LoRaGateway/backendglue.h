#ifndef _XILAB_BACKEND_GLUE_H_
#define _XILAB_BACKEND_GLUE_H_

#include <ArduinoHttpClient.h>
#include <WiFi.h>
#include "configuration.h"
#include "utils.h"

namespace xilab{
    namespace network{
        namespace http{
            enum BackendResults{
                OK = 0,
                NOT_INITIALIZED = 101,
                NOT_CONNECTED = 102
            };

            class BackendGlue{
                private:
                    bool initialized_m = false;
                    String token;
                    WiFiClient wifi;
                    HttpClient* client = nullptr; 
                public:
                    int init(){
                        LOGn("[BACKEND GLUE] Initialize backendg glue.");
                        delay(1000);
                        
                        LOGn("[BACKEND GLUE] Connect to network.");
                        int status = connect();
                        if(status != WL_CONNECTED){
                            LOGn("[BACKEND GLUE] Cannot connect to network, retry at a later point.");
                            return BackendResults::NOT_INITIALIZED;
                        }
                        LOGn("[BACKEND GLUE] Connection established.");


                        if(client != nullptr){
                            client->stop();
                            delete client;
                        }
                        LOGn("[BACKEND GLUE] Create net http client");
                        client = new HttpClient(wifi, configuration::server::SERVER_ADDRESS, configuration::server::PORT);
                        initialized_m = true;
                        LOGn("[BACKEND GLUE] Request new authorization token.");
                        int statusCode = refreshToken();
                        
                        LOGn("[BACKEND GLUE] Initialization done yield status code %i.", statusCode);
                        return statusCode;
                    }

                    int send(String uuid, int current, int charge, double longitude, double latitude){
                        
                        LOGn("[BACKEND GLUE] Send data to server.");
                        if(!initialized_m){
                            LOGn("[BACKEND GLUE] Module not initialized.");
                            return BackendResults::NOT_INITIALIZED;
                        }else if(disconnected()){
                            LOGn("[BACKEND GLUE] Cannot send data due a los of connection, please try again later.");
                            connect();
                            return NOT_CONNECTED;
                        }
                    
                    	String batteryQuery = charge >= 0 ? String("battery=") + charge : "";
                    	String currentQuery = current >= 0 ? String("waterLevel=") + current : "";
                    	String latitudeQuery = latitude >= 0 ? String("latitude=") + latitude : "";
                    	String longitudeQuery = longitude >= 0 ? String("longitude=") + longitude : "";

                        LOGn("[BACKEND GLUE] Build request.");
                        client->beginRequest();
                        client->patch("/device" + uuid + "?" + batteryQuery + "&" + currentQuery + "&" + latitudeQuery + "&" + longitudeQuery);
                        client->sendHeader("Authorization", String("Bearer ") + token);
                        client->endRequest();

                        int statusCode = client->responseStatusCode();
                        String response = client->responseBody();
                        return (statusCode >= 200 && statusCode < 300) ? BackendResults::OK : statusCode;
                    }

                    int refreshToken(){
                        if(!initialized_m){
                            LOGn("[BACKEND GLUE] Module not initialized.");
                            return BackendResults::NOT_INITIALIZED;
                        }


                        LOGn("[BACKEND GLUE] Refresh authorization token.");
                        client->beginRequest();
                        client->get("/token/create");  
                    
                        client->sendBasicAuth(configuration::server::API_USER_NAME, configuration::server::API_USER_PASSWORD);
                        client->endRequest();

                        int statusCode = client->responseStatusCode();
                        String response = client->responseBody();
                        
                        LOGn("[BACKEND GLUE] Token refreshed with status code %i.", statusCode);
                        if(statusCode < 200 || statusCode >= 300){
                            return statusCode;
                        }
                        
                        //Todo extract token
                        token = utils::json::extractString(response, "token");
                        return BackendResults::OK;
                    }

                    bool isInitialized(){
                        return initialized_m;
                    }
                private:
                    bool disconnected(){
                        return false;
                    }

                    int connect(){  
                        
                        LOGn("[BACKEND GLUE] Start connecting to network.");
                        int status = WL_IDLE_STATUS;
                        for(int i = 0; i < configuration::retries::LORA && status != WL_CONNECTED; i++) {
                            LOGn("[BACKEND GLUE] Connection attempt %i." , i);
                            WiFi.disconnect();
                            delay(500);
                            status = WiFi.begin(configuration::wlan::SSID, configuration::wlan::PASSWORD);
                            LOGn("[BACKEND GLUE] Network status %i.", status);
                            delay(500);
                        }
                        return status;
                    }
            };
        } // namespace http
    } // namespace network
} // namespace xilab

#endif // _XILAB_BACKEND_GLUE_H_