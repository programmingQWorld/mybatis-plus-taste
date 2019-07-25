package im.lincq.mybatisplus.taste.toolkit;

public class StringUtils {

    /**
     * 判断字符串是否为空
     * @param str 需要判断字符串
     * @return 判断结果
     */
    public static boolean isEmpty (String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * 判断字符串是否不为空
     * @param str 需要判断字符串
     * @return 判断结果
     */
    public static boolean isNotEmpty (String str) {
        return (str != null) && !"".equals(str.trim());
    }

    private static final char UNDERLINE = '_';


    /**
     * 字符串驼峰转下划线格式
     * @param param 需要转换的字符串
     * @return  转换好的字符串
     */
    public static String camelToUnderline (String param) {
        if (isEmpty(param)) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
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
            return "";
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
}
