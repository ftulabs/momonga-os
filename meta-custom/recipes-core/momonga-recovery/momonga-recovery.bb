SUMMARY = "Unattended recovery settings for a remotely managed board"
DESCRIPTION = "Reboots on kernel panic and arms the hardware watchdog through systemd, \
so a hung board recovers without physical access."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://90-momonga-panic.conf \
    file://90-momonga-watchdog.conf \
"

S = "${WORKDIR}"

do_install() {
    install -D -m 0644 ${S}/90-momonga-panic.conf ${D}${sysconfdir}/sysctl.d/90-momonga-panic.conf
    install -D -m 0644 ${S}/90-momonga-watchdog.conf ${D}${sysconfdir}/systemd/system.conf.d/90-momonga-watchdog.conf
}

FILES:${PN} = " \
    ${sysconfdir}/sysctl.d/90-momonga-panic.conf \
    ${sysconfdir}/systemd/system.conf.d/90-momonga-watchdog.conf \
"
