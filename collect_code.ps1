$outputFile = "all_code.txt"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $scriptDir

# Remove output file if it exists
if (Test-Path $outputFile) {
    Remove-Item $outputFile
}

# Directories to search and file extensions to include
$directories = @("src", "scripts")
$extensions = @("*.java", "*.sql", "*.properties", "*.xml")

$files = Get-ChildItem -Path $directories -Include $extensions -Recurse -File -ErrorAction SilentlyContinue

foreach ($file in $files) {
    $relativePath = $file.FullName.Substring($scriptDir.Length + 1)
    
    Add-Content -Path $outputFile -Value "================================================================="
    Add-Content -Path $outputFile -Value "File: $relativePath"
    Add-Content -Path $outputFile -Value "================================================================="
    Get-Content $file.FullName | Add-Content -Path $outputFile
    Add-Content -Path $outputFile -Value "`n"
}

Write-Host "Code collected successfully in: $(Join-Path $scriptDir $outputFile)"
