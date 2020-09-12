@ECHO OFF
IF "%1"=="start" (
    ECHO start middleware
    start "middleware-1.0" java -jar C:\\middleware\bin\\middleware-1.0.jar --spring.config.location=file:///C:\middleware\conf\application.properties
) ELSE IF "%1"=="stop" (
    ECHO stop middleware
    TASKKILL /FI "WINDOWTITLE eq middleware-1.0"
) ELSE (
    ECHO please, use "run.bat start" or "run.bat stop"
)
pause