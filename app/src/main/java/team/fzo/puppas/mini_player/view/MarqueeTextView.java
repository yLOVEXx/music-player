package team.fzo.puppas.mini_player.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @Override
    public boolean isFocused() {
        return true;
    }
}