
apt update
apt install nut nut-server nut-client nut-cgi
# 配置驱动
nano /etc/nut/ups.conf
# 文件尾部添加下面内容
[tgbox850]
        driver=usbhid-ups
        port=auto
        vendorid=0463
        desc='SANTAK TGBOX-850 UPS'
        pollinterval=1
          override.battery.charge.low=80
root@server:~#
# 分割线

# 配置服务器
nano /etc/nut/nut.conf
# 修改模式 MODE=netserver

# 修改网络监听配置
nano /etc/nut/upsd.conf
#文件添加 LISTEN 0.0.0.0 3493

# 创建客户端连接时所需用户名(master)和密码(wang998321)
nano /etc/nut/upsd.users
# 添加下面内容
[master]
    password = wang998321
    upsmon primary
# 配置WEB(CGI)服务
nano /etc/nut/hosts.conf
# 添加： MONITOR tgbox850@localhost "SANTAK TGBOX-850 UPS"
#
nano /etc/nut/upsmon.conf
# 添加 MONITOR tgbox850@localhost 1 master wang998321 primary
#确认CGI目录安全性
nano /etc/nut/upsset.conf
# 取消注释该行： I_HAVE_SECURED_MY_CGI_DIRECTORY

# 重启相关服务
systemctl restart nut-driver@tgbox850
systemctl restart nut-server
systemctl restart nut-monitor

systemctl list-units --type=service

# apache web设置
nano /etc/apache2/apache2.conf
# 添加下面内容
Alias /nut /usr/share/nut/www
<Directory /usr/share/nut/www>
        Options +SymLinksIfOwnerMatch
        AllowOverride All
        Require all granted
</Directory>


#修改serve-cfg-bin.conf文件内容
nano /etc/apache2/conf-available/serve-cgi-bin.conf
# 添加：

	<IfDefine ENABLE_USR_LIB_CGI_BIN>
		ScriptAlias /cgi-bin/ /usr/lib/cgi-bin/
		<Directory "/usr/lib/cgi-bin">
			AllowOverride None
            AddHandler cgi-script .cgi
			Options +ExecCGI -MultiViews +SymLinksIfOwnerMatch
			Require all granted
		</Directory>
	</IfDefine>
# 重启服务
systemctl restart apache2
# 验证配置是否生效
a2enmod cgi # 输出类似：  Module cgid already enabled

# 验证是否生效 http://192.168.2.99/nut