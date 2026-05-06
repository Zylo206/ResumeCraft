param(
    [string]$AppName = "ResumeCraft",
    [switch]$SkipFrontendBuild,
    [switch]$SkipJpackage
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$serverDir = Join-Path $repoRoot "server"
$jarName = "resume-craft-server-0.1.0.jar"
$jarPath = Join-Path $serverDir "target\$jarName"
$packageDir = Join-Path $repoRoot "target\win-app"

function Assert-Command($Name, $InstallHint) {
    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "$Name was not found. $InstallHint"
    }
}

Assert-Command "java" "Install JDK 17+ and make sure java.exe is on PATH."
Assert-Command "mvn" "Install Maven 3.9+ and make sure mvn.cmd is on PATH."

if (-not $SkipFrontendBuild) {
    Assert-Command "npm.cmd" "Install Node.js 18+ and make sure npm.cmd is on PATH."
    Push-Location $repoRoot
    try {
        & npm.cmd run build:desktop
        if ($LASTEXITCODE -ne 0) {
            throw "Desktop frontend build failed with exit code $LASTEXITCODE"
        }
    } finally {
        Pop-Location
    }
}

Push-Location $serverDir
try {
    & mvn clean -DskipTests package
    if ($LASTEXITCODE -ne 0) {
        throw "Backend package build failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}

if (-not (Test-Path $jarPath)) {
    throw "Backend jar was not created: $jarPath"
}

if ($SkipJpackage) {
    Write-Host "Jar created: $jarPath"
    exit 0
}

Assert-Command "jpackage" "Install a full JDK 17+ that includes jpackage.exe."

if (Test-Path $packageDir) {
    Remove-Item -LiteralPath $packageDir -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $packageDir | Out-Null

& jpackage `
    --type app-image `
    --name $AppName `
    --dest $packageDir `
    --input (Join-Path $serverDir "target") `
    --main-jar $jarName `
    --java-options "-Dspring.profiles.active=local" `
    --java-options "-Dfile.encoding=UTF-8" `
    --java-options "-Djava.awt.headless=false" `
    --java-options "-DRESUMECRAFT_DESKTOP_APP=true"
if ($LASTEXITCODE -ne 0) {
    throw "jpackage failed with exit code $LASTEXITCODE"
}

$exePath = Join-Path $packageDir "$AppName\$AppName.exe"
if (-not (Test-Path $exePath)) {
    throw "jpackage finished, but launcher was not found: $exePath"
}

Write-Host "Windows app image created:"
Write-Host $exePath
