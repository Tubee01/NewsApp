package com.example.android.newsapp;

public class NewsItem {

    private String mTitle;
    private String mSectionName;
    private String mWebURL;
    private String mCoverImagePath;

    public NewsItem(String title, String sectionName, String webURL, String coverImagePath) {
        this.mTitle = title;
        this.mSectionName = sectionName;
        this.mWebURL = webURL;
        this.mCoverImagePath = coverImagePath;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getWebURL() {
        return mWebURL;
    }

    public String getCoverImagePath() {
        return mCoverImagePath;
    }
}