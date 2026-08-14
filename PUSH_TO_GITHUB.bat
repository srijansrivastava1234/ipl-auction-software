@echo off
title IPL Auction - Push to GitHub
color 0A
echo ===================================================
echo   IPL AUCTION SOFTWARE - GITHUB AUTOMATED PUSH
echo ===================================================
echo.
echo Target Repository: https://github.com/srijansrivastava1234/ipl-auction-software
echo Target Branch:     feature/frontend-ui
echo.

cd /d "%~dp0"

echo 1. Initializing Git repository...
git init
git checkout -b feature/frontend-ui

echo 2. Staging all Week 1 files...
git add .

echo 3. Creating Git commit...
git commit -m "feat(frontend): complete Week 1 UI architecture, CSS design system, JWT storage & API interceptor"

echo 4. Adding remote repository...
git remote remove origin 2>nul
git remote add origin https://github.com/srijansrivastava1234/ipl-auction-software.git

echo 5. Pushing to GitHub...
git push -u origin feature/frontend-ui

echo.
echo ===================================================
echo SUCCESS! Your Week 1 code has been pushed to GitHub.
echo ===================================================
pause
