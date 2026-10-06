install_login_motd () {
    install -D -m 0644 "${LOGIN_MOTD_FILE}" "${IMAGE_ROOTFS}${sysconfdir}/issue.net"
    install -D -m 0755 "${LOGIN_MOTD_SCRIPT}" "${IMAGE_ROOTFS}${sbindir}/login-motd"
    install -D -m 0644 "${LOGIN_MOTD_PROFILE}" "${IMAGE_ROOTFS}${sysconfdir}/profile.d/99-login-motd.sh"
    install -d "${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config.d"
    cat > "${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config.d/10-banner.conf" <<'EOF'
Banner /etc/issue.net
PrintMotd no
EOF
    cat >> "${IMAGE_ROOTFS}${sysconfdir}/zprofile" <<'EOF'
# zsh does not read /etc/profile, so invoke the login status report here too.
if [ -n "${SSH_CONNECTION:-}" ] && [ -t 1 ]; then
    /usr/sbin/login-motd
fi
EOF
}
