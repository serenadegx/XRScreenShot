# XRScreenShot
截屏监听

1.ContentObserver监听资源变化

2.适配华为、小米、oppo、魅族等手机

3.优化线程操作(不需要再写子线程更新UI的操作)

![image](https://github.com/serenadegx/XRScreenShot/blob/master/1550730578106.gif)

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
code
      
      XRScreenHot.with(context).start(new ScreenHotListener() {
            /**
             * 
             * @param path 截图路径
             * @param dateTaken 时间戳
             */
            @Override
            public void onScreenHotSuccess(final String path, long dateTaken) {
                               iv.setImageURI(Uri.fromFile(new File(path)));
            }
        });
      
      //回收
      XRScreenHot.with(context).recycle();
      
 注意：6.x及更高系统，需要权限适配（存储）
      
 参考：
 
 https://blog.csdn.net/xiaohanluo/article/details/53737655

