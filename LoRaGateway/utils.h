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

#endif // _XILAB_UTILS_H_