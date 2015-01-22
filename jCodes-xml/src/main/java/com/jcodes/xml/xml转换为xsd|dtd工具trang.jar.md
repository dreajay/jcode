xml转换为xsd 之 trang.jar

trang英文操作指南
http://www.thaiopensource.com/download/old/relaxng/20030122/trang-manual.html


1、执行环境

Trang的执行环境：JRE1.4+

2、执行说明

Trang的执行：
java -jar trang.jar -I rng|rnc|dtd|xml -O rng|rnc|dtd|xsd [其它参数] 输入文件名 输出文件名
-I : 输入文件的格式
-O : 输出文件的格式

必须是大写，小写不识别，命令：

java -jar trang.jar -I xml -O xsd orders.xml orders.xsd，

java -jar trang.jar user.xml user.xsd

注意：生成的xsd并不是可以直接使用，可能要修改部分类型，如type等

如下xml:

```
<?xml version="1.0" encoding="UTF-8"?>   
<orders>   
	<order seq="0001" date="2007-06-12">   
	    <customer name="C1" />   
	   <goods>   
	      <item id="01" name="book" />   
	      <item id="02" name="CD" />   
	   </goods>   
	</order>   
	<order seq="0002" date="2007-06-12">   
	    <customer name="C2" />   
	   <goods>   
	      <item id="05" name="Note" />   
	      <item id="06" name="Pen" />   
	   </goods>   
	</order>   
</orders>  
```


