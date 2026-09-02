@echo off
title IPL Auction - Push to GitHub (Weeks 1 to 6.5 Complete)
color 0A
echo ===================================================
echo   IPL AUCTION SOFTWARE - GITHUB AUTOMATED PUSH
echo   Member 4: Weeks 1 to 6.5 Milestone (55% Scope)
echo ===================================================
echo.
echo Target Repository: https://github.com/srijansrivastava1234/ipl-auction-software
echo Target Branch:     feature/frontend-ui
echo.

cd /d "%~dp0"

:: Ensure Git is in PATH
set "PATH=%PATH%;C:\Program Files\Git\cmd;C:\Program Files (x86)\Git\cmd;%LocalAppData%\Programs\Git\cmd"

echo 1. Checking Git Installation...
where git >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Git executable not found in standard paths!
    echo Please ensure Git is installed at C:\Program Files\Git\cmd\git.exe
    pause
    exit /b 1
)

echo 2. Ensuring on feature/frontend-ui branch...
git checkout -b feature/frontend-ui 2>nul || git checkout feature/frontend-ui 2>nul

echo 3. Staging all files...
git add .

echo 4. Creating Git commit...
git commit -m "feat(frontend): complete Weeks 1 to 6.5 frontend suite (Auth, Admin, Live Bidding Podium, Squad & Purse Analytics)" 2>nul

echo 5. Pushing to GitHub...
echo.
git push -u origin feature/frontend-ui

echo.
if %ERRORLEVEL% EQU 0 (
    echo ===================================================
    echo SUCCESS! Your code has been pushed to GitHub.
    echo ===================================================
) else (
    echo ===================================================
    echo PUSH FAILED or CANCELED. Please check your network
    echo connection or GitHub authorization status.
    echo ===================================================
)
pause
