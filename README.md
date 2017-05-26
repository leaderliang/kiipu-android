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

## SwipeRefreshLayout 
```
//改变加载显示的颜色  
swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.RED);  
//设置背景颜色  
swipeRefreshLayout.setBackgroundColor(Color.YELLOW);  
//设置初始时的大小  
swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);  
//设置监听  
swipeRefreshLayout.setOnRefreshListener(this);  
//设置向下拉多少出现刷新  
swipeRefreshLayout.setDistanceToTriggerSync(100);  
//设置刷新出现的位置  
swipeRefreshLayout.setProgressViewEndTarget(false, 200); 
```

## FloatingActionButton
```
app:fabSize=”normal” 用来定义 FAB 的大小
app:elevation 　　为空闲状态下的阴影深度，
app:pressedTranslationZ　　 为按下状态的。
app:backgroundTint 　　是指定默认的背景颜色 
app:rippleColor 　　是指定点击时的背景颜色 
app:border 　　 Width 　border的宽度 
app:fabSize 　　是指FloatingActionButton的大小，可选normal|mini 
app:pressedTranslationZ 　　按下去时的z轴的便宜
```

## NavigationView

## 设置MenuItem的字体颜色 
NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);  
        navigationView.setNavigationItemSelectedListener(this);  
        Resources resource=(Resources)getBaseContext().getResources();  
        ColorStateList csl=(ColorStateList)resource.getColorStateList(R.color.navigation_menu_item_color);  
navigationView.setItemTextColor(csl);  
/设置MenuItem默认选中项/  
navigationView.getMenu().getItem(0).setChecked(true); 

/此处是设置menu图标的颜色为图标本身的颜色 设置后图标恢复黑色/
navigationView.setItemIconTintList(null);
navigationView.getMenu().removeItem(R.id.item_collection);


## 获得 LayoutInflater 实例的三种方式
```
http://www.cnblogs.com/top5/archive/2012/05/04/2482328.html

LayoutInflater inflater = getLayoutInflater();//调用Activity的getLayoutInflater() 
LayoutInflater inflater = LayoutInflater.from(context);  
LayoutInflater inflater =  (LayoutInflater)context.getSystemService
                              (Context.LAYOUT_INFLATER_SERVICE);
```
## 按钮波纹效果的两个属性
```
android:background="?android:attr/selectableItemBackground"> // 波纹有边界
android:background="?android:attr/selectableItemBackgroundBorderless"> // 波纹超出边界，就是一个圆型
```

## Manifest 小属性
> 设置为true后,当用户按了“最近任务列表”时候,该Task不会出现在最近任务列表中,可达到隐藏应用的目的。
> android:excludeFromRecents="true" 

## 布局中的小属性
> android:clipToPadding="false"
> 常常用于paddingTop，假设 内部有个属性设置了PaddingTop但是滑动的时候就忽视paddingTop的 则使用该属性 

## Bug 专区
```
 W/WindowAnimator: Failed to dispatch window animation state change.
                                                 android.os.DeadObjectException
                                                     at android.os.BinderProxy.transactNative(Native Method)
                                                     at android.os.BinderProxy.transact(Binder.java:503)
                                                     at android.view.IWindow$Stub$Proxy.onAnimationStopped(IWindow.java:534)
                                                     at com.android.server.wm.WindowAnimator.updateWindowsLocked(WindowAnimator.java:286)
                                                     at com.android.server.wm.WindowAnimator.animateLocked(WindowAnimator.java:678)
                                                     at com.android.server.wm.WindowAnimator.access$000(WindowAnimator.java:53)
                                                     at com.android.server.wm.WindowAnimator$1.doFrame(WindowAnimator.java:123)
                                                     at android.view.Choreographer$CallbackRecord.run(Choreographer.java:856)
                                                     at android.view.Choreographer.doCallbacks(Choreographer.java:670)
                                                     at android.view.Choreographer.doFrame(Choreographer.java:603)
                                                     at android.view.Choreographer$FrameDisplayEventReceiver.run(Choreographer.java:844)
                                                     at android.os.Handler.handleCallback(Handler.java:739)
                                                     at android.os.Handler.dispatchMessage(Handler.java:95)
                                                     at android.os.Looper.loop(Looper.java:148)
                                                     at android.os.HandlerThread.run(HandlerThread.java:61)
                                                     at com.android.server.ServiceThread.run(ServiceThread.java:46)
05-25 18:17:20.607 31975-31975/? W/System: ClassLoader referenced unknown path: /data/app/com.mycreat.kiipu-1/lib/arm
```

## Android 开发小工具之：Custom Tabs
> http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0914/3451.html


## 后期待研究系统 view
```
android.support.v7.widget.AppCompatImageView
android.support.v7.widget.AppCompatTextView

```

