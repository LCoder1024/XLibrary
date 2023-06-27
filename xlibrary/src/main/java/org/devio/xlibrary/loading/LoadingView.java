package org.devio.xlibrary.loading;

import android.content.Context;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

import org.devio.xlibrary.R;

public class LoadingView {
    private static LoadingPopupView loadingPopup;


    public static void startLoading(Context context) {
        startLoading(context, 0);
    }

    public static void startLoading(Context context, int layoutId) {
        if (loadingPopup == null) {
            loadingPopup = (LoadingPopupView) new XPopup.Builder(context).hasShadowBg(false).asLoading("加载中...", layoutId == 0 ?
                            R.layout.layout_loaidng : layoutId,
                    LoadingPopupView.Style.Spinner).show();
        } else {
            loadingPopup.show();
        }
    }

    public static void dismissLoading() {
        if (loadingPopup != null) {
            loadingPopup.dismiss();
            loadingPopup = null;
        }
    }
}
