package com.example.fixcarapp.DangKy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixcarapp.DangNhap.LoginActivity;
import com.example.fixcarapp.R;
import com.example.fixcarapp.TaiKhoan.User;
import com.example.fixcarapp.TrungTam.DanhSachTrungTam.Item_Center;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private TextInputEditText etName, etEmail, etPassword, etPasswordConfirm;
    private RadioGroup rgUserType;
    private Button btnRegister;
    private  DatabaseReference databaseReference1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase Authentication và Realtime Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference1 = FirebaseDatabase.getInstance().getReference("Centers");
        etName = findViewById(R.id.txtiename);
        etEmail = findViewById(R.id.txtieemail);
        etPassword = findViewById(R.id.txtiep);
        etPasswordConfirm = findViewById(R.id.txtierp);
        rgUserType = findViewById(R.id.rgUserType);
        btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        btnRegister.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirm = etPasswordConfirm.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedRoleId = rgUserType.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(RegisterActivity.this, "Vui lòng chọn vai trò!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRole = findViewById(selectedRoleId);
        String role = selectedRole.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            User newUser = new User(userId, name, email,"", "", "", role);
                            databaseReference.child(userId).setValue(newUser)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            if(role.equals("Trung tâm cứu hộ"))
                                            {
                                                String centerId = userId;
                                                Item_Center itemCenter = new Item_Center(centerId,name,email,"","","","");
                                                databaseReference1.child(centerId).setValue(itemCenter)
                                                        .addOnCompleteListener(task2 -> {
                                                            if(task2.isSuccessful()) {
                                                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công trung tâm hỗ trợ", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Lỗi lưu thông tin: " + task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}

//tk scuuho@gmail.com
//mk scuuho123

// lắng nghe cảm biến nhiệt độ từ Vitual sensor trong extened controls bằng sensorManager
