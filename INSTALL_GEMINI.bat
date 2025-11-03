@echo off
echo Installing Gemini CLI, thanks for your patience.
echo.
npm install -g @google/gemini-cli
echo.
if %errorlevel% equ 0 (
    echo Instalacion completada exitosamente!
) else (
    echo Hubo un error durante la instalacion.
)
echo.
pause