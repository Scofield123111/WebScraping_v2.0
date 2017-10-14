package main;

/**
 * 小说章节实体类
 * 实现Comparable接口，按照章节序号排序
 */
class NovelChapter implements Comparable<NovelChapter> {
    private int index;//章节序号
    private String url;//章节链接地址
    private String content;//章节正文

    NovelChapter(int index, String url) {
        this.index = index;
        this.url = url;
    }

    String getUrl() {
        return url;
    }

    String getContent() {
        return content;
    }

    void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(NovelChapter another) {
        return Integer.compare(this.index, another.index);
    }

    @Override
    public String toString() {
        return "NovelChapter{" +
                "index=" + index +
                ", url='" + url + '\'' +
                '}';
    }

}
