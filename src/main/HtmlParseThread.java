package main;

import utils.RegexProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 网页解析线程类
 */
class HtmlParseThread extends Thread {
    private String threadName;
    private ConcurrentLinkedQueue<NovelChapter> inputQueue;
    private ConcurrentLinkedQueue<NovelChapter> resultQueue;
    private int totalSize;

    HtmlParseThread(String name, ConcurrentLinkedQueue<NovelChapter> inputQueue, ConcurrentLinkedQueue<NovelChapter> resultQueue, int size) {
        threadName = "线程" + name;
        this.inputQueue = inputQueue;
        this.resultQueue = resultQueue;
        totalSize = size;
    }

    @Override
    public void run() {
        System.out.println(threadName + "已启动");

        BufferedReader in = null;
        while (!inputQueue.isEmpty()) {
            NovelChapter nc = inputQueue.poll();

            String ncUrl = nc.getUrl();
            System.out.println(threadName + "开始处理：\t" + ncUrl);

            try {
                URL realUrl = new URL(ncUrl);
                URLConnection connection = realUrl.openConnection();
                connection.setConnectTimeout(20000);//超时时间20s
                connection.connect();

                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                nc.setContent(RegexProcess.parseString(sb.toString()));
                resultQueue.add(nc);
                System.out.println(threadName + "已处理完毕：\t" + ncUrl + "\t共计" + totalSize + "章，已处理" + resultQueue.size() + "章");

            } catch (Exception e) {
                System.out.println("连接超时，请检查网络");
            }
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
