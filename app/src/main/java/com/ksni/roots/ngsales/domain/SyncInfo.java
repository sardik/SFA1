package com.ksni.roots.ngsales.domain;

import android.util.Log;
import android.widget.ProgressBar;

/**
 * Created by #roots on 13/10/2015.
 */
public class SyncInfo {
    private final static String TAG = SyncInfo.class.getSimpleName();
    public enum SyncState {
        NOT_STARTED,
        QUEUED,
        SyncING,
        COMPLETE
    }
    private volatile SyncState mSyncState = SyncState.NOT_STARTED;
    private final String mFilename;
    private volatile Integer mProgress;
    private final Integer mFileSize;
    private volatile ProgressBar mProgressBar;

    public SyncInfo(String filename, Integer size) {
        mFilename = filename;
        mProgress = 0;
        mFileSize = size;
        mProgressBar = null;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }
    public void setProgressBar(ProgressBar progressBar) {
        Log.d(TAG, "setProgressBar " + mFilename + " to " + progressBar);
        mProgressBar = progressBar;
    }

    public void setSyncState(SyncState state) {
        mSyncState = state;
    }
    public SyncState getSyncState() {
        return mSyncState;
    }

    public Integer getProgress() {
        return mProgress;
    }

    public void setProgress(Integer progress) {
        this.mProgress = progress;
    }

    public Integer getFileSize() {
        return mFileSize;
    }

    public String getFilename() {
        return mFilename;
    }

}
