package com.example.maptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.odgnss.android.sdk.lib.client.LogoutClient;
import com.odgnss.android.sdk.lib.common.Constants;
import com.odgnss.android.sdk.lib.log.Logger;
import com.odgnss.android.sdk.lib.utils.ServiceUtil;
import com.odgnss.android.sdk.lib.utils.SetupUtil;
import android.content.ServiceConnection;
import org.apache.log4j.chainsaw.Main;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import com.odgnss.android.sdk.lib.share.ProfileInfo;
import com.odgnss.android.sdk.lib.data.ProfileResponseData;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Messenger;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.osmdroid.config.Configuration;
import java.util.ArrayList;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.odgnss.android.sdk.lib.service.CoreService;
import android.content.ServiceConnection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends AppCompatActivity implements LocationListener {

    // IMEI -- 國際行動裝置辨識碼，相當於手機的身分證
    // 1. Collect location data        app --> sdk
    // 2. Log Receiver                 sdk --> app
    // 3. Send location message        sdk --> server
    // 4. Responded message            server --> sdk
    // 5. Response Receiver            sdk --> app


    private Messenger coreMessager;
    private ServiceConnection mCoreServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder)
        {
            coreMessager = new Messenger(serviceBinder);
        }
        public void onServiceDisconnected(ComponentName name)
        {
            Logger.Log("onCoreServiceDisconnected()" + name.getClassName());
        }
    };


    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
//        map.setMinZoomLevel(1d);
//        map.setMaxZoomLevel(19d);
//        map.getTileProvider().getTileCache().setAutoEnsureCapacity(true);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(18);
        GeoPoint startPoint = new GeoPoint(22.28056, 114.17222);
        mapController.setCenter(startPoint);

        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this.getApplicationContext()), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.myLocationOverlay);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}