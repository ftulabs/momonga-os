SUMMARY = "Unattended recovery and A/B updates for a remotely managed board"
DESCRIPTION = "Reboots on kernel panic, arms the hardware watchdog through systemd, and \
reboots out of emergency mode after a console timeout, so a hung board recovers without \
physical access. momonga-ota installs a root filesystem into the inactive A/B slot; that \
slot is verified only once the board is reachable (momonga-boot-trial), otherwise the UEFI \
boot chain falls back to the previous slot."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://90-momonga-panic.conf \
    file://90-momonga-watchdog.conf \
    file://50-momonga-reboot.conf \
    file://50-momonga-trial.conf \
    file://momonga-boot-trial \
    file://momonga-boot-trial.service \
    file://momonga-ota \
"

S = "${WORKDIR}"

inherit systemd

do_install() {
    install -D -m 0644 ${S}/90-momonga-panic.conf ${D}${sysconfdir}/sysctl.d/90-momonga-panic.conf
    install -D -m 0644 ${S}/90-momonga-watchdog.conf ${D}${sysconfdir}/systemd/system.conf.d/90-momonga-watchdog.conf
    install -D -m 0644 ${S}/50-momonga-reboot.conf ${D}${systemd_system_unitdir}/emergency.service.d/50-momonga-reboot.conf
    install -D -m 0644 ${S}/50-momonga-trial.conf ${D}${systemd_system_unitdir}/nv_update_verifier.service.d/50-momonga-trial.conf
    install -D -m 0644 ${S}/momonga-boot-trial.service ${D}${systemd_system_unitdir}/momonga-boot-trial.service
    install -D -m 0755 ${S}/momonga-boot-trial ${D}${sbindir}/momonga-boot-trial
    install -D -m 0755 ${S}/momonga-ota ${D}${sbindir}/momonga-ota
}

SYSTEMD_SERVICE:${PN} = "momonga-boot-trial.service"

FILES:${PN} = " \
    ${sysconfdir}/sysctl.d/90-momonga-panic.conf \
    ${sysconfdir}/systemd/system.conf.d/90-momonga-watchdog.conf \
    ${systemd_system_unitdir} \
    ${sbindir} \
"
RDEPENDS:${PN} = " \
    bash coreutils curl gawk grep sed zstd tegra-redundant-boot-base tailscale \
    util-linux-blockdev util-linux-findmnt util-linux-flock util-linux-mount util-linux-sulogin \
"
