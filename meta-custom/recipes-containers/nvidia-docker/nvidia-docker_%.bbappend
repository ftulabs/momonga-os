FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://daemon.json"

# One owner for /etc/docker/daemon.json: the NVIDIA runtime registration from
# upstream plus the xavier site settings (data root on the SD card, NVIDIA as
# default runtime, local registry).
do_install:append() {
    install -m 0644 ${WORKDIR}/daemon.json ${D}${sysconfdir}/docker/daemon.json
}
