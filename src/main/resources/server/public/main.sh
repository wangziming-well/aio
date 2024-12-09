# ubuntu镜像 https://mirrors.aliyun.com/ubuntu/
# 设置root密码，和启动root ssh
sudo passwd root
su root
nano /etc/ssh/sshd_config
#将 PermitRootLogin 设置为 yes
systemctl restart sshd



# 解决内存buffer/cache占用过高问题
nano /etc/sysctl.conf
# 使用下面配置
vm.dirty_ratio = 5
vm.dirty_background_ratio =5
vm.dirty_writeback_centisecs=100
vm.dirty_expire_centisecs=30000
vm.drop_caches=3
vm.swappiness=100
vm.vfs_cache_pressure=133

sysctl -p # 使配置生效

