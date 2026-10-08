SUMMARY = "Python bindings for the NVIDIA Management Library"
HOMEPAGE = "https://github.com/gpuopenanalytics/pynvml"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://pynvml.py;beginline=1;endline=25;md5=0d40c8a4dddaca3ce481c2855e7d5f4d"

PYPI_PACKAGE = "nvidia_ml_py"

SRC_URI[sha256sum] = "bebe4e48f51b1dc75028c0815cb7bfa14a31a5bb80be70c9d980c6036953fc3d"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core"
