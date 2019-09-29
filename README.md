# dubbo-client-ui
任意dubbo服务的桌面客户端

* 自动解析api的服务和参数
* 支持多环境通过zookeeper或者dubbo直连
* 支持动态脚本填充参数
* 支持通过maven自动解析依赖，动态下载依赖包
* 请自行安装maven环境，确保mvn命令参正常运行

## 使用方法

下载dist目录下面的文件

/dubbo-client-ui-1.0-SNAPSHOT.jar

/lib/*

```shell script
java -jar dubbo-client-ui-1.0-SNAPSHOT.jar 即可运行
```


## 操作手册

主界面如下

<img src="https://raw.githubusercontent.com/hyberbin/dubbo-client-ui/master/dist/img/1.png" >

1 第一次运行程序时没有应用服务目录，我们需要点击左上角【文件】-->【pom导入】

<img src="https://raw.githubusercontent.com/hyberbin/dubbo-client-ui/master/dist/img/2.png" >

2 填写好api包对应的group、artifactId、version信息后，直接点击【√】确认，系统自动解析依赖并安装相关jar包到本地。

<img src="https://raw.githubusercontent.com/hyberbin/dubbo-client-ui/master/dist/img/3.png" >

3 右上角下拉框中可以添加dubbo的运行环境参数，添加相关的url、端口、协议保存即可

<img src="https://raw.githubusercontent.com/hyberbin/dubbo-client-ui/master/dist/img/4.png" >

4 展开左边api树，选择一个接口，系统会自动解析接口的参数名和类型，只需要在表格上填写好相关参数后就可以请求接口了。

<img src="https://raw.githubusercontent.com/hyberbin/dubbo-client-ui/master/dist/img/5.png" >
