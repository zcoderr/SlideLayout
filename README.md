# SlideLayout
A slidable custome layout library for Android.

 

Features



Demo

Slidelayout-sample.apk

Download

gradle:

    compile 'xyz.zhenhua:slidelayout:1.0.2'

Maven:

    <dependency>
      <groupId>xyz.zhenhua</groupId>
      <artifactId>slidelayout</artifactId>
      <version>1.0.2</version>
      <type>pom</type>
    </dependency>

Usage

Simple:

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

Set Listener:

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
