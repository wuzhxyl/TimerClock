package com.example.hlkhjk_ok.timer;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by hlkhjk_ok on 17/5/9.
 */

public class appModelInfo {
    private String appname;
    private String pkg;
    private boolean isSel;
    private Bitmap icon;

    public appModelInfo(String appname, boolean isSel, Bitmap icon, String pkg) {
        this.appname = appname;
        this.isSel = isSel;
        this.icon = icon;
        this.pkg = pkg;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public boolean isSel() {
        return isSel;
    }

    public void setSel(boolean sel) {
        isSel = sel;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("pkg: ").append(pkg).append("appname: ").append(appname).append(" isSel: ").append(isSel).append(" Bitmap ").append(icon).toString();
    }
}
