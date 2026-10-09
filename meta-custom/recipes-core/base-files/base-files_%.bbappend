# The rootfs image is only as large as its contents plus
# IMAGE_ROOTFS_EXTRA_SPACE; grow it to fill its A/B slot on first boot.
#
# /home lives on the SD card. nofail on the site entries: a missing SD card
# or an unreachable NFS server must not stop the board from reaching ssh;
# without the card, /home falls back to the rootfs copy.
#
# The model share is on leader, a tailnet name that resolves through
# MagicDNS once tailscaled runs. It mounts on first access (automount), so
# boot never waits for leader, and an access while leader is down fails
# and the next one tries again.
do_install:append() {
    sed -i -E 's#^(/dev/root[[:space:]]+/[[:space:]]+auto[[:space:]]+)defaults#\1defaults,x-systemd.growfs#' ${D}${sysconfdir}/fstab
    grep -q 'x-systemd.growfs' ${D}${sysconfdir}/fstab || bbfatal "fstab has no /dev/root entry to grow"
    cat >> ${D}${sysconfdir}/fstab <<'FSTAB'
LABEL=xavier-sd                          /mnt/sdcard     ext4  defaults,nofail                                     0 2
/mnt/sdcard/home                         /home           none  bind,x-systemd.requires-mounts-for=/mnt/sdcard,nofail 0 0
leader:/mnt/raid/cluster-models          /mnt/nfs-models nfs   ro,soft,timeo=30,retrans=2,_netdev,nofail,x-systemd.after=tailscaled.service,x-systemd.automount,x-systemd.mount-timeout=30 0 0
FSTAB
}
