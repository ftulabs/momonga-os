#!/bin/sh

format_size() {
    awk -v bytes="${1:-0}" 'BEGIN {
        if (bytes >= 1073741824) printf "%.1f GB", bytes / 1073741824
        else if (bytes >= 1048576) printf "%.0f MB", bytes / 1048576
        else printf "%.0f KB", bytes / 1024
    }'
}

read_meminfo() {
    awk -v key="$1" '$1 == key ":" { print $2; exit }' /proc/meminfo 2>/dev/null
}

read_first() {
    [ -r "$1" ] && sed -n '1p' "$1" 2>/dev/null
}

uptime_seconds=$(cut -d. -f1 /proc/uptime 2>/dev/null || printf '0')
uptime=$(awk -v seconds="${uptime_seconds:-0}" 'BEGIN {
    days = int(seconds / 86400); hours = int((seconds % 86400) / 3600); minutes = int((seconds % 3600) / 60)
    if (days) printf "%dd %dh %dm", days, hours, minutes
    else if (hours) printf "%dh %dm", hours, minutes
    else printf "%dm", minutes
}')

distribution=$(awk -F= '$1 == "PRETTY_NAME" { gsub(/^"|"$/, "", $2); print $2; exit }' /etc/os-release 2>/dev/null)
[ -n "$distribution" ] || distribution="Linux $(uname -m)"
architecture=$(uname -m 2>/dev/null || printf 'unknown')
kernel=$(uname -sr 2>/dev/null || printf 'unknown')
cpu=$(awk -F: '/^(model name|Hardware)[[:space:]]*:/ { sub(/^[[:space:]]+/, "", $2); print $2; exit }' /proc/cpuinfo 2>/dev/null)
[ -n "$cpu" ] || cpu="$(uname -m 2>/dev/null || printf 'unknown CPU')"
cpu_freq=$(awk 'NR == 1 { printf "%.2f GHz", $1 / 1000000 }' /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq 2>/dev/null)
[ -n "$cpu_freq" ] || cpu_freq=$(awk -F: '/cpu MHz/ { printf "%.2f GHz", $2 / 1000; exit }' /proc/cpuinfo 2>/dev/null)
load=$(awk '{ print $1, $2, $3 }' /proc/loadavg 2>/dev/null)
[ -n "$load" ] || load="unavailable"

mem_total=$(read_meminfo MemTotal)
mem_available=$(read_meminfo MemAvailable)
[ -n "$mem_total" ] || mem_total=0
[ -n "$mem_available" ] || mem_available=0
mem_used=$((mem_total - mem_available))
swap_total=$(read_meminfo SwapTotal)
swap_free=$(read_meminfo SwapFree)
[ -n "$swap_total" ] || swap_total=0
[ -n "$swap_free" ] || swap_free=0
root_storage=$(df -hP / 2>/dev/null | awk 'NR == 2 { print $3 "/" $2 " used (" $5 ")" }')
[ -n "$root_storage" ] || root_storage="unavailable"
processes=$(ps -e 2>/dev/null | awk 'NR > 1 { count++ } END { print count+0 }')

tailscale_ip=$(command -v tailscale >/dev/null 2>&1 && tailscale ip -4 2>/dev/null | sed -n '1p')
[ -n "$tailscale_ip" ] || tailscale_ip="unavailable"
public_ip="unavailable"
if command -v curl >/dev/null 2>&1; then
    public_ip=$(curl -4 -fsS --connect-timeout 1 --max-time 2 https://api.ipify.org 2>/dev/null)
    [ -n "$public_ip" ] || public_ip="unavailable"
fi

cuda="unavailable"
for version_file in /usr/local/cuda/version.json /usr/local/cuda/version.txt; do
    if [ -r "$version_file" ]; then
        if [ "${version_file##*.}" = "json" ]; then
            cuda=$(awk '
                /"cuda"[[:space:]]*:/ { in_cuda=1 }
                in_cuda && /"version"[[:space:]]*:/ {
                    if (match($0, /"version"[[:space:]]*:[[:space:]]*"[^"]+"/)) {
                        value=substr($0, RSTART, RLENGTH)
                        sub(/^.*:[[:space:]]*"/, "", value)
                        sub(/"$/, "", value)
                        print value
                        exit
                    }
                }
            ' "$version_file")
        else
            cuda=$(sed -nE 's/^CUDA Version ([^ ]+).*/\1/p' "$version_file" | sed -n '1p')
        fi
        [ -n "$cuda" ] && break
    fi
done
if [ -z "$cuda" ] || [ "$cuda" = "unavailable" ]; then
    nvcc=$(command -v nvcc 2>/dev/null || [ ! -x /usr/local/cuda/bin/nvcc ] || printf '%s' /usr/local/cuda/bin/nvcc)
    if [ -n "$nvcc" ]; then
        cuda=$($nvcc --version 2>/dev/null | sed -nE 's/.*release ([0-9.]+).*/\1/p' | sed -n '1p')
    fi
    [ -n "$cuda" ] || cuda="unavailable"
fi

gpu_load_file=
for path in /sys/devices/gpu.0/load /sys/class/devfreq/*gpu*/load; do
    if [ -r "$path" ]; then gpu_load_file=$path; break; fi
done
gpu_load=$(awk 'NR == 1 { printf "%.1f%%", $1 / 10 }' "$gpu_load_file" 2>/dev/null)
[ -n "$gpu_load" ] || gpu_load="unavailable"
gpu_freq_file=${gpu_load_file%/load}/cur_freq
gpu_freq=$(awk 'NR == 1 { printf "%.0f MHz", $1 / 1000000 }' "$gpu_freq_file" 2>/dev/null)
gpu_name=$(read_first /sys/devices/gpu.0/devfreq/*/name)
[ -n "$gpu_name" ] || gpu_name="NVIDIA integrated GPU"

printf '\n==========[ System Information ]===============================================\n'
printf '      Hostname = %s\n' "$(hostname 2>/dev/null || printf 'unknown')"
printf '       Address = %s (tailnet)\n' "$tailscale_ip"
printf '               = %s (internet)\n' "$public_ip"
printf '        Kernel = %s\n' "$kernel"
printf '  Distribution = %s %s\n' "$distribution" "$architecture"
printf '        Uptime = %s\n' "$uptime"
printf '          CUDA = %s\n' "$cuda"
printf '           CPU = %s\n' "$cpu"
printf '                 load: %s | %s\n' "$load" "${cpu_freq:-frequency unavailable}"
printf '           GPU = %s\n' "$gpu_name"
printf '                 load: %s | %s\n' "$gpu_load" "${gpu_freq:-frequency unavailable}"
printf '        Memory = %s/%s used\n' "$(format_size $((mem_used * 1024)))" "$(format_size $((mem_total * 1024)))"
printf '          Swap = %s free, %s total\n' "$(format_size $((swap_free * 1024)))" "$(format_size $((swap_total * 1024)))"
printf '          Root = %s\n' "$root_storage"
printf '     Processes = %s\n' "$processes"

containers="Docker unavailable"
if command -v docker >/dev/null 2>&1; then
    if command -v timeout >/dev/null 2>&1; then
        docker_output=$(timeout 3 docker ps --format 'table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}' 2>/dev/null)
    else
        docker_output=$(docker ps --format 'table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}' 2>/dev/null)
    fi
    if [ $? -eq 0 ]; then
        container_count=$(printf '%s\n' "$docker_output" | awk 'NR > 1 && NF { count++ } END { print count+0 }')
        containers="$container_count running"
    else
        containers="Docker unavailable"
    fi
fi
printf '    Containers = %s\n' "$containers"

printf '\n==========[ Storage Information ]==============================================\n'
df -h 2>/dev/null || printf 'Storage information unavailable\n'

printf '\n==========[ Containers ] ======================================================\n'
if [ "$containers" = "Docker unavailable" ]; then
    printf 'Docker unavailable\n'
elif [ "$container_count" -eq 0 ]; then
    printf 'No running containers\n'
else
    printf '%s\n' "$docker_output"
fi
