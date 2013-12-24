#!/bin/sh

# --------------------------------------------------
# Environment check
# --------------------------------------------------
# RAPIDBEANS_PROJECT_HOME is the parent folder of one or more project folders like org.rapidbeans.clubadmin
if [ "${RAPIDBEANS_PROJECT_HOME}" = "" }; then
	echo "ERROR Environment variable RAPIDBEANS_PROJECT_HOME is not defined"
	exit 1
fi

# RAPIDBEANS_TOOLS_HOME is where the necessary development tools (JDK, Ant, Eclipse, ...)
# are installed. Basically you should install these tools keeping the following simple folder strucure.
# <RAPIDBEANS_TOOLS_HOME>/<tool name>/<version>. The tool names are defined in the "Tool homes" section.
if [ "${RAPIDBEANS_TOOLS_HOME}" = "" }; then
	echo "ERROR Environment variable RAPIDBEANS_TOOLS_HOME is not defined"
	exit 1
fi

# LOCAL_REPOSITORY is a Maven 2 compatible local repository that stores all the third party libraries
# needed to build the projects under RAPIDBEANS_PROJECT_HOME.
# Usually you only need on single Maven local repository at all on one developer machine.
if [ "${LOCAL_REPOSITORY}" = "" }; then
	echo "ERROR Environment variable LOCAL_REPOSITORY is not defined"
	exit 1
fi

# --------------------------------------------------
# Tool versions
# --------------------------------------------------
export JAVA_VERSION=1.5.0
export ANT_VERSION=1.7.0
# Maven is not yet used
# export MAVEN_VERSION=2.0.9
export ECLIPSE_VERSION=3.3.2

# --------------------------------------------------
# Tool homes
# --------------------------------------------------
export ANT_HOME=${RAPIDBEANS_TOOLS_HOME}/Ant/${ANT_VERSION}
export JAVA_HOME=${RAPIDBEANS_TOOLS_HOME}/JDK/${JAVA_VERSION}
# Maven is not yet used
# export MAVEN_HOME=${RAPIDBEANS_TOOLS_HOME}/Maven/${MAVEN_VERSION}
if [ -d "${RAPIDBEANS_TOOLS_HOME}/Eclipse/${ECLIPSE_VERSION}/eclipse" ]; then
	export ECLIPSE_HOME=${RAPIDBEANS_TOOLS_HOME}/Eclipse/${ECLIPSE_VERSION}/eclipse
else
	export ECLIPSE_HOME=${RAPIDBEANS_TOOLS_HOME}/Eclipse/eclipse/${ECLIPSE_VERSION}
fi

# --------------------------------------------------
# Command path
# --------------------------------------------------
# Maven is not yet used
# export PATH=${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${ANT_HOME}/bin:${PATH}
export PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:${PATH}

echo "RAPIDBEANS_PROJECT_HOME = \"%RAPIDBEANS_PROJECT_HOME%\""
