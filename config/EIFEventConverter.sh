#!/bin/bash

JAVA_EXECUTABLE=/usr/lib/jvm/jre/bin/java
EIFEventConverter_HOME=/opt/EIFEventConverter/

start() {

cd ${EIFEventConverter_HOME}

nohup ${JAVA_EXECUTABLE} -jar EIFEventConverter.jar >/dev/null 2>&1 &
echo $! > EIFEventConverter.pid

}

stop() {
cd /opt/EIFEventConverter/
kill -9 `cat EIFEventConverter.pid`
rm EIFEventConverter.pid

}

case "$1" in 
    start)
       start
       ;;
    stop)
       stop
       ;;
    *)
       echo "Usage: $0 {start|stop}"
esac

exit 0 