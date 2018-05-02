# ProgressView
material design progress view（material design 风格进度条样式）

# Usage Example

in the build.gradle:
```
compile 'com.whathappen:progresslibrary:1.0.0'
```

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
CircleProgress Effect:

![effect](https://github.com/whatshappen/ProgressView/blob/master/screen_shot/dialogStyle.gif)

# Special Note
<p style='color:red'>When setting progress view the properties in the code, be sure to builder().</p>

# Readme
#### CircleProgress attrs:
> * strokeWidth: Progress bar width (进度条宽度)
> * maxProgress: Biggest progress (最大进度)
> * currentProgress: Current progress (当前进度)
> * startAngle: Starting angle (开始角度)
> * backgroundColor: Background color (进度条默认颜色)
> * progressColor: Progress color (进度颜色)
> * textColor: Progress value font color (进度值字体颜色)
> * textSize: Progress value font size(进度值字体大小)
> * progressColors_start: Gradation start color (渐变色样式开始颜色)
> * progressColors_center: Gradation center color (渐变色样式中间颜色)
> * progressColors_end: Gradation end color (渐变色样式结束颜色)
> * progressType: Progress style (进度值样式：默认，渐变，刻度)
> * dialDefaultColor: Dial color (刻度颜色)
> * singleDialWidth: Dial width (刻度宽度)
> * lineWidth: Dial interval width (刻度间隔宽度)
> * progressValueStyle: Progress value style (进度值显示风格)
> * progressValueType: Progress value type (进度值风格:float,int)
> * dialTextSize: Dial font size (刻度值字体大小)


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
