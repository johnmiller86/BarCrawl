package com.example.crawlapp.barcrawl;

class Crawl {

    public static final String TAG = Crawl.class.getSimpleName();
    public static final String TABLE = "crawls";
    public static final String KEY_CRAWL_ID = "crawlId";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_CRAWL_NAME = "crawl_name";
    public static final String KEY_CRAWL_DATE = "crawl_date";

    private String crawlName, crawlDate;


    public String getCrawlName() {
        return crawlName;
    }

    public void setCrawlName(String crawlName) {
        this.crawlName = crawlName;
    }

    public String getCrawlDate() {
        return crawlDate;
    }

    public void setCrawlDate(String crawlDate) {
        this.crawlDate = crawlDate;
    }
}