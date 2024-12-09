package com.example.fixcarapp.TrangChu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fixcarapp.MapActivity;
import com.example.fixcarapp.R;
import com.example.fixcarapp.TrungTam.DanhSachTrungTam.Item_Center;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements LocationListener, OnMapReadyCallback {
    LocationManager locationManager;
    double longitude, latitude;
    String currentLocation;
    TextView tvLocation,tvName;
    ImageView icLocation;
    private DatabaseReference databaseReference;
    FirebaseUser user;
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvName = view.findViewById(R.id.tvName);
        Date currentDate = new Date();
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            String userID = user.getUid();
            databaseReference.child(userID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            String name = task.getResult().child("name").getValue(String.class);
                            tvName.setVisibility(View.VISIBLE);
                            tvName.setText(name);
                        }
                    });
        }
        tvLocation =view.findViewById(R.id.tvLocation);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startLocationUpdates();
        }

        icLocation = view.findViewById(R.id.icLocation);
        icLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MapActivity.class);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("currentLocation", currentLocation);
                startActivity(intent);
            }
        });
        // Định dạng ngày giờ theo tiếng Việt
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("vi", "VN"));
        String formattedDate = dateFormat.format(currentDate);
        tvDate.setText(formattedDate);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void stopLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        getAddressFromLocation(latitude, longitude);
    }

    public void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);
                currentLocation = addressText;
                tvLocation.setText(addressText);
            } else {
                tvLocation.setText("Không thể lấy vị trí.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            tvLocation.setText("Lỗi khi lấy vị trí.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    startLocationUpdates();
                }
            } else {
                tvLocation.setText("Yêu cầu quyền truy cập.");
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map; // Đảm bảo rằng bạn đang gán đúng kiểu dữ liệu
        // Thiết lập bản đồ
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Lấy vị trí hiện tại của người dùng
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        GoogleMap finalGoogleMap = googleMap;
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                // Hiển thị marker vị trí hiện tại
                finalGoogleMap.addMarker(new MarkerOptions().position(userLocation).title("Vị trí của bạn"));

                // Di chuyển camera đến vị trí hiện tại
                finalGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            }
            addRescuePoints();
        });
    }
    private void addRescuePoints() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Centers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item_Center item = snapshot.getValue(Item_Center.class);
                    if (item != null) {
                        String tenCenter = item.getTenCenter();
                        String diachiCenter = item.getDiachiCenter();
                        LatLng rescuePoint = getLocationFromAddress(diachiCenter);
                        if (rescuePoint != null) {
                            googleMap.addMarker(new MarkerOptions().position(rescuePoint).title(tenCenter));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private LatLng getLocationFromAddress(String address) {
        Geocoder geocoder = new Geocoder(getContext()); // Lấy Geocoder từ context của activity hoặc fragment
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(address, 1); // Lấy danh sách các địa chỉ từ địa chỉ văn bản
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0); // Lấy địa chỉ đầu tiên trong danh sách
                return new LatLng(location.getLatitude(), location.getLongitude()); // Trả về LatLng
            }
        } catch (IOException e) {
            Log.e("Geocoder", "Unable to get location from address", e);
        }
        return null; // Trả về null nếu không tìm được vị trí
    }
}