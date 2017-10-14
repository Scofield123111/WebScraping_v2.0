package main;

import utils.GetResourceEncoding;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

class WebScraping {

    private static final int THREAD_AMOUNTS = 120; //创建的线程数

    /**
     * @param url      用户输入的章节目录的url
     * @param filePath 保存的文件路径
     */
    void run(String url, String filePath) {
        long lBefore = System.currentTimeMillis();

        /* 创建两个线程安全的队列，一个装输入，一个装输出 */
        ConcurrentLinkedQueue<NovelChapter> inputQueue = buildNCQueue(url);
        ConcurrentLinkedQueue<NovelChapter> resultQueue = new ConcurrentLinkedQueue<>();

        /* 创建线程 */
        int size = inputQueue.size();
        HtmlParseThread[] threads = new HtmlParseThread[THREAD_AMOUNTS];
        for (int i = 0; i < THREAD_AMOUNTS; i++) {
            threads[i] = new HtmlParseThread(Integer.toString(i), inputQueue, resultQueue, size);
            threads[i].start();
        }

        /* 主线程等待子线程结束 */
        for (int i = 0; i < THREAD_AMOUNTS; i++) {
            try {
                threads[i].join();
                System.out.println("线程" + i + "已结束");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long lAfter = System.currentTimeMillis();
        System.out.println("\r\n处理网页总计用时：" + (lAfter - lBefore) + "ms");

        /* 输出队列元素排序，并组装 */
        NovelChapter[] ncArr = new NovelChapter[resultQueue.size()];
        ncArr = resultQueue.toArray(ncArr);
        Arrays.sort(ncArr);
        StringBuilder sb = new StringBuilder();
        for (NovelChapter nc : ncArr
                ) {
            sb.append(nc.getContent());
        }

        /* 写入文件 */
        writeToFile(sb.toString(), filePath);

    }

    /**
     * 根据章节目录url，获取待处理队列
     *
     * @param url 用户输入的章节目录的url
     * @return 线程安全的待处理队列
     */
    private static ConcurrentLinkedQueue<NovelChapter> buildNCQueue(String url) {
        long lBefore = System.currentTimeMillis();

        String[] splitUrls = url.split("/");
        final String urlPrefix = "http://" + splitUrls[2];
        final String novelNumbering = splitUrls[3];//小说编号

        ConcurrentLinkedQueue<NovelChapter> ncQueue = new ConcurrentLinkedQueue<>();
        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.connect();

            System.out.println("源网页编码格式：" + GetResourceEncoding.getUrlEncode(realUrl));

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            boolean isBelongToList = false;//是否是HTML页面list标签内的内容
            int dtCounter = 0;//dt标签计数器
            int unNeededDtCounter = 0;//不需要的dt标签计数器
            String urlMatchString = "<a href=\"/" + novelNumbering + "/";

            String line;
            int chapterCounter = 0;
            while ((line = in.readLine()) != null) {
                if (line.contains("<div id=\"list\">")) { //匹配list标签
                    isBelongToList = true;
                }

                if (isBelongToList && line.contains("<dt>")) {
                    dtCounter++;
                    if (line.contains("最新章节") || line.contains("作品相关")) {
                        unNeededDtCounter++;
                    }
                }

                if (isBelongToList && dtCounter > unNeededDtCounter && line.contains(urlMatchString)) { //因为第一个<dt>标签是最新章节的段落，所以从第二个开始
                    chapterCounter++;
                    String chapterUrl = urlPrefix + line.substring(line.indexOf(novelNumbering) - 1, line.indexOf("\">"));
                    ncQueue.add(new NovelChapter(chapterCounter, chapterUrl));
                }
            }

        } catch (Exception e) {
            System.out.println("连接失败，请检查网络和输入网址是否正确！");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println("关闭BufferedReader失败！");
            }
        }


        long lAfter = System.currentTimeMillis();
        System.out.println("生成小说章节urlList用时：" + (lAfter - lBefore) + "ms");
        System.out.println("正在爬取小说正文，共" + ncQueue.size() + "章！请耐心等待\r\n\r\n");

        return ncQueue;
    }

    /**
     * 将结果写入文件
     *
     * @param result   解析完的字符串
     * @param filePath 保存的文件路径
     */
    private void writeToFile(String result, String filePath) {
        long lBefore = System.currentTimeMillis();

        File file = new File(filePath);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(result);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            System.out.println("文件路径不正确");
        }

        long lAfter = System.currentTimeMillis();
        System.out.println("写入文件用时：" + (lAfter - lBefore) + "ms");
        System.out.println("已成功将txt文本保存到: " + filePath + "  路径下！");
    }

}