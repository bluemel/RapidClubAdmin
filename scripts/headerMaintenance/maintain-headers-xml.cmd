@echo off
setlocal
if not defined CYGWIN_HOME ( echo environment variable CYGWIN_HOME is not defined & endlocal & set ERRORLEVEL=1& goto END )
set PATH=%CYGWIN_HOME%\bin;%PATH%
sh maintain-headers-xml.sh
endlocal

:END
