#!/bin/sh
# distrobox init hook: give the box user the supplementary groups it has on
# the host, by GID, so device nodes (kvm, video, render...) behave the same.
# Runs as root inside the box at every start; the host root is /run/host.
user="$1"
host_group=/run/host/etc/group
[ -n "$user" ] && [ -r "$host_group" ] || exit 0
primary_gid=$(id -g "$user")
grep -E "^[^:]*:[^:]*:[0-9]+:(.*,)?${user}(,.*)?$" "$host_group" |
while IFS=: read -r name _ gid _; do
    [ "$gid" = "$primary_gid" ] && continue
    box_name=$(getent group "$gid" | cut -d: -f1)
    if [ -z "$box_name" ]; then
        # Name taken by another GID in the box image: keep the host GID.
        getent group "$name" >/dev/null && name="host-$name"
        groupadd -g "$gid" "$name" || continue
        box_name=$name
    fi
    usermod -aG "$box_name" "$user"
done
exit 0
