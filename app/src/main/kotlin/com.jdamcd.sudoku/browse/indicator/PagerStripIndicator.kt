package com.jdamcd.sudoku.browse.indicator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.jdamcd.sudoku.R
import com.jdamcd.sudoku.util.ViewUtil

class PagerStripIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : HorizontalScrollView(context, attrs, defStyle), PageIndicator {

    private val tabLayoutParams: LinearLayout.LayoutParams

    private var pager: ViewPager? = null
    private var listener: OnPageChangeListener? = null

    private val tabsContainer: LinearLayout

    private var tabCount: Int = 0
    private lateinit var rectPaint: Paint
    private var tabBackground: Int = 0
    private var tabTextColour = -0x99999a
    private val indicatorColours: IntArray
    private var tabTextSize = 12
    private var scrollOffset = 52
    private var indicatorHeight = 8
    private var tabPadding = 24

    private var currentPosition = 0
    private var currentPositionOffset = 0f
    private var lastScrollX = 0

    private var checkedTabWidths = false

    init {
        isFillViewport = true
        setWillNotDraw(false)

        tabsContainer = LinearLayout(context)
        tabsContainer.orientation = LinearLayout.HORIZONTAL
        tabsContainer.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(tabsContainer)

        initDimens()
        initPaints()
        initSystemAttributes(context, attrs)

        indicatorColours = resources.getIntArray(R.array.tab_colours)
        tabLayoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
    }

    private fun initDimens() {
        val dm = resources.displayMetrics
        scrollOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset.toFloat(), dm).toInt()
        indicatorHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight.toFloat(), dm).toInt()
        tabPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding.toFloat(), dm).toInt()
        tabTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize.toFloat(), dm).toInt()
    }

    private fun initPaints() {
        rectPaint = Paint()
        rectPaint.isAntiAlias = true
        rectPaint.style = Style.FILL
    }

    @SuppressLint("ResourceType")
    private fun initSystemAttributes(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, ATTRS)
        tabTextSize = a.getDimensionPixelSize(0, tabTextSize)
        tabTextColour = a.getColor(1, tabTextColour)
        tabBackground = a.getResourceId(2, tabBackground)
        a.recycle()
    }

    override fun setViewPager(view: ViewPager) {
        if (pager === view) {
            return
        }
        view.adapter ?: throw IllegalStateException("ViewPager does not have adapter instance.")
        this.pager = view
        view.addOnPageChangeListener(this)
        notifyDataSetChanged()
    }

    override fun setViewPager(view: ViewPager, initialPosition: Int) {
        setViewPager(view)
        setCurrentItem(initialPosition)
    }

    override fun setCurrentItem(item: Int) {
        currentPosition = item
        requestLayout()
    }

    override fun setOnPageChangeListener(listener: OnPageChangeListener) {
        this.listener = listener
    }

    @SuppressLint("NewApi")
    override fun notifyDataSetChanged() {
        tabsContainer.removeAllViews()

        tabCount = pager?.adapter!!.count
        for (i in 0 until tabCount) {
            addTextTab(i, pager?.adapter!!.getPageTitle(i).toString())
        }

        updateTabStyles()
        checkedTabWidths = false

        viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    currentPosition = pager!!.currentItem
                    scrollToChild(currentPosition, 0)
                }
            }
        )
    }

    private fun addTextTab(position: Int, title: String) {
        val tab = TextView(context)
        tab.text = title
        tab.gravity = Gravity.CENTER
        TextViewCompat.setTextAppearance(tab, R.style.TabText)
        tab.setOnClickListener { pager!!.currentItem = position }

        tabsContainer.addView(tab)
    }

    private fun updateTabStyles() {
        for (i in 0 until tabCount) {
            val v = tabsContainer.getChildAt(i)

            v.layoutParams = tabLayoutParams
            v.setBackgroundResource(R.drawable.tab_ripple)

            if (v is TextView) {
                v.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize.toFloat())
                v.setTextColor(tabTextColour)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            return
        }

        val myWidth = measuredWidth
        val childWidth = (0 until tabCount).sumBy { tabsContainer.getChildAt(it).measuredWidth }

        if (!checkedTabWidths && childWidth > 0 && myWidth > 0) {
            if (childWidth <= myWidth) {
                for (i in 0 until tabCount) {
                    tabsContainer.getChildAt(i).layoutParams = tabLayoutParams
                }
            }

            checkedTabWidths = true
        }
    }

    private fun scrollToChild(position: Int, offset: Int) {
        if (tabCount == 0) return

        var newScrollX = tabsContainer.getChildAt(position).left + offset

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX
            scrollTo(newScrollX, 0)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isInEditMode || tabCount == 0) return

        val currentTab = tabsContainer.getChildAt(currentPosition)
        var lineLeft = currentTab.left.toFloat()
        var lineRight = currentTab.right.toFloat()

        rectPaint.color = ViewUtil.blendColours(colourAtPosition(currentPosition), colourAtPosition(currentPosition + 1), currentPositionOffset)

        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
            // Start interpolating left and right coordinates between current and next tab
            val nextTab = tabsContainer.getChildAt(currentPosition + 1)
            val nextTabLeft = nextTab.left.toFloat()
            val nextTabRight = nextTab.right.toFloat()

            lineLeft = currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft
            lineRight = currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight
        }
        canvas.drawRect(lineLeft, (height - indicatorHeight).toFloat(), lineRight, height.toFloat(), rectPaint)
    }

    private fun colourAtPosition(position: Int): Int {
        return indicatorColours[position % indicatorColours.size]
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        currentPosition = position
        currentPositionOffset = positionOffset

        if (currentPositionOffset == 1.0f) {
            currentPosition++
            currentPositionOffset = 0f
        }

        scrollToChild(position, (positionOffset * tabsContainer.getChildAt(position).width).toInt())
        invalidate()

        listener?.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            scrollToChild(pager!!.currentItem, 0)
        }
        listener?.onPageScrollStateChanged(state)
    }

    override fun onPageSelected(position: Int) {
        listener?.onPageSelected(position)
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        currentPosition = savedState.currentPosition
        requestLayout()
    }

    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.currentPosition = currentPosition
        return savedState
    }

    private class SavedState : BaseSavedState {
        var currentPosition: Int = 0

        constructor(superState: Parcelable?) : super(superState)

        private constructor(input: Parcel) : super(input) {
            currentPosition = input.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(currentPosition)
        }

        companion object {
            @JvmField @Suppress("unused")
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(input: Parcel): SavedState {
                    return SavedState(input)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.textSize, android.R.attr.textColor, android.R.attr.selectableItemBackground)
    }
}
