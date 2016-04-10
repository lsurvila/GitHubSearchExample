package com.lsurvila.githubsearchexample;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AndroidUtils {

    private final Resources resources;

    public AndroidUtils(@NonNull Resources resources) {
        this.resources = resources;
    }

    @NonNull
    public String getString(@StringRes int stringRes, Object... formatArgs) {
        return resources.getString(stringRes, formatArgs);
    }

    /**
     * Returns true if the string is null or 0-length.
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    public String getQueryFromUrl(String url, String key) {
        return Uri.parse(url).getQueryParameter(key);
    }

    public Scheduler getMainThread() {
        return AndroidSchedulers.mainThread();
    }

    public Scheduler getRunningThread() {
        return Schedulers.io();
    }

}
