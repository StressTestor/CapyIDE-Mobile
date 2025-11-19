@echo off
REM Standard Gradle wrapper batch script - creates gradlew if missing
setlocal

if "%DEBUG%" == "" set DEBUG=

if not "%JAVA_HOME%" == "" goto haveJava

if not "%JAVA_HOME%" == "" goto haveJava

rem Find JDK
for %%i in ("java" "javaw") do (
    for %%j in ("%%~dpi*") do (
        set "JAVA_HOME=%%~dpnj"
        goto haveJava
    )
)

echo No JDK found. Please set JAVA_HOME.
exit /b 1

:haveJava

set GRADLE_HOME=

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

set APP_HOME=%DIRNAME%

set APP_NAME="gradlew.bat"
set APP_BASE_NAME=%APP_NAME:~0,-4%

set DEFAULT_JVM_OPTS=

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

"%JAVA_HOME%\bin\java" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% ^
 "-Dorg.gradle.appname=%APP_BASE_NAME%" ^
 -classpath "%CLASSPATH%" ^
 org.gradle.wrapper.GradleWrapperMain %*

:end
endlocal