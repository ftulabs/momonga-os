if [ -n "${SSH_CONNECTION:-}" ] && [ -t 1 ]; then
    /usr/sbin/login-motd
fi
