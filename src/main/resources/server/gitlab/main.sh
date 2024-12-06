# 修改gitlab被nginx反向代理时的配置：
#修改 /etc/gitlab/gitlab.rb的下面配置
# external_url 'https://gitlab.wangziming.com'
# gitlab_rails['trusted_proxies'] = ['192.168.2.103']
# nginx['listen_port'] = 80
# nginx['listen_https'] = false
gitlab-ctl reconfigure # 重启
