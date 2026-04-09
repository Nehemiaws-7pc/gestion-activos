@echo off
cd /d "%~dp0backend"
echo Iniciando backend...
call mvnw.cmd spring-boot:run
