package io.hefuyi.listener.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import io.hefuyi.listener.util.DensityUtil;

/**
 * Created by liyanju on 2017/9/13.
 */

public class CustomSwipeToRefresh extends SwipeRefreshLayout {

    private static final int SCROLL_BUFFER_DIMEN = 1;
    private static int scrollBuffer;
    private WebView webView;

    public CustomSwipeToRefresh(Context context) {
        super(context);
    }

    public CustomSwipeToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        View childView = getChildAt(0);
        if (childView != null && childView instanceof WebView) {
            webView = (WebView) childView;
        }
    }

    private void initializeBuffer() {
        scrollBuffer = DensityUtil.dip2px(getContext(), SCROLL_BUFFER_DIMEN);
    }

    @Override public void addView(View child) {
        super.addView(child);
        if (child instanceof WebView) this.webView = (WebView) child;
    }

    @Override
    public void addView(View child, LayoutParams params) {
        super.addView(child, params);
        if (child instanceof WebView) this.webView = (WebView) child;
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        if (child instanceof WebView) this.webView = (WebView) child;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
        if (child instanceof WebView) this.webView = (WebView) child;
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent event) {
        return webView != null && webView.getScrollY() <= scrollBuffer
                && super.onInterceptTouchEvent(event);
    }
}
