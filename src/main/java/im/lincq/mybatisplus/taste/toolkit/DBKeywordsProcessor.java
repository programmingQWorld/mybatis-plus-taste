package im.lincq.mybatisplus.taste.toolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author lincq
 * @date 2019/8/26 01:11
 */
public class DBKeywordsProcessor {
    protected static final Logger logger = Logger.getLogger("DBKeywordsProcessor");
    private static Set<String> KEYWORDS = null;

    static {
        BufferedReader br = null;
        try {
            InputStream in = DBKeywordsProcessor.class.getClassLoader().getResourceAsStream("database_keywords.dic");
            br = new BufferedReader(new InputStreamReader(in));
            if (KEYWORDS == null) {
                KEYWORDS = new HashSet<String>();
            }
            String keyword = null;
            while ((keyword = br.readLine()) != null) {
                KEYWORDS.add(keyword);
            }
        } catch (IOException e) {
            logger.warning("If you want to support the keyword query, must have database_keywords.dic. \n"
                    + e.getMessage());
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * <p>
     * 数据库关键词加反引号
     * </p>
     *
     * @param keyword
     *            数据库关键词
     * @return
     */
    public static String convert(String keyword) {
        if (KEYWORDS.contains(keyword)) {
            return String.format("`%s`", keyword);
        }
        return keyword;
    }

}
