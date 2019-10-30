package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
//import com.google.android.material.navigation.NavigationView;
//import com.tomtom.core.maps.RenderedFeature;
import com.google.android.gms.location.LocationRequest;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.common.permission.AndroidPermissionChecker;
import com.tomtom.online.sdk.common.permission.PermissionChecker;
import com.tomtom.online.sdk.geofencing.GeofencingApi;
import com.tomtom.online.sdk.geofencing.ReportServiceResultListener;
import com.tomtom.online.sdk.geofencing.data.report.ReportServiceQuery;
import com.tomtom.online.sdk.geofencing.data.report.ReportServiceQueryBuilder;
import com.tomtom.online.sdk.geofencing.data.report.ReportServiceResponse;
import com.tomtom.online.sdk.location.LocationSource;
import com.tomtom.online.sdk.location.LocationSourceFactory;
import com.tomtom.online.sdk.map.BalloonViewAdapter;
import com.tomtom.online.sdk.map.BaseBalloonViewAdapter;
import com.tomtom.online.sdk.map.BaseMarkerBalloon;
import com.tomtom.online.sdk.map.CameraPosition;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.Marker;
import com.tomtom.online.sdk.map.MarkerBalloon;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.Polyline;
import com.tomtom.online.sdk.map.PolylineBuilder;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.map.RouteStyle;
import com.tomtom.online.sdk.map.SimpleMarkerBalloon;
import com.tomtom.online.sdk.map.SingleLayoutBalloonViewAdapter;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.ui.currentlocation.CurrentLocationView;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.data.FullRoute;
import com.tomtom.online.sdk.routing.data.RouteQuery;
import com.tomtom.online.sdk.routing.data.RouteQueryBuilder;
import com.tomtom.online.sdk.routing.data.RouteType;
import com.tomtom.online.sdk.routing.data.TravelTimeType;

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, com.tomtom.online.sdk.location.LocationUpdateListener {
    /*
    @Override
    public void onLocationChanged(Location location)
    {

        TextView koordynaty = findViewById(R.id.koordynaty);
        koordynaty.setText("koordynaty: "+map.getUserLocation());
        System.out.println("Say my location: "+map.getUserLocation());
    }
    */
    //public static final UUID = new UUID("");
    String nazwyPobliskichGlobalne = "";
    RouteBuilder trasaJedzenie;
    RouteBuilder trasaZobacz;
    RouteBuilder trasaBary;
    private LocationSource locationSource;
    private LatLng currentLocation;
    private static final int PERMISSION_REQUEST_LOCATION = 0;

    private void initCurrentLocation() {
        PermissionChecker permissionChecker = AndroidPermissionChecker.createLocationChecker(this);
        if(permissionChecker.ifNotAllPermissionGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, PERMISSION_REQUEST_LOCATION);
        }
        LocationSourceFactory locationSourceFactory = new LocationSourceFactory();
        locationSource = locationSourceFactory.createDefaultLocationSource(this, this,  LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(2000)
                .setInterval(5000));
        locationSource.activate();
        map.getUiSettings().getCurrentLocationView().hide();

    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION:
                if(grantResults.length >= 2 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    locationSource.activate();
                }
                else {
                    Toast.makeText(this, "Location permissions not granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    boolean pierwszeWejscie = true;
    MarkerBuilder meMarkerBuilder;
    LatLng currentPosition;
    ArrayList<LatLng> koordynaty = new ArrayList<LatLng>();
    @Override
    public void onLocationChanged(final Location location) {

        Drawable tyDraw = getDrawable(R.drawable.ty);
        Icon tym = Icon.Factory.fromDrawable("ty", tyDraw, 0.2);
        currentLocation = new LatLng(location);
        LatLng mojeLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        System.out.println(location);
        if(!pierwszeWejscie)
            map.removeMarkerByTag("ja");
        pierwszeWejscie = false;

        if(koordynaty.isEmpty()){
            koordynaty.add(mojeLatLng);
            System.out.println("Wejscie do metody 2");
        }
        meMarkerBuilder = new MarkerBuilder(mojeLatLng);
        System.out.println(map);
        map.addMarker(meMarkerBuilder.tag("ja").icon(tym));
        koordynaty.add(mojeLatLng);
        if(((koordynaty.get(0).getLatitude()-koordynaty.get(1).getLatitude())>0.00008||(koordynaty.get(0).getLongitude()-koordynaty.get(1).getLongitude())>0.00008)||(((koordynaty.get(0).getLatitude()-koordynaty.get(1).getLatitude())<-0.00008||(koordynaty.get(0).getLongitude()-koordynaty.get(1).getLongitude())<-0.00008))) {
            Polyline linia = PolylineBuilder.create()
                    .coordinates(koordynaty)
                    .color(Color.rgb(255,199,54))
                    .width(8)
                    .build();
            map.getOverlaySettings().addOverlay(linia);
            koordynaty.add(0, koordynaty.get(1));
            koordynaty.remove(1);
            System.out.println("NARYSOWAŁEM");
            System.out.println("NARYSOWAŁEM");
            System.out.println("NARYSOWAŁEM");
            System.out.println("NARYSOWAŁEM");
        }
        System.out.println(koordynaty.get(0)+"- KOORDYNAT 1 &&&&&&&&&&&&&&& "+koordynaty.get(1)+" KOORDYNAT 2");
        koordynaty.remove(1);
        GeofencingApi geofencingApi = GeofencingApi.create(this);
        ReportServiceQuery query = ReportServiceQueryBuilder.create(currentLocation.toLocation())
                .withProject(PROJEKT)
                .withRange((float) 100000.0)
                .withObject(OBJECT)
                .build();
        geofencingApi.obtainReport(query,resultListener);

        System.out.println("UDALO SIE!!!!!!!!!!!");
    }

    private TomtomMap map;
    private static final UUID PROJEKT = UUID.fromString("0b563ec0-15f4-4b00-b524-2fc3a5a7ca64");
    private static final UUID OBJECT = UUID.fromString("d184b3a5-eb38-4174-8597-241f18e238ed");
    ArrayList<LatLng> listaLokaliJedzenie = new ArrayList<LatLng>();
    ArrayList<LatLng> listaDoZobaczenia = new ArrayList<LatLng>();
    ArrayList<LatLng> listaBarow = new ArrayList<LatLng>();
    String czyJestTraffic = "nieMa";
   // Location lokacja = map.getUserLocation();
    //JEDZENIE !!!
    String opisyJedzenie[] = {"Skosztuj piw z całego świata!","Nowoczesna kuchnia w przystępnej cenie","Najlepsza kuchnia włoska w całym Rzeszowie","Spróbuj najlepszego kebaba w Rzeszowie!","Włoskie pizze i makaron"};
    String nazwyJedzenie[] = {"Piwa Świata", "KukNuk", "Osterai Bellannuna", "Dara Kebab", "Pizza e Pasta"};
    LatLng piwaSwiata = new LatLng(50.036881, 22.006207);
    LatLng kukNuk = new LatLng(50.038130, 22.003789);
    LatLng osteriaBellannuna = new LatLng(50.038412, 21.999612);
    LatLng daraKebab = new LatLng(50.039861, 22.002863);
    LatLng PizzaEPasta = new LatLng(50.029875, 22.017859);
    //JEDZENIE!!!
    //ZOBACZ!!
    String opisyZobacz[] = {"Główny budynek uniwersytetu","Pomnik upmiętnijący czyn rewolcyjny","Stadion - wart zobaczenia","Piękny zamek idealny do zwiedzania","Galeria - idealna do spędzenia wolnego czasu"};
    String nazwyZobacz[] = {"Uniwersytet Rzeszowski", "Pomnik Czynu R.", "Stadion Stal", "Zamek Lubomirskich", "Galeria Rzeszów"};
    LatLng uniwersytetRzeszowski = new LatLng(50.030625, 22.014375);
    LatLng pomnikCzynuRewolucyjnego = new LatLng(50.040985, 21.999444);
    LatLng stadionStal = new LatLng(50.022300, 21.996178);
    LatLng zamekLubomirskich = new LatLng(50.032607, 22.000248);
    LatLng galeriaRzeszow = new LatLng(50.042461, 21.998271);
    //ZOBACZ!!
    //BARY!!!!!!
    String opisyBary[] = {"Zagraj na pegazusie i napij się piwa","Pograj w planszówki, gry na konsole i nie tylko!","Spędź miło wieczór w przytulnym miejscu","Browar z dobrmy piwem"};
    String nazwyBary[] = {"Corner Pub Rzeszów","Cybermachina","HokusPokus","Manufaktura"};
    LatLng cornerPubRzeszow = new LatLng(50.037118, 22.005686);
    LatLng cybermachina = new LatLng(50.038123, 22.003853);
    LatLng hokusPokus = new LatLng(50.036220, 22.001335);
    LatLng manufaktura = new LatLng(50.036338, 22.003245);

    //BARY!!!!!!
    View inflatedLayout;
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.map.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/
    TextView wPoblizu;

    @Override
    public void onMapReady(@NonNull TomtomMap tomtomMap) {
        this.map = tomtomMap;
        //listaLokaliJedzenie.add(piwaSwiata);
        wPoblizu = findViewById(R.id.textView3);
        map.getMarkerSettings().setMarkerBalloonViewAdapter(createCustomViewAdapter());
        wPoblizu.setVisibility(View.INVISIBLE);

        //map.setMyLocationEnabled(true);

        System.out.println(map.getUiSettings().isMyLocationEnabled()+"Czy jest enabled!!!!!!!!");
        System.out.println(map.getUserLocation()+"Tu zaczynamy");

        //System.out.println(lokacja);

        System.out.println(map.getUserLocation());
        System.out.println(map.getUserLocation());
        System.out.println(tomtomMap.getUserLocation());
        Location location = map.getUserLocation();//for each na 3 listach!!
        Location lokacjaUrz = new Location(uniwersytetRzeszowski.toLocation());
        Location lokacjaStadionStal = new Location(stadionStal.toLocation());
        //50.036881, 22.006207
        inflatedLayout = LayoutInflater.from(MainActivity.this).inflate(R.layout.jedzenie_balloon,null, false);

        TextView tekst = (TextView)inflatedLayout.findViewById(R.id.textView);
        TextView tekst2 = (TextView)inflatedLayout.findViewById(R.id.textView2);
        tekst.setText("Świat piw");
        tekst2.setText("Miejsce do zakupu wszelakich piw kraftowych i nie tylko!");

        System.out.println(tekst.getText());

        RoutingApi routingApi = OnlineRoutingApi.create(getApplicationContext());
        ArrayList<RouteQuery> routeQueries = new ArrayList<RouteQuery>();
        for(int i=0; i<4; i++)
           routeQueries.add(new RouteQueryBuilder(listaLokaliJedzenie.get(i), listaLokaliJedzenie.get(i+1)).withRouteType(RouteType.FASTEST).build());
        routeQueries.add(new RouteQueryBuilder(listaLokaliJedzenie.get(4), listaLokaliJedzenie.get(0)).withRouteType(RouteType.FASTEST).build());
        /*for(int i=0; i<4; i++)
            routeQueries.add(new RouteQueryBuilder(listaDoZobaczenia.get(i), listaDoZobaczenia.get(i+1)).withRouteType(RouteType.FASTEST).build());
        *///routeQueries.add(new RouteQueryBuilder(listaDoZobaczenia.get(4), listaDoZobaczenia.get(0)).withRouteType(RouteType.FASTEST).build());
       /* for(int i=0; i<3; i++)
            routeQueries.add(new RouteQueryBuilder(listaBarow.get(i), listaBarow.get(i+1)).withRouteType(RouteType.FASTEST).build());
        routeQueries.add(new RouteQueryBuilder(listaBarow.get(3), listaBarow.get(0)).withRouteType(RouteType.FASTEST).build());
        */

        ArrayList<LatLng> listaJedzenieWayPoints = new ArrayList<LatLng>();
        listaJedzenieWayPoints.addAll(listaLokaliJedzenie);
        //listaJedzenieWayPoints.remove(4);
        listaJedzenieWayPoints.remove(0);
        LatLng[] jedzenie = listaJedzenieWayPoints.toArray(new LatLng[listaJedzenieWayPoints.size()]);

        ArrayList<LatLng> listaBarWayPoints = new ArrayList<LatLng>();
        listaBarWayPoints.addAll(listaBarow);
        listaBarWayPoints.remove(0);
        LatLng[] bary = listaBarWayPoints.toArray(new LatLng[listaBarWayPoints.size()]);

        ArrayList<LatLng> listaZobaczWayPoints = new ArrayList<LatLng>();
        listaZobaczWayPoints.addAll(listaDoZobaczenia);
        listaZobaczWayPoints.remove(0);
        LatLng[] zobacz = listaZobaczWayPoints.toArray(new LatLng[listaZobaczWayPoints.size()]);

        RouteQuery routeQueryJedzenie = RouteQueryBuilder.create(listaLokaliJedzenie.get(0), listaLokaliJedzenie.get(0))
                .withWayPoints(jedzenie)
                .withComputeBestOrder(true)
                .withConsiderTraffic(false).build();

        RouteQuery routeQueryBary = RouteQueryBuilder.create(listaBarow.get(0), listaBarow.get(0))
                .withWayPoints(bary)
                .withComputeBestOrder(true)
                .withConsiderTraffic(false).build();

        RouteQuery routeQueryZobacz = RouteQueryBuilder.create(listaDoZobaczenia.get(0), listaDoZobaczenia.get(0))
                .withComputeTravelTimeFor(TravelTimeType.ALL)
                .withWayPoints(zobacz)
                .withComputeBestOrder(true)
                .withConsiderTraffic(true).build();


        System.out.println(map.getUiSettings().getTrafficRasterFlowStyle()+"jkhjjkjkjh");
        Button btnTraffic = findViewById(R.id.btnTraffic);
        btnTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(czyJestTraffic.equals("nieMa"))
                {
                    map.getUiSettings().turnOnRasterTrafficIncidents();
                    map.getUiSettings().turnOnRasterTrafficFlowTiles();
                    Location location2 = map.getUserLocation();
                    System.out.println("WSZEDŁEM!!!!!   "+location2);
                    System.out.println(map.getUiSettings().getTrafficRasterFlowStyle()+"jkhjjkjkjh");
                    sayMyLocation();
                    czyJestTraffic = "jest";
                    btnTraffic.setTextColor(Color.rgb(255,131,0));
                }else
                {
                    map.getUiSettings().turnOffTraffic();
                    btnTraffic.setTextColor(Color.rgb(255,255,255));
                    System.out.println("WSZEDŁEM 2!!!!!");
                    czyJestTraffic = "nieMa";
                }
            }
        });


        routingApi.planRoute(routeQueryJedzenie)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(routeResult -> {
                    for (FullRoute fullRoute : routeResult.getRoutes()) {
                        RouteBuilder routeBuilder = new RouteBuilder(
                                fullRoute.getCoordinates());
                        //map.addRoute(routeBuilder);
                        trasaJedzenie = routeBuilder;
                    }
                });
        routingApi.planRoute(routeQueryBary)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(routeResult -> {
                    for (FullRoute fullRoute : routeResult.getRoutes()) {
                        RouteBuilder routeBuilder = new RouteBuilder(
                                fullRoute.getCoordinates());
                        //map.addRoute(routeBuilder);
                        trasaBary = routeBuilder;
                    }
                });
        routingApi.planRoute(routeQueryZobacz)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(routeResult -> {
                    for (FullRoute fullRoute : routeResult.getRoutes()) {
                        RouteBuilder routeBuilder = new RouteBuilder(
                                fullRoute.getCoordinates());
                        //map.addRoute(routeBuilder);
                        trasaZobacz = routeBuilder;
                    }
                });

        ImageButton btnJedzenie = findViewById(R.id.imageButton);
        ImageButton btnZobacz = findViewById(R.id.imageButton2);
        ImageButton btnBary = findViewById(R.id.imageButton3);

        map.clearRoute();

        btnJedzenie.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                btnBary.setImageResource(R.drawable.ic_local_bar_black_24dp);
                btnJedzenie.setImageResource(R.drawable.jedzenie_wektorowe);
                btnZobacz.setImageResource(R.drawable.ic_local_see_black_24dp);
                //btnJedzenie.setBackground(getDrawable(R.drawable.jedzenie_wcisniete));
                //btnJedzenie.setBackgroundResource(R.drawable.jedzenie_wcisniete);
                //btnZobacz.setBackgroundResource(R.drawable.ikonka_zobacz););
                //btnBary.setBackgroundResource(R.drawable.ikonka_bar);

                //btnBary.setBackground(getDrawable(R.drawable.ikonka_bar));
                //btnZobacz.setBackground(getDrawable(R.drawable.ikonka_zobacz));
                map.clearRoute();
                map.addRoute(trasaJedzenie);
            }
        });
        wPoblizu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                wPoblizu.setVisibility(View.INVISIBLE);
            }
        });
        btnZobacz.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //btnZobacz.setBackground(getDrawable(R.drawable.zobacz_wcisniete));
                //btnBary.setBackground(getDrawable(R.drawable.ikonka_bar));
                //btnJedzenie.setBackground(getDrawable(R.drawable.ikonka_jedzenie));
                //btnJedzenie.setBackgroundResource(R.drawable.ikonka_jedzenie);
                //btnZobacz.setBackgroundResource(R.drawable.ikonka_zobacz);
                //btnBary.setBackgroundResource(R.drawable.bary_wcisniete);
                btnBary.setImageResource(R.drawable.ic_local_bar_black_24dp);
                btnJedzenie.setImageResource(R.drawable.ic_restaurant_black_24dp);
                btnZobacz.setImageResource(R.drawable.zobacz_wektorowe);
                map.clearRoute();
                map.addRoute(trasaZobacz);
            }
        });

        btnBary.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //btnBary.setBackground(getDrawable(R.drawable.bary_wcisniete));
                //btnJedzenie.setBackground(getDrawable(R.drawable.ikonka_jedzenie));
                //btnZobacz.setBackground(getDrawable(R.drawable.ikonka_zobacz));
                //btnJedzenie.setBackgroundResource(R.drawable.ikonka_jedzenie);
                //btnZobacz.setBackgroundResource(R.drawable.ikonka_zobacz);
                //btnBary.setBackgroundResource(R.drawable.zobacz_wcisniete);
                btnBary.setImageResource(R.drawable.bary_wektorowe);
                btnJedzenie.setImageResource(R.drawable.ic_restaurant_black_24dp);
                btnZobacz.setImageResource(R.drawable.ic_local_see_black_24dp);
                map.clearRoute();
                map.addRoute(trasaBary);
            }
        });

        Drawable draw = getDrawable(R.drawable.ikonka_jedzenie);
        Drawable draw2 = getDrawable(R.drawable.ikonka_bar);
        Drawable draw3 = getDrawable(R.drawable.ikonka_zobacz);
        SingleLayoutBalloonViewAdapter balloonViewAdapter = new SingleLayoutBalloonViewAdapter(R.layout.jedzenie_balloon);

        //tekst = (TextView)balloonViewAdapter.getBalloonView().findViewById(R.id.textView);
        //tekst.setText("Świat piw");

       // tomtomMap.getMarkerSettings().setMarkerBalloonViewAdapter(new SingleLayoutBalloonViewAdapter(R.layout.jedzenie_balloon));
        BaseMarkerBalloon baseMarkerBalloon = new BaseMarkerBalloon();
        //baseMarkerBalloon.setText("Piwa świata - miejsce w którym zakupisz przeróżne piwa kraftowe i nie tylko!");

        //baseMarkerBalloon.addProperty("textView#text","popl");

        //System.out.println("!!!!!!!!!!!!TUTAJ!!!!!!!!!!!!!!!!");
        //baseMarkerBalloon.
        //System.out.println(baseMarkerBalloon.);
        //System.out.println(baseMarkerBalloon.getStringProperty("textView"));
        //System.out.println("!!!!!!!!!!!!TUTAJ!!!!!!!!!!!!!!!!");
        String address = "Adres";
        String poiName = "Opis wiekszy niz adres";

        //BaseMarkerBalloon markerBalloonData = new BaseMarkerBalloon();
        Icon ikonkaJedzenie = Icon.Factory.fromDrawable("res", draw, 0.5);
        Icon ikonkaZobaz = Icon.Factory.fromDrawable("res2",draw3,0.5);
        Icon ikonkaBar = Icon.Factory.fromDrawable("res3", draw2, 0.5);
        int iterator = 0;
        for(LatLng latlng : listaLokaliJedzenie)
        {
            address = nazwyJedzenie[iterator];
            poiName = opisyJedzenie[iterator];
            iterator++;
            BaseMarkerBalloon markerBalloonData = new BaseMarkerBalloon();
            markerBalloonData.addProperty(getString(R.string.poi_name_key), poiName);
            markerBalloonData.addProperty(getString(R.string.address_key), address);
            markerBalloonData.addProperty("obrazek", R.drawable.ic_restaurant_black_24dp);
            tomtomMap.addMarker(new MarkerBuilder(latlng)
                    .icon(ikonkaJedzenie)
                    .markerBalloon(markerBalloonData));

        }
        iterator = 0;
        for(LatLng latlng : listaBarow)
        {
            System.out.println(nazwyBary[iterator]+"TO JEST NAZWA BARU!!!!!!!!!!!!!");
            address = nazwyBary[iterator];
            poiName = opisyBary[iterator];
            iterator++;
            BaseMarkerBalloon markerBalloonData = new BaseMarkerBalloon();
            markerBalloonData.addProperty(getString(R.string.poi_name_key), poiName);
            markerBalloonData.addProperty(getString(R.string.address_key), address);
            markerBalloonData.addProperty("obrazek", R.drawable.ic_local_bar_black_24dp);
            tomtomMap.addMarker(new MarkerBuilder(latlng)
                    .icon(ikonkaBar)
                    .markerBalloon(markerBalloonData));

        }
        iterator = 0;
        for(LatLng latlng : listaDoZobaczenia)
        {
            address = nazwyZobacz[iterator];
            poiName = opisyZobacz[iterator];
            iterator++;
            BaseMarkerBalloon markerBalloonData = new BaseMarkerBalloon();
            markerBalloonData.addProperty(getString(R.string.poi_name_key), poiName);
            markerBalloonData.addProperty(getString(R.string.address_key), address);
            markerBalloonData.addProperty("obrazek", R.drawable.ic_local_see_black_24dp);
            tomtomMap.addMarker(new MarkerBuilder(latlng)
                    .icon(ikonkaZobaz)
                    .markerBalloon(markerBalloonData));


        }
        //MarkerBuilder markerBuilder = new MarkerBuilder(piwaSwiata)
          //      .icon(Icon.Factory.fromDrawable("res",draw, 0.5))
            //    .markerBalloon(baseMarkerBalloon);
        //tomtomMap.addMarker(markerBuilder);



        LatLng rzeszow = new LatLng(50.036951, 22.004251);
        //SimpleMarkerBalloon balloon = new SimpleMarkerBalloon("Amsterdam");
        //tomtomMap.addMarker(new MarkerBuilder(rzeszow).markerBalloon(balloon));
        tomtomMap.centerOn(CameraPosition.builder(rzeszow).zoom(12).build());
        System.out.println("Jestem 238");
        //map.getUiSettings().turnOnRasterTrafficIncidents();
        //map.getUiSettings().turnOnRasterTrafficFlowTiles();

        if(location!=null) {
            GeofencingApi geofencingApi = GeofencingApi.create(this);
            ReportServiceQuery query = ReportServiceQueryBuilder.create(location)
                    .withProject(PROJEKT)
                    .withRange((float) 75.0)
                    .build();
            geofencingApi.obtainReport(query,resultListener);
            System.out.println("UDALO SIE!!!!!!!!!!!");
        }
        System.out.println(location);
        //map.centerOnMyLocation();
        sayMyLocation();

        //map.getUserLocation() = currentLocation;
        //map.getUiSettings().getCurrentLocationView().setOnMapComponentClickCallback();
        initCurrentLocation();

    }
    public void sayMyLocation()
    {
        System.out.println("Say my location: "+map.getUserLocation());
    }

    private ReportServiceResultListener resultListener = new ReportServiceResultListener() {

        @Override
        public void onResponse(@NonNull ReportServiceResponse response) {
            System.out.println("JESTEM WEWNATRZ1");
            System.out.println("JESTEM WEWNATRZ2");
            System.out.println("JESTEM WEWNATRZ3");
            System.out.println("JESTEM WEWNATRZ4");
            System.out.println("JESTEM WEWNATRZ5");
            System.out.println("JESTEM WEWNATRZ6");
            System.out.println("JESTEM WEWNATRZ7");
            System.out.println(response);
            String nazwa = "";
            String nazwyPobliskich = "";
            String responseString = response.toString();
            int indexOfName = responseString.indexOf("name");
            System.out.println(indexOfName);
            int indexOfOutside = responseString.indexOf("outside");

            while(indexOfName<indexOfOutside&&indexOfName!=-1)
            {
                int i = 6;
                char wybranyCharacter = responseString.charAt(indexOfName+i);
                while(wybranyCharacter!='\'')
                {
                    nazwa = nazwa + wybranyCharacter;
                    i++;
                    wybranyCharacter = responseString.charAt(indexOfName+i);
                }
                indexOfName = responseString.indexOf("name",indexOfName+5);
                System.out.println(indexOfName);
                switch(nazwa)
                {
                    case "piwaSwiata": nazwyPobliskich+="\"Piwa Świata\""; break;
                    case "kukNuk" : nazwyPobliskich+="\"KukNuk\""; break;
                    case "osteriaBellannuna" : nazwyPobliskich+= "\"Osteria Bellannuna\""; break;
                    case "daraKebab" : nazwyPobliskich += "\"Dara Kebab\""; break;
                    case "PizzaEPasta" : nazwyPobliskich += "\"PizzaEPasta\""; break;
                    case "uniwersytetRzeszowski" : nazwyPobliskich += "\"Uniwersytet Rzeszowski\""; break;
                    case "pomnikCzynuRewolucyjnego" : nazwyPobliskich += "\"Pomnik Czynu Rewolucyjnego\""; break;
                    case "stadionStal" : nazwyPobliskich += "\"Stadion Stal\""; break;
                    case "zamekLubomirskich" : nazwyPobliskich += "\"Zamek Lubomirskich\""; break;
                    case "galeriaRzeszow" : nazwyPobliskich += "\"Galeria Rzeszów\""; break;
                    case "cornerPubRzeszow" : nazwyPobliskich += "\"Corner Pub Rzeszów\""; break;
                    case "cybermachina" : nazwyPobliskich += "\"Cybermachina\""; break;
                    case "hokusPokus" : nazwyPobliskich += "\"HokusPokus\""; break;
                    case "manufaktura" : nazwyPobliskich += "\"Manufaktura\""; break;
                }
                nazwyPobliskich += " ";
                nazwa = "";

            }
            if(!nazwyPobliskich.equals(nazwyPobliskichGlobalne)&&!nazwyPobliskich.equals(""))
            {
                System.out.println(nazwyPobliskich + " TO SA NAZWY POBLISKIE");
                System.out.println(nazwyPobliskichGlobalne + " TO SA NAZWY POBLISKIE GLOBALNE");
                wPoblizu.setText("Jesteś w pobliżu: "+nazwyPobliskich);
                nazwyPobliskichGlobalne = nazwyPobliskich;
                wPoblizu.setVisibility(View.VISIBLE);
            }
            System.out.println(nazwa + "<---------------------------------------- NAZWY KTORE SA");
            System.out.println(PizzaEPasta.getLongitude() + "-LONG    "+PizzaEPasta.getLatitude()+"-LAT");
        }


        @Override
        public void onError(Throwable error) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //Dodanie jedzenia
        listaLokaliJedzenie.add(piwaSwiata);
        listaLokaliJedzenie.add(kukNuk);
        listaLokaliJedzenie.add(osteriaBellannuna);
        listaLokaliJedzenie.add(daraKebab);
        listaLokaliJedzenie.add(PizzaEPasta);
        //Dodanie rzeczy do zobaczenia
        listaDoZobaczenia.add(uniwersytetRzeszowski);
        listaDoZobaczenia.add(pomnikCzynuRewolucyjnego);
        listaDoZobaczenia.add(zamekLubomirskich);
        listaDoZobaczenia.add(stadionStal);
        listaDoZobaczenia.add(galeriaRzeszow);
        //Dodanie rzeczy do bary
        listaBarow.add(cornerPubRzeszow);
        listaBarow.add(cybermachina);
        listaBarow.add(hokusPokus);
        listaBarow.add(manufaktura);
        //LayoutInflater inflater =
        //inflatedLayout = inflater.inflate(R.layout.jedzenie_balloon);
       // LayoutInflater inflater = (LayoutInflater) getSystemService(Context.)

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getAsyncMap(this);


        //sayMyLocation();

    }



    private SingleLayoutBalloonViewAdapter createCustomViewAdapter() {
        return new SingleLayoutBalloonViewAdapter(R.layout.jedzenie_balloon) {
            @Override
            public void onBindView(View view, final Marker marker, BaseMarkerBalloon baseMarkerBalloon) {
                TextView tytul = view.findViewById(R.id.textView);
                TextView opis = view.findViewById(R.id.textView2);
                ImageView obrazek = view.findViewById(R.id.imageView);
                //tytul.setText(baseMarkerBalloon.getStringProperty(getApplicationContext().getString(R.string.poi_name_key)));
                //opis.setText(baseMarkerBalloon.getStringProperty(getApplicationContext().getString(R.string.address_key)));
                tytul.setText(baseMarkerBalloon.getStringProperty(getApplicationContext().getString(R.string.address_key)));
                opis.setText(baseMarkerBalloon.getStringProperty(getApplicationContext().getString(R.string.poi_name_key)));
                obrazek.setImageResource(baseMarkerBalloon.getIntProperty("obrazek"));

                /*btnAddWayPoint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setWayPoint(marker);
                    }

                    private void setWayPoint(Marker marker) {
                        wayPointPosition = marker.getPosition();
                        tomtomMap.clearRoute();
                        drawRouteWithWayPoints(departurePosition, destinationPosition, new LatLng[] {wayPointPosition});
                        marker.deselect();
                    }
                });*/
                sayMyLocation();
            }
        };
    }

}
