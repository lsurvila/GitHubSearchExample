package com.lsurvila.githubsearchexample;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;

public class AndroidUtils {

    private final Resources resources;

    public AndroidUtils(@NonNull Resources resources) {
        this.resources = resources;
    }

    @NonNull
    public String getString(@StringRes int stringRes, Object... formatArgs) {
        return resources.getString(stringRes, formatArgs);
    }

    public boolean isStringEmpty(String text) {
        return TextUtils.isEmpty(text);
    }

}
