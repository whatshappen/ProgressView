# ProgressView
material design progress view（material design 风格进度条样式）

# Usage Example

write 'com.whathappen.progresslibrary.view.CircleProgress' in you layout.xml file:

```xml
	<com.whathappen.progresslibrary.view.CircleProgress
        android:id="@+id/circle_progress"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:padding="10dp"
        cp:backgroundColor="@android:color/white"
        cp:dialTextSize="8sp"
        cp:currentProgress="32"
        cp:progressType="gradientType_Dial"
        cp:progressValueStyle="outerValueTypeShowDial"/>
```

# Effect

![effect](https://github.com/whatshappen/ProgressView/blob/master/screen_shot/dialogStyle.gif)




# License
Copyright 2018 whatshappen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
