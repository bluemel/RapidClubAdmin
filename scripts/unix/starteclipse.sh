#!/bin/sh

if [ "${RAPIDBEANS_RAPIDBEANS_PROJECT_HOME}" = "" ]; then
	echo "ERROR environment variable RAPIDBEANS_RAPIDBEANS_PROJECT_HOME is not defined"
	exit 1
fi
if [ ${ECLIPSE_WORKSPACE}" == "" ]; then
	echo "ERROR environment variable ECLIPSE_WORKSPACE is not defined"
	exit 1
fi
${RAPIDBEANS_PROJECT_HOME}/scripts/windows/setenv.cmd
${ECLIPSE_HOME}/eclipse -data "${RAPIDBEANS_ECLIPSE_WORKSPACE}" &
