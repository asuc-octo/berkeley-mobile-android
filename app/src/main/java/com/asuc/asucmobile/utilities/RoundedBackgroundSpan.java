package com.asuc.asucmobile.utilities;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;

public class RoundedBackgroundSpan extends ReplacementSpan {

    private final int mBackgroundColor;
    private final int mTextColor;
    private final float mCornerRadius;
    private final float mPadding;
    private final float mTextSize;

    public RoundedBackgroundSpan(int backgroundColor, int textColor, float cornerRadius, float padding, float textSize) {
        super();
        mBackgroundColor = backgroundColor;
        mTextColor = textColor;
        mCornerRadius = cornerRadius;
        mPadding = padding;
        mTextSize = textSize;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) (mPadding + paint.measureText(text.subSequence(start, end).toString()) + mPadding);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        float width = paint.measureText(text.subSequence(start, end).toString());
        RectF rect = new RectF(x - mPadding + 5, top - mPadding + 10,
                x + width + mPadding, bottom + mPadding);
        paint.setColor(mBackgroundColor);
        canvas.drawRoundRect(rect, mCornerRadius, mCornerRadius, paint);
        paint.setColor(mTextColor);
        paint.setTextSize(mTextSize);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(text, start, end, x + mPadding + 5, y, paint);
    }
}