#ifndef _XILAB_UTILS_H_
#define _XILAB_UTILS_H_

/**
 * @brief This is a helper method
 * It provides a printf style serial printing
 * @param fmt Format
 * @param ... arguments
 */
void LOGn(const char *fmt, ...) {
#ifdef DEBUG
    char buff[1024];
    va_list pargs;
    va_start(pargs, fmt);
    vsnprintf(buff, 1024, fmt, pargs);
    va_end(pargs);
    Serial.println(buff);
#endif
}

namespace utils{
    namespace json{
        int extractInteger(String json, String val){
            int result = -1;


            return result;
        }

        float extractFloat(String json, String val){
            float result = -1;


            return result;
        }
        
        String extractString(String json, String val){
            String result = "";


            return result;
        }
    } // namespace json
    
} // utils

#endif // _XILAB_UTILS_H_