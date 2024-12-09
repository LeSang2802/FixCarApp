//package com.example.fixcarapp.TrungTam;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.fixcarapp.R;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//
//
//public class AddInformationActivity extends AppCompatActivity {
//
//    private EditText etTime, etDescription, etQuantity;
//    private Button btnSave;
//    private int requestId; // Lưu id của Request
//
//    // Firebase Realtime Database reference
//    private DatabaseReference database;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_information);
//
//        // Khởi tạo Firebase database
//        database = FirebaseDatabase.getInstance().getReference("requests");
//
//        // Kết nối các view
//        etTime = findViewById(R.id.et_time);
//        etDescription = findViewById(R.id.et_description);
//        etQuantity = findViewById(R.id.et_quantity);
//        btnSave = findViewById(R.id.btn_save);
//
//        // Nhận id của Request từ Intent
//        requestId = getIntent().getIntExtra("requestId", -1); // Lấy id từ Intent
//
//        btnSave.setOnClickListener(v -> saveAdditionalInfo());
//    }
//
//    private void saveAdditionalInfo() {
//        // Lấy dữ liệu từ EditText
//        String additionalTime = etTime.getText().toString().trim();
//        String additionalDescription = etDescription.getText().toString().trim();
//        int additionalQuantity = Integer.parseInt(etQuantity.getText().toString().trim());
//
//        // Tạo đối tượng AdditionalInfo mới
//        AdditionalInfo additionalInfo = new AdditionalInfo(additionalTime, additionalDescription, additionalQuantity);
//
//        // Lưu thông tin bổ sung vào Firebase dưới node `additional_info`
//        DatabaseReference requestRef = database.child(String.valueOf(requestId)).child("additional_info");
//        requestRef.setValue(additionalInfo)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(AddInformationActivity.this, "Thông tin bổ sung đã được lưu", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(AddInformationActivity.this, "Lưu thông tin thất bại", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//}
//
//
//

package com.example.fixcarapp.TrungTam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fixcarapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddInformationActivity extends AppCompatActivity {

    private EditText etTime, etDescription, etQuantity;
    private Button btnSave;
    private int requestId; // Lưu id của Request

    // Firebase Realtime Database reference
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_information);

        // Khởi tạo Firebase database
        database = FirebaseDatabase.getInstance().getReference("requests");

        // Kết nối các view
        etTime = findViewById(R.id.et_time);
        etDescription = findViewById(R.id.et_description);
        etQuantity = findViewById(R.id.et_quantity);
        btnSave = findViewById(R.id.btn_save);

        // Nhận id của Request từ Intent
        requestId = getIntent().getIntExtra("requestId", -1); // Lấy id từ Intent

        btnSave.setOnClickListener(v -> saveAdditionalInfo());
    }

    private void saveAdditionalInfo() {
        // Lấy dữ liệu từ EditText
        String additionalTime = etTime.getText().toString().trim();
        String additionalDescription = etDescription.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();

        // Kiểm tra các trường không để trống
        if (additionalTime.isEmpty() || additionalDescription.isEmpty() || quantityText.isEmpty()) {
            Toast.makeText(AddInformationActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra số lượng có hợp lệ không
        int additionalQuantity;
        try {
            additionalQuantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(AddInformationActivity.this, "Số lượng phải là một số hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng AdditionalInfo mới
        AdditionalInfo additionalInfo = new AdditionalInfo(additionalTime, additionalDescription, additionalQuantity);

        // Lưu thông tin bổ sung vào Firebase dưới node `additional_info`
        DatabaseReference requestRef = database.child(String.valueOf(requestId)).child("additional_info");
        requestRef.setValue(additionalInfo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddInformationActivity.this, "Thông tin bổ sung đã được lưu", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddInformationActivity.this, "Lưu thông tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

