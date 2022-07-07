#!/bin/bash
java $2 -cp $APP_PATH/$JAR_FILE -Dloader.path=$APP_PATH/lib org.springframework.boot.loader.PropertiesLauncher $1