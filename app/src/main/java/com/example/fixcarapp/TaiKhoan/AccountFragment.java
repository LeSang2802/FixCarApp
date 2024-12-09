package com.example.fixcarapp.TaiKhoan;
import android.app.AlertDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.fixcarapp.DangNhap.LoginActivity;
import com.example.fixcarapp.LichSuYeuCau.HistoryActivity;
import com.example.fixcarapp.TrungTam.DanhSachTrungTam.CenterInformationFragment;
import com.example.fixcarapp.R;
import com.example.fixcarapp.TrungTam.RescueCenterActitvity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccountFragment extends Fragment {
    private Button btnLogout,btnHistory,btnUpdatePassword,btnUpdateAccount;
    private FirebaseUser user;

//    private SensorManager sensorManager;
//    private Sensor temperatureSensor;
//    private SensorEventListener temperatureListener;
//    private TextView tvTemperature;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

//        tvTemperature = view.findViewById(R.id.tvTemperature);
//        sensorManager = (SensorManager) requireActivity().getSystemService(getContext().SENSOR_SERVICE);
//        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
//
//        if (temperatureSensor != null) {
//            temperatureListener = new SensorEventListener() {
//                @Override
//                public void onSensorChanged(SensorEvent event) {
//                    float temp = event.values[0];
//                    tvTemperature.setText(String.format(Locale.getDefault(), "%.1f°C", temp));
//                }
//
//                @Override
//                public void onAccuracyChanged(Sensor sensor, int accuracy) {
//                }
//            };
//            sensorManager.registerListener(temperatureListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        } else {
//            tvTemperature.setText("Không có cảm biến nhiệt độ");
//        }



        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("vi", "VN"));
        String formattedDate = dateFormat.format(currentDate);
        tvDate.setText(formattedDate);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnHistory = view.findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), HistoryActivity.class);
            startActivity(intent);
        });

        btnUpdateAccount = view.findViewById(R.id.btnUpdateAccount);
        btnUpdateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UpdateAccountActivity.class);
            startActivity(intent);
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        btnUpdatePassword = view.findViewById(R.id.btnUpdatePassword);
        btnUpdatePassword.setOnClickListener(v -> {
            changePass();
        });
        return view;
    }

    private void changePass()
    {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.fragment_change_password,null);
        EditText edtOldPass,edtNewPass;
        Button btnAccept;
        TextView tvWarning;
        ImageView imgClose;
        edtOldPass = view.findViewById(R.id.edtOldPass);
        edtNewPass = view.findViewById(R.id.edtNewpass);
        btnAccept = view.findViewById(R.id.btnAccept);
        imgClose = view.findViewById(R.id.imgClose);
        tvWarning = view.findViewById(R.id.tvWarning);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = edtOldPass.getText().toString().trim();
                String newPassword = edtNewPass.getText().toString().trim();
                if(oldPassword == null && newPassword == null)
                {
                    tvWarning.setVisibility(View.VISIBLE);
                    tvWarning.setText("Vui lòng nhập đủ mật khẩu cũ và mật khẩu mới");
                }
                if (oldPassword.equals(newPassword)) {
                    tvWarning.setVisibility(View.VISIBLE);
                    tvWarning.setText("Mật khẩu mới phải khác mật khẩu cũ");
                    return; // Dừng lại nếu mật khẩu cũ và mới giống nhau
                }
                if (user != null) {
                    String email = user.getEmail();
                    if (!email.isEmpty() && !oldPassword.isEmpty() && !newPassword.isEmpty()) {
                        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
                        user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                            if (reauthTask.isSuccessful()) {
                                // Nếu xác thực lại thành công, cập nhật mật khẩu
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                Toast.makeText(requireContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(requireContext(), "Đổi mật khẩu thất bại: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                tvWarning.setVisibility(View.VISIBLE);
                                tvWarning.setText("Mật khẩu cũ không đúng");
                            }
                        });
                    } else {
                        tvWarning.setVisibility(View.VISIBLE);
                        tvWarning.setText("Vui lòng nhập đủ mật khẩu cũ và mật khẩu mới");
                    }
                } else {
                    Toast.makeText(requireContext(), "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        imgClose.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (temperatureListener != null) {
//            sensorManager.unregisterListener(temperatureListener);
//        }
//    }

}