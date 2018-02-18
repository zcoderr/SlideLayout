# SlideLayout
A slidable custome layout library for Android.

[![License](https://camo.githubusercontent.com/e5f0d52475ce71aa1caf8ff4aa3e036dd5b9f811/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f6c6963656e73652d417061636865253230322d677265656e2e737667)](https://www.apache.org/licenses/LICENSE-2.0)[ ![Download](https://api.bintray.com/packages/zacharywang/maven/slidelayout/images/download.svg?version=1.0.2)](https://bintray.com/zacharywang/maven/slidelayout/1.0.2/link)

## Features

![gif](https://github.com/2acharyW/Resource/raw/master/gif/slidelayout.gif)

## Demo

[Slidelayout-sample.apk](https://github.com/2acharyW/Resource/raw/master/apk/slidelayout-simple.apk)

## Download

###### gradle:

```xml
compile 'xyz.zhenhua:slidelayout:1.0.2'
```

###### Maven:

```xml
<dependency>
  <groupId>xyz.zhenhua</groupId>
  <artifactId>slidelayout</artifactId>
  <version>1.0.2</version>
  <type>pom</type>
</dependency>
```

## Usage

###### Simple:

```xml
<xyz.zhenhua.slidablelayout.SlidableLayout
    android:id="@+id/slidelayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <View
    	android:id="@+id/view"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </View>
</xyz.zhenhua.slidablelayout.SlidableLayout>

slideLayout = findViewById(R.id.slidelayout);
slideLayout.init();
```

###### Set Listener:

```java
slideLayout.setSlideableListener(new SlidableLayout.SlideableListener() {
    @Override
    public void onSlideStart() {
		//do something
    }

    @Override
    public void onSpringStart() {
		//do something
    }

    @Override
    public void onSpringStop() {
		//do something
    }

    @Override
    public void onDismiss() {
		//do something
    }
});
```

