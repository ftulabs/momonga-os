FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://virt-host.cfg \
    file://0001-nvidia-galen-p2888-0001-p2822-0000-poll-sd-card.patch \
"

# Load the KVM guest transports at boot. vsock has no net-pf-40 alias in this
# kernel, so an AF_VSOCK socket (Cuttlefish's host services) does not autoload
# it; vhost_vsock pulls it in.
KERNEL_MODULE_AUTOLOAD += "vhost_vsock vhost_net"
