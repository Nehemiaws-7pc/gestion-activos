@echo off
echo Iniciando backend en nueva ventana...
start "Backend" cmd /k "cd /d "%~dp0backend" && mvnw.cmd spring-boot:run"

echo Iniciando frontend en nueva ventana...
start "Frontend" cmd /k "cd /d "%~dp0frontend" && npm start"

echo Servidores iniciados. Cierra las ventanas para detenerlos.
