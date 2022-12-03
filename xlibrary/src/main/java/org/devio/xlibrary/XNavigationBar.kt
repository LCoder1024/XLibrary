package org.devio.xlibrary

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blankj.utilcode.util.SizeUtils

class XNavigationBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var navAttrs: Attrs
    private var titleView: TextView? = null
    private var titleContainer: LinearLayout? = null

    //左右按钮
    private var mLeftLastViewId = View.NO_ID
    private var mRightLastViewId = View.NO_ID
    private val mLeftViewList = ArrayList<View>()
    private val mRightViewList = ArrayList<View>()

    init {
        navAttrs = parseNavAttrs(context, attrs, defStyleAttr)

        if (!TextUtils.isEmpty(navAttrs.navTitle)) {
            setTitle(navAttrs.navTitle!!)
        }
    }

    fun setNavListener(listener: OnClickListener) {
        if (navAttrs.navIcon != 0) {
            val navBackView = addLeftImage(navAttrs.navIcon!!, R.id.id_nav_left_back_view)
            navBackView.setOnClickListener(listener)
        }
    }

    fun addLeftImage(resId: Int, viewId: Int): ImageView {
        val imageView = generateImageView()
        imageView.setImageResource(resId)
        imageView.id = viewId
        if (mLeftViewList.isEmpty()) {
            imageView.setPadding(navAttrs.horPadding * 2, 0, navAttrs.horPadding, 0)
        } else {
            imageView.setPadding(navAttrs.horPadding, 0, navAttrs.horPadding, 0)
        }
        addLeftView(imageView, generateImageViewLayoutParams())
        return imageView
    }

    private fun addLeftView(view: ImageView, params: LayoutParams) {
        val viewId = view.id
        if (viewId == View.NO_ID) {
            throw IllegalStateException("left view must has an unique id.")
        }
        if (mLeftLastViewId == View.NO_ID) {
            params.addRule(ALIGN_PARENT_LEFT, viewId)
        } else {
            params.addRule(RIGHT_OF, mLeftLastViewId)
        }
        mLeftLastViewId = viewId
        params.alignWithParent = true  //alignParentIfMissing
        mLeftViewList.add(view)
        addView(view, params)
    }

    fun addRightImage(resId: Int, viewId: Int): ImageView {
        val imageView = generateImageView()
        imageView.setImageResource(resId)
        imageView.id = viewId
        if (mRightViewList.isEmpty()) {
            imageView.setPadding(navAttrs.horPadding, 0, navAttrs.horPadding * 2, 0)
        } else {
            imageView.setPadding(navAttrs.horPadding, 0, navAttrs.horPadding, 0)
        }
        addRightView(imageView, generateImageViewLayoutParams())
        return imageView
    }

    private fun addRightView(view: ImageView, params: LayoutParams) {
        val viewId = view.id
        if (viewId == View.NO_ID) {
            throw java.lang.IllegalStateException("right view must has an unique id.")
        }
        if (mRightLastViewId == View.NO_ID) {
            params.addRule(ALIGN_PARENT_RIGHT, viewId)
        } else {
            params.addRule(LEFT_OF, mRightLastViewId)
        }
        mRightLastViewId = viewId
        params.alignWithParent = true
        mRightViewList.add(view)
        addView(view, params)
    }


    private fun generateImageViewLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
    }

    private fun generateImageView(): ImageView {

        return ImageView(context)

    }

    fun setTitle(title: String) {
        ensureTitleView()
        titleView?.text = title
        titleView?.visibility = if (TextUtils.isEmpty(title)) View.GONE else View.VISIBLE
    }

    private fun ensureTitleView() {
        if (titleView == null) {
            titleView = TextView(context, null)
            titleView?.apply {
                gravity = Gravity.CENTER
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
                setTextColor(navAttrs.navTextColor)
                updateTitleViewStyle()
                ensureTitleContainer()
                titleContainer?.addView(titleView, 0)
            }
        }
    }

    private fun ensureTitleContainer() {
        if (titleContainer == null) {
            titleContainer = LinearLayout(context)
            titleContainer?.apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER

                val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
                params.addRule(CENTER_IN_PARENT)
                this@XNavigationBar.addView(titleContainer, params)
            }
        }

    }

    private fun updateTitleViewStyle() {
        if (titleView != null) {
            titleView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, navAttrs.navTextSize)
            titleView?.typeface = Typeface.DEFAULT_BOLD
        }
    }

    private fun parseNavAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int): Attrs {
        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.navigationStyle, value, true)

        //xml-->theme.navigationStyle---navigationstyle
        val defStyleRes = if (value.resourceId != 0) value.resourceId else R.style.navigationStyle
        val array = context.obtainStyledAttributes(
            attrs,
            R.styleable.XNavigationBar,
            defStyleAttr,
            defStyleRes
        )
        val navIcon = array.getResourceId(R.styleable.XNavigationBar_nav_icon, 0)
        val navTitle = array.getString(R.styleable.XNavigationBar_nav_title)
        val horPadding = array.getDimensionPixelSize(R.styleable.XNavigationBar_hor_padding, 0)
        val navTextSize = array.getDimensionPixelSize(
            R.styleable.XNavigationBar_title_text_size,
            SizeUtils.sp2px(18f)
        )
        val navTextColor = array.getColorStateList(R.styleable.XNavigationBar_title_text_color)


        array.recycle()

        return Attrs(navIcon, navTitle, horPadding, navTextSize.toFloat(), navTextColor)
    }

    data class Attrs(
        val navIcon: Int,
        val navTitle: String?,
        val horPadding: Int,
        val navTextSize: Float,
        val navTextColor: ColorStateList?
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (titleContainer != null) {
            //计算出标题栏左侧已占用的空间
            var leftUseSpace = paddingLeft
            for (view in mLeftViewList) {
                leftUseSpace += view.measuredWidth
            }

            //计算出标题栏右侧已占用的空间
            var rightUseSpace = paddingRight
            for (view in mRightViewList) {
                rightUseSpace += view.measuredWidth
            }
            //这里只是他想要的宽度 500，300
            val titleContainerWidth = titleContainer!!.measuredWidth
            //为了让标题居中，左右空余距离一样
            val remainingSpace = measuredWidth - Math.max(leftUseSpace, rightUseSpace) * 2
            if (remainingSpace < titleContainerWidth) {
                val size =
                    MeasureSpec.makeMeasureSpec(remainingSpace, MeasureSpec.EXACTLY)
                titleContainer!!.measure(size, heightMeasureSpec)
            }
        }
    }
}