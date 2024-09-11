@echo off
chcp 65001 >nul
set /p user_input=请输入你的名字: 
echo 你好, %user_input%!
pause