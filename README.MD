# android app 更新下载安装，通知栏提示下载进度

[![](https://jitpack.io/v/ALguojian/AppUpdate.svg)](https://jitpack.io/#ALguojian/AppUpdate)

- Add it in your root build.gradle at the end of repositories:

```
    allprojects {

        repositories {

            maven { url 'https://www.jitpack.io' }

        }
    }

```


 - Add the dependency


```
    dependencies {

        implementation 'com.github.ALguojian:AppUpdate:1.0.0'

    }
```

4.因为使用了广播和适配7.0，所以清单文件需要配置如下

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

5. 使用方式如下,强制更新已经实现，使用只需要设置是否开启，下载进度也做了回调，下载完成会自动打开安装页面，下载出错也会直接回调进度下载进度100%，请自行处理

```

  public class MainActivity extends AppCompatActivity {


      private UpdateAppReceiver updateAppReceiver;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          setContentView(R.layout.activity_main);

          updateAppReceiver = new UpdateAppReceiver();

          IntentFilter intentFilter = new IntentFilter();

          //你的包名
          intentFilter.addAction("com.alguojian.appupdate");

          registerReceiver(updateAppReceiver, intentFilter);

          updateAppReceiver.setNotification(this);

          ActivityCompat.requestPermissions(this,
                  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NOTIFICATION_POLICY
                          , Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE}, 200);

          findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  AppUpdate.into(MainActivity.this)

                          //设置下载地址，不支持中文
                          .setUpdatePath("http://issuecdn.baidupcs.com/issue/netdisk/apk/BaiduNetdisk_7.15.1.apk")

                          //设置通知栏通知的图标
                          .setNotificationIcon(R.mipmap.ic_launcher)

                          //设置版本名字
                          .setVersionName("1.3.0")

                          //设置版本更新内容
                          .setUpdateInfo("我们更我们更新了我们更新了我们更新了我们更新了新了")

                          //设置是否强制更新
                          .setEnforceUpdate(true)

                          //设置更新是的回调
                          .setOnUpdateClick(new UpDateCallback() {
                              @Override
                              public void cancel() {

                                  System.out.println("--------------点击取消更新");
                              }

                              @Override
                              public void success() {
                                  System.out.println("--------------点击开始更新");
                              }

                          })
                          .setOnUpdateNoWifiClick(new ClickCallback() {
                              @Override
                              public void cancel() {

                                  System.out.println("--------------不是wifi，取消更新");
                              }

                              @Override
                              public void success() {

                                  System.out.println("--------------点击开始更新");
                              }
                          })
                          .setDownProgressListener(new UpdateProgressCallBack() {
                              @Override
                              public void downloadProgress(int progress) {

                                  if (100 == progress) {

                                      updateAppReceiver.startInstall(MainActivity.this);

                                  }
                              }
                          }).startUpdate();
              }
          });
      }

      @Override
      protected void onDestroy() {
          unregisterReceiver(updateAppReceiver);
          updateAppReceiver=null;
          super.onDestroy();

      }
  }

```
