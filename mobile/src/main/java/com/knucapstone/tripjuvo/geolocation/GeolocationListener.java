package com.knucapstone.tripjuvo.geolocation;

import android.location.Location;


public interface GeolocationListener
{
	void onGeolocationRespond(Geolocation geolocation, Location location);
	void onGeolocationFail(Geolocation geolocation);
}
