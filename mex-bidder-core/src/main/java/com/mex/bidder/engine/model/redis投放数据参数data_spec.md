# Creatives 数据规范

version:           0.7.2

update date:       2015-08-27

## 交换机制

* redis
* pub/sub

### Redis 存放

key:   rtb.creatives
value: Hash

$ hkeys rtb.creatives
1) "count"                       //参与竞价的创意数
2) "update_at"                   //更新时间
3) "md5"                         //内容md5的值
4) "content"                     //创意列表json序列化后的字符串

其中: content格式

creative1|json + "\n" + creative2|json + "\n" + ... + creativen|json + "\n" + #(number_of_rows + 1)

### pub/sub机制

channel: rtb.notification.creatives

message: "update"

publish时机:

1. 定时检测比较当前可参与竞价创意列表content md5与Redis中存放的值

2. 如果相同， 不做处理

3. 如果不同， 将新的值写入Redis， 同时执行(publish "rtb.notification.creatives" "update")

## 默认值约定

- 值为null, String类型值"", Int类型值为-1的, Double类型值为-1的， 表示"不限"或者忽略此字段的过滤.

- 字符串多值, 表示此值用“;"分割的多个字符串组成的.

## 各数据字段

### IDs

creatives文件增加以下字段：

creative_id

类型: String

说明: 创意viewid, 格式: base64("$account_id-$campaign_id-$adgroup_id-$bcreative_id")

bcreative_id

说明: 创意id

adgroup_id

说明: 广告组id

campaign_id

说明: 活动id

account_id

说明: 广告主id


注: bcreative_id,adgroup_id, campaign_id, account_id: 都是自增的内部id, 整型

### ctype 点击类型

字段名: ctype

说明:   表示产品的推广类型

类型：   字符串

可取值:

   "1" 移动应用

   "2" 网页

### cur_adv 广告主结算货币

字段名: cur_adv

说明:    广告主结算货币

类型：   String

可取值:

  "CNY" - 人民币
  "USD" - 美元


### 竞价方式

字段名: bidmode

类型 : 字符串

可取值:

    "1" - RTB公开竞价

    "2" - "RTB受邀竞价"

    "3" - "程序化预定"

    "4" - "优先购买"

### 出价类型

字段名: paymode

类型 : 字符串

可取值:

    "1" - CPM竞价

    "2" - 固定CPC

    "3" - 固定CPM


### 目标价格

"price_type":    "cpc",                             //出价算法. 类型：String， 取值范围: "cpc", "ecpm", "cpa"

"price":        0.5000,                             //出价. 类型: Double。 其值与price_${cur_adv}一样

"price_rmb":    0.5000,                             //按人民币计算的出价. 类型: Double

"price_usd":    0.07692307692307693,                //按美元计算的出价. 类型: Double


（注: 考虑向后兼容性， 暂时保留价格的相关字段： "cpa", "cpa_rmb", "cpa_usd", "cpc", "cpc_rmb", "cpc_usd", "ecpm", "ecpm_rmb", "ecpm_usd", 其业务规则也保持跟之前一致。

cpc, ecpm, ecpm 三者选其一， 类型为Double

选中的结算方式按广告主结算货币换算为人民币对应的值：xxx_rmb和美元对应的值：xxx_usd

选中的结算方式,对应的字段都置为-1

按 CPC 结算:

cpc_rmb - CPC人民币

cpc_usd - CPC美元

按CPM结算:

ecpm_rmb - CPM人民币

ecpm_usd - CPM美元

按CPA结算：

cpa_rmb - CPA人民币

cpa_usd - CPM美元

)

### target_os 操作系统

字段名: target_os

说明:  定向的操作系统编码

类型：   字符串

可取值:

  "0" - Android'
  
  "1" - iOS

  ""  - 空串表示不限操作系统

### 操作系统版本

 操作系统目前主要支持Android, IOS, 它们的原始版本规则是：MAJOR.MINOR.PATCH
 
 如: 
 
Android: 2.3.6

 MAJOR=2
 MINOR=3
 PATCH=6
 
IOS: 8.1

 MAJOR=8
 MINOR=1
 PATCH=0
 
 creative中表示的版本号规则是:
   
 c_os_version =  MAJOR * 10000 + MINOR * 100 + PATCH
 
 即c_os_version是从原始版本按上述规则转换成了一个整数
 
 那么Android: 2.3.6 对应的creative中表示的版本号是:
  
  2*10000 + 3 * 100 + 6 = 20306

 这个转换规则假定使用f函数来表示, 如果原始版本为空或者非法版本f处理的结果为-1
 
 每个creative 只针对一种类型的OS作版本定向，包括最低支持版本(os_version_min_orig)和最高支持版本(os_version_max_orig)两个选项
 
 在creative表增加的字段如下所示:
 
#### os_version_max
 
 os_version_max= f(os_version_max_orig)

#### os_version_min 

 os_version_max= f(os_version_min_orig)

#### os_version
  
  creative 文件同时包含了一个os_version字段， 规则是：
  
  os_version="${os_version_min_orig}-${os_version_max_orig}"
  
  那么合法的os_version值可以有: 
  
     "2.3.6-5.0"
     
     "6.0-"   不限最高版本
     
     "-8.1"   不限最低版本
     
     "-"      版本不限

### 网络类型: carrier_types

creatives文件增加字段: carrier_types

网络类型的字典表, 参考RTB标准:

```
  [{:name :unknown :value "0" :text "Unknown"}
   {:name :ethernet  :value "1"  :text "Ethernet"}
   {:name :wifi :value "2" :text "Wifi"}
   {:name :unknown_g :value "3" :text "Unknown Generation"}
   {:name :2g :value "4" :text "2G"}
   {:name :3g :value "5" :text "3G"}
   {:name :4g :value "6" :text "4G"}]
```

多个值使用";"分割，空值使用空串"".

e.g.

```
...
carrier_types: "0;3;4;5"
...
```

### 推广产品名称
字段： app_name

类型： String

### 推广产品Landing page
字段： app_url

类型： String

### 国家 

字段: country

规则： 多值(;分割), abbr2 大写

### 广告主域名

字段： adv_domain_name

类型： String

### 文件地址

file_url        （废弃）

file_url_cn     中国区域访问地址

file_url_abroad 国外访问地址


注: 

  对于原生广告， 上述字段表示logo图片的url
  对于非原生广告， 上述字段表示图片素材的url

### 文件类型

素材的文件类型

字段： filetype

类型： String

注： 无对应的素材文件时， 其filetype的值为"".

e.g. ".jpg", ".mp4"

### 素材规格(宽高)

width  宽: Int

height 高: Int

注:

  对于原生广告， 上述字段表示icon图片的宽高
  对于非原生广告， 上述字段表示素材(图片，富媒体，视频）的规格宽高

### 频次控制相关

相关字段： frequency_imp, frequency_imp_number, requency_imp_unit, frequency_click, frequency_click_number, frequency_click_unit

### 活动展示频次

frequency_imp    是否展示频次控制   
 
  取值：

  true 是
  false 否

当frequency_imp为true时, 增加以下两个字段:

requency_imp_unit  展示频次控制间隔单位

  取值:

    "d" 天
 
frequency_imp_number 展示频次控制的间隔内的次数
  
  整数


### 活动点击频次

frequency_click    是否点击频次控制   
 
  取值：

  true 是
  false 否

当frequency_click为true时, 增加以下两个字段:

requency_click_unit  点击频次控制间隔单位

  取值:

    "d" 天
 
frequency_click_number 点击频次控制的间隔内的次数
  
  整数

注： 为了兼容性， 暂时保留frequency, frequency_unit, frequency_number， 其值与对应活动的展示频次相同

### 是否需要Device Id

相关字段: need_deviceid

取值:

  true 需要Device Id

  false 不需要Device Id


### 省市定向

相关字段: mvgeoids

取值:

   mvgeoids 列表串, 多个之间使用';'分割

比如:

   选择定向的省市为:  广东省深圳市, 河南省

```
{
...
mvgeoids: "1156440300;1156410000",
...
}
```

选中某省，隐含表示同时包含旗下所有的城市

### 投放渠道

相关字段: network_id

类型: string

取值:

   渠道编码串, 多个之间使用';'分割
   
   空串表示不限渠道

### 语言

相关字段: language

类型: string

取值:

   语言编码串, 多个之间使用';'分割
   
   空串表示不限语言
   
注: 语言编码值规范为ISO 639-1 codes。 

示例:

```
{
...
language: "zh;en;ab"
...
}
```

（参考：

http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes

http://en.wikipedia.org/wiki/ISO_639-1

）

### 设备类型定向

字段: device_type

类型： String

取值:  "1" Phone

      "2" Pad

    多个之间使用';'分割

### 设备型号定向

相关字段: manufacture, model

manufacture 厂商

  类型 string
  
  取值

   厂商编码串, 多个之间使用';'分割
   
   空串表示不限厂商
  
model 型号

  类型 string
  
  取值
  
   型号编码串, 多个之间使用';'分割
   
   空串表示不限型号

(注: manufacture 和 model可同时存在值)

e.g.

```
manufacture:	"Samsung"
model:	"Nexus 7;D850"
```

### 设备ID定投

相关字段: devicegroup_type, devicegroup_refid

devicegroup_type 设备id定投类型

  类型 string
  
  取值

   none   不限
   white  重定量
   black  缩量
   
   空串也表示不限厂商
  
devicegroup_refid 关联的设号组

  类型 string
  
  取值
  
   设备号组对应的编号
   
 注: 如果devicegroup_type的值为none, 则devicegroup_refid字段不会传递

e.g.

```
devicegroup_refid:	"20150417183318"
devicegroup_type: "white"
```
### 经纬度定向

字段： lnglats

类型: Array[Object]

说明: 经纬度信息

...
lnglats: [
  {"lng": 116.380849,                     // 定位点-经度: Double
   "lat": 39.939076,                      // 定位点-维度: Double
   "radius": 1000                         // 半径(范围): Int,  单位: m
  },
  {"lng": 116.127886,
   "lat": 39.696115, 
   "radius": 500
  }
],
...


注: lnglats值为null或者[]时, 表示无经纬度定向


### media_type 媒体类型

字段： media_type

类型: String

说明: 媒体类型

取值范围:

 "0"          APP展示广告
 "2"          APP原生广告
 "4"          移动网站
 "8"          PC网站

值类型: 单值 （即每个广告组只能对应一个媒体类型)

### 定向媒体类别

媒体类别有两类： App定向媒体类别和Web定向媒体类别，具体见iDSP字典表: app和web sheet

它跟媒体类型有个(target_media_type)对应关系:

App定向媒体类别  <-> APP展示广告

Web定向媒体类别  <-> 移动网站和PC网站

每种类别支持一级分类和二级分类


#### wcat2 定向媒体类别第二级

wcat2 表示此广告可投的所有二级媒体类别

字段： wcat2

类型: String

说明: 媒体类别第二级的编码(code)串

值类型： 多值(多个之间使用";"分割)

示例:

{
...
media_type       : "0"
wcat2            : "2203;2501"
...
}

### cat2 广告主行业类型

cat2 表示广告创意对应的行业类型的第二级

字段： cat2

类型: String

说明: 广告主行业类型第二级的编码(code), 可为空

值类型： 单值

示例: 

{
...
"cat2":  "9901"
...
}


### material_type 创意素材类型

字段： material_type

类型: String

说明: 创意素材类型

值类型： 单值

取值范围： 

  "0" 图片

  "2" 原生广告

  "4" 富媒体

  "6" 视频
  
  "12" 第三方-筷子创意

### display_type 创意展现类型

字段： display_type

   注: 同时为了兼容性保留creative_type字段，其值跟display_type一样.

类型: String

说明: 创意展现类型

值类型： 单值

取值范围：

对于mobile流量:

  "0"   横幅

  "1"   全插屏

  "2"   不限
  
  "3"   原生

对于web流量:

  "10" 固定

  "11" 悬浮

对于Video流量:

  "20" 前贴片

  "25" 暂停

  "26" 悬浮


### exts 扩展字段
```
  exts: {                                                     // 此创意扩展字段:    JSON Object
    native: {                                                 // 原生广告(创意展现类型为:"3"原生)的扩展字段: JSON Object
             title:     ""                                    // 标题：String,     未设置： ""
             content:   ""                                    // 描述: String,     未设置： ""
             action_name: ""                                  // 按钮名字: String,  未设置： ""
             stars:     1                                     // 星型评级: Int,    未设置： -1
             downloads: 1                                     // 下载数:  Int,     未设置： -1
             players:   1                                     // 玩家数:  Int,     未设置:  -1
             author: ""                                       // 用户名: String,   未设置: ""
             tag:    ""                                       // 标签:   String,   未设置: ""
             ad_version: "2"                                  // 广告版本: String,  取值范围: "2" 新版, "1" 旧版 , "" 未设置
             images: [                                        // 图片: JSON Array  未设置:  []
                {
                  url_cn: "http://i.ad-mex.com/xxxx"          // 国内访问url
                  url_abroad: "http://iaws.ad-mex.com/xxxx"   // 国外访问url
                  width: 500,                                 // 宽:Int,         未设置:  -1
                  height: 600,                                // 高:Int,         未设置:  -1
                  type: 1                                     // 类型:Int,       取值范围， 参考openRTB, 1 - Icon, 2 - Logo, 3 - Main
                },
             ]
          },
    richmedia: {                                             //对于富媒体广告(创意素材类型为:"4"富媒体)的扩展字段: JSON Object
            source: ""                                       //富媒体源代码: String
    },
    video: {                                                  //对于视频广告(创意素材类型为:"6"视频)的扩展字段: JSON Object
            duration: 30,                                     //视频时长源代码: Int, 单位为秒  时长未设置时值为: -1
            data_rate: 300                                    //视频码流:  Int, 单位为Kbps， 码流未设置时值为: -1
    },
    tp_kuaizi: {                                                     // 对于第三方对接筷子科技的创意(创意素材类型为: "12" 筷子科技创意)的扩展字段: JSON Object
            link: "http://dcp.kuaizitech.com/?kz_id=127,276,12,114"  // 第三方创意URL: String
    }
  }
```