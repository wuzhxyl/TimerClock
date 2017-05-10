package com.example.hlkhjk_ok.timer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hlkhjk_ok on 17/5/9.
 */

public class applistAdapater extends ArrayAdapter<appModelInfo> {
    private View currView;
    private int resID;
    private int position=-1;

    public applistAdapater(Context mContext, int resid, List<appModelInfo> infos) {
        super(mContext, resid, infos);
        this.resID = resid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resID, null);

            holder.appname = (TextView) view.findViewById(R.id.name);
            holder.view    = (ImageView) view.findViewById(R.id.icon);
            holder.selCtl  = (RadioButton) view.findViewById(R.id.sel);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        appModelInfo info = getItem(position);
        holder.appname.setText(info.getAppname());
        holder.selCtl.setChecked(info.isSel());
        holder.view.setImageBitmap(info.getIcon());

        if (info.isSel()) { this.position = position; }
        return view;
    }

    protected class ViewHolder {
        private ImageView view;
        private TextView appname;
        private RadioButton selCtl;
    }

    @Override
    public int getPosition(@Nullable appModelInfo item) {
        return super.getPosition(item);
    }

    public View getCurrView() {
        return currView;
    }

    public void setCurrView(View currView) {
        setSelected(false);
        this.currView = currView;
        setSelected(true);
    }

    public void setSelected(boolean isSel) {
        View view = getCurrView();
        if (view == null) { return ;}
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.selCtl.setChecked(isSel);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
