package com.example.wzh.test1;

import android.os.Parcelable;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;

/**
 * Created by wzh on 2017/6/20.
 */

public class NoUnderlineSpan extends UnderlineSpan{
    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
    }
}
