#define DEBUG
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <stdio.h>

#include "utils.h"
#include "loracom.h"
#include "wlan.h"

xilab::BackendGlue backend;

void LOGn(const char *fmt, ...);

void setup() {
  lora_setup();
  backend.init();
}



void loop(){
  execute();
}
