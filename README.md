# Jetson AGX Xavier Yocto Image

This project builds a Yocto Linux image for the NVIDIA Jetson AGX Xavier Developer Kit.

The image uses L4T r35.6.4 and the Yocto Scarthgap release. It uses RPM packages and systemd.

## Image Content

The image includes these items:

- CUDA 11.4, cuDNN, and TensorRT from the Jetson BSP.
- CUDA 12.2 compatibility libraries.
- Docker, Docker Compose, and NVIDIA container tools.
- Rust, Cargo, CMake, Git, Python, and common debug tools.
- OpenSSH and Tailscale.
- Bash and zsh. The image build sets the root login shell to `/bin/bash`.

The Xavier GPU uses CUDA architecture 7.2.

## Host Requirements

Use a supported Linux host. Ubuntu 22.04 LTS or Ubuntu 24.04 LTS is suitable.

Install these host packages:

```sh
sudo apt-get update
sudo apt-get install gawk wget git diffstat unzip texinfo gcc build-essential \
  chrpath socat cpio python3 python3-pexpect xz-utils debianutils \
  iputils-ping file usbutils
```

Provide at least 300 GB of free disk space. Provide 32 GB of RAM when possible.

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

The image installs `sudo`. Members of `wheel` can run administrative commands with `sudo`, for example `sudo -i`. Use `su -` when you need a root login shell. The image uses SSH key authentication for root. Configure a key in `config/local.private.conf` before you build. The key installs at `/home/root/.ssh/authorized_keys`.

## Reproducibility

The setup script pins these external layers:

| Layer | Revision |
| --- | --- |
| meta-openembedded | `b5874ea07d69919d9b40d59f2c2f0bbd24bc3259` |
| meta-virtualization | `e066aa71b00d8ef5121fcab3a7ac813058cda09c` |
| meta-tegra | `0c507bfe8d64a0e113beeff8f45e7fe0dfb5bc80` |
| meta-tailscale | `c70a30954839eef1923627e3a2f056692611f789` |

The custom recipes in `meta-custom/` are part of this project. The source archives for the NVIDIA container library use fixed commit IDs and SHA-256 checksums.

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
