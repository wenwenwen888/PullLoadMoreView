# PullLoadMoreView
仿微信下拉显示小程序的控件(简单实现)

先看预览图(转换后有一点点失真):

<img src="https://github.com/wenwenwen888/PullLoadMoreView/blob/master/preview/1.gif" width="30%" height="30%">

前言
-------
1. 该库主要参考[这篇文章](https://www.jianshu.com/p/e409de213938) ，实现的原理大家看这篇文章就好了，感谢该Po

2. 在该篇文章的前提下，一共作出了以下修改
	* 把Kotlin改为了Java
	* 把可滑动控件的主布局改为了NestedScrollView(原文为ListView)，按照原理，你可以改为任意一个可滑动的控件(但是需要您亲自下载library修改)
	* 一些代码的优化，譬如
		* NestedScrollView的布局直接在xml里实现
		* 接口的添加和优化，使得更加方便

3. 需要修改更多内容的可以下载library自行修改

4. 有不妥之处请Issues指出,谢谢


Usage
--------

With Gradle:
```groovy
  implementation 'com.wenwenwen888:pullloadmoreview:1.1.0'
```


How to use（直接clone项目查看demo更加直观哦）
--------
一：xml主布局配置
```java
 <?xml version="1.0" encoding="utf-8"?>
<com.wenwenwen.view.PullLoadMoreView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/pullLoadMoreView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:top_background_color="#fff">

    <android.support.v4.widget.NestedScrollView
        android:id="@+id/nestedScrollview"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <TextView
                android:layout_width="match_parent"
                android:layout_height="300dp"
                android:gravity="center"
                android:text="test1" />

            <TextView
                android:layout_width="match_parent"
                android:layout_height="300dp"
                android:gravity="center"
                android:text="test2" />

            <TextView
                android:layout_width="match_parent"
                android:layout_height="300dp"
                android:gravity="center"
                android:text="test3" />

            <TextView
                android:layout_width="match_parent"
                android:layout_height="300dp"
                android:gravity="center"
                android:text="test4" />

        </LinearLayout>

    </android.support.v4.widget.NestedScrollView>

</com.wenwenwen.view.PullLoadMoreView>
```

```java
	//此配置为顶部布局的背景颜色
	app:top_background_color="#fff"
```
二: 头部xml的配置
```java
	//添加头部布局
 	pullLoadMoreView.addHeadView(R.layout.top_layout);
```
三：可设置回调监听
```java
  //添加监听滑动布局的open/close
        pullLoadMoreView.setViewStateListener(new PullLoadMoreView.ViewStateListener() {
            @Override
            public void onViewState(PullLoadMoreView.VIewState viewState) {
                if (viewState == PullLoadMoreView.VIewState.OPEN) {
                    Toast.makeText(MainActivity.this, "Open", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Close", Toast.LENGTH_SHORT).show();
                }
            }
        });
```
 
# License

    Copyright 2019 wenwenwen888

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
