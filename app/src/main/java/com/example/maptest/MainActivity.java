package com.example.maptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.pm.PermissionInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.odgnss.android.sdk.lib.service.CoreService;

import android.content.ServiceConnection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends FragmentActivity implements LocationListener {
//public class MainActivity extends ActivityCompat implements LocationListener {


    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;


    // IMEI -- 國際行動裝置辨識碼，相當於手機的身分證
    // 1. Collect location data        app --> sdk
    // 2. Log Receiver                 sdk --> app
    // 3. Send location message        sdk --> server
    // 4. Responded message            server --> sdk
    // 5. Response Receiver            sdk --> app

    private Messenger coreMessager;
    private ServiceConnection mCoreServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
            coreMessager = new Messenger(serviceBinder);
        }

        public void onServiceDisconnected(ComponentName name) {
            Logger.Log("onCoreServiceDisconnected()" + name.getClassName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(3);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    return;
                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
                return;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if(location != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.e("Laaaaaa", String.valueOf(latitude));
                Log.e("Loooooo", String.valueOf(longitude));
            }
            locationManager.requestLocationUpdates(bestProvider, 5000, 0, this);
        }

        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this.getApplicationContext()), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.myLocationOverlay);

    }

    private void requestPermissionsIfNecessary(String[] strings) {
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("Latitude", "enabled");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("Latitude","disable");
    }


}