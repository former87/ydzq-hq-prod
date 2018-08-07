
APP_MAINCLASS=com.ydzq.hq.ServerMain

APP_HOME=`pwd`

CLASSPATH=$APP_HOME/resource
for i in "$APP_HOME"/lib/*.jar; do
  CLASSPATH="$CLASSPATH":"$i"
done
java -Djava.awt.headless=true -XX:+HeapDumpOnOutOfMemoryError -Dio.netty.leakDetectionLevel=paranoid -Djava.rmi.server.hostname=47.94.214.147 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -classpath $CLASSPATH $APP_MAINCLASS $* > /dev/null &