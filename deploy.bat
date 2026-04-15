@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Starting Deployment on Windows
echo ========================================
echo.

REM Build the application
echo [1/5] Building JAR...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo [ERROR] Build failed!
    exit /b 1
)
echo [OK] Build successful
echo.

REM Build Docker image
echo [2/5] Building Docker image...
docker build -t rest-api:latest .
if errorlevel 1 (
    echo [ERROR] Docker build failed!
    exit /b 1
)
echo [OK] Docker image built
echo.

REM Stop and remove old container
echo [3/5] Removing old container...
docker stop rest-api 2>nul
docker rm rest-api 2>nul
echo [OK] Cleanup complete
echo.

REM Run new container
echo [4/5] Starting new container...
docker run -d --name rest-api --restart unless-stopped -p 8080:8080 rest-api:latest
if errorlevel 1 (
    echo [ERROR] Container failed to start!
    exit /b 1
)
echo [OK] Container started
echo.

REM Wait for startup
echo Waiting for application to start...
timeout /t 10 /nobreak >nul

REM Verify deployment
echo [5/5] Verifying deployment...
curl -f http://localhost:8080/ >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Deployment verification failed!
    echo.
    echo Container logs:
    docker logs rest-api
    exit /b 1
)
echo [OK] API is responding
echo.

echo ========================================
echo DEPLOYMENT SUCCESSFUL!
echo ========================================
echo.
echo Application running at: http://localhost:8080
echo.
echo To stop: docker stop rest-api
echo To view logs: docker logs rest-api
echo.
pause