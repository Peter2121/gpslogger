/*
*    This file is part of GPSLogger for Android.
*
*    GPSLogger for Android is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    GPSLogger for Android is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
*/


package com.mendhak.gpslogger;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.mendhak.gpslogger.common.Utilities;
import net.kataplop.gpslogger.R;

import java.util.Locale;

public class Faqtivity extends SherlockActivity
{


    /**
     * Event raised when the form is created for the first time
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        Utilities.LogDebug("Faqtivity.onCreate");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.faq);

        String fileName = "help.html";
        WebView browser = (WebView)findViewById(R.id.faqwebview);
        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(false);
//        String deviceLocale=Locale.getDefault().getLanguage();
        String deviceLocale=getResources().getConfiguration().locale.getLanguage();
        Utilities.LogDebug("Got default locale: "+deviceLocale);
        switch(deviceLocale) {
            case "ru":
                fileName = "help_ru.html";
                break;
            case "fr":
                fileName = "help_fr.html";
                break;
            case "en":
                fileName = "help_en.html";
                break;
            default:
                break;
        }
        browser.loadUrl("file:///android_asset/"+fileName);

        // enable the home button so you can go back to the main screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Called when one of the menu items is selected.
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int itemId = item.getItemId();
        Utilities.LogInfo("Option item selected - " + String.valueOf(item.getTitle()));

        switch (itemId)
        {
            case android.R.id.home:
                Intent intent = new Intent(this, GpsMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
