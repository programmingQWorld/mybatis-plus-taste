package im.lincq.mybatisplus.taste.toolkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * 下划线字符
     */
    public static final char UNDERLINE = '_';

    /**
     * 空字符串
     */
    public static String EMPTY_String = "";

    /**
     * 判断字符串是否为空
     *
     * @param str 需要判断字符串
     * @return 判断结果
     */
    public static boolean isEmpty(String str) {
        return str == null || EMPTY_String.equals(str.trim());
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 需要判断字符串
     * @return 判断结果
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    /**
     * 字符串驼峰转下划线格式
     *
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String camelToUnderline(String param) {
        if (isEmpty(param)) {
            return EMPTY_String;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            // && i > 0 可能是想要避免如 'IP' 这样的情况
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String underlineToCamel(String param) {
        if (isEmpty((param))) {
            return EMPTY_String;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * <p>判断字符串是否为纯大写字母</p>
     *
     * @param str 要匹配的字符串
     * @return boolean
     */
    public static boolean isUpperCase(String str) {
        return match("^[A-Z]+$", str);
    }


    /**
     * <p>正则表达式字符串</p>
     *
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str符合regex的正则表达式格式返回true，否则返回false.
     */
    public static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static String quotaMark(Object obj) {
        String srcStr = String.valueOf(obj);
        if (obj instanceof String && !srcStr.matches("/'(.+)/'")) {
            return "\'" + srcStr + "\'";
        }
        return srcStr;
    }

    public static String concatCapitalize(String concatStr, final String str) {

        if (isEmpty(concatStr)) {
            concatStr = EMPTY_String;
        }

        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return concatStr + Character.toUpperCase(firstChar) +str.substring(1);
    }

    public static String capitalize(final String str) {
        return concatCapitalize(null, str);
    }
}
