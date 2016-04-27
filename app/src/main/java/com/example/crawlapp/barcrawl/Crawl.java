package com.example.crawlapp.barcrawl;

public class Crawl {

    public static final String TAG = Crawl.class.getSimpleName();
    public static final String TABLE = "crawls";
    public static final String KEY_CRAWL_ID = "crawlId";
    public static final String KEY_CRAWL_NAME = "crawl_name";
    public static final String KEY_CRAWL_DATE = "crawl_date";

    private String crawlId, crawlName, crawlDate;


    public String getCrawlId() {
        return crawlId;
    }

    public void setCrawlId(String crawlId) {
        this.crawlId = crawlId;
    }

    public String getCrawlName() {
        return crawlName;
    }

    public void setCrawlName(String crawlName) {
        this.crawlName = crawlName;
    }

    public String getCrawlDate() {
        return crawlDate;
    }

    public void setCrawlDate(String crawlDatea) {
        this.crawlDate = crawlDate;
    }
}