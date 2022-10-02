#ifndef _XILAB_GPS_H_
#define _XILAB_GPS_H_

namespace xilab{

    enum GPS_Results{
        OK = 400,
        NOT_INITIALZED = 401,
        LOCATED = 402,
        NOT_YET_ALLOWED = 403
    };
    class GPS{
        public:
            static GPS& getInstance(){
                static GPS instance;
                return instance;
            }
        private:
            GPS(){}

            bool initialized_m = false;

            float latitude_m;
            float longitude_m;
            float altitude_m;

            long lastLocation = 0;
            long locationOffset = 500;
        public:

            bool isInitialized(){
                return initialized_m;
            }

            GPS_Results init(){
                initialized_m = true;
                return GPS_Results::OK;
            } 

            GPS_Results locate(){
                if(!isLocationAllowed()){
                    return NOT_YET_ALLOWED;
                }
                
                lastLocation = millis();
                //longitude_m = ...
                //latitude_m = ...
                //altitude_m = ...
                return GPS_Results::LOCATED;
            }

            bool isLocationAllowed(){
                return (lastLocation + locationOffset) > millis();
            }

            float getLongitude(){
                return longitude_m;
            }

            float getLatitude(){
                return latitude_m;
            }
            
            float getAltitude(){
                return altitude_m;
            }
    };
} // namespace xilab


#endif // _XILAB_GPS_H_