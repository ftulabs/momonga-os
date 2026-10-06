SUMMARY = "Enable systemd network time synchronization"
LICENSE = "CLOSED"

inherit allarch systemd

RDEPENDS:${PN} = "systemd"

SRC_URI = "file://10-network-time.conf"

do_install() {
    install -D -m 0644 ${WORKDIR}/10-network-time.conf ${D}${sysconfdir}/systemd/timesyncd.conf.d/10-network-time.conf
    install -d ${D}${sysconfdir}/systemd/system/sysinit.target.wants
    ln -sf ${systemd_system_unitdir}/systemd-timesyncd.service ${D}${sysconfdir}/systemd/system/sysinit.target.wants/systemd-timesyncd.service
}

FILES:${PN} = "${sysconfdir}/systemd"
