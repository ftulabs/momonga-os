# The rootfs image is only as large as its contents plus
# IMAGE_ROOTFS_EXTRA_SPACE; grow it to fill its A/B slot on first boot.
#
# nofail on the site entries: a missing SD card or an unreachable NFS server
# must not stop the board from reaching ssh.
do_install:append() {
    sed -i -E 's#^(/dev/root[[:space:]]+/[[:space:]]+auto[[:space:]]+)defaults#\1defaults,x-systemd.growfs#' ${D}${sysconfdir}/fstab
    grep -q 'x-systemd.growfs' ${D}${sysconfdir}/fstab || bbfatal "fstab has no /dev/root entry to grow"
    cat >> ${D}${sysconfdir}/fstab <<'FSTAB'
LABEL=xavier-sd                          /mnt/sdcard     ext4  defaults,nofail                                     0 2
10.0.191.10:/mnt/raid/cluster-models     /mnt/nfs-models nfs   ro,soft,timeo=30,retrans=2,_netdev,nofail          0 0
FSTAB
}
