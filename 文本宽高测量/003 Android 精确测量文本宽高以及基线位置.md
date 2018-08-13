# 003 Android 精确测量文本宽高以及基线位置

笔者最近在做一款弹幕控件，里面涉及到绘制文本，以及文本边框。而绘制文本边框需要知道文本的左边位置，以及文本的宽高。

通常来说，使用 Canvas 绘制文本，可以通过画笔 Paint 设置文字的大小。但是画笔的大小与文字的宽高并无直接关系。

大家应该能说上几种测量文字宽高的方法，如：

方案1. 通过 Paint 的 measureText 方法，可以测量文字的宽度
方案2. 通过获取 Paint 的 FontMetrics, 根据 FontMetrics 的 leading, ascent, 和 descent可以获取文字的高度。
方案3. 通过 Paint 的 getTextBounds 获取文本的边界矩形 Rect，根据 Rect 可以计算出文字的宽高。
方案4. 通过 Paint 获取文字的 Path, 根据 Path 获取文本的边界矩形 Rect, 根据 Rect 可以计算出文字的宽高。

表面上看，我们有以上四种方案可以获取文字的宽或高。但是不幸的，这四种方案里，有些方法获取到的数值不是真实的文字宽高。

我们通过以下测试代码，分别测试字母 "e" 和 "j"。

```java
private void measureText(String str) {
    if (str == null) {
        return;
    }
    float width1 = mPaint.measureText(str);
    Log.i("lxc", "width1 ---> " + width1);

    Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
    float height1 = Math.abs(fontMetrics.leading + fontMetrics.ascent) + fontMetrics.descent;
    Log.i("lxc", "height1 ---> " + height1);

    Rect rect = new Rect();
    mPaint.getTextBounds(str, 0, str.length(), rect);
    float width2 = rect.width();
    float height2 = rect.height();
    Log.i("lxc", "width2 ---> " + width2);
    Log.i("lxc", "height2 ---> " + height2);


    Path textPath = new Path();
    mPaint.getTextPath(str, 0, str.length(), 0.0f, 0.0f, textPath);
    RectF boundsPath = new RectF();
    textPath.computeBounds(boundsPath, true);
    float width3 = boundsPath.width();
    float height3 = boundsPath.height();
    Log.i("lxc", "width3 ---> " + width3);
    Log.i("lxc", "height3 ---> " + height3);
}
```

日志输出如下：

```
08-13 22:50:20.777 4977-4977/com.orzangleli.textbounddemo I/lxc: width1 ---> 21.0
08-13 22:50:20.777 4977-4977/com.orzangleli.textbounddemo I/lxc: height1 ---> 46.875
08-13 22:50:20.777 4977-4977/com.orzangleli.textbounddemo I/lxc: width2 ---> 18.0
08-13 22:50:20.778 4977-4977/com.orzangleli.textbounddemo I/lxc: height2 ---> 22.0
08-13 22:50:20.778 4977-4977/com.orzangleli.textbounddemo I/lxc: width3 ---> 17.929688
08-13 22:50:20.778 4977-4977/com.orzangleli.textbounddemo I/lxc: height3 ---> 21.914062
08-13 22:50:20.778 4977-4977/com.orzangleli.textbounddemo I/lxc:  <----分割线---->
08-13 22:50:20.778 4977-4977/com.orzangleli.textbounddemo I/lxc: width1 ---> 10.0
08-13 22:50:20.778 4977-4977/com.orzangleli.textbounddemo I/lxc: height1 ---> 46.875
08-13 22:50:20.778 4977-4977/com.orzangleli.textbounddemo I/lxc: width2 ---> 8.0
08-13 22:50:20.778 4977-4977/com.orzangleli.textbounddemo I/lxc: height2 ---> 37.0
08-13 22:50:20.778 4977-4977/com.orzangleli.textbounddemo I/lxc: width3 ---> 8.046875
08-13 22:50:20.778 4977-4977/com.orzangleli.textbounddemo I/lxc: height3 ---> 37.36328
```

首先，我们可以确定字母 "e" 和 "j" 的显示高度应该不一样，而使用第二种 FontMetrics 方案计算出的两种情况文字高度一样，而且从代码的调用上
看，我们也是直接根据 Paint 获取的 FontMetrics, 与文字内容无关。所以我们需要测量文字真实高度的话，需要排除第二种方案了。

我们准备一个自定义 View，在 onDraw 方法中使用 mPaint 绘制一个文本 "e"， 然后截图测量文本宽高，得出以下结果：

<img src="http://7xvdj7.com1.z0.glb.clouddn.com/%E6%96%87%E6%9C%AC%E5%AE%BD%E9%AB%98.png" w="150px"></img>

可以看到，文本的宽为 18， 高为 22。 可以得出以下结论：

方案1测量结果为近似值，存在一定误差。
方案3测量结果准确。
方案4测量结果精度更高，数值基本与方案3一致。

再多说几句。与测量文字高度类似，我们如何获取文字的基线 baseline 位置。

<img src="http://7xvdj7.com1.z0.glb.clouddn.com/QQ20180813-231257.png">

一般的博客上会告诉我们，如果需要计算文字的基线 baseline 位置，可以通过 FontMetrics 来计算。FontMetrics 基线上面的值为负数，基线下面的值为正数。baseline 计算公式为：

```baseline = ascent + leading```

如果你真的使用了这个公式就会发现坑。这个公式计算的基线位置实际上是默认字体的基线位置，与文字内容无关。我们可以看下面的例子：

在自定义 View 的 onDraw 方法中，绘制一个字符 "e"， 绘制y坐标为 baseline，所以文字应该会顶着 Activity 的边界。

```java
@Override
protected void onDraw(Canvas canvas) {
   super.onDraw(canvas);

   Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
   float baseline = Math.abs(fontMetrics.leading + fontMetrics.ascent);
   canvas.drawText("e", 0, baseline, mPaint);
}
```
显示结果为：

<img src="http://7xvdj7.com1.z0.glb.clouddn.com/QQ20180813-231809@2x.png">

那问题来了，究竟怎么计算才能计算出真实的文本的基线位置呢。

我们使用之前的方案3来试试。代码如下：

```java
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    String str = "e";
    Rect rect = new Rect();
    mPaint.getTextBounds(str, 0, str.length(), rect);
    float baseline = Math.abs(rect.top);
    canvas.drawText(str, 0, baseline, mPaint);
}
```
看看效果， 已经能够满足我们的需求，左上都顶着 Activity 显示了。

<img src="http://7xvdj7.com1.z0.glb.clouddn.com/QQ20180813-232426@2x.png"/>

** 总结 **

精确测量文本宽高时，尽量不要使用 FontMetrics 去做。如果要求不精确，可以使用 Paint 的 measureText 方法计算文本宽度，
如果要求精确测量，可以使用 Paint 的 getTextBounds 方法 或者 getTextPath 方法，获取文本的边界框矩形 Rect， 所获的
Rect 的宽高即为文本的宽高， Rect的 top 为文本上边界距基线的距离， Rect 的 bottom 为文本下边距距离基线的距离。

本文涉及的代码可以在我的 GitHub 项目 AndroidBlogDemo 。
