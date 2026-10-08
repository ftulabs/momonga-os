# Jetson AGX Xavier Yocto Image

This project builds a Yocto Linux image for the NVIDIA Jetson AGX Xavier Developer Kit.

The image uses L4T r35.6.4 and the Yocto Scarthgap release. It uses RPM packages and systemd.

## Image Content

The image includes these items:

- CUDA 11.4, cuDNN, and TensorRT from the Jetson BSP.
- CUDA 12.2 compatibility libraries.
- Docker, Docker Compose, Podman, and NVIDIA container tools.
- Rust, Cargo, CMake, Git, Python, Node and common debug tools.
- OpenSSH and Tailscale.
- Bash and zsh. The image build sets the root login shell to `/bin/bash`.

The Xavier GPU uses CUDA architecture 7.2.

## Host Requirements

Use a supported Linux host. The build is tested against Ubuntu 24.04 LTS, but 22.04 or other distros may work too.

Install these host packages:

```sh
sudo apt-get update
sudo apt-get install gawk wget git diffstat unzip texinfo gcc build-essential \
  chrpath socat cpio python3 python3-pexpect xz-utils debianutils \
  iputils-ping file usbutils
```

Provide at least 300 GB of free disk space. Provide 32 GB of RAM when possible.

## Per-user Distrobox

The Xavier image provides Docker and NVIDIA's Docker runtime. Install
Distrobox under your home directory, then create a container from NVIDIA's
L4T JetPack image. Choose an image tag compatible with the Xavier's L4T
release from the [NGC L4T JetPack catalog](https://catalog.ngc.nvidia.com/orgs/nvidia/containers/l4t-jetpack):

This project uses L4T 35.6.4. NGC currently lists `r35.4.1` as its newest R35
JetPack image, so that tag is older and is not an exact match.

```sh
curl -fsSL https://raw.githubusercontent.com/89luca89/distrobox/main/install \
  | sh -s -- --prefix "$HOME/.local"
export PATH="$HOME/.local/bin:$PATH"
export CONTAINER_MANAGER=docker
# Replace with the tag you intend to use; r35.4.1 is only an older example.
L4T_IMAGE=nvcr.io/nvidia/l4t-jetpack:r35.4.1
distrobox create --name l4t-dev \
  --image "$L4T_IMAGE" \
  --additional-flags "--runtime=nvidia --network=host"
distrobox enter l4t-dev
```

Add both `export` lines to `~/.profile`. Docker must be running, and your user
must be allowed to access its daemon. Docker daemon access is effectively
root access. The L4T JetPack image includes CUDA, cuDNN, TensorRT, VPI, and
Jetson multimedia libraries. Install any additional packages inside the
container; they stay in its storage:

```sh
sudo apt-get update
sudo apt-get install -y build-essential
```

The NVIDIA runtime uses the image's CSV mounts (not CDI) to pass the Xavier's
GPU devices and driver libraries. To display an app on your computer, connect
to the Xavier with `ssh -X`, enter the Distrobox, and start the app. The image
includes `xauth` and enables SSH X11 forwarding; no screen or display server
is needed on the Xavier. `--network=host` lets the container reach SSH's
forwarding proxy on the Xavier. Your computer must have a working X11 display
(Xorg or XWayland).

## Create a Build Directory

Clone this project from its GitHub repository. Change to the project directory.

Run the setup script:

```sh
./scripts/setup-jetson-xavier-build
```

Before you run the script, you can create local access configuration:

```sh
cp config/local.private.conf.example config/local.private.conf
```

Set the SSH public key in `config/local.private.conf`. Git ignores this file. The setup script adds it to the build configuration.

The script does these actions:

1. Clones the required external layers with shallow Git clones.
2. Checks the exact revision of each external layer.
3. Creates `build/conf/local.conf`.
4. Creates `build/conf/bblayers.conf` with paths for this machine.

The script stops if the build directory already has a configuration. Use a new build directory to make another configuration:

```sh
./scripts/setup-jetson-xavier-build /work/jetson-build
```

## Build the Image

Start the build environment:

```sh
. oe-init-build-env build
```

Build the image:

```sh
bitbake core-image-minimal
```

BitBake writes images to this directory:

```text
build/tmp/deploy/images/jetson-agx-xavier-devkit/
```

The first build downloads source files and creates build output. Do not add `build/`, download files, sstate files, or external layers to Git.

## Flash the Xavier

The build creates a Tegra flash bundle. Flashing erases the Xavier internal eMMC. Use a supported Linux host, a direct USB cable, and stable power for the host and Xavier.

1. Build the image.
2. Set the Xavier to Force Recovery Mode. Power off the Xavier. Connect the baseboard Type-C port `J512` to the build host. Hold the `FORCE RECOVERY` button, press and release `POWER`, then release `FORCE RECOVERY`.
3. Confirm that the host detects the Xavier:

```sh
lsusb | grep '0955:7019'
```

4. Extract the `*.tegraflash.tar.gz` file for the image. Replace `<timestamp>` with the name that BitBake created:

```sh
deploy_dir=build/tmp/deploy/images/jetson-agx-xavier-devkit
flash_dir=/tmp/xavier-flash
mkdir -p "$flash_dir"
tar -xzf "$deploy_dir/core-image-minimal-jetson-agx-xavier-devkit.rootfs-<timestamp>.tegraflash.tar.gz" -C "$flash_dir"
```

5. Run the flash script from the extracted directory:

```sh
cd "$flash_dir"
sudo ./doflash.sh
```

The script programs the boot firmware and the image to the Xavier internal eMMC. Do not disconnect the USB cable or power during this process. The Xavier restarts when the flash process completes.

This procedure is for an unfused development kit. A device with Secure Boot fuses needs its signing keys and the matching `doflash.sh` signing options.

## Device Access

The image enables the OpenSSH server. The tracked project does not contain SSH keys or Tailscale auth keys.

`config/issue.net` is shown before SSH authentication. After an interactive SSH login,
the image reports load, memory, root filesystem usage, and Tegra CPU/GPU/thermal data
when the corresponding kernel interfaces are available.

To add one SSH public key after setup, add these lines to the local `build/conf/local.conf` file before the build:

```bitbake
CORE_IMAGE_EXTRA_INSTALL:append = " ssh-keys"
SSH_AUTHORIZED_KEY = "ssh-ed25519 AAAA... user@host"
```

Do not commit that local configuration file.

To join Tailscale, start the device and run `tailscale up`. Complete the login step that Tailscale shows.

## Time

The image uses the `Asia/Ho_Chi_Minh` timezone. It starts `systemd-timesyncd` at boot and synchronizes time after the Ethernet network is available. The configured NTP servers are `time.cloudflare.com` and `time.google.com`.

## First Boot Users

`/etc/passwd` is present in the final image. The image sets the root login shell to `/bin/bash`.

You can create and manage users after flashing. Sign in as root on the local console or through SSH with the key that you configured. Then create a user and set its password:

```sh
useradd --create-home --shell /bin/bash xavier
passwd xavier
```

Use `usermod` to change groups. Add an administrator to `wheel` and, when needed, to the Docker group:

```sh
usermod --append --groups wheel,docker xavier
```

The image installs `sudo`. Members of `wheel` can run administrative commands with `sudo`, for example `sudo -i`. Use `su -` when you need a root login shell. The image uses SSH key authentication for root. Configure a key in `config/local.private.conf` before you build. The key installs at `/root/.ssh/authorized_keys`.

## Reproducibility

The setup script pins these external layers:

| Layer | Revision |
| --- | --- |
| meta-openembedded | `b5874ea07d69919d9b40d59f2c2f0bbd24bc3259` |
| meta-virtualization | `e066aa71b00d8ef5121fcab3a7ac813058cda09c` |
| meta-tegra | `0c507bfe8d64a0e113beeff8f45e7fe0dfb5bc80` |
| meta-tailscale | `c70a30954839eef1923627e3a2f056692611f789` |

The custom recipes in `meta-custom/` are part of this project. The source archives for the NVIDIA container library use fixed commit IDs and SHA-256 checksums.

## Extra ARM64 Packages

The `meta-custom` layer provides prebuilt ARM64 packages for Neovim, `bat`, `fzf`, and Node.js with npm. These recipes package official upstream Linux ARM64 release binaries, so they do not compile Neovim, Rust, or V8. It also enables Zsh's dynamic modules, which are required by plugins such as Powerlevel10k and fzf-tab. Build the RPMs and repository index with:

```sh
bitbake neovim bat fzf nodejs24 zsh
bitbake package-index
```

The Node.js and Neovim binaries require glibc 2.28 and 2.34 or newer, respectively. Confirm those requirements against the target image before installing. The target's RPM dependency solver may not detect every required glibc symbol version. The `nodejs24` RPM provides both Node.js 24 and npm.

ARM64 RPM feeds are published as GitHub Releases named `momonga-rpm-feed-*`. A GitHub Actions workflow deploys each release feed to GitHub Pages at `/rpm/momonga/aarch64/` and generates browsable directory indexes at every directory level. The RPMs use the Momonga RPM signing key; the workflow publishes its public key beside the feed. Once the custom domain `momonga.ftds.online` is configured for GitHub Pages, use this DNF configuration:

```ini
[momonga-extra]
name=Momonga Extra Packages
baseurl=https://momonga.ftds.online/rpm/momonga/aarch64/
enabled=1
gpgcheck=1
gpgkey=https://momonga.ftds.online/rpm/momonga/aarch64/RPM-GPG-KEY-momonga
repo_gpgcheck=0
```

The build result can change when an upstream download is removed or changed. Keep a copy of `build/downloads/` if you must make the same build without network access.

## Clean Data

These paths are local build data. Git ignores them:

- `build/`
- `downloads/`
- `sstate-cache/`
- `meta-openembedded/`
- `meta-virtualization/`
- `meta-tegra/`
- `meta-tailscale/`

To remove one build configuration, remove its build directory. Do not remove a directory until you no longer need its images, logs, downloads, or sstate cache.
