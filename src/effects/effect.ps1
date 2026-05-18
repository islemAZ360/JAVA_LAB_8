$text = $args[0]
if (-not $text) { $text = "Reconnecting to server..." }

$esc    = [char]27
$colors = @(234,236,238,240,242,244,246,248,250,252,254,255,254,252,250,248,246,242,238,234)
$step   = 0
$mutex  = [System.Threading.Mutex]::new($false, "Global\TerminalWriteMutex")

# Mở 2 dòng trống cho animation + input
[Console]::Write("`n`n")

try {
    while ($true) {
        $bottom   = [Console]::WindowHeight
        $animLine = $bottom - 1   # dòng animation (trên input)

        $sb = [System.Text.StringBuilder]::new()
        $sb.Append("$esc[s")                     | Out-Null  # save cursor (tại input)
        $sb.Append("$esc[${animLine};1H")        | Out-Null  # nhảy đến dòng animation
        $sb.Append("$esc[2K")                    | Out-Null  # xóa dòng

        for ($i = 0; $i -lt $text.Length; $i++) {
            $idx = ($step - $i + 200) % $colors.Count
            $col = $colors[$idx]
            $sb.Append("$esc[38;5;${col}m$($text[$i])") | Out-Null
        }

        $sb.Append("$esc[0m") | Out-Null
        $sb.Append("$esc[u")  | Out-Null  # restore cursor về input

        $mutex.WaitOne() | Out-Null
        try     { [Console]::Write($sb.ToString()) }
        finally { $mutex.ReleaseMutex() }

        $step++
        Start-Sleep -Milliseconds 50
    }
}
finally {
    [Console]::Write("$esc[?25h")
    $mutex.Dispose()
}


$text = $args[0]
if (-not $text) { $text = "Reconnecting to server..." }

# Standart exit key ANSI
$esc = [char]27
$colors = @(234, 236, 238, 240, 242, 244, 246, 248, 250, 252, 254, 255, 254, 252, 250, 248, 246, 242, 238, 234)
$step = 0

Write-Host -NoNewline "$esc[?25l" # Hide cusor

try {
    while($true) {
        $out = "$esc[1G" # Back to start of line

        for($i=0; $i -lt $text.Length; $i++) {
            $idx = ($step - $i + 200) % $colors.Count
            $col = $colors[$idx]
            $out += "$esc[38;5;${col}m$($text[$i])"
        }
        Write-Host -NoNewline ($out + "$esc[0m")
        $step++
        Start-Sleep -m 50
    }
} finally {
    Write-Host -NoNewline "$esc[?25h"
}


# effect.ps1
$text = $args[0]
if (-not $text) { $text = "Processing..." }

# gray-white gradient
$colors = @(234, 236, 238, 240, 242, 244, 246, 248, 250, 252, 254, 255, 254, 252, 250, 248, 246, 242, 238, 234)
$step = 0

# Hide cusor
Write-Host -NoNewline "`e[?25l"

try {
    while($true) {
        $out = "`r"
        for($i=0; $i -lt $text.Length; $i++) {
            $idx = ($step - $i + 200) % $colors.Count
            $col = $colors[$idx]
            $out += "`e[38;5;${col}m$($text[$i])"
        }
        Write-Host -NoNewline ($out + "`e[0m")
        $step++
        Start-Sleep -m 50
    }
} finally {
    # Show cusor
    Write-Host -NoNewline "`e[?25h"
}
