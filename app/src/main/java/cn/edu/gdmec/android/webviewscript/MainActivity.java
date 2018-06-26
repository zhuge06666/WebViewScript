package cn.edu.gdmec.android.webviewscript;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
  private ProgressDialog mProgressDialog;
  private WebView mWebView1,mWebView2;
  private Button mButton1,mButton2,mButton3;
  private EditText et1,userName,pass;
  private TextView tv1;
  public class AndroidtoJs extends Object{
      @JavascriptInterface
      public void sayHi(String msg){
          Toast.makeText(MainActivity.this,"Javascript传来的数据是:"+msg,Toast.LENGTH_LONG).show();
      }
  }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressDialog=new ProgressDialog(this);
        mWebView1=findViewById(R.id.mWebview1);
        mWebView2=findViewById(R.id.mWebview2);
        mButton1=findViewById(R.id.button1);
        mButton2=findViewById(R.id.button2);
        mButton3=findViewById(R.id.login);
        et1=findViewById(R.id.editText1);
        tv1=findViewById(R.id.textView1);
        userName=findViewById(R.id.username);
        pass=findViewById(R.id.password);
        mWebView1.getSettings().setJavaScriptEnabled(true);
        mWebView2.getSettings().setJavaScriptEnabled(true);
        mWebView1.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView2.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView1.addJavascriptInterface(new AndroidtoJs(),"hybrid");
        mWebView1.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast.makeText(MainActivity.this,"javascript传来的数据是:"+message,Toast.LENGTH_LONG).show();
                return super.onJsAlert(view,url,message,result);
            }
        });
        //mWebView1.loadUrl("file://android_asset/lists.html");
        mWebView1.loadUrl("https://wrysj9ff.qcloud.la/lists.html");
        mWebView2.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String js="var script=document.createElement('script');\n"+
                        "script.type='text/javascript';\n"+
                        "var username=document.getElementById('username');\n"+
                        "username.value='"+userName.getText().toString()+"';\n"+
                        "var password=document.getElementById('password');\n"+
                        "password.value='"+pass.getText().toString()+"';\n"+
                        "var loginbtn=document.getElementById('btn-submit');\n"+
                        "loginbtn.click();";
                mWebView2.loadUrl("javascript:"+js);
                mProgressDialog.hide();
            }
        });
        mWebView1.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri=Uri.parse(url);
                if (uri.getScheme().equals("js")){
                    String num=uri.getQueryParameter("arg1");
                    Toast.makeText(MainActivity.this,"javascript传来的数据："+num,Toast.LENGTH_LONG).show();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String js=getFromAssets("injection.js");
                view.loadUrl("javascript:"+js);
                mProgressDialog.hide();
            }
        });
    mButton1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mWebView1.loadUrl("javascript:register('"+et1.getText().toString()+"')");
        }
    });
    mButton2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mWebView1.evaluateJavascript("javascript:register('" + et1.getText().toString() + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    tv1.setText("共"+s+"人注册");
                }
            });
        }
    });
    mButton3.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mWebView2.loadUrl("https://login.m.taobao.com/login.htm");
        }
    });
    }
    public String getFromAssets(String fileName){
        try {
            InputStreamReader inputStreamReader=new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String line="";
            String Result="";
            while ((line=bufferedReader.readLine())!=null)
                Result += line;
                return Result;

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
