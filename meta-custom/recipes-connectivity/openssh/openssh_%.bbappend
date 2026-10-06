FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " file://root-login.conf"

do_install:append() {
	install -d ${D}${sysconfdir}/ssh/sshd_config.d
	install -m 0644 ${WORKDIR}/root-login.conf ${D}${sysconfdir}/ssh/sshd_config.d/root-login.conf
}

FILES:${PN}-sshd += "${sysconfdir}/ssh/sshd_config.d/root-login.conf"
CONFFILES:${PN}-sshd += "${sysconfdir}/ssh/sshd_config.d/root-login.conf"
