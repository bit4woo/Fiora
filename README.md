

## Fiora

项目简介：Fiora是LoL中的无双剑姬的名字，她善于发现对手防守弱点，实现精准打击。该项目为PoC框架[nuclei](https://github.com/projectdiscovery/nuclei)提供图形界面，实现快速搜索、一键运行等功能，提升[nuclei](https://github.com/projectdiscovery/nuclei)的使用体验。

项目地址：https://github.com/bit4woo/Fiora

项目作者：[bit4woo](https://github.com/bit4woo)

视频教程：https://www.bilibili.com/video/bv1Ha411z7T1

## 安装运行

### 一、作为burp插件运行

1、访问https://github.com/bit4woo/Fiora/releases

2、下载最新jar包

3、如下方法安装插件

![image-20220101172629795](README.assets/image-20220101172629795.png)

4、如果你想使用最新的功能，可以使用如下方法自行打包。

```
git clone https://github.com/bit4woo/knife
cd knife
mvn package
```



### 二、作为独立程序运行

该程序即可作为burp插件运行，也可以作为独立程序运行。命令行下通过java启动程序的命令：

```
java -jar Fiora-202100220-jar-with-dependencies.jar      
```

![image-20220101173315536](README.assets/image-20220101173315536.png)

程序截图

![image-20220101173647192](README.assets/image-20220101173647192.png)



## 注意说明

1、你需要自行安装nuclei到本地环境，并且将命令加入环境变量。安装方法可以参考[官方文档](https://nuclei.projectdiscovery.io/nuclei/get-started/#running-nuclei)。

2、nuclei的模板文件存放的默认路径是当前用户路径下，即 YourUserHome/nuclei-templates。



## 使用方法

以grafana的PoC为例。

### 搜索PoC

程序会自动扫描nuclei-templates目录下的所有PoC文件，并加载进程序中，可以通过关键词搜索来找到想要的PoC。

![image-20220101194244053](README.assets/image-20220101194244053.png)

### 生成PoC命令

选中想要的PoC，右键选择“generate Command Of This PoC”即可。命令会写入剪切板，直接粘贴运行即可。优点是可以对命令行进行再次编辑，但是需要自行粘贴后运行。

![image-20220101195315472](README.assets/image-20220101195315472.png)

```
#生产的单个PoC 
nuclei -t C:\Users\P52\nuclei-templates\vulnerabilities\grafana\grafana-file-read.yaml -u http://example.com -proxy http://127.0.0.1

#生产workflow PoC
nuclei -w C:\Users\P52\nuclei-templates\workflows\grafana-workflow.yaml -u http://example.com -proxy http://127.0.0.1


nuclei -tags grafana -u http://example.com -proxy http://127.0.0.1
```

### 直接执行PoC

和生成PoC命令类似，但是它会直接执行生成的命令，不需要粘贴。优点是更便捷，但是无法编辑命令行。

![image-20220101200920749](README.assets/image-20220101200920749.png)

