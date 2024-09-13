#!/bin/bash
# git拉取函数
echo -e "\033[1;37m =========开始重构静态文档网站========= \033[0m"

function gitpull(){
        local remote_url=$1
        local dir=$2
        if test ! -d "$dir"
        then
                git clone $remote_url $dir
        else
                cd $dir
                git pull origin
        fi
}
# 高亮色前缀
info_prefix="\033[1;36m[INFO]\033[0m "
success_prefix="\033[1;32m[SUCCESS]\033[0m "
#拉取图片、笔记和配置
git_base_dir="/usr/local/git"
git_base_remote_url="git@gitee.com:wangziming707"

img_git_remote_url="${git_base_remote_url}/note-pic.git"
img_git_dir="${git_base_dir}/note-pic"
note_git_remote_url="${git_base_remote_url}/note.git"
note_git_dir="${git_base_dir}/note"
config_git_remote_url="${git_base_remote_url}/docusaurus-config.git"
config_git_dir="${git_base_dir}/docusaurus-config"

echo -e "${info_prefix}拉取图片:"
gitpull $img_git_remote_url $img_git_dir
echo -e "${info_prefix}拉取笔记:"
gitpull $note_git_remote_url $note_git_dir
echo -e "${info_prefix}拉取配置:"
gitpull $config_git_remote_url $config_git_dir

docusaurus_base_dir="/opt/my-website"
# 复制笔记
note_target_dir="${docusaurus_base_dir}/docs"
echo -e "${info_prefix}复制笔记到:${note_target_dir}"
rm -rf ${note_target_dir}/*
cp -r ${note_git_dir}/* $note_target_dir
echo -e "${success_prefix}复制笔记成功"
# 复制配置
config_target_dir=$docusaurus_base_dir
echo -e "${info_prefix}复制配置到:${docusaurus_base_dir}"
cp -r ${config_git_dir}/config/* $config_target_dir
echo -e "${success_prefix}复制配置成功"

# 编译DealMarkdown
echo -e "${info_prefix}编译DealMarkdown.java"
cd $config_git_dir
javac DealMarkdown.java
echo -e "${success_prefix}编译完成"

# 转换md适配mdx格式
echo -e "${info_prefix}遍历转换md文件以适配docusaurus渲染"
java DealMarkdown $note_target_dir
echo -e "${success_prefix}转换完成"
# 生成docusaurus静态文件
cd /opt/my-website
echo -e "${info_prefix}开始构建docusaurus静态页面文件"
npm run build -- --out-dir /var/www/html
echo -e "${success_prefix}构建完成"

# 复制图片
img_target_dir="/var/www/html/image"
echo -e "${info_prefix}复制图片到:${img_target_dir}"
rm -rf ${img_target_dir}/*
cp -r ${img_git_dir}/img $img_target_dir
echo -e "${success_prefix}复制图片成功"

echo -e "\033[1;37m =========重构静态文档网站完成========= \033[0m"