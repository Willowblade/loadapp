package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.PI
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingProgress = 0.0f

    private var buttonBackgroundColor = 0
    private var buttonLoadingColor = 0
    private var circleColor = 0
    private var textColor = 0

    private val valueAnimator = ValueAnimator()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 40.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    private val path = Path()

    private val fullButtonRect = Rect(0, 0, width, height)
    private val ovalRect = RectF(0f, 0f, 30f, 30f)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        // TODO start valueAnimator from here I guess
    }


    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            buttonLoadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = buttonBackgroundColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.color = buttonLoadingColor
        canvas.drawRect(0f, 0f, loadingProgress * width, height.toFloat(), paint)
        paint.color = textColor
        canvas.drawText("Download LOL", width.toFloat() / 2, height.toFloat() / 2, paint)
        paint.color = circleColor
        canvas.drawArc(ovalRect, 90f, 270f, true, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val width: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        widthSize = width
        heightSize = height
        setMeasuredDimension(width, height)
    }

}