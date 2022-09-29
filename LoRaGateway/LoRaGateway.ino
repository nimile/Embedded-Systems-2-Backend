#define DEBUG
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <stdio.h>

#include "loracom.h"
//#include "GPS.h"

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


void setup() {
  lora_setup();
  //setup_gps();
}



void loop(){
  execute();
  //loop_gps();
}
