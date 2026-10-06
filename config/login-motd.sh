#!/bin/sh

format_kib() {
    awk -v kib="$1" 'BEGIN {
        if (kib >= 1048576) printf "%.1f GiB", kib / 1048576
        else if (kib >= 1024) printf "%.1f MiB", kib / 1024
        else printf "%d KiB", kib
    }'
}

read_meminfo() {
    awk -v key="$1" '$1 == key ":" { print $2; exit }' /proc/meminfo
}

uptime_seconds=$(cut -d. -f1 /proc/uptime 2>/dev/null || printf '0')
uptime=$(awk -v seconds="$uptime_seconds" 'BEGIN {
    days = int(seconds / 86400); hours = int((seconds % 86400) / 3600); minutes = int((seconds % 3600) / 60)
    if (days) printf "%dd %dh %dm", days, hours, minutes
    else if (hours) printf "%dh %dm", hours, minutes
    else printf "%dm", minutes
}')

mem_total=$(read_meminfo MemTotal)
mem_available=$(read_meminfo MemAvailable)
mem_used=$((mem_total - mem_available))
disk=$(df -hP / 2>/dev/null | awk 'NR == 2 { print $3 " / " $2 " (" $5 ")" }')
load=$(cut -d' ' -f1-3 /proc/loadavg 2>/dev/null || printf 'unavailable')

printf '\nSystem: %s | Kernel: %s | Uptime: %s\n' "$(hostname)" "$(uname -r)" "$uptime"
printf 'Load: %s | Memory: %s / %s | Root: %s\n' "$load" "$(format_kib "$mem_used")" "$(format_kib "$mem_total")" "$disk"

if [ -r /proc/device-tree/compatible ] && tr '\000' '\n' 2>/dev/null < /proc/device-tree/compatible | grep -qi 'nvidia,tegra'; then
    cpu_freq=$(awk 'NR == 1 { printf "%.0f MHz", $1 / 1000 }' /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq 2>/dev/null)
    gpu_load_file=$(for path in /sys/devices/gpu.0/load /sys/class/devfreq/*gpu*/load; do [ -r "$path" ] && { printf '%s\n' "$path"; break; }; done)
    gpu_load=$(awk 'NR == 1 { printf "%.0f%%", $1 / 10 }' "$gpu_load_file" 2>/dev/null)
    temperatures=$(for zone in /sys/class/thermal/thermal_zone*; do
        [ -r "$zone/temp" ] && [ -r "$zone/type" ] || continue
        temp=$(cat "$zone/temp")
        [ "$temp" -ge 1000 ] 2>/dev/null && temp=$((temp / 1000))
        printf '%s:%sC ' "$(cat "$zone/type")" "$temp"
    done)

    printf 'Tegra: detected'
    [ -n "$cpu_freq" ] && printf ' | CPU: %s' "$cpu_freq"
    [ -n "$gpu_load" ] && printf ' | GPU: %s' "$gpu_load"
    [ -n "$temperatures" ] && printf ' | Thermal: %s' "$temperatures"
    printf '\n'
fi
