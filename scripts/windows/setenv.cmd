@echo off

:: --------------------------------------------------
:: Environment check
:: --------------------------------------------------
:: RAPIDBEANS_PROJECT_HOME is the parent folder of one or more project folders like org.rapidbeans.clubadmin
if not defined RAPIDBEANS_PROJECT_HOME echo ERROR Environment variable RAPIDBEANS_PROJECT_HOME is not defined& goto EXIT

:: RAPIDBEANS_TOOLS_HOME is where the necessary development tools (JDK, Ant, Eclipse, ...)
:: are installed. Basically you should install these tools keeping the following simple folder strucure.
:: <RAPIDBEANS_TOOLS_HOME>/<tool name>/<version>. The tool names are defined in the "Tool homes" section.
if not defined RAPIDBEANS_TOOLS_HOME echo ERROR Environment variable RAPIDBEANS_TOOLS_HOME is not defined& goto EXIT

:: LOCAL_REPOSITORY is a maven 2 compatible local repository that stores all the third party libraries
:: needed to build the projects under RAPIDBEANS_PROJECT_HOME.
:: Usually you only need on single Maven local repository at all on one developer machine.
if not defined LOCAL_REPOSITORY echo ERROR Environment variable LOCAL_REPOSITORY is not defined& goto EXIT

:: --------------------------------------------------
:: Tool versions
:: --------------------------------------------------
set JAVA_VERSION=1.6.0
set ANT_VERSION=1.8.1
:: Maven is not yet used
:: set MAVEN_VERSION=2.2.1
set ECLIPSE_VERSION=3.5.2

:: --------------------------------------------------
:: Tool homes
:: --------------------------------------------------
set ANT_HOME=%RAPIDBEANS_TOOLS_HOME%\Ant\%ANT_VERSION%
set JAVA_HOME=%RAPIDBEANS_TOOLS_HOME%\JDK\%JAVA_VERSION%
:: Maven is not yet used
:: set MAVEN_HOME=%RAPIDBEANS_TOOLS_HOME%\Maven\%MAVEN_VERSION%
if exist "%RAPIDBEANS_TOOLS_HOME%\Eclipse\%ECLIPSE_VERSION%\eclipse" set ECLIPSE_HOME=%RAPIDBEANS_TOOLS_HOME%\Eclipse\%ECLIPSE_VERSION%\eclipse& goto CONT
set ECLIPSE_HOME=%RAPIDBEANS_TOOLS_HOME%\Eclipse\eclipse\%ECLIPSE_VERSION%
:CONT

:: --------------------------------------------------
:: Command path
:: --------------------------------------------------
:: Maven is not yet used
:: set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%ANT_HOME%\bin;%PATH%
set PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%

echo RAPIDBEANS_PROJECT_HOME="%RAPIDBEANS_PROJECT_HOME%"

:EXIT
