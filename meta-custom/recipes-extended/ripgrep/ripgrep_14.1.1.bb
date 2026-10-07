SUMMARY = "ripgrep - fast, recursive search tool like grep, written in Rust"
HOMEPAGE = "https://github.com/BurntSushi/ripgrep"
DESCRIPTION = "ripgrep recursively searches directories for a regex pattern while respecting .gitignore."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=8d0d0aa488af0ab9aafa3b85a7fc8e12"

SRC_URI = "crate://crates.io/ripgrep/${PV};name=ripgrep"
SRC_URI[ripgrep.sha256sum] = "f77b8032dc584527975f34aa5a897d0ef5a785573fda778771a614ff9da501d9"
S = "${CARGO_VENDORING_DIRECTORY}/ripgrep-${PV}"

inherit cargo cargo-update-recipe-crates

DEPENDS:append:class-target = " libstd-rs"

require ${BPN}-crates.inc

BBCLASSEXTEND = "native"
