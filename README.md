# WebScraping_v2.0
<br/>
java网站爬虫v2.0，可以爬取笔趣读网站上小说的全部章节正文，输出为txt文本文件（自定义路径）

### 本版本更新内容:

1、采取多线程处理，大幅减少运行时间。经测试，在网络稳定的情况下，爬取两千章的小说大概耗时在10s左右。

2、用户接口处输入网址改成小说的章节列表地址，比如http://www.biqudu.com/0_32/ ，可自动从其中获取各个章节的url
```
<div id="list">
		<dl>	
                    <dt>《全职高手》第一卷</dt>                    
                    <dd> <a href="/0_32/1003682.html">第一章 被驱逐的高手</a></dd>
                    <dd> <a href="/0_32/1003683.html">第二章 C区47号</a></dd>
                    ...
		</dl>
</div>
```
经分析网页源码，小说各章节的url都放在`<div id="list">...</div>`标签中

### 使用说明:

1.仅限于爬取笔趣读网站上的小说http://www.biqudu.com/

2.用户接口在UserInterface这个类中，可输入小说章节列表的网址，txt文本保存的地址（注意转义）
```
public class UserInterface {
    public static void main(String[] args) {
        /* 小说章节列表的路径，格式为: "http://www.biqudu.com/" + 小说编号 */
        String url = "http://www.biqudu.com/0_168/";
        /* 要保存的文件路径 */
        String filePath = "//Users//scott//Downloads//择天记.txt";
        new WebScraping().run(url, filePath);
    }
}
```

3.笔趣读网站上小说章节目录的网址格式：http://www.biqudu.com/0_32/  （其中0_32是小说的编号）。

4.如果不能使用，可能是jar包未导入

