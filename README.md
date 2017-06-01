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

diskCacheStrategy(DiskCacheStrategy strategy).设置缓存策略。
DiskCacheStrategy.SOURCE：缓存原始数据，
DiskCacheStrategy.RESULT：缓存变换(如缩放、裁剪等)后的资源数据，
DiskCacheStrategy.NONE：什么都不缓存，
DiskCacheStrategy.ALL：缓存SOURC和RESULT。
默认采用DiskCacheStrategy.RESULT策略，
对于download only操作要使用DiskCacheStrategy.SOURCE

### 一些使用技巧
Glide.with(context).resumeRequests()和 Glide.with(context).pauseRequests()

当列表在滑动的时候，调用pauseRequests()取消请求，滑动停止时，调用resumeRequests()恢复请求。这样是不是会好些呢？

Glide.clear()

当你想清除掉所有的图片加载请求时，这个方法可以帮助到你。

ListPreloader

如果你想让列表预加载的话，不妨试一下ListPreloader这个类


如果你的项目需要大量使用图片，比如是类似 Instagram 一类的图片社交 App ，那么推荐使用 Fresco。Fresco 是 Facebook 作品，关于内存的占用优化更好，但是同时包也更大，门槛也更高，初级工程师不建议使用。官方地址：

### 和 Glide 图片加载相关的库
一个基于Glide的transformation库，拥有裁剪，着色，模糊，滤镜等多种转换效果
```
compile 'jp.wasabeef:glide-transformations:2.0.2'
    // If you want to use the GPU Filters
compile 'jp.co.cyberagent.android.gpuimage:gpuimage-library:1.4.1'
```
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
navigationView.setItemIconTintList(null);// set menu item default color
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

## ImageView 设置宽高比
ImageView 有个属性 android:adjustViewBounds="true" ，意思是图片是否保持宽高比；
设置完属性后，需要在代码中动态设置 maxWidth、MaxHeight 才会生效
例如 16 ：9
imageView.setMaxWidth(screenWidth);  
imageView.setMaxHeight(screenWidth / (16 / 9));



## Android 开发小工具之：Custom Tabs
> http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0914/3451.html


## 后期待研究系统 view
```
android.support.v7.widget.AppCompatImageView
android.support.v7.widget.AppCompatTextView

```

## RecyclerView 相关布局

> 创建线性布局
  LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
  mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
  mRecyclerView.setLayoutManager(mLinearLayoutManager);

参数解析：
spanCount: 每列或者每行的item个数，设置为1，就是列表样式  该构造函数默认是竖直方向的网格样式,每列或者每行的item个数，设置为1，就是列表样式网格样式的方向，水平（OrientationHelper.HORIZONTAL）或者竖直（OrientationHelper.VERTICAL）
reverseLayout: 是否逆向，true：布局逆向展示，false：布局正向显示

GridLayoutManager mGirdLayoutManager = new GridLayoutManager(this, Constants.SPAN_COUNT, GridLayoutManager.VERTICAL, false);
recyclerView.setLayoutManager(mGirdLayoutManager);

// 创建 瀑布流
StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(SPAN_COUNT,OrientationHelper.VERTICAL);
mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

// StaggeredGridLayoutManager 管理 RecyclerView的布局  瀑布流   http://blog.csdn.net/zhangphil/article/details/47604581
RecyclerView.LayoutManager mLayoutManager;
StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
mRecyclerView.setLayoutManager(mLayoutManager);

## FloatingActionButton
```
Github地址：https://github.com/Clans/FloatingActionButton
项目中：FloatingActionButton FloatingActionMenu 的所有属性
需要在 xml 文件中添加依赖 xmlns:fab="http://schemas.android.com/apk/res-auto"
Floating action button
Here are all the FloatingActionButton's xml attributes with their default values which means that you don't have to set all of them:
<com.github.clans.fab.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|right"
        android:layout_marginBottom="8dp"
        android:layout_marginRight="8dp"
        android:src="@drawable/your_icon_drawable"
        app:fab_colorNormal="#DA4336"
        app:fab_colorPressed="#E75043"
        app:fab_colorRipple="#99FFFFFF"
        app:fab_showShadow="true"
        app:fab_shadowColor="#66000000"
        app:fab_shadowRadius="4dp"
        app:fab_shadowXOffset="1dp"
        app:fab_shadowYOffset="3dp"
        app:fab_size="normal"
        app:fab_showAnimation="@anim/fab_scale_up"
        app:fab_hideAnimation="@anim/fab_scale_down"
        app:fab_label=""
        app:fab_progress_color="#FF009688"
        app:fab_progress_backgroundColor="#4D000000"
        app:fab_progress_indeterminate="false"
        app:fab_progress_max="100"
        app:fab_progress="0"
        app:fab_progress_showBackground="true"/>
        
Floating action menu
Here are all the FloatingActionMenu's xml attributes with their default values which means that you don't have to set all of them:
     
        <com.github.clans.fab.FloatingActionMenu
                android:id="@+id/menu"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_alignParentBottom="true"
                android:layout_alignParentRight="true"
                android:layout_marginRight="10dp"
                android:layout_marginBottom="10dp"
                android:layout_marginLeft="10dp"
                fab:menu_fab_size="normal"
                fab:menu_showShadow="true"
                fab:menu_shadowColor="#66000000"
                fab:menu_shadowRadius="4dp"
                fab:menu_shadowXOffset="1dp"
                fab:menu_shadowYOffset="3dp"
                fab:menu_colorNormal="#DA4336"
                fab:menu_colorPressed="#E75043"
                fab:menu_colorRipple="#99FFFFFF"
                fab:menu_animationDelayPerItem="50"
                fab:menu_icon="@drawable/fab_add"
                fab:menu_buttonSpacing="0dp"
                fab:menu_labels_margin="0dp"
                fab:menu_labels_showAnimation="@anim/fab_slide_in_from_right"
                fab:menu_labels_hideAnimation="@anim/fab_slide_out_to_right"
                fab:menu_labels_paddingTop="4dp"
                fab:menu_labels_paddingRight="8dp"
                fab:menu_labels_paddingBottom="4dp"
                fab:menu_labels_paddingLeft="8dp"
                fab:menu_labels_padding="8dp"
                fab:menu_labels_textColor="#FFFFFF"
                fab:menu_labels_textSize="14sp"
                fab:menu_labels_cornerRadius="3dp"
                fab:menu_labels_colorNormal="#333333"
                fab:menu_labels_colorPressed="#444444"
                fab:menu_labels_colorRipple="#66FFFFFF"
                fab:menu_labels_showShadow="true"
                fab:menu_labels_singleLine="false"
                fab:menu_labels_ellipsize="none"
                fab:menu_labels_maxLines="-1"
                fab:menu_labels_style="@style/YourCustomLabelsStyle"
                fab:menu_labels_position="left"
                fab:menu_openDirection="up"
                fab:menu_backgroundColor="@android:color/transparent"
                fab:menu_fab_label="your_label_here"
                fab:menu_fab_show_animation="@anim/my_show_animation"
                fab:menu_fab_hide_animation="@anim/my_hide_animation">
        
                <com.github.clans.fab.FloatingActionButton
                    android:id="@+id/menu_item"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:src="@drawable/ic_star"
                    fab:fab_size="mini"
                    fab:fab_label="Menu item 1" />
        
            </com.github.clans.fab.FloatingActionMenu>
```
## ActionBar (更详细的介绍 http://blog.csdn.net/liu149339750/article/details/8282471)
setHomeButtonEnabled这个小于4.0版本的默认值为true的。但是在4.0及其以上是false，该方法的作用：决定左上角的图标是否可以点击。没有向左的小图标。 true 图标可以点击  false 不可以点击。

actionBar.setDisplayHomeAsUpEnabled(true)    // 给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP

actionBar.setDisplayShowHomeEnabled(true)   //使左上角图标是否显示，如果设成false，则没有程序图标，仅仅就个标题，否则，显示应用程序图标，对应id为Android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME

actionBar.setDisplayShowCustomEnabled(true)  // 使自定义的普通View能在title栏显示，即actionBar.setCustomView能起作用，对应ActionBar.DISPLAY_SHOW_CUSTOM

actionBar.setDisplayShowTitleEnabled(true)   //对应ActionBar.DISPLAY_SHOW_TITLE。
其中setHomeButtonEnabled和setDisplayShowHomeEnabled共同起作用，如果setHomeButtonEnabled设成false，即使setDisplayShowHomeEnabled设成true，图标也不能点击

最后，如果希望点击图标左侧箭头返回上一页，需要加载选项菜单后，对于菜单项的点击事件调用如下方法：
 public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
      //Android.R.id.home对应应用程序图标的id
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

# Material Design
所有组件的阴影去掉方式可使用
app:elevation="0dp"








