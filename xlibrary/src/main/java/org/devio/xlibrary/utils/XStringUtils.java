package org.devio.xlibrary.utils;

import android.text.TextUtils;

import androidx.annotation.Nullable;

/**
 * 工具类
 */
public class XStringUtils {

    /**
     * 字符串的处理，为空时默认返回空字符串
     *
     * @param str 待校验的字符串
     * @return 待校验字符串不为空时返回原字符串/为空时返回空字符串
     */
    public static String getStrEmpty(@Nullable String str) {
        if (str != null && !TextUtils.isEmpty(str) && !str.equalsIgnoreCase("null") && !("").equals(str)) {
            return str;
        }
        return "";
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 待校验的字符串
     * @return 空为true、非空为false
     */
    public static boolean isEmpty(String str) {
        return getStrEmpty(str).equals("");
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 相等为true、不相等为false
     */
    public static boolean isEquals(String str1, String str2) {

        if (isEmpty(str1) || isEmpty(str2)) {
            return false;
        }
        return str1.equals(str2);
    }


}
