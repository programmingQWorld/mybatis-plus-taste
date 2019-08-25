package im.lincq.mybatisplus.taste.toolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lincq
 * @date 2019/8/26 01:11
 */
public class DBKeywordsProcessor {

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
            e.printStackTrace();
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
