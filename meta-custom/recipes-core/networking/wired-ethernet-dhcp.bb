SUMMARY = "Enable DHCP for wired Ethernet interfaces"
LICENSE = "CLOSED"

inherit allarch systemd

RDEPENDS:${PN} = "systemd"

SRC_URI = " \
    file://05-dummy-unmanaged.network \
    file://10-wired-dhcp.network \
    file://10-any.conf \
"

do_install() {
    install -D -m 0644 ${WORKDIR}/05-dummy-unmanaged.network ${D}${sysconfdir}/systemd/network/05-dummy-unmanaged.network
    install -D -m 0644 ${WORKDIR}/10-wired-dhcp.network ${D}${sysconfdir}/systemd/network/10-wired-dhcp.network
    install -D -m 0644 ${WORKDIR}/10-any.conf ${D}${sysconfdir}/systemd/system/systemd-networkd-wait-online.service.d/10-any.conf
    install -d ${D}${sysconfdir}/systemd/system/multi-user.target.wants
    ln -sf ${systemd_system_unitdir}/systemd-networkd.service ${D}${sysconfdir}/systemd/system/multi-user.target.wants/systemd-networkd.service
    ln -sf ${systemd_system_unitdir}/systemd-resolved.service ${D}${sysconfdir}/systemd/system/multi-user.target.wants/systemd-resolved.service
}

FILES:${PN} = "${sysconfdir}/systemd"
