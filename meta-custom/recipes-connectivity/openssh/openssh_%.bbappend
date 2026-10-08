FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " file://root-login.conf file://authorized-keys.conf"

do_install:append() {
	install -d ${D}${sysconfdir}/ssh/sshd_config.d
	install -m 0644 ${WORKDIR}/root-login.conf ${D}${sysconfdir}/ssh/sshd_config.d/root-login.conf
	install -m 0644 ${WORKDIR}/authorized-keys.conf ${D}${sysconfdir}/ssh/sshd_config.d/authorized-keys.conf
}

FILES:${PN}-sshd += "${sysconfdir}/ssh/sshd_config.d/root-login.conf ${sysconfdir}/ssh/sshd_config.d/authorized-keys.conf"
CONFFILES:${PN}-sshd += "${sysconfdir}/ssh/sshd_config.d/root-login.conf ${sysconfdir}/ssh/sshd_config.d/authorized-keys.conf"
