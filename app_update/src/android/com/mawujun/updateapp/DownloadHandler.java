package com.mawujun.updateapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import java.io.File;
import java.util.HashMap;

/**
 * Created by LuoWen on 2015/12/14.
 */
public class DownloadHandler extends Handler {
    private String TAG = "DownloadHandler";

    private Context mContext;
    /* 更新进度条 */
    private ProgressBar mProgress;
    /* 记录进度条数量 */
    private int progress;
    /* 下载保存路径 */
    private String mSavePath;
    /* 保存解析的XML信息 */
    private HashMap<String, String> mHashMap;
    private MsgHelper msgHelper;
    private AlertDialog mDownloadDialog;
    private com.mawujun.updateapp.MsgBox msgBox;

    public DownloadHandler(Context mContext, ProgressBar mProgress, AlertDialog mDownloadDialog, String mSavePath, HashMap<String, String> mHashMap) {
        this.msgHelper = new MsgHelper(mContext.getPackageName(), mContext.getResources());
        this.mDownloadDialog = mDownloadDialog;
        this.mContext = mContext;
        this.mProgress = mProgress;
        this.mSavePath = mSavePath;
        this.mHashMap = mHashMap;
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            // 正在下载
            case Constants.DOWNLOAD:
                // 设置进度条位置
                mProgress.setProgress(progress);
                break;
            case Constants.DOWNLOAD_FINISH:
                updateMsgDialog();
                // 安装文件
                installApk();
                break;
            default:
                showErrorDialog(msg.what);
                break;
        }
    }

    private void showErrorDialog(int msg){
        mDownloadDialog.dismiss();
        //msgBox = new com.mawujun.updateapp.MsgBox(mContext);
        //msgBox.showErrorDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(msgHelper.getString(MsgHelper.UPDATE_ERROR_TITLE));
        switch (msg) {
            case Constants.REMOTE_FILE_NOT_FOUND:
                builder.setMessage("下载的apk文件不存在!");
                break;
            case Constants.NETWORK_ERROR:
                builder.setMessage("网络错误!");
                break;
            default:
                builder.setMessage(msgHelper.getString(MsgHelper.UPDATE_ERROR_MESSAGE));
                break;
        }

        // 更新
        builder.setPositiveButton(msgHelper.getString(MsgHelper.UPDATE_ERROR_YES_BTN), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                // TODO Auto-generated method stub
                // finish();
            }
        });
        builder.create().show();
    }

    public void updateProgress(int progress) {
        this.progress = progress;
    }

    public void updateMsgDialog() {
        mDownloadDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE); //Update in background
        mDownloadDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.VISIBLE); //Install Manually
        mDownloadDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE); //Download Again

        mDownloadDialog.setTitle(msgHelper.getString(MsgHelper.DOWNLOAD_COMPLETE_TITLE));
        mDownloadDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
                .setOnClickListener(downloadCompleteOnClick);
    }

    private OnClickListener downloadCompleteOnClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            installApk();
        }
    };

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkFile = new File(mSavePath, mHashMap.get("name"));
        if (!apkFile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}
