SUMMARY = "Site configuration of the xavier host"
DESCRIPTION = "Mount points for the SD card (label xavier-sd) and the NFS model share, and \
Docker ordering on the SD card mount, and passwordless sudo for wheel. The Docker daemon settings live in the \
nvidia-docker bbappend."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://10-sdcard.conf file://90-wheel-nopasswd"

S = "${WORKDIR}"

do_install() {
    install -D -m 0644 ${S}/10-sdcard.conf ${D}${systemd_system_unitdir}/docker.service.d/10-sdcard.conf
    install -D -m 0440 ${S}/90-wheel-nopasswd ${D}${sysconfdir}/sudoers.d/90-wheel-nopasswd
    install -d ${D}/mnt/sdcard ${D}/mnt/nfs-models
}

FILES:${PN} = " \
    ${systemd_system_unitdir}/docker.service.d/10-sdcard.conf \
    ${sysconfdir}/sudoers.d/90-wheel-nopasswd \
    /mnt/sdcard \
    /mnt/nfs-models \
"
RDEPENDS:${PN} = "sudo docker-moby nvidia-docker nvidia-container-toolkit nfs-utils-client"
