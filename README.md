# kiipu-android
## 应用下 build.gradle 初始化时 dependencies 的库解释
#### 测试是软件开发中非常重要的一部分，Android 中是使用 junit 测试框架，Android测试主要分两类本地测试和Instrumented测试，本地测试其实就是普通的Java程序测试，它运行在本地的JVM，Instrumented 测试则需要一台 Android 设备来运行测试。
```
androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations' }) //声明 Instrumented 测试依赖
testCompile 'junit:junit:4.12' //声明本地测试的依赖
```
## Glide、Fresco
#### 图片加载可能跟网络请求一样，基本是所有 App 开发必备的功能，选择一款成熟稳定的图片加载库重要性不言而喻，目前主流的图片加载有 Picasso、Glide、Fresco

Glide 是 Google 员工基于 Picasso 基础上进行开发的，所以自然各方面比 Picasso 更有优势，而且支持 Gif，所以推荐大家优先选择 Glide 库，官方地址：

> https://github.com/bumptech/glide

如果你的项目需要大量使用图片，比如是类似 Instagram 一类的图片社交 App ，那么推荐使用 Fresco。Fresco 是 Facebook 作品，关于内存的占用优化更好，但是同时包也更大，门槛也更高，初级工程师不建议使用。官方地址：

> https://github.com/facebook/fresco

这两款图片加载库，基本算是在 16 年使用最多，被认可最高的两个图片加载库了。

## CardView的常用属性

属性 | 作用
---|---
card_view:cardElevation |	阴影的大小
card_view:cardMaxElevation	 | 阴影最大高度
card_view:cardBackgroundColor |	卡片的背景色
card_view:cardCornerRadius |	卡片的圆角大小
card_view:contentPadding |	卡片内容于边距的间隔
card_view:contentPaddingBottom |	卡片内容与底部的边距
card_view:contentPaddingTop	| 卡片内容与顶部的边距
card_view:contentPaddingLeft |	卡片内容与左边的边距
card_view:contentPaddingRight |	卡片内容与右边的边距
card_view:contentPaddingStart |	卡片内容于边距的间隔起始
card_view:contentPaddingEnd	 | 卡片内容于边距的间隔终止
card_view:cardUseCompatPadding |	设置内边距，V21+的版本和之前的版本仍旧具有一样的计算方式
card_view:cardPreventConrerOverlap |	在V20和之前的版本中添加内边距，这个属性为了防止内容和边角的重叠

## Glide 使用

#### diskCacheStrategy(DiskCacheStrategy strategy).设置缓存策略。
> DiskCacheStrategy.SOURCE：缓存原始数据；

> DiskCacheStrategy.RESULT：缓存变换(如缩放、裁剪等)后的资源数据；

> DiskCacheStrategy.NONE：什么都不缓存；

> DiskCacheStrategy.ALL：缓存SOURC和RESULT；

> 默认采用DiskCacheStrategy.RESULT策略，对于download only操作要使用DiskCacheStrategy.SOURCE。