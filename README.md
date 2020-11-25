
[![](https://jitpack.io/v/DragonTotem/WegeneScan.svg)](https://jitpack.io/#DragonTotem/WegeneScan)

# WegeneScan

To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.DragonTotem:WegeneScan:1.0'
	}
  
 可以继承BaseCaptureActivity，只需要实现三个方法就可以使用了，必须在initView中给出viewfinderView、surfaceView
