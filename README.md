# 有关ninikki（奈奈酱）解密的一些小代码
## 注意，工程非常简陋，因此你可能遇到一下问题
1. 没有注释
2. 没有依赖
## 建议
1. 使用idea编辑器
## 使用教程-com.neko.decrypt-
### 第一步 下载idea编辑器
1. 进入官网：https://www.jetbrains.com/zh-cn/idea/download/
2. 点击右边 “其他版本”
3. 下载Communication Edition安装包，看准windows的exe后缀
4. 安装
### 第二步 编译
1. 打开软件
2. 打开项目，切记一定选择的是主文件夹，neko.decrypt-main这个名字
主文件夹内结构一般如下
- com
- LICENSE
- README.md
- gson-2.12.1.jar
3. 右上角齿轮，打开里面的项目结构
4. “项目”中，sdk选择“23”，语言等级选择“［预览］23”
5. “库”中，添加gson-2.12.1.jar文件
6. 编译运行
## 特别注意
- 你要运行的文件为UnLocker.java，而不是MMK.java
- UnLocker.java中，15行的两个地址分别是输入和输出，必须修改，请一定填绝对地址
- 如果遇到文件不可编译，在项目结构里看看com这个文件夹的属性有没有设置成源代码，一般这个属性的文件夹为蓝色文件夹
- 从github下载zip文件并解压
- 解密文件一定至少带一套衣服，不带语音包没事
## 全新GUI版本已发布
- 下载安装JDK并且配置环境
- 直接运行jar文件
## 无GUI版本使用说明
指令：
` java -jar UnLocker.jar [option] `
` -i [input directory] `
` -o [output directory] `
windows用户可以使用` bat `文件在项目根目录进行处理，把需要解密的文件夹放入` input `文件夹中，运行` UnLock.bat `即可
## 那么，祝你好运！
- 特别鸣谢 HistremRakik
