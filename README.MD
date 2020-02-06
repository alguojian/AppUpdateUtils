## android app 更新下载安装，通知栏提示下载进度

[![](https://www.jitpack.io/v/ALguojian/AppUpdate.svg)](https://www.jitpack.io/#ALguojian/AppUpdate)
#### 1. Add it in your root build.gradle at the end of repositories:

```
    allprojects {

        repositories {
            maven { url 'https://www.jitpack.io' }
        }
    }

```

#### 2.Add the dependency


```
    dependencies {

        implementation 'com.github.alguojian:AppUpdate:1.0.7'

    }
```

#### 3.因为使用了广播和适配7.0，所以清单文件需要配置如下

```
<provider

       android:name="android.support.v4.content.FileProvider"
       android:authorities="${applicationId}.fileprovider"
       android:exported="false"
       android:grantUriPermissions="true">

  <meta-data
         android:name="android.support.FILE_PROVIDER_PATHS"
         android:resource="@xml/download_file_path" />
 </provider>

 ```

> download_file_path配置的信息是：只需要吧第一个地址改为你的包名即可
```
<?xml version="1.0" encoding="utf-8"?>

<paths>

    <!--app目录-->
       <external-path
           name="files_root"
           path="Android/data/com.alguojian.appupdate/" />

       <!--sd卡根目录-->
       <external-path
           name="external_storage_root"
           path="." />

</paths>
```

#### 4. 使用方式如下,强制更新已经实现，使用只需要设置是否开启，下载进度也做了回调，下载完成会自动打开安装页面，下载出错也会直接回调进度下载进度100%，请自行处理

```
    //下载使用了okdownload，有时会下载失败，所以需要在application添加okhttp配置，如果已经使用切勿重复配置
    AppUpdate.initThis(this)

    //动态注册广播，用于接收下载进度，以及通知栏
    updateAppReceiver = UpdateAppReceiver(this)
    registerReceiver(updateAppReceiver, updateAppReceiver.intentFilter)

  
    //更新
    
    AppUpdate.with(this@MainActivity)
            
            //设置下载地址，不支持中文
            .setUpdatePath("https://ossqiyu.oss-cn-hangzhou.aliyuncs.com/Versions/yuebeikafei137.apk")
            
            //设置通知栏通知的图标
            .setNotificationIconAndName(R.mipmap.ic_launcher, "约杯咖啡")
            
            //设置版本名字
            .setVersionName("1.3.4.1")

            //设置版本更新标题
            .setUpdateTitle("发现新版本")
            
            //设置版本更新内容
            .setUpdateInfo(getString(R.string.app_update_message))

            //设置是否强制更新
            .setEnforceUpdate(boolean)
            
            //设置是否支持断点续传下载，默认不支持
            .setBreakpointDown(false)
            
            //设置更新弹框回调
            .setOnUpdateClick(ClickCallback {

            })
            
            //设置下载进度回调
            .setDownProgressListener(UpdateProgressCallBack {

            })
            .startUpdate()
    

      @Override
      protected void onDestroy() {
          super.onDestroy();
          //解除绑定
          unregisterReceiver(updateAppReceiver);
      }
  }

```
