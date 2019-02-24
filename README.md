##### 1、概述
&emsp;&emsp;Long Long Ago，由于当时技术有限，使用一些第三方库才能勉强能够实现这个效果，但是还是存在一些瑕疵，当进度很小的时候，会有溢出的情况，导致进度部分压住了装饰框，没有体现出圆角，显得非常丑陋。最近学习了clipPath方法，正好可以解决了这个问题。

![progress.gif](https://upload-images.jianshu.io/upload_images/3034670-0122c67fcde15794.gif?imageMogr2/auto-orient/strip)
##### 2、绘制流程分析
###### 2.1、绘制装饰框
1、left=装饰框画笔宽度的一半<br/>
2、top=装饰框画笔宽度的一半<br/>
3、right=控件宽度-装饰框画笔宽度的一半<br/>
4、bottom=控件高度-装饰框画笔宽度的一半<br/>
5、圆角半径radius=装饰框高度的一半<br/>
```
mProgressRect = new RectF(mDecoratorWidth + mProWidth / 2f,
                mDecoratorWidth + mProWidth / 2f,
                getMeasuredWidth() - mDecoratorWidth + mProWidth / 2f,
                getMeasuredHeight() - mDecoratorWidth - mProWidth / 2f);
```
```
  canvas.drawRoundRect(mDecoratorRect, 
                mDecoratorRect.height() / 2, 
                mDecoratorRect.height() / 2,
                mDecoratorPaint);
```
###### 2.2、绘制裁剪区和进度区
1、left=装饰框画笔宽度+进度条画笔宽度的一半<br/>
2、top=装饰框画笔宽度+进度条画笔宽度的一半<br/>
3、right=控件宽度-装饰框画笔宽度-进度条画笔宽度的一半<br/>
4、bottom=控件高度-装饰框画笔宽度-进度条画笔宽度的一半<br/>
5、圆角半径radius=进度区域高度的一半
```
 /**
     * 绘制裁剪区域和进度区域
     *
     * @param canvas
     */
    private void drawClipAreaAndProArea(Canvas canvas) {
        canvas.save();
        canvas.clipPath(mClipPath);
        mProgressRect.right = mDecoratorWidth + mProWidth / 2f + (getMeasuredWidth() - mDecoratorWidth * 2f - mProWidth) * (mCurPro / mTotalPro);
        canvas.drawRoundRect(mProgressRect, mProgressRect.height() / 2, mProgressRect.height() / 2, mProgressPaint);
        canvas.restore();
    }
```
###### 2.3、绘制文本
1、baseX=进度条区域宽度+装饰区域画笔宽度-文字区域的宽度-1
减10的目的是使文字与进度条有一定的间距<br/>
2、baseY=控件高度/2f+(fontMetrics.bottom-fontMetrics.top)-fontMetrics.bottom<br/>
3、fontMetrics的获取方法<br/>
&emsp;&emsp;获取fontMetrics之前，需要对mTextPaint对象进行初始化、设置文字大小和颜色等。
```
fontMetrics = mTextPaint.getFontMetrics();
```
```
 /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        BigDecimal percent = new BigDecimal(mCurPro * 100).divide(new BigDecimal(mTotalPro), 0, BigDecimal.ROUND_FLOOR);
        String mTextStr = percent + "%";
        mTextPaint.getTextBounds(mTextStr, 0, mTextStr.length(), mTextRect);
        /*进度条区域宽度大于文本区域宽度才可绘制*/
        if (mProgressRect.width() > mTextRect.width() + 10) {
            float max = mProgressRect.width() + mDecoratorWidth - 10;
            baseX = max - mTextRect.width();
            canvas.drawText(mTextStr, baseX, baseY, mTextPaint);
        }
    }
```
##### 3、提供一些方法
```
  public void setProgress(float curPro, float totalPro) {
        this.mTotalPro = totalPro;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "mCurPro", 0, curPro);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setDuration(1000);
        objectAnimator.start();

    }
```
##### 4、属性说明
|属性名|默认值|备注|
|:--:|:--:|:--:
|decorator_width|5|装饰框宽度|
|cur_pro|0|当前进度|
|total_pro|0|总进度|
|pro_color| Color.RED|进度条颜色|
|decorator_color|Color.BLACK|装饰框颜色|
|text_color|Color.WHITE|文本颜色|
|text_size|20|文字大小|
##### 5、总结
&emsp;&emsp;以前并不知道有clipPath这个方法，所以一直找不到解决方案，导致这样的结果归根结底还是因为基础薄弱、基础不扎实和缺乏源码阅读能力。源码阅读确实很枯燥，但是里边有很多值得我们学习的知识，和做人的道理是一模一样的，我们要做到取其精华去其糟粕，为我所用即可。


