package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则处理工具类
 */
public class RegexProcess {

    public static String parseString(String arg) {
        String beginStringRegex = "<div id=\"content\">";
        String endStringRegex = "</div>";

        Pattern p = Pattern.compile(beginStringRegex + ".+" + endStringRegex);
        Matcher m = p.matcher(arg);
        String content = "";
        while (m.find()) {
            content = arg.substring(m.start() + beginStringRegex.length() - 1, m.end() - endStringRegex.length());
        }
        Pattern p1 = Pattern.compile("<br/>\\s*");
        Matcher m1 = p1.matcher(content);
        content = m1.replaceAll("\r\n");

        Pattern p2 = Pattern.compile("&nbsp;");
        Matcher m2 = p2.matcher(content);
        content = m2.replaceAll(" ");

        Pattern p3 = Pattern.compile("<script>.*</script>");
        Matcher m3 = p3.matcher(content);
        content = m3.replaceAll("");

        return content;
    }

}
