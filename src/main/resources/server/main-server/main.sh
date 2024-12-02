# ubuntu镜像 https://mirrors.aliyun.com/ubuntu/
# 设置root密码并允许root ssh远程登录

sudo passwd root
nano /etc/ssh/sshd_config # PermitRootLogin 改成 yes
systemctl restart ssh

# docker docker-compose 安装
apt update
apt install docker.io docker-compose


# 设置nfs挂载
apt install nfs-common
mkdir -p /docker/public/remote
mkdir -p /docker/public/local
mount 192.168.2.101:/mnt/main-pool/dockers /docker/public/remote

# 设置自动挂载
nano /etc/fstab
# 在文件末尾添加一行，格式如下
# 192.168.2.101:/mnt/main-pool/dockers /docker/public/remote nfs defaults 0 0
mount -a # 测试自动挂载
df -h # 检查挂载是否成功


# docker 开启remote api
nano /lib/systemd/system/docker.service
# 在原来的位置改为：
# ExecStart=/usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock -H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock

# nginx SSL证书
apt update
apt install certbot python3-certbot-nginx
# 为要配置的域名建立server
nano /etc/nginx/sites-available
# 在文件中添加对应域名的server，例如：新增
server {
  server_name nextcloud.wangziming.com; # managed by Certbot
	root /var/www/html;
	index index.html index.htm index.nginx-debian.html;
}
# 分割线
nginx -t
systemctl restart nginx
#申请证书
certbot --nginx -d emby.wangziming.com -d nextcloud.wangziming.com --cert-name wangziming.com
