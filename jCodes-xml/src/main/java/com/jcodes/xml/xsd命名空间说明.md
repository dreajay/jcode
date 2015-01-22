原文地址：http://blog.sina.com.cn/s/blog_5f53615f0100wl29.html

xml命名空间可以是任意的字符串，但往往是一个绝对的url地址，例如：http://www.abc.com/schema，命名空间语法是：
xmlns[:prefix]="命名空间字符串"。
例如，orderlist.xsd定义：
------------------
<?xml version="1.0"?>
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" targetNamespace=http://www.abc.com/schema/list xmlns="http://www.abc.com/schema/list"  elementFormDefault="unqualified" >
 <xsd:attributeGroup name="OrderTypeAttributes">
        <xsd:attribute name="ID" type="xsd:string"/>
        <xsd:attribute name="Num" type="xsd:integer"/>
    </xsd:attributeGroup>

    <xsd:complexType name="OrderType">
        <xsd:attributeGroup ref="OrderTypeAttributes"/>
    </xsd:complexType>
    
    <xsd:complexType name="OrderListType">
        <xsd:sequence>
            <xsd:element name="Order" type="OrderType" minOccurs="0" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>
    
    <xsd:element name="OrderList" type="OrderListType"/>
</xsd:schema>
 
 
1.elementFormDefault用于指定xml使用局部元素时是否需要加上前缀，等于"qualified"时需要，用“unqualified”不需要，elementFormDefault对全局元素不起作用，全局元素在任何情况下都要加上前缀限定，除非默认了其命名空间。attributeFormDefault与elementFormDefault类似，attributeFormDefault用于指定xml的局部属性引用规则。
orderlist.xml如下：
-----------------
<?xml version="1.0"?>
<p1:OrderList xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xmlns:p1="http://www.abc.com/schema/list"
     xsi:schemaLocation="http://www.abc.com/schema/list orderlist.xsd">
    <p1:Order ID="abc" Num="1"></p1:Order>
    <p1:Order ID="def" Num="2"></p1:Order>
</p1:OrderList>
注意：schemaLocation指定xsd存放路径，这里采用相对路径，schemaLocation="http://www.abc.com/schema/list orderlist.xsd"是指定上面定义的xmlns:p1="http://www.abc.com/schema/list"命名空间的xsd文件的访问路径。如果引入多个命名空间可以如下定义：
<p1:OrderList xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xmlns:p1="http://www.abc.com/schema/list"
     xmlns:p2="http://www.abc.com/schema/order"
     xsi:schemaLocation="http://www.abc.com/schema/list orderlist.xsd
                         http://www.abc.com/schema/order order.xsd">
 

2.如果是elementFormDefault="unqualified"则不用加前缀，orderlist.xml如下：
-----------------
<?xml version="1.0"?>
<p1:OrderList xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xmlns:p1="http://www.abc.com/schema/list"
     xsi:schemaLocation="http://www.abc.com/schema orderlist.xsd">
    <Order ID="abc" Num="1"></Order>
    <Order ID="def" Num="2"></Order>
</p1:OrderList>
 
3.targetNamespace，目标命名空间，指定本xsd命名空间，也可以理解成本xsd给该命名空间定义类型。xmlns是引入默认命名空间，如上面xsd，targetNamespace=http://www.abc.com/schema/list xmlns="http://www.abc.com/schema/list"  意思是给http://www.abc.com/schema/list命名空间定义类型，并引入http://www.abc.com/schema/list命名的类型，该命名空间没有前缀限定，作为默认命名空间，因此在该xsd文档中应用自身定义的类型就不用再加上前缀限定了(targetNamespace属性值和xmlns属性值如果相等，则文中引用本文的类型不用加前缀限定)。
可以不指定命名空间——无命名空间
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
</xsd:schema>
或者：
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
    xmlns=http://www.chance.com/schema/list 
    xmlns:order="http://www.abc.com/schema/order"
    elementFormDefault="unqualified"
    attributeFormDefault="unqualified">
</xsd:schema>
但对无命名空间的xsd的引用会有些限制。
 
4.include 元素和 import 元素之间的区别在于：import 元素允许从具有不同目标命名空间的架构文档引用架构组件，而 include 元素则将其他具有相同目标命名空间（或没有指定的目标命名空间）的架构组件添加到包含架构。简言之，import 元素允许您使用不同目标命名空间（或没有指定的目标命名空间）架构的架构组件，并且被导入和导入的文档不能同时没有命名空间，include 元素允许您将所包含架构的所有组件添加到包含架构中。
 
include例子：
--------------
http://www.abc.com/schema的order.xsd如下：
<?xml version="1.0"?>
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
    targetNamespace="http://www.abc.com/schema" 
    xmlns="http://www.abc.com/schema" 
    elementFormDefault="unqualified"
    attributeFormDefault="unqualified">

    <xsd:simpleType name="ClassType">
        <xsd:restriction base="xsd:string">
            <xsd:enumeration value="日用品"/>
            <xsd:enumeration value="笔"/>
            <xsd:enumeration value="纸"/>
            <xsd:enumeration value="文件夹"/>
        </xsd:restriction>
    </xsd:simpleType>

    <xsd:complexType name="OrderType">
        <xsd:all minOccurs="1">
            <xsd:element name="Class" type="ClassType"/>
            <xsd:element name="Num" type="xsd:int"/>
            <xsd:element name="Date" type="xsd:date"/>
        </xsd:all>
        <xsd:attribute name="ID" type="xsd:string" use="required"/>
    </xsd:complexType>

    <xsd:complexType name="OrdersType">
        <xsd:sequence minOccurs="0" maxOccurs="unbounded">
            <xsd:element name="Order" type="OrderType"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:element name="Orders" type="OrdersType"/>

</xsd:schema>
 
http://www.abc.com/schema的orderlist.xsd如下：
<?xml version="1.0"?>
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
    targetNamespace="http://www.abc.com/schema"
    xmlns="http://www.abc.com/schema"
    elementFormDefault="unqualified"
    attributeFormDefault="unqualified">
    
    <xsd:include schemaLocation="order.xsd"/>

    <xsd:attributeGroup name="OrderTypeAttributes">
        <xsd:attribute name="ID" type="xsd:string"/>
        <xsd:attribute name="Class" type="ClassType"/>
        <xsd:attribute name="Num" type="xsd:integer"/>
    </xsd:attributeGroup>

    <xsd:complexType name="OrderType2">
        <xsd:attributeGroup ref="OrderTypeAttributes"/>
    </xsd:complexType>
    
    <xsd:complexType name="OrderListType">
        <xsd:sequence>
            <xsd:element name="Order" type="OrderType2" minOccurs="0" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>
    
    <xsd:element name="OrderList" type="OrderListType"/>

</xsd:schema>  
注意：两个文件的命名空间都是http://www.abc.com/schema，orderlist.xsd把order.xsd包含进来就相当于是一个文件了，在一个个文件里不能包含两个相同的类型，因此主要把orderlist.xsd里的OrderType改成OrderType2
 
 
import例子：
--------------
http://www.abc.com/schema/order的order_import.xsd如下：
<?xml version="1.0"?>
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
    targetNamespace="http://www.abc.com/schema/order" 
    xmlns="http://www.abc.com/schema/order" 
    elementFormDefault="unqualified"
    attributeFormDefault="unqualified">

    <xsd:simpleType name="ClassType">
        <xsd:restriction base="xsd:string">
            <xsd:enumeration value="日用品"/>
            <xsd:enumeration value="笔"/>
            <xsd:enumeration value="纸"/>
            <xsd:enumeration value="文件夹"/>
        </xsd:restriction>
    </xsd:simpleType>

    <xsd:complexType name="OrderType">
        <xsd:all minOccurs="1">
            <xsd:element name="Class" type="ClassType"/>
            <xsd:element name="Num" type="xsd:int"/>
            <xsd:element name="Date" type="xsd:date"/>
        </xsd:all>
        <xsd:attribute name="ID" type="xsd:string" use="required"/>
    </xsd:complexType>

    <xsd:complexType name="OrdersType">
        <xsd:sequence minOccurs="0" maxOccurs="unbounded">
            <xsd:element name="Order" type="OrderType"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:element name="Orders" type="OrdersType"/>

</xsd:schema>
 
http://www.abc.com/schema的orderlist_import.xsd如下：
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
    targetNamespace=http://www.chance.com/schema/list
    xmlns=http://www.chance.com/schema/list 
    xmlns:order="http://www.abc.com/schema/order"
    elementFormDefault="unqualified"
    attributeFormDefault="unqualified">

    <!--上面需要指定命名空间别名，然后这里指定xsd文件url-->
    <xsd:import namespace="http://www.abc.com/schema/order" schemaLocation="order_import.xsd"/>

    <xsd:attributeGroup name="OrderTypeAttributes">
        <xsd:attribute name="ID" type="xsd:string"/>
        <xsd:attribute name="Class" type="order:ClassType"/>
        <xsd:attribute name="Num" type="xsd:integer"/>
    </xsd:attributeGroup>

    <xsd:complexType name="OrderType">
        <xsd:attributeGroup ref="OrderTypeAttributes"/>
    </xsd:complexType>
    
    <xsd:complexType name="OrderListType">
        <xsd:sequence>
            <xsd:element name="Order" type="OrderType2" minOccurs="0" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>
    
    <xsd:element name="OrderList" type="OrderListType"/>

</xsd:schema>
注意：两个xsd的命名空间不一样：http://www.abc.com/schema/order ，http://www.abc.com/schema/list，改成一样会报错。 <xsd:attribute name="Class" type="order:ClassType"/>加上了order前缀限定。
 
5.redefine是include的增强版，用法和include一样，不过它允许在当前文档中重新定义类型覆盖redefine里的类型，不过重定义只允许对原有类型进行限制和扩展，重定义不能违反原有约束。 
 
关于更多的xml命名空间说明可以参见：http://msdn.microsoft.com/zh-cn/library/ms256480(VS.80).aspx
顺便提下两个很好用的xml工具：stylus studio 和 xmlspy。

