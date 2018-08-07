APP_HOME=`pwd`
pid=`ps -ef|grep -i $APP_HOME |grep -v "grep"|awk '{print $2}'`
kill $pid