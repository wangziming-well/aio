
# 挂载目录
mkdir -p /docker/public/media-library
mkdir -p /docker/public/downloads
mount -t nfs 192.168.2.101:/mnt/main-pool/media  /docker/public/media-library
mount -t nfs 192.168.2.101:/mnt/download-pool/downloads /docker/public/downloads
df -h | grep nfs
# 取消挂载
umount /docker/public/media-library
umount /docker/public/downloads
# 设置开机自动挂载
nano /etc/fstab
# 在文件见中添加下面内容
192.168.2.101:/mnt/main-pool/media  /docker/public/media-library nfs defaults 0 0
192.168.2.101:/mnt/download-pool/downloads /docker/public/downloads nfs defaults 0 0
mount -a # 验证添加的内容是否有效


# 配置下载和媒体目录
cd /docker/public/media-library
mkdir Movie TV Anime Unknown

cd /docker/public/downloads
mkdir Movie TV Anime
# 将emby docker容器中网页文件所在文件夹挂载到宿主机

docker stop emby-server
mkdir /docker/emby/system
  docker cp emby-server:/system/dashboard-ui /docker/emby/system/dashboard-ui
# 然后在portainer中将emby-server容器的这个目录挂载上并重新启动

# 配置emby-server的第三方播放器打开插件
# https://greasyfork.org/en/scripts/459297-embylaunchpotplayer
# 将上面地址的油猴脚本的js文件externalPlayer.js添加到 /docker/emby/system/dashboard-ui 文件夹下
# 然后编辑 index.html,在</body>前添加上 <script src="./externalPlayer.js" ></script>