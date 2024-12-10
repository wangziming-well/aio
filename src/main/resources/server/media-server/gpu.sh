
# 安装gpu驱动
# 禁用 默认的驱动
nano /etc/modprobe.d/blacklist-nouveau.conf
# 添加下面内容
blacklist nouveau
options nouveau modeset=0

# 更新 initramfs
update-initramfs -u

#让ubuntu自己推荐驱动
ubuntu-drivers devices
# 自动安装
ubuntu-drivers autoinstall

reboot # 重启

nvidia-smi # 查看显卡情况

# docker使用gpu
# 安装 NVIDIA Container Toolkit
# 添加 NVIDIA 的包仓库 GPG 密钥
curl -s -L https://nvidia.github.io/nvidia-docker/gpgkey | sudo apt-key add -
# 添加 NVIDIA 的包仓库
distribution=$(. /etc/os-release;echo $ID$VERSION_ID)
curl -s -L https://nvidia.github.io/nvidia-docker/$distribution/nvidia-docker.list | sudo tee /etc/apt/sources.list.d/nvidia-docker.list
#  安装 NVIDIA Container Toolkit
apt update
apt install -y nvidia-docker2


# 设置 NVIDIA 为默认运行时
nano /etc/docker/daemon.json
# 添加或修改如下内容：
{
    "runtimes": {
        "nvidia": {
            "path": "nvidia-container-runtime",
            "runtimeArgs": []
        }
    },
    "default-runtime": "nvidia"
}

# 重启 Docker 服务以应用更改
systemctl restart docker