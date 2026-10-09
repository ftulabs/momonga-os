# Login users from MOMONGA_USERS, "name:uid:shell" separated by spaces.
# Each gets a group with the same number, the groups in MOMONGA_USER_GROUPS,
# and the SSH keys in MOMONGA_USER_KEY_<name> (several separated by a literal
# \n). The keys go in /etc/ssh/authorized_keys/<name>, not the home directory:
# /home is a bind mount from the SD card and would hide them.

inherit extrausers

MOMONGA_USERS ??= ""
MOMONGA_USER_GROUPS ??= "wheel,docker,kvm,video,render"

python __anonymous() {
    groups = d.getVar('MOMONGA_USER_GROUPS')
    for entry in (d.getVar('MOMONGA_USERS') or '').split():
        name, uid, shell = entry.split(':')
        d.appendVar('EXTRA_USERS_PARAMS',
                    ' groupadd -g %s %s; useradd -m -u %s -g %s -G %s -s %s %s;'
                    % (uid, name, uid, name, groups, shell, name))
}

ROOTFS_POSTPROCESS_COMMAND += "install_momonga_user_keys;"
python install_momonga_user_keys () {
    import os
    keydir = os.path.join(d.getVar('IMAGE_ROOTFS'), 'etc/ssh/authorized_keys')
    for entry in (d.getVar('MOMONGA_USERS') or '').split():
        name = entry.split(':')[0]
        keys = d.getVar('MOMONGA_USER_KEY_' + name)
        if not keys:
            continue
        os.makedirs(keydir, exist_ok=True)
        path = os.path.join(keydir, name)
        with open(path, 'w') as f:
            f.write(keys.replace('\\n', '\n').strip() + '\n')
        os.chmod(path, 0o644)
}
install_momonga_user_keys[vardeps] += "MOMONGA_USERS ${@' '.join('MOMONGA_USER_KEY_' + e.split(':')[0] for e in (d.getVar('MOMONGA_USERS') or '').split())}"
