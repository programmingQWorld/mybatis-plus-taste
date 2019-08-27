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
    private static final String ESCAPE_CHARACTER = "`";
    private static final Set<String> KEYWORDS = new HashSet<String>();

    static {
        BufferedReader br = null;
        try {
            InputStream in = DBKeywordsProcessor.class.getClassLoader().getResourceAsStream("database_keywords.dic");
            br = new BufferedReader(new InputStreamReader(in));
            String keyword = null;
            while ((keyword = br.readLine()) != null) {
                KEYWORDS.add(keyword);
            }
        } catch (IOException e) {
            logger.warning("If you want to support the keyword query, must have database_keywords.dic \n"
                    + e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String convert(String keyword) {
        if (KEYWORDS.contains(keyword)) {
            return new StringBuffer().append(ESCAPE_CHARACTER).append(keyword).append(ESCAPE_CHARACTER).toString();
        }
        return keyword;
    }

}
