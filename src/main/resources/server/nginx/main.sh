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
certbot --nginx -d emby.wangziming.com -d nextcloud.wangziming.com -d gitlab.wangziming.com --cert-name wangziming.com


# 生成自建证书
apt-get install openssl
mkdir /home/private-ssl
cd /home/private-ssl
openssl genpkey -algorithm RSA -out mb3admin.com.pem
openssl req -new -key mb3admin.com.pem -out request.csr
openssl x509 -req -in request.csr -signkey mb3admin.com.pem -out mb3admin.com.cert.pem

openssl x509 -in mb3admin.com.cert.pem -text -noout # 验证证书

# emby认证
# 将 emby-auth放入 /etc/nginx/sites-available 后
ln -s /etc/nginx/sites-available/emby-auth /etc/nginx/sites-enabled/
nginx -t
systemctl restart nginx
# 然后修改host文件，或者openwrt的dns拦截： /mb3admin.com/192.168.2.103
# 将之前生成的mb3admin.com.cert.pem证书文件放到emby容器中
# 进入容器中，执行下面命令
cp /path/to/mb3admin.com.cert.pem /etc/ssl/certs/
# 重启容器，验证证书