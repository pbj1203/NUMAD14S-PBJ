package edu.neu.madcourse.bojunpan.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
 
public class MyWebChromeClient extends WebChromeClient {
 
    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
    }
 
    @Override
    public boolean onCreateWindow(WebView view, boolean dialog,
            boolean userGesture, Message resultMsg) {
        return super.onCreateWindow(view, dialog, userGesture, resultMsg);
    }
 
    @Override
    public boolean onJsAlert(WebView view, String url, String message,
            JsResult result) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view
                .getContext());
        builder.setTitle("Guess Results").setMessage(
                message).setPositiveButton("OK", null);
        builder.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                Log.v("onJsAlert", "keyCode==" + keyCode + "event=" + event);
                return true;
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        result.confirm();
        return true;
    }
 
    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message,
            JsResult result) {
        return super.onJsBeforeUnload(view, url, message, result);
    }

 
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }
 
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }
 
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
    }
 
    @Override
    public void onRequestFocus(WebView view) {
        super.onRequestFocus(view);
    }
 
}
