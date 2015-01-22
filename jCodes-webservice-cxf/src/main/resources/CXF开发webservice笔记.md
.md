CXF开发webservice笔记

参考CXF官方文档，官方文档例子实在很齐全

CXF User's Guide：
http://cxf.apache.org/docs/index.html

服务端开发：
http://cxf.apache.org/docs/developing-a-service.html

客户端开发：
http://cxf.apache.org/docs/how-do-i-develop-a-client.html


下载：
http://cxf.apache.org/download.html
apache-cxf-2.7.14.tar.gz
解压后包含：
bin:很多实用工具
docs:java文档
lib:包含所有依赖包
modules：包含各种模块
samples：包含很多例子可以参考，例子描述：

cxf例子
Apache CXF Example Projects:
http://cxf.apache.org/docs/sample-projects.html
http://svn.apache.org/viewvc/cxf/trunk/distribution/src/main/release/samples/
JAX-WS Examples
WSDL-First Examples
JAX-REST Examples
Javascript Examples
WS-* Examples
Data Bindings Examples
CXF and JMS Examples
JBI Examples
JCA Examples
Miscellaneous Examples
CORBA Examples


cxf与spring集成
http://cxf.apache.org/docs/writing-a-service-with-spring.html

cxf开发RESTful services
参考例子JAX-REST Examples
http://cxf.apache.org/docs/restful-services.html

使用 Apache Maven 和 CXF 构建 RESTful web services
http://www.ibm.com/developerworks/cn/opensource/os-restfulwebservices/index.html


cxf maven:
推荐使用maven来开发，参考文章：http://cxf.apache.org/docs/using-cxf-with-maven.html
pom依赖：
	<properties>
		<cxf.version>2.7.14</cxf.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-rt-frontend-jaxws</artifactId>
			<version>${cxf.version}</version>
		</dependency>
		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-rt-transports-http</artifactId>
			<version>${cxf.version}</version>
		</dependency>
		<!-- Jetty is needed if you're using the CXFServlet -->
		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-rt-transports-http-jetty</artifactId>
			<version>${cxf.version}</version>
		</dependency>
	</dependencies>


cxf3和cxf2
cxf3完全实现了JAX-RS 2.0，cxf只支持部分，还要一些其他的特性，完整说明：
cxf3：http://cxf.apache.org/docs/30-migration-guide.html
cxf2：http://cxf.apache.org/docs/27-migration-guide.html


