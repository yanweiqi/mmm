#!/bin/bash
# ------------------------------------------------------------------------------
# FileName:		start.sh
# Version:		1.0.0
# Date:			2016-03-18
# Description:	Java应用启动脚本
# Notes:		启动脚本,应用启动功能
# ------------------------------------------------------------------------------

# 环境变量

# 全局变量:bin相对路径,conf目录,logs目录,调试参数
RELATIVE_PATH=$(dirname $0)
OPT_PATH=$(cd ${RELATIVE_PATH}/.. && pwd)
CONF_PATH="${OPT_PATH}/conf"
LIBS_PATH="${OPT_PATH}/lib"
LOGS_PATH="/logs/${OPT_PATH##*/}"

echo "init
    OPT_PATH=${OPT_PATH}
    CONF_PATH=${CONF_PATH}
    LIBS_PATH=${LIBS_PATH}
    LOGS_PATH=${LOGS_PATH}
"

# 服务名称,服务端口,lib列表,stdout日志
SERVER_NAME=$(grep 'application.name' ${CONF_PATH}/application.properties | awk -F '=' '{print $2}')
LIB_JARS=$(ls $LIBS_PATH | grep .jar | awk '{print "'${LIBS_PATH}'/"$0}' | tr "\n" ":")
STDOUT_FILE="${OPT_PATH}/logs/stdout.log"
PID_FILE="/var/run/dsp-bidder/${SERVER_NAME}.pid"

echo "load
    SERVER_NAME=${SERVER_NAME}
    SERVER_PORT=${SERVER_PORT}
    STDOUT_FILE=${STDOUT_FILE}
"

# JVM启动参数
source $CONF_PATH/jvm.conf
# JVM 参数
JVM_OPTS=" -server -Xmx"${JVM_Xmx}" -Xms"${JVM_Xms}" -Xmn"${JVM_Xmn}" -Xss"${JVM_Xss}" -XX:+PrintCommandLineFlags -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -verbose:gc -Xloggc:logs/gc.log"
# Main Class
JAVA_MAIN=$(grep 'application.main.class' ${CONF_PATH}/application.properties | awk -F '=' '{print $2}')

echo "JVM OPTS
    JVM_Xmx=${JVM_Xmx}
    JVM_Xms=${JVM_Xms}
    JVM_Xmn=${JVM_Xmn}
    JVM_Xss=${JVM_Xss}
    JVM_OPTS=${JVM_OPTS}
    JAVA_MAIN=${JAVA_MAIN}
"

function start ()
{
    echo "starting ${SERVER_NAME}"
    echo $JAVA_OPTS
    nohup java $JAVA_OPTS -classpath $CONF_PATH:$LIB_JARS $JAVA_MAIN -conf $CONF_PATH/main.cnf.json >> $STDOUT_FILE 2>&1 &
}

mkdir -p ${LOGS_PATH}
mkdir -p ${OPT_PATH}/logs
cd ${OPT_PATH}

if [[ -f ${PID_FILE} ]]; then
    #判断PID文件是否存在
    PID=$(cat ${PID_FILE})
    echo "${SERVER_NAME} is running,PID：${PID},Please close first,Delete if the PID does not exist：${PID_FILE} rerun"
    exit 189
else
    start
    PID=$(ps -ef | grep ${CONF_PATH} | grep -v grep | awk '{print $2}')
    if [[ -n ${PID} ]]; then
        #判断进程与端口是否启动
        echo "${SERVER_NAME} Service started successfully"
        echo "${PID}" > ${PID_FILE}
    else
        echo "${SERVER_NAME} Service startup failed"
        exit 189
    fi
fi
