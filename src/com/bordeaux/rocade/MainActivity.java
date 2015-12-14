package com.bordeaux.rocade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.bordeaux.rocade.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MainActivity.this.setTitle("Rocade Bordeaux");

		final Context myApp = this;
		setContentView(R.layout.activity_main);
		final Activity activity = this;

		CookieSyncManager.createInstance(myApp);
		CookieSyncManager.getInstance().sync();

		// Configuration de la WebView :
		WebSettings settings = getWebView().getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);

		final WebView webview = (WebView) getWebView();
		webview.setWebViewClient(new MyWebViewClient());
		webview.setWebChromeClient(new MyWebChromeClient());

		// Chargement de la page
		webview.loadUrl("http://hackjack.info/rocade/bordeaux");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final android.webkit.JsResult result) {
			Log.d("alert", message);
			Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT)
					.show();
			result.confirm();
			return true;
		}
	}

	public class MyWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (state == 0) {
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setMessage("Chargement de la carte");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
				state = 1;
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			pDialog.dismiss();
			pDialog.cancel();
			state = 0;
		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			view.loadUrl("file:///android_asset/error.html");

		}

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url != null && !url.startsWith("http://hackjack.info/rocade") && !url.startsWith("file:///android_asset/")) {
				Context context = getBaseContext();
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				return true;
			} else
			return false;
		}

		private ProgressDialog pDialog;
		private int state = 0;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		final WebView webview = (WebView) getWebView();
		switch (item.getItemId()) {
		case R.id.home:
			webview.loadUrl("http://hackjack.info/rocade/bordeaux");
			return true;
		case R.id.quit:
			finish();
			return true;
		case R.id.news:
			Intent Intent = new Intent(getBaseContext(),
					NewsActivity.class);
			startActivity(Intent);
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (getWebView().canGoBack() == true) {
					getWebView().goBack();
				} else {
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	private WebView getWebView() {

		WebView wvSite = (WebView) findViewById(R.id.webview);
		wvSite.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

		return wvSite;
	}

	public String getScriptContent(String filename) {
		try {
			AssetManager mngr = getBaseContext().getAssets();
			return getStringFromInputStream(mngr.open(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null)
				sb.append(line);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return sb.toString();
	}

}
