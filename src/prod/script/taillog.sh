APP_HOME=data/Logs
if [ -n "$1" ]
then
  tail -f $APP_HOME/$1.log
else
  tail -f $APP_HOME/app.log
fi
