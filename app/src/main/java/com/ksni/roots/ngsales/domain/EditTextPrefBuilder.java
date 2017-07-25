package com.ksni.roots.ngsales.domain;

import android.content.Context;
import android.preference.EditTextPreference;

/**
 * Created by #roots on 20/12/2015.
 */
public class EditTextPrefBuilder {

    private final Context context;

    private String title;
    private String summary;
    private boolean enabled;

    public static EditTextPrefBuilder getBuilder(Context context) {
        return new EditTextPrefBuilder(context);
    }

    EditTextPreference ep = null;

    public EditTextPreference build() {
        ep = new EditTextPreference(context);
        ep.setTitle(title);
        ep.setSummary(summary);
        ep.setEnabled(enabled);
        return ep;

    }

    private EditTextPrefBuilder(Context context) {
        super();
        this.context = context;
        this.title = "";
        this.summary = "";
        this.enabled = false;
    }

    public EditTextPrefBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public EditTextPrefBuilder setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public EditTextPrefBuilder setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

}