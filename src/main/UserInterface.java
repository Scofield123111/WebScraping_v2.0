package main;

public class UserInterface {
    public static void main(String[] args) {
        /* 小说章节列表的路径，格式为: "http://www.biqudu.com/" + 小说编号 */
        String url = "http://www.biqudu.com/0_168/";
        /* 要保存的文件路径 */
        String filePath = "//Users//scott//Downloads//择天记.txt";
        new WebScraping().run(url, filePath);
    }
}
