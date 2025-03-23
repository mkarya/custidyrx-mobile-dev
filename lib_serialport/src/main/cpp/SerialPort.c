#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>

#include "SerialPort.h"

#include "android/log.h"

static const char *TAG = "serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

///////////////////////////////////////////////////////////////////
int set_dtr_pin_level(int fd, int level) {
    int status, ret;

    ret = ioctl(fd, TIOCMGET, &status);
    if (ret < 0) {
        LOGD("set_dtr_pin_level, TIOCMGET failed !!!!");
        return ret;
    }
    if (level)
        status &= ~TIOCM_DTR;
    else
        status |= TIOCM_DTR;
    ret = ioctl(fd, TIOCMSET, &status);
    if (ret < 0) {
        LOGD("set_dtr_pin_level failed !!!!");
    }

    return ret;
}

int set_rts_pin_level(int fd, int level) {
    int status, ret;

    ret = ioctl(fd, TIOCMGET, &status);
    if (ret < 0) {
        LOGD("set_rts_pin_level, TIOCMGET failed !!!!");
        return ret;
    }
    if (level)
        status &= ~TIOCM_RTS;
    else
        status |= TIOCM_RTS;
    ret = ioctl(fd, TIOCMSET, &status);
    if (ret < 0) {
        LOGD("set_rts_pin_level failed !!!!");
    }

    return ret;
}


int get_dtr_pin_level(int fd) {
    int status, ret, level;

    ret = ioctl(fd, TIOCMGET, &status);
    if (status & TIOCM_DTR)
        level = 0;
    else
        level = 1;

    if (ret < 0) {
        LOGD("get_dtr_pin_level failed !!!!");
        return ret;
    }

    return level;
}

int get_rts_pin_level(int fd) {
    int status, ret, level;

    ret = ioctl(fd, TIOCMGET, &status);
    if (status & TIOCM_RTS)
        level = 0;
    else
        level = 1;

    if (ret < 0) {
        LOGD("get_rts_pin_level failed !!!!");
        return ret;
    }

    return level;
}


///////////////////////////////////////////////////////////////////
static speed_t getBaudRate(jint baudRate) {
    switch (baudRate) {
        case 0:
            return B0;
        case 50:
            return B50;
        case 75:
            return B75;
        case 110:
            return B110;
        case 134:
            return B134;
        case 150:
            return B150;
        case 200:
            return B200;
        case 300:
            return B300;
        case 600:
            return B600;
        case 1200:
            return B1200;
        case 1800:
            return B1800;
        case 2400:
            return B2400;
        case 4800:
            return B4800;
        case 9600:
            return B9600;
        case 19200:
            return B19200;
        case 38400:
            return B38400;
        case 57600:
            return B57600;
        case 115200:
            return B115200;
        case 230400:
            return B230400;
        case 460800:
            return B460800;
        case 500000:
            return B500000;
        case 576000:
            return B576000;
        case 921600:
            return B921600;
        case 1000000:
            return B1000000;
        case 1152000:
            return B1152000;
        case 1500000:
            return B1500000;
        case 2000000:
            return B2000000;
        case 2500000:
            return B2500000;
        case 3000000:
            return B3000000;
        case 3500000:
            return B3500000;
        case 4000000:
            return B4000000;
        default:
            return (speed_t) -1;
    }
}

JNIEXPORT jobject JNICALL
Java_com_naz_serial_port_SerialPort_open(JNIEnv *env, jclass clazz, jstring path, jint baud_rate,
                                         jint flags) {

    int fd;
    speed_t speed;
    jobject mFileDescriptor;

    /* Check arguments */
    {
        speed = getBaudRate(baud_rate);
        if (speed == -1) {
            /* TODO: throw an exception */
            LOGE("Invalid baudrate");
            return NULL;
        }
    }

    /* Opening device */
    {
        jboolean iscopy;
        const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
        LOGD("Opening serial port %s with flags 0x%x", path_utf, O_RDWR | flags);
        fd = open(path_utf, O_RDWR | flags);
        LOGD("open() fd = %d", fd);
        tcflush(fd, TCIOFLUSH);
        (*env)->ReleaseStringUTFChars(env, path, path_utf);
        if (fd == -1) {
            /* Throw an exception */
            LOGE("Cannot open port");
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Configure device */
    {
        struct termios cfg;
        LOGD("Configuring serial port");
        if (tcgetattr(fd, &cfg)) {
            LOGE("tcgetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }

        cfmakeraw(&cfg);
        cfsetispeed(&cfg, speed);
        cfsetospeed(&cfg, speed);

        if (tcsetattr(fd, TCSANOW, &cfg)) {
            LOGE("tcsetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }
    }

    set_dtr_pin_level(fd, 1);
    set_rts_pin_level(fd, 1);
    LOGD("ret dtr pin level %d\n", get_dtr_pin_level(fd));
    LOGD("get rts pin leve %d\n", get_rts_pin_level(fd));
    /* Create a corresponding file descriptor */
    {
        jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
        jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
        jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
        mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
        (*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint) fd);
    }

    return mFileDescriptor;
}

JNIEXPORT void JNICALL
Java_com_naz_serial_port_SerialPort_close(JNIEnv *env, jobject thiz) {
    jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
    jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

    jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
    jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

    jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
    jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

    set_dtr_pin_level(descriptor, 0);
    set_rts_pin_level(descriptor, 0);
    LOGD("close(fd = %d)", descriptor);
    close(descriptor);
}