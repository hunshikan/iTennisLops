package com.itennishy.lops.service;

import com.itennishy.lops.executor.JSchExecutor;
import com.itennishy.lops.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class YumConfigService {

    /**
     * 配置远程Yum源
     *
     * @param jSchUtil
     * @param serverLink
     */
    public void setRemoteYumReposity(JSchExecutor jSchUtil, String serverLink) {
        try {
            String cmd = "(\n" +
                    "cat << EOF\n" +
                    "[remote]\n" +
                    "name=remote\n" +
                    "baseurl=" + serverLink + "\n" +
                    "gpgcheck=0\n" +
                    "enabled=1\n" +
                    "EOF\n" +
                    ") > /etc/yum.repos.d/remote.repo";
            jSchUtil.execCmd(cmd);
        } catch (Exception e) {
            log.error("Exception Happens:", e);
        }
    }

    /**
     * 配置本地Yum源
     *
     * @param jSchUtil
     */
    public void setLocalYumReposity(JSchExecutor jSchUtil, String mediapath) {
        try {
            String cmd = "(\n" +
                    "cat << EOF\n" +
                    "[local]\n" +
                    "name=local\n" +
                    "baseurl=file://" + mediapath + "\n" +
                    "gpgcheck=0\n" +
                    "enabled=1\n" +
                    "EOF\n" +
                    ") > /etc/yum.repos.d/local.repo";
            jSchUtil.execCmd(cmd);
            log.info("配置文件应该修改完毕:" + "local.repo");
        } catch (Exception e) {
            log.error("Exception Happens:", e);
        }
    }

    /**
     * 全路径挂载
     *
     * @param jSchUtil
     * @param isoFileAndPath
     * @param mediapath
     */
    public void setMountISOWithConfig(JSchExecutor jSchUtil, String isoFileAndPath, String mediapath) {
        try {
            if (new File(isoFileAndPath).exists()) {
                jSchUtil.execCmd("umount  " + mediapath);
                log.info("卸载目录成功:" + mediapath);
                int status = jSchUtil.execCmd("mount -o loop " + isoFileAndPath + " " + mediapath);
                if (status == 0)
                    log.info("本地镜像文件挂载成功!");
                else
                    log.error("本地镜像文件挂载失败!");
            } else {
                log.error("本地镜像文件不存在，请重试:" + isoFileAndPath);
            }
        } catch (Exception e) {
            log.error("Exception Happens:", e);
        }
    }
}