
nano /etc/sysctl.conf
#添加：
net.core.rmem_max=16777216
net.core.wmem_max=16777216
net.ipv4.tcp_rmem=4096 87380 16777216
net.ipv4.tcp_wmem=4096 65536 16777216
net.ipv4.tcp_congestion_control=bbr
# 应用配置
sysctl -p
# 验证配置是否生效
sysctl net.core.rmem_max