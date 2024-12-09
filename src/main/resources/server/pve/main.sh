# 安装proxmox-pve-exporter
# 创建用户
groupadd --system prometheus
useradd -s /sbin/nologin --system -g prometheus prometheus
mkdir /etc/prometheus/
# 安装
apt update
apt install python3 python3-pip
python3 -m pip install prometheus-pve-exporter --break-system-packages
# 创建配置文件
vi /etc/prometheus/pve.yml
# 填入下面内容
default:
	user: root@pam
	password: wang998321
	verify_ssl: false
# 设置权限
chown -R prometheus:prometheus /etc/prometheus/
chmod -R 775 /etc/prometheus/
# 创建systemed
tee /etc/systemd/system/prometheus-pve-exporter.service<<EOF
[Unit]
Description=Prometheus exporter for Proxmox VE
Documentation=https://github.com/znerol/prometheus-pve-exporter

[Service]
Restart=always
User=prometheus
ExecStart=/usr/local/bin/pve_exporter

[Install]
WantedBy=multi-user.target
EOF
# 设置开机自启动
systemctl daemon-reload
systemctl start prometheus-pve-exporter
systemctl enable prometheus-pve-exporter
# 验证： 访问 pve_ip:9221/pve



# 安装node-exporter
wget -P /tmp https://github.com/prometheus/node_exporter/releases/download/v1.4.0-rc.0/node_exporter-1.4.0-rc.0.linux-amd64.tar.gz
tar -xzvf /tmp/node_exporter-1.4.0-rc.0.linux-amd64.tar.gz -C /tmp
mv /tmp/node_exporter-1.4.0-rc.0.linux-amd64 /usr/local/bin/

useradd -rs /bin/false node_exporter

tee /etc/systemd/system/node_exporter.service<<EOF
[Unit]
Description=Node Exporter
After=network.target

[Service]
User=node_exporter
Group=node_exporter
Type=simple
ExecStart=/usr/local/bin/node_exporter

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable node_exporter
systemctl start node_exporter