SUMMARY = "Site configuration of the xavier host"
DESCRIPTION = "Docker data root on the SD card (label xavier-sd), NVIDIA default runtime, \
and the local registry, matching the board's previous Ubuntu installation."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://daemon.json \
    file://10-sdcard.conf \
"

S = "${WORKDIR}"

do_install() {
    install -D -m 0644 ${S}/daemon.json ${D}${sysconfdir}/docker/daemon.json
    install -D -m 0644 ${S}/10-sdcard.conf ${D}${systemd_system_unitdir}/docker.service.d/10-sdcard.conf
    install -d ${D}/mnt/sdcard ${D}/mnt/nfs-models
}

FILES:${PN} = " \
    ${sysconfdir}/docker/daemon.json \
    ${systemd_system_unitdir}/docker.service.d/10-sdcard.conf \
    /mnt/sdcard \
    /mnt/nfs-models \
"
RDEPENDS:${PN} = "docker-moby nvidia-container-toolkit nfs-utils-client"
