#!/bin/bash
# ------------------------------------------------------------------------------
# FileName:		stop.sh
# Version:		1.0.0
# Date:			2016-03-18
# Description:	java应用关闭脚本
# Notes:		关闭脚本,应用关闭功能
# ------------------------------------------------------------------------------

# 环境变量

# 全局变量:bin相对路径,conf目录,logs目录,调试参数
RELATIVE_PATH=$(dirname $0)
OPT_PATH=$(cd ${RELATIVE_PATH}/.. && pwd)
CONF_PATH="${OPT_PATH}/conf"
LOGS_PATH="/logs/${OPT_PATH##*/}"

echo "init
    OPT_PATH=${OPT_PATH}
    CONF_PATH=${CONF_PATH}
    LOGS_PATH=${LOGS_PATH}
"

# 服务名称,服务端口,lib列表,stdout日志
SERVER_NAME=$(grep 'application.name' ${CONF_PATH}/application.properties | awk -F '=' '{print $2}')
PID_FILE="/var/run/dsp-bidder/${SERVER_NAME}.pid"

echo "load
    SERVER_NAME=${SERVER_NAME}
    SERVER_PORT=${SERVER_PORT}
"

function stop ()
{
    echo "closing service ${SERVER_NAME}"
    kill -9 $1
}

if [[ -f ${PID_FILE} ]]; then
    #判断PID文件是否存在
    PID=$(cat ${PID_FILE})
    stop ${PID}
    PID=$(ps -elf | grep ${CONF_PATH} | grep -v grep | awk '{print $4}')
    if [[ -n ${PID} ]]; then
        #判断进程与端口是否启动
        echo "${SERVER_NAME} service stop failure,PID:${PID}"
        exit 189
    else
        echo "${SERVER_NAME} already closed"
        $(which mv) -f ${PID_FILE} /tmp/
    fi
else
    echo "${SERVER_NAME} do not start, please start, if the existence of the service process, please carefully check the process after the close"
fi
