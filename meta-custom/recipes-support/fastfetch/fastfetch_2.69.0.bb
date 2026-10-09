SUMMARY = "Fast system information tool"
DESCRIPTION = "A fastfetch-like system information tool with MomongaOS branding."
HOMEPAGE = "https://github.com/fastfetch-cli/fastfetch"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2090e7d93df7ad5a3d41f6fb4226ac76"

SRC_URI = "git://github.com/fastfetch-cli/fastfetch.git;protocol=https;branch=dev \
           file://0001-add-momonga-logo.patch"
SRCREV = "0c3b852bf7bad2837a814c7a31bf332092048a2b"

S = "${WORKDIR}/git"

PR = "r1"

DEPENDS = "linux-libc-headers zlib"

inherit cmake

EXTRA_OECMAKE = " \
    -DBUILD_FLASHFETCH=OFF \
    -DBUILD_TESTS=OFF \
    -DENABLE_IMAGE_LOGO=OFF \
    -DENABLE_VULKAN=OFF \
    -DENABLE_WAYLAND=OFF \
    -DENABLE_XCB_RANDR=OFF \
    -DENABLE_XRANDR=OFF \
    -DENABLE_DRM=OFF \
    -DENABLE_VADRM=OFF \
    -DENABLE_VAX11=OFF \
    -DENABLE_VDPAU=OFF \
    -DENABLE_GIO=OFF \
    -DENABLE_DCONF=OFF \
    -DENABLE_EET=OFF \
    -DENABLE_DBUS=OFF \
    -DENABLE_SQLITE3=OFF \
    -DENABLE_RPM=OFF \
    -DENABLE_EGL=OFF \
    -DENABLE_GLX=OFF \
    -DENABLE_OPENCL=OFF \
    -DENABLE_PULSE=OFF \
    -DENABLE_DDCUTIL=OFF \
    -DENABLE_ELF=OFF \
    -DENABLE_LUA=OFF \
    -DENABLE_LIBZFS=OFF \
    -DENABLE_WCWIDTH=ON \
    -DSET_TWEAK=OFF \
"

FILES:${PN} += "${bindir}/fastfetch \
               ${datadir}/fastfetch \
               ${datadir}/bash-completion \
               ${datadir}/zsh \
               ${datadir}/fish \
               ${datadir}/licenses/fastfetch"
