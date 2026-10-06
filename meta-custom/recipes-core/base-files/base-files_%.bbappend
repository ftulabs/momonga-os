# nofail on both entries: a missing SD card or an unreachable NFS server must
# not stop the board from reaching ssh.
do_install:append() {
    cat >> ${D}${sysconfdir}/fstab <<'FSTAB'
LABEL=xavier-sd                          /mnt/sdcard     ext4  defaults,nofail                                     0 2
10.0.191.10:/mnt/raid/cluster-models     /mnt/nfs-models nfs   ro,soft,timeo=30,retrans=2,_netdev,nofail          0 0
FSTAB
}
