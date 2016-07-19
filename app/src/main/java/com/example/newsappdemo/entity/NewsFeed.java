package com.example.newsappdemo.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rana on 7/16/16.
 */
public class NewsFeed implements Parcelable
{
    private String mId;
    private int mCategoryId;
    private String mAritcleImage;
    private String mTitle;
    private String mArticleDetails;

    public NewsFeed(String id, int category, String articleImage, String title, String articleDetails)
    {
        this.mId = id;
        this.mCategoryId = category;
        this.mAritcleImage = articleImage;
        this.mTitle = title;
        this.mArticleDetails = articleDetails;
    }


    protected NewsFeed(Parcel in) {
        mCategoryId = in.readInt();
        mAritcleImage = in.readString();
        mTitle = in.readString();
        mArticleDetails = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCategoryId);
        dest.writeString(mAritcleImage);
        dest.writeString(mTitle);
        dest.writeString(mArticleDetails);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewsFeed> CREATOR = new Creator<NewsFeed>() {
        @Override
        public NewsFeed createFromParcel(Parcel in) {
            return new NewsFeed(in);
        }

        @Override
        public NewsFeed[] newArray(int size) {
            return new NewsFeed[size];
        }
    };

    public int getCategoryId() {
        return mCategoryId;
    }

    public String getTitleImage() {
        return mAritcleImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArticleDetails() {
        return mArticleDetails;
    }

    public String getId() {
        return mId;
    }
}
