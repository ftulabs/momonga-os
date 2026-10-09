# Nobody watches the console: when the root partition does not mount, reboot
# instead of waiting in a shell. Unverified boots use up the slot's UEFI
# retries, and the boot chain then falls back to the other slot.
do_install:append() {
    sed -i 's#^\[ \$count -lt 5 \] || exec sh$#[ $count -lt 5 ] || { echo "Cannot mount $rootdev, rebooting in 10 s"; sleep 10; reboot -f; }#' ${D}/init
    grep -q 'reboot -f' ${D}/init || bbfatal "init-boot.sh changed: no 'exec sh' fallback to replace"
}
