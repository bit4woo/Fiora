

## Fiora

项目简介：LoL中的英雄无双剑姬的名字，她善于发现对手防守弱点，实现精准打击。该项目为PoC框架[nuclei](https://github.com/projectdiscovery/nuclei)提供图形界面，实现快速搜索、一键运行等功能，提升[nuclei](https://github.com/projectdiscovery/nuclei)的使用体验。

项目地址：https://github.com/bit4woo/Fiora

项目作者：[bit4woo](https://github.com/bit4woo)

视频教程：

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

```
nuclei -t C:\Users\P52\nuclei-templates\cnvd\CNVD-2020-56167.yaml -u http://example.com -proxy http://127.0.0.1

nuclei -t C:\Users\P52\nuclei-templates\cnvd\CNVD-2020-56167.yaml -u http://example.com -proxy http://127.0.0.1
```

