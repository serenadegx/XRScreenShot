# XRScreenShot
截屏监听

1.ContentObserver监听资源变化

2.适配华为、小米、oppo、魅族等手机

3.优化线程操作(不需要再写子线程更新UI的操作)

![image](https://github.com/serenadegx/XRWebview/blob/master/1545730427868.gif)

# 使用

Add it in your root build.gradle at the end of repositories:

	    allprojects {
		    repositories {
			    ...
			    maven { url 'https://jitpack.io' }
		    }
	    }

Add the dependency

	    dependencies {
	        implementation 'com.github.serenadegx:XRScreenShot:1.0.1'
	    }
      
      
      XRScreenHot.with(this).start(new ScreenHotListener() {
            @Override
            public void onScreenHotSuccess(final String path, long dateTaken) {
                
            }
      });
      
      //回收
      XRScreenHot.with(this).recycle();
