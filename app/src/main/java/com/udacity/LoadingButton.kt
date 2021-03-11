package com.udacity

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
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

    private var animationDuration: Long = 2000

    private var completedListener: (() -> Unit)? = null

    private var valueAnimator: ValueAnimator = ValueAnimator()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textAlignment
        textSize = 40.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val pieSize = 50f
    private val pieOffset = 50f

    fun setCompletedListener(listener: () -> Unit) {
        completedListener = listener
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        // TODO start valueAnimator from here I guess
        println("Observed changing the button state from $old to $new")
        if (old != ButtonState.Loading && new == ButtonState.Loading) {
            if (valueAnimator.isRunning) {
                valueAnimator.cancel()
            }
            valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
                duration = animationDuration
                interpolator = LinearInterpolator()
                addUpdateListener {
                    loadingProgress = it.animatedValue as Float
                    invalidate()
                }
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {
                        println("Animation started")
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        println("Animation finished")
                        buttonState = ButtonState.Completed
                        invalidate()
                        completedListener?.invoke()
                    }

                    override fun onAnimationCancel(animator: Animator) {
                        println("Animation canceled")
                    }

                    override fun onAnimationRepeat(animator: Animator) {
                        println("Animation repeated")
                    }

                })
            }
            valueAnimator.start()
        }
        if (new == ButtonState.Completed) {
            if (valueAnimator.isRunning) {
                valueAnimator.cancel()
            }
            invalidate()
        }
    }

    fun setLoading() {
        animationDuration = 2000L
        buttonState = ButtonState.Loading
    }

    fun resetLoading() {
        animationDuration *= 2
        buttonState = ButtonState.Loading
    }

    fun setFinished() {
        buttonState = ButtonState.Completed
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
        if (buttonState == ButtonState.Loading) {
            paint.color = buttonLoadingColor
            canvas.drawRect(0f, 0f, loadingProgress * width, height.toFloat(), paint)
            paint.color = textColor
            canvas.drawText("Downloading...", width.toFloat() / 2, height.toFloat() / 2 - (paint.descent() + paint.ascent()) / 2, paint)
            val textWidth = paint.measureText("Downloading...") * 1.2f;
            paint.color = circleColor
            canvas.drawArc(
                    width.toFloat() / 2f + textWidth / 2,
                    height.toFloat() / 2f - pieSize / 2f,
                    width.toFloat() / 2f + textWidth / 2 + pieSize,
                    height.toFloat() / 2f + pieSize / 2f,
                    90f,
                    360f * loadingProgress,
                    true,
                    paint
            )
        } else {
            paint.color = textColor
            canvas.drawText("Push to download!", width.toFloat() / 2, height.toFloat() / 2 - (paint.descent() + paint.ascent()) / 2, paint)
        }
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