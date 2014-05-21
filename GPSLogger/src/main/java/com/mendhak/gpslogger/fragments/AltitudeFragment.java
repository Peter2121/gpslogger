/*
 *    This file is part of GPSLogger for Android.
 *
 *    Copyright Marc Poulhiès <dkm@kataplop.net>
 *
 *    GPSLogger for Android is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 2 of the License, or
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

package com.mendhak.gpslogger.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.mendhak.gpslogger.common.AppSettings;
import com.mendhak.gpslogger.common.Utilities;
import net.kataplop.gpslogger.R;


public class AltitudeFragment extends SherlockFragment implements  IWidgetFragment {
    private TextView tvAltitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.altitude_fragment, container, false);
        tvAltitude = (TextView) v.findViewById(R.id.big_altitude);

        return v;
    }


    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        IWidgetContainer c = (IWidgetContainer) getActivity();
        c.setTitle(getTitle(), this);
    }

    @Override
    public String getTitle() {
        String title = getString(R.string.altitude) + " (";

        if (AppSettings.shouldUseImperial()) {
            title += getString(R.string.feet) + ")";
        } else {
            title += getString(R.string.meters) + ")";
        }
        return title;
    }

    @Override
    public void onLocationChanged(final Location loc) {
        Utilities.LogDebug("AltitudeFragment.onLocationChanged");

        if (loc == null) {
            return;
        }

        setAltitude(loc);
    }

    private void setAltitude(final Location loc) {


        if (loc.hasAltitude()) {
            double altitude = loc.getAltitude();

            if (AppSettings.shouldUseImperial()) {
                altitude = altitude * 3.28084;
            }

            final String saltitude = Integer.toString((int)altitude);

            tvAltitude.setText(saltitude);
        } else {
            tvAltitude.setText(R.string.not_applicable);
        }
    }

    @Override
    public void setSatelliteInfo(int number) {
    }

    @Override
    public void setStatus(final String message) {
    }

    @Override
    public void clear() {
        tvAltitude.setText("-");
    }
}