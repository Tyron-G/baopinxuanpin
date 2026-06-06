# 2026-06-05 本地开发启动脚本：先等待后端健康，再启动前端
$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $PSScriptRoot
$LogDir = Join-Path $Root "runtime-logs"
$BackendDir = Join-Path $Root "backend"
$FrontendDir = Join-Path $Root "frontend"
$BackendHealthUrl = "http://127.0.0.1:8088/actuator/health"

New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

function Stop-PortProcess {
    param([int]$Port)

    $listeners = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    foreach ($listener in $listeners) {
        $processId = $listener.OwningProcess
        if ($processId -and $processId -ne $PID) {
            Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
            Write-Output "Stopped port $Port process $processId"
        }
    }
}

function Wait-BackendHealthy {
    param([int]$TimeoutSeconds = 60)

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-RestMethod -Uri $BackendHealthUrl -TimeoutSec 3
            if ($response.status -eq "UP") {
                Write-Output "Backend health is UP"
                return
            }
        } catch {
            Start-Sleep -Seconds 2
        }
    }
    throw "Backend health check timeout: $BackendHealthUrl"
}

Stop-PortProcess -Port 8088
Stop-PortProcess -Port 5173
Start-Sleep -Seconds 2

$backendOut = Join-Path $LogDir "backend-8088.out.log"
$backendErr = Join-Path $LogDir "backend-8088.err.log"
$frontendOut = Join-Path $LogDir "frontend-5173.out.log"
$frontendErr = Join-Path $LogDir "frontend-5173.err.log"

$backend = Start-Process `
    -FilePath "mvn.cmd" `
    -ArgumentList @("spring-boot:run") `
    -WorkingDirectory $BackendDir `
    -RedirectStandardOutput $backendOut `
    -RedirectStandardError $backendErr `
    -WindowStyle Hidden `
    -PassThru
Write-Output "Started backend PID=$($backend.Id)"

Wait-BackendHealthy -TimeoutSeconds 90

$frontend = Start-Process `
    -FilePath "npm.cmd" `
    -ArgumentList @("run", "dev") `
    -WorkingDirectory $FrontendDir `
    -RedirectStandardOutput $frontendOut `
    -RedirectStandardError $frontendErr `
    -WindowStyle Hidden `
    -PassThru
Write-Output "Started frontend PID=$($frontend.Id)"

foreach ($port in @(8088, 5173)) {
    $listener = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($listener) {
        Write-Output "LISTEN $port PID=$($listener.OwningProcess)"
    } else {
        Write-Output "NOT_LISTEN $port"
    }
}

Write-Output "Frontend: http://127.0.0.1:5173/"
Write-Output "Backend:  http://127.0.0.1:8088/"
