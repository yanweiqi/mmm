# ZPLAY ADX RTB 接口协议

标签（空格分隔）： adx api
<font color="green">
<sub>v0.2
2016.07.18<sub>
</font>

---
<font color="green">
## 目录
</font>
==========
* [文档更新记录](#History)
* [Zplay Adx RTB 接口协议](#API_Protocol)
   * [接口说明](#API_Summary)
   * [向DSP发送的广告询价请求接口(Bid Request)](#BID_REQUEST)
     * [接口信息（BidRequest）](#BID_REQUEST_INFO)
     * [App信息（BidRequest.App）](#BID_REQUEST_APP)
     * [设备信息（BidRequest.Device）](#BID_REQUEST_DEVICE)
     * [曝光信息（BidRequest.Imp）](#BID_REQUEST_IMP)
         * [横幅（BidRequest.Impression.Banner）](#BID_REQUEST_IMP_BANNER)
         * [视频（BidRequest.Impression.Video））](#BID_REQUEST_IMP_VIDEO)
         * [原生广告（NativeRequest））](#BID_REQUEST_IMP_NATIVE)
            * [Asset（NativeRequest.Asset）](#NATIVE_REQUEST_ASSET)
            * [Image（NativeRequest.Asset.Image）](#NATIVE_REQUEST_ASSET_IMAGE)
            * [Title（NativeRequest.Asset.Title）](#NATIVE_REQUEST_ASSET_TITLE)
            * [Data（NativeRequest.Asset.Data）](#NATIVE_REQUEST_ASSET_DATA)
     * [用户信息（BidRequest.User）](#BID_REQUEST_USER)
         * [用户扩展信息（BidRequest.User.Data）](#BID_REQUEST_USER_DATA)
         * [用户人群属性信息（BidRequest.User.Data.Segment）](#BID_REQUEST_USER_DATA_SEGMENT)
     * [网站信息（BidRequest.Site）](#BID_REQUEST_SITE)
     * [出品方信息（BidRequest.Site.Publisher](#BID_REQUEST_SITE_PUBLISHER)
     * [BidRequest 例子](#BID_REQUEST_EXAMPLE)
   *  [DSP返回的广告出价接口(Bid Response)](#BID_REPONSE)
      *  [接口信息（BidResponse）](#BID_RESPONSE_INFO)
      *  [SeatBid信息（BidResponse.SeatBid）](#BID_RESPONSE_SEATBID)
      *  [Bid信息（BidResponse.SeatBid.Bid）](#BID_RESPONSE_SEATBID_BID)
          *  [原生广告Native（NativeResponse）](#BID_RESPONSE_SEATBID_BID_NATIVE)
              *  [Asset（NativeResponse.Asset）](#NATIVE_RESPONSE_ASSET)
              *  [Title（NativeResponse.Asset.Title）](#NATIVE_RESPONSE_ASSET_TITLE)
              *  [Image（NativeResponse.Asset.Image）](#NATIVE_RESPONSE_ASSET_IMAGE)
              *  [Data（NativeResponse.Asset.Data)](#NATIVE_RESPONSE_ASSET_DATA)
              *  [Link（NativeResponse.Asset.Link)](#NATIVE_RESPONSE_ASSET_LINK)
              *  [LinkExt（NativeResponse.Asset.Link.Ext)](#NATIVE_RESPONSE_ASSET_LINK_EXT)
      * [BidResponse 例子](#BID_RESPONSE_EXAMPLE)
   *  [向DSP发送的竞价结果接口(Win Notice)](#WIN_NOTICE)
*  [宏](#BID_MACRO)
*  [结算价格解析方法](#BID_PRICE_DECRYPT)





---
<div id="History"></div>
<font color="green">
## 文档更新记录
</font>

版本|作者|时间|备注
---|---|---|---
v0.1|卫海滨|2016.01.26|创建
v0.2|崔英杰|2016.07.18|BidRequest添加了Site对象，App对象添加了publisher对象

<font color="gray">备注：版本变更记录看这个表，小改动请参看git commit comment</font>


---
<div id="API_Protocol"></div>
## Zplay Adx RTB Json接口协议


---
<div id="API_Summary"></div>
<font color="green">
### 接口说明
</font>

Zplay Adx RTB 总共包含三个步骤。
1. Zplay Adx向DSP发送广告询价请求(Bid Request)
2. DSP向Zplay Adx返回出价结果，及广告代码
3. Zplay Adx向获胜的DSP发送竞价获胜通知

这三个步骤分别对应三个接口，接口需要注意有：


1. ADX 的 RTB API参考通用OpenRTB规范：http://code.google.com/p/openrtb/。大体遵循该规范，但对一些字段有调整，为了方便阅读在文档中用互通颜色标注。
*  黑色：OpenRTB已有字段
*  <font color="orange">橙色</font>：这是OpenRTB原有，但Zplay Adx中修改的字段
*  <font color="lightgray">灰色</font>：这是OpenRTB原有，但Zplay Adx中移除的字段
*  <font color="DeepSkyBlue">蓝色</font>：这是Zplay Adx扩展的字段
2. 协议采用 HTTP POST，开启keep-alive，消息格式为ProtoBuf。目前 timeout 设为 100ms。请求头中需要设 Content-Type 为 application/x-protobuf。
3. 不出价可以返回HTTP 状态码204 (No Content)
4. 竞价获胜通知win-notice、展示通知impression-notice、点击通知click-notice均是GET请求
5. 字段中所有中文必须使用UTF-8编码

---

<div id="BID_REQUEST"></div>
<font color="green">
### 向DSP发送的广告询价请求接口(Bid Request)
</font>

<div id="BID_REQUEST_INFO"></div>
<font color="green">
#### 一. 接口信息（BidRequest）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
id|string||**是**|生成的唯一竞价ID，32个字符组成的字符串，由Zplay ADX生成, 例：“57a6a0811829faf34a78ca625c383ec9”
<font color="orange">app</font>|object||**是**|App对象。应用信息
<font color="orange">device</font>|object||**是**|Device对象。设备信息
<font color="orange">imp[]</font>|object||**是**|Imp对象。但只会填1个元素
bcat[]|object||否|禁用的广告类别，iab详见[IAB §6.1](http://www.iab.net/media/file/OpenRTB_API_Specification_Version2.0_FINAL.PDF)
user|object||否|User对象。用户信息
test|bool|false|否|true标记本次请求是测试请求。当为测试请求时，DSP需要返回一个带有广告的应答，该应答广告不会被展现给用户，也不会对该次广告展现计费。适用于联调测试。
<font color="DeepSkyBlue">ext|object||**否**|BidRequest的扩展


<font color="green">
#### 1. BidRequest扩展
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
<font color="DeepSkyBlue">version</font>|int32||**是**|当前协议版本号，目前为1


---

<div id="BID_REQUEST_APP"></div>
<font color="green">
#### 二. App信息（BidRequest.App）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
<font color="orange">id</font>|string||**是**|应用ID，由Zplay Adx生成, 例：“z0000001”
<font color="orange">name</font>|string||**是**|应用名称, 例：“曙光之战”
ver|string||否|应用版本
bundle|string||否|Android应用为包名，例：“com.zplay.demo”；iOS应用为iTunes ID，例：“12345678”
cat[]|string||否|应用类型，详见[IAB §6.1](http://www.iab.net/media/file/OpenRTB_API_Specification_Version2.0_FINAL.PDF)
publisher|对象||否|[出品方信息](#BID_REQUEST_SITE_PUBLISHER)



---

<div id="BID_REQUEST_DEVICE"></div>
<font color="green">
#### 三. 设备信息（BidRequest.Device）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
<font color="orange">os</font>|string||**是**|只能是"ios"，"android"或"wp"（windows phone）（注意大小写）
dnt|bool|false|否|禁止跟踪用户的标志，
osv|string||是|操作系统版本,例：“9.0.1”
make|string||否|生产厂商, 例：“Samsung”
model|string||否|设备型号, 例：“iPhone”
ip|string||是|设备ipv4地址, 例：“8.8.8.8”
ua|string||否|设备user agent, 例：“Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16”
hwv|string||否|设备硬件版本号, 例：“6S”是iPhone 6S的版本号
w|int32||否|设备屏幕宽度，单位：像素， 例：1920
h|int32||否|设备屏幕高度，单位：像素， 例：1080
ppi|int32||否|设备屏幕像素密度，单位：每英寸像素个数， 例：400
macsha1|string||否|mac地址 SHA1；iOS无此字段， android也只是部分机器能拿到
didsha1|string||否|Android为IMEI SHA1；iOS无此字段，(cdma手机传meid码)
<font color="orange">dpidsha1</font>|string||**是**|Android为ANDROID ID SHA1；iOS为ADID(也叫IDFA) SHA1， 例："8a319e9fdf05dd8f571b6e0dc2dc2a8263a6974b"
connectiontype|枚举||否|网络连接类型，0：未知，1：以太网，2：wifi， 3：位置蜂窝网络， 4：2G网络，5：3G网络，6：4G网络，详见proto文件
devicetype|枚举||否|设备类型，1：移动设备，4：手机， 5：平板
geo|对象||否|[Geo对象](#BID_REQUEST_DEVICE_GEO)，请求设备的经纬度
<font color="DeepSkyBlue">ext</font>|object||**否**|设备信息的扩展


  <div id="BID_REQUEST_DEVICE"></div>
  <font color="green">
  #### 三. 设备信息的扩展（BidRequest.Device.Ext）
  </font>

  <font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
  ---|---|---|---
  <font color="DeepSkyBlue">plmn</font>|string||否|国家运营商编号, 例:"46000"
  <font color="DeepSkyBlue">imei</font>|string||否|imei码明文，(cdma手机传meid码)
  <font color="DeepSkyBlue">mac</font>|string||否|mac地址明文
  <font color="DeepSkyBlue">android_id</font>|string||否|Android Id明文
  <font color="DeepSkyBlue">adid</font>|string||否|iOS ADID(也叫IDFA)或Android ADID(国内手机一般没有）
  <font color="DeepSkyBlue">orientation</font>|string||否|设备屏幕方向：1: 竖向，2: 横向


---
<div id="BID_REQUEST_DEVICE_GEO"></div>
<font color="green">
##### 附：Geo对象（BidRequest.Device.Geo）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
lat|double||否|纬度,例：39.9167，是WGS84坐标
lon|double||否|经度,例：116.3833，是WGS84坐标
country|string||否|国家代码，请参见[ISO-3166-1 Alpha-3](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3)
region|string||否|国内是省名，美国是州的2个字母缩写，其他国家请参见[ISO-3166-2](https://en.wikipedia.org/wiki/ISO_3166-2)
city|string||否|城市名称, 例：“北京”
LocationType|枚举||否|位置来源，1：根据gps位置，2：根据IP， 3：用户提供，其他详见proto文件
<font color="DeepSkyBlue">ext</font>|object||**否**|Geo的扩展

<div id="BID_REQUEST_DEVICE_GEO"></div>
<font color="green">
##### 附：Geo对象扩展（BidRequest.Device.Geo.Ext）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
<font color="DeepSkyBlue">accu</font>|int32|0|否|精度，请参见[Decimal degrees](https://en.wikipedia.org/wiki/Decimal_degrees)
<font color="DeepSkyBlue">street</font>|string||否|街道名称， 例：“知春路”

---
<div id="BID_REQUEST_IMP"></div>
<font color="green">
#### 四. 曝光信息（BidRequest.Imp）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
id|string||**是**|曝光ID
<font color="orange">bidfloor</font>|double||**是**|底价，单位是分
bidfloorcur|string|"CNY"|否|报价货币单位，目前只支持人民币:"CNY"
instl|bool|0|否|1表示插屏，0表示不是插屏
banner|对象||否|banner对象
video|对象||否|video对象
native|对象||否|native对象, 下面包含NativeRequest
tagid|string||否|广告位id
<font color="DeepSkyBlue">ext</font>|object||**否**|曝光信息的扩展

<div id="BID_REQUEST_IMP"></div>
<font color="green">
####. 曝光信息扩展（BidRequest.Imp.Ext）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
<font color="DeepSkyBlue">is_splash_screen</font>|bool|false|否|是否为开屏广告
<font color="DeepSkyBlue">inventory_types</font>|int[]|[1]|是|支持的素材类型数组, 1:图片，2:图文，3:视频，4:html5

---
<div id="BID_REQUEST_IMP_BANNER"></div>
<font color="green">
##### 1. 横幅信息（BidRequest.Impression.Banner）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
<font color="orange">w</font>|int32||**是**|广告位宽度
<font color="orange">h</font>|int32||**是**|广告位高度
wmax|int32||否|最大宽度，这个属性存在时，w是推荐宽度
hmax|int32||否|最大高度，这个属性存在时，h是推荐高度
wmin|int32||否|最小宽度，这个属性存在时，w是推荐宽度
hmin|int32||否|最小高度，这个属性存在时，h是推荐高度
pos|枚举|0|否|广告位位置，0：未知，4：头部，5：底部，6：侧边栏，7：全屏，其他详见proto文件



---
<div id="BID_REQUEST_IMP_VIDEO"></div>
<font color="green">
##### 2. 视频（BidRequest.Impression.Video）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
mimes|array||是|支持的视频类型
protocols|array||是|支持的视频响应协议
minduration|int32||否|最短时间，单位：秒
maxduration|int32||否|最长时间，单位：秒
<font color="orange">w</font>|int32||**是**|广告位宽度
<font color="orange">h</font>|int32||**是**|广告位高度
pos|枚举|0|否|广告位位置，0：未知，4：头部，5：底部，6：侧边栏，7：全屏，其他详见proto文件

---
<div id="BID_REQUEST_IMP_NATIVE"></div>
<font color="green">
##### 3. 原生广告（BidRequest.Impression.NativeRequest）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
layout|int||否|原生广告布局样式，2：应用墙，3：信息流，5：走马灯，其他请参看IAB openrtb标准
assets|array||是|原生广告元素列表


---
<div id="NATIVE_REQUEST_ASSET"></div>
<font color="green">
##### 3.1. 原生广告Asset（NativeRequest.Asset）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
id|int||是|元素id
required|int|1|否|广告元素是否必须，1：必须，0：可选
title|对象||否|文字元素
img|对象||否|图片元素
data|对象||否|其他数据元素



---
<div id="NATIVE_REQUEST_ASSET_IMAGE"></div>
<font color="green">
##### 3.1.1 原生广告Image（NativeRequest.Asset.Image）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
type|int||否|image元素的类型，1：Icon，2:LOGO, 3：Large image
w|int||否|宽度
h|int||否|高度



---
<div id="NATIVE_REQUEST_ASSET_TITLE"></div>
<font color="green">
##### 3.1.2 原生广告Title（NativeRequest.Asset.Title）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
len|int||是|title元素最大文字长度


---
<div id="NATIVE_REQUEST_ASSET_DATA"></div>
<font color="green">
##### 3.1.3 原生广告Data（NativeRequest.Asset.Data）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
type|int||是|数据类型，2：Description，3：Rating, 其他请参看IAB openrtb协议
len|int||是|data元素最大长度



---
<div id="BID_REQUEST_USER"></div>
<font color="green">
#### 五. 用户信息（BidRequest.User）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
id|string||否|用户id
yob|int32||否|生日年份，例：1995
gender|string||否|男："M", 女："F", 其他："0"
geo|对象||否|[Geo对象](#BID_REQUEST_DEVICE_GEO)，用户家庭位置
data[]|对象||否|Data对象，用户的扩展信息


---
<div id="BID_REQUEST_USER_DATA"></div>
<font color="green">
##### 1. 用户扩展信息（BidRequest.User.Data）
</font>
<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
segment[]|对象||否|Segment对象，用户人群属性


<div id="BID_REQUEST_USER_DATA_SEGMENT"></div>
<font color="green">
##### 2. 用户人群属性信息（BidRequest.User.Data.Segment）
</font>
<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
id|string||否|属性id
value|string||否|属性值


---
<div id="BID_REQUEST_SITE"></div>
<font color="green">
#### 六. Site信息（BidRequest.Site）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
id|string||否|网站id
name|string||否|网站名称
domain|string||否|网站域名
cat|string[]||否|网站类别，详见[IAB §6.1](http://www.iab.net/media/file/OpenRTB_API_Specification_Version2.0_FINAL.PDF)
sectioncat|string[]||否|当前频道类别，详见[IAB §6.1](http://www.iab.net/media/file/OpenRTB_API_Specification_Version2.0_FINAL.PDF)
pagecat|string[]||否|当前页面类别，详见[IAB §6.1](http://www.iab.net/media/file/OpenRTB_API_Specification_Version2.0_FINAL.PDF)
page|string||否|当前页面URL地址
ref|string||否|当前页面Referrer URL地址
search|string||否|当前页面的搜索关键词来源
mobile|int||否|是否移动网站，1：为移动网站
keywords|string||否|网页关键字，可多个，逗号隔离
publisher|对象||否|[出品方信息](#BID_REQUEST_SITE_PUBLISHER)


---
<div id="BID_REQUEST_SITE_PUBLISHER"></div>
<font color="green">
#### 七. 出品方信息（BidRequest.Site.Publisher）
</font>
<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
id|string||否|出品方id
name|string||否|名称
domain|string||否|出品方顶级网站域名
cat|string[]||否|出品方类别，详见[IAB §6.1](http://www.iab.net/media/file/OpenRTB_API_Specification_Version2.0_FINAL.PDF)




<div id="BID_REQUEST_EXAMPLE"></div>
<font color="green">
#### 八. BidRequest 例子
</font>


```
{
    "id": "9778c264ec15469e93308a901eea5df8",
    "imp": [
        {
            "id": "1",
            "banner": {
                "w": 320,
                "h": 50,
                "pos": 5
            },
            "instl": false,
            "tagid": "1",
            "bidfloor": 130,
            "bidfloorcur": "CNY",
            "ext": {
                "inventory_types": [
                    1
                ]
            }
        }
    ],
    "app": {
        "id": "2000550",
        "name": "消灭星星",
        "cat": [
            "IAB9-5"
        ],
        "ver": "2.0",
        "bundle": "com.zplay.removestar"
    },
    "device": {
        "dnt": true,
        "ua": "Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3",
        "ip": "111.206.85.18",
        "geo": {
            "lat": 39.9167,
            "lon": 116.3833,
            "country": "CHN",
            "region": "北京",
            "city": "北京",
            "type": 2,
            "ext": {
                "accu": 0
            }
        },
        "didsha1": "9c22ec063caab63508e822a5e6408e5543870063",
        "dpidsha1": "93a73cd98f5a38b63f4c4a4c69f9263f9b5750f2",
        "make": "Apple",
        "model": "iPhone 5S",
        "os": "ios",
        "osv": "9.0.2",
        "w": 1920,
        "h": 1080,
        "ppi": 400,
        "connectiontype": 2,
        "devicetype": 4,
        "macsha1": "54caecc03e0a559a8554e14cfcd8c7b987c56ee4",
        "ext": {
            "plmn": "46000",
            "imei": "358142035282084",
            "mac": "00:16:3E:00:5D:67",
            "android_id": "f07a13984f6d116a",
            "adid": "236A005B-700F-4889-B9CE-999EAB2B605D"
        }
    },
    "test": false,
    "ext": {
        "version": 1,
        "is_ping": null
    }
}
```



---

<div id="BID_RESPONSE"></div>
<font color="green">
### DSP向Zplay Adx返回出价结果，及广告代码(BidResponse)
</font>

<div id="BID_RESPONSE_INFO"></div>
<font color="green">
#### 一. 接口信息（BidResponse）
</font>


<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
id|string||**是**|在BidRequest中传入的id
seatbid[]|对象数组||否|SeatBid对象，若提出竞价则需提供一个，并且只接受一个
nbr|枚举||否|未竞价原因，0：未知错误，1：技术错误，2：无效请求，4：可疑的伪造流量，5：数据中心代理服务器ip，6：不支持设备，7：被屏蔽媒体，8：不匹配的用户，其他请参看proto文件


---

<div id="BID_RESPONSE_SEATBID"></div>
<font color="green">
#### 二. SeatBid信息（BidResponse.SeatBid）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
bid[]|对象数组||否|Bid对象，只接受一个


---
<div id="BID_RESPONSE_SEATBID_BID"></div>
<font color="green">
#### 三. Bid信息（BidResponse.SeatBid.Bid）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
id|string||**是**|由DSP提供的竞价id
impid|string||**是**|曝光id
price|double||**是**|出价，单位为分，不能低于曝光最低价格，否则会被当做无效应答。目前只支持人民币
<font color="orange">adid</font>|string||**是**|物料ID，由DSP提供。DSP必须保证如果adid相同，则物料的所有字段相同（除了nurl、clkurl、imptrackers、clktrackers）。如果DSP提供的adid满足以下条件会受到惩罚：1、提交过多不同的adid；2、相同adid的其他字段不同
<font color="orange">nurl</font>|string||否|竞价获胜通知url,win notice url, GET方法调用。可以使用[宏](#BID_MACRO)。推荐使用[曝光监测链接](#BID_WIN_NOTICE)来获取获胜通知。
<font color="orange">bundle</font>|string||否|Android应用为包名，例：“com.zplay.demo”；iOS应用为iTunes ID，例：“12345678”
iurl|string||否|广告素材的图片URL。banner广告必填
w|int32||否|素材宽度, 当给出的广告素材尺寸与广告位尺寸不完全一致时，素材宽高信息必须给出。
h|int32||否|素材高度
cat|string[]||否|广告类别，详见[IAB §6.1](http://www.iab.net/media/file/OpenRTB_API_Specification_Version2.0_FINAL.PDF)
<font color="DeepSkyBlue">ext</font>|string||**否**|bid信息的扩展


<div id="BID_RESPONSE_SEATBID_BID"></div>
<font color="green">
#### 三. Bid信息的扩展（BidResponse.SeatBid.Bid.Ext）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
<font color="DeepSkyBlue">app_ver</font>|string||否|app推广广告的话，需要提供app的版本号
<font color="DeepSkyBlue">clkurl</font>|string||否|广告点击跳转地址，允许使用[宏](#BID_MACRO)，例http://www.zplay.cn/ad/{AUCTION_BID_ID}
<font color="DeepSkyBlue">imptrackers[]</font>|string[]||否|曝光追踪地址，允许有多个追踪地址，允许使用[宏](#BID_MACRO)
<font color="DeepSkyBlue">clktrackers[]</font>|string[]||否|点击追踪地址，允许有多个追踪地址，允许使用[宏](#BID_MACRO)
<font color="DeepSkyBlue">html_snippet</font>|string||否|html广告代码，允许使用[宏](#BID_MACRO)
<font color="DeepSkyBlue">inventory_type</font>|int|1|否|广告资源类型, 1:图片，2:图文，3:视频，4:html5
<font color="DeepSkyBlue">title</font>|string||否|图文广告中的标题
<font color="DeepSkyBlue">desc</font>|string||否|图文广告中的描述
<font color="DeepSkyBlue">action</font>|int|1|否|广告动作类型， 1: 在app内webview打开目标链接， 2： 在系统浏览器打开目标链接, 3：打开地图，4： 拨打电话，5：播放视频, 6:App下载
<font color="DeepSkyBlue">download_file_name</font>|string||否|下载文件名，动作类型为下载类型时需要





---
<div id="BID_RESPONSE_SEATBID_BID_NATIVE"></div>
<font color="green">
#### 1. 原生广告Native（NativeResponse）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
assets|array||是|原生广告元素列表
link|array||否|目标链接，默认链接对象，当assets中不包括link对象时，使用此对象
imptracker|array||否|曝光追踪地址数组


---
<div id="NATIVE_RESPONSE_ASSET"></div>
<font color="green">
#### 1.1 原生广告Asset（NativeResponse.Asset）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
id|array||是|广告元素ID
title|对象||否|文字元素
img|对象||否|图片元素
data|对象||否|其他数据元素
link|对象||否|Link对象，点击地址


---
<div id="NATIVE_RESPONSE_ASSET_TITLE"></div>
<font color="green">
#### 1.1.1 原生广告Title（NativeResponse.Asset.Title）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
text|string||是|title元素的内容文字


---
<div id="NATIVE_RESPONSE_ASSET_IMAGE"></div>
<font color="green">
#### 1.1.2 原生广告Image（NativeResponse.Asset.Image）
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
url|string||是|image元素的URL地址
w|int||否|宽度，单位像素
h|int||否|高度，单位像素



---
<div id="NATIVE_RESPONSE_ASSET_DATA"></div>
<font color="green">
#### 1.1.3 原生广告Data（NativeResponse.Asset.Data)
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
label|string||否|数据显示的名称
value|string||是|数据的内容文字




---
<div id="NATIVE_RESPONSE_ASSET_LINK"></div>
<font color="green">
#### 1.1.4 原生广告Link（NativeResponse.Asset.Link)
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
url|string||是|点击URL
clicktracker|array||否|点击跟踪URL
<font color="DeepSkyBlue">ext</font>|object||否|原声广告Link的扩展


---
<div id="NATIVE_RESPONSE_ASSET_LINK_EXT"></div>
<font color="green">
#### 1.1.4 原生广告Link扩展（NativeResponse.Asset.Link.Ext)
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">类型</font>|<font color="DarkBlue">默认值</font>|<font color="DarkBlue">必填</font>|<font color="DarkBlue">备注</font>
---|---|---|---
<font color="DeepSkyBlue">link_type</font>|int||否|点击动作类型， 2：下载app, 3:打开网页， 6：去app store下载app， 其他请参看iab openrtb


原生广告定义遵循OpenRTB Dynamic Native Ads Specification 1.0标准，请下载文档：http://www.iab.net/media/file/OpenRTB-Native-Ads-Specification-1_0-Final.pdf
 


<div id="BID_RESPONSE_EXAMPLE"></div>
<font color="green">
#### 三. BidResponse 例子
</font>

```
{
    "id": "9778c264ec15469e93308a901eea5df8",
    "seatbid": [
        {
            "bid": [
                {
                    "id": "1",
                    "impid": "1",
                    "price": 2000,
                    "adid": "ad5-6421",
                    "AdmOneof": null,
                    "iurl": "http://www.zplay.com/ad.jpg&dsp=5",
                    "ext": {
                        "imptrackers": [
                            "http://www.zplay.com/it?p={AUCTION_BID_PRICE}&id={AUCTION_BID_ID}&dsp=5"
                        ],
                        "clktrackers": [
                            "http://www.zplay.com/ct?p={AUCTION_BID_PRICE}&id={AUCTION_BID_ID}&dsp=5"
                        ],
                        "clkurl": "http://www.zplay.com/click?p={AUCTION_BID_PRICE}&id={AUCTION_BID_ID}&dsp=5",
                        "inventory_type": 2,
                        "title": "MMR",
                        "desc": "GEhIT0BkO0swcwMUeajjKEM1am7FPK7muqE85yQINij7R9CFSJWGHxPd7nV7ouwy2fXKj04cY55qLDcB4CC8vmvdrkuvPdMPNzvpUMj3umFVKU1oV9FUcUG15z2TlJomisIlr7LttkU6bdyBLGduOBCFaF8Lxx1AezxcnfOPFNaoAlHXbxZTX7NESqd6nG4qi",
                        "action": 6,
                        "download_file_name": "V4giR50ofBsGyGCh0QF7IxjxhEJ4X"
                    }
                }
            ]
        }
    ]
}
```


---
<div id="BID_WIN_NOTICE"></div>
<font color="green">
### 向DSP发送的竞价结果接口(Win Notice)
</font>

通过对曝光监测链接中特定参数的[宏](#BID_MACRO)替换，将广告的计费价格发送给赢得竞价的DSP平台。




---
<div id="BID_MACRO"></div>
<font color="green">
### 宏
</font>

<font color="DarkBlue">字段</font>|<font color="DarkBlue">含义</font>
---|---|---|---
{AUCTION_BID_ID}|竞价ID
{AUCTION_BID_PRICE}|最终结算价格，该价格是被加密的，解密方法请参见[结算价格解析方法](#BID_PRICE_DECRYPT)
{AUCTION_IMP_ID}|曝光id
{AUCTION_IP}|用户ip
{AUCTION_DID_SHA1}|请参见[设备](#)didsha1字段
{AUCTION_DPID_SHA1}|请参见[设备](#)dpidsha1字段
{AUCTION_TIMESTAMP}|GMT unix timestamp, 单位为秒
{AUCTION_CLICK_URL}|广告点击跳转URL
{AUCTION_RANDOM_NUM}|随机数，用来保证url不会被客户端缓冲


---
<div id="BID_PRICE_DECRYPT"></div>
<font color="green">
### 结算价格解析方法
</font>

DSP 获取到的结算价格，是经过加密后的结算价格。需要配合密钥才能解密成功。每个DSP 有一个唯一的结算价格解密密钥，以及一个完整性效验密匙，请联系Zplay  Adx团队获取，并妥善保管。

为方便说明，约定如下变量与操作：
<font color="DarkBlue">字段</font>|<font color="DarkBlue">含义</font>
---|---|---|---
P<sub>settle</sub>|原始价格
P<sub>encrpt</sub>|加密的价格
d_key|解密密匙，32字节
i_key|完整性密匙，32字节
time_stamp|时间戳
integrity|完整性签名
side_word|价格加密干扰码，8字节
+|字符串连接
^|异或
WebSafeBase64Encode()|标准base64 编码（RFC2045），替换“+”为“-”；“/”为“_”，会省略填补的字符
WebSafeBase64Decode()|标准base64 编码（RFC2045），需要替换“-”为“+”；“_”为“/”，并填补占位符
E<sub>enc</sub>|加密后的密文
E<sub>src</sub>|原始密文

#### 解密请按如下步骤：

*  原始密文E<sub>src</sub>右端补齐'=' 直到字符串长度为4的倍数为止
*  用WebSafeBase64Decode解码该字符串，结果应为长度16字节的数据
    数据格式如下:
    > time_stamp(4) | P<sub>encrpt</sub>(8) | integrity(4)
    >
    > 其中time_stamp 为小字节字序的int32 值，是加密价格时的unix time stamp。

*  使用秘钥d_key对time_stamp, 进行如下操作
    > mac = hmac.New(sha1.New, d_key)
    > mac.Write(time_stamp)
    > side_word = mac.Sum(nil)[:8]
*  将P<sub>encrpt</sub>与side_word进行按字节异或操作， 得到值既为P<sub>settle</sub>, 是float64值按小字节字序， 单位为分

#### 校验请按如下步骤

* 将P<sub>settle</sub>与time_stamp按小字节字序合并为12字节的数据， 用i_key进行如下操作
    > mac = hmac.New(sha1.New, i_key)
    > mac.Write(P<sub>settle</sub> + time_stamp) （并不是数字的相加，而是合并为12字节的数据）
    > result = mac.Sum(nil)[:4]
* 将上一步骤得出的结果，与integrity进行比较， 相等表示校验成功，否则失败。
