@echo off

if not defined RAPIDBEANS_PROJECT_HOME echo ERROR environment variable RAPIDBEANS_PROJECT_HOME is not defined& set ERRORLEVEL=1& goto EXIT
if not defined RAPIDBEANS_ECLIPSE_WORKSPACE echo ERROR environment variable RAPIDBEANS_ECLIPSE_WORKSPACE is not defined& set ERRORLEVEL=1& goto EXIT

call %RAPIDBEANS_PROJECT_HOME%\scripts\windows\setenv.cmd
start /B %ECLIPSE_HOME%\eclipse.exe -data %RAPIDBEANS_ECLIPSE_WORKSPACE%

:EXIT
