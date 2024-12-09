package com.example.fixcarapp.TrungTam;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fixcarapp.R;
import com.example.fixcarapp.TaoYeuCau.Request;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private Context context;
    private List<Request> requestList;

    public RequestAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.tvId.setText(String.valueOf(request.getId()));
        holder.tvAddress.setText(request.getAddress());
        holder.tvProblem.setText(request.getProblem());
        holder.tvVehicle.setText(request.getVehicle());
        holder.tvPhone.setText(request.getPhone());
        holder.tvTime.setText(request.getTime());

        // Sự kiện cho nút "Thêm Thông Tin"
        holder.btnThemThongTin.setOnClickListener(v -> {
            // Chuyển đến AddInformationActivity
            Intent intent = new Intent(context, AddInformationActivity.class);
            intent.putExtra("requestId", request.getId()); // Truyền ID của Request vào AddInformationActivity
            context.startActivity(intent);
        });

        // Ẩn hoặc hiện các nút dựa trên trạng thái

        if ("COMPLETED".equals(request.getStatus())) {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancle.setVisibility(View.GONE);
            holder.btnComplete.setVisibility(View.GONE);
        } else {
            // Hiển thị các button tùy thuộc vào trạng thái khác
            if ("ACCEPTED".equals(request.getStatus())) {
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancle.setVisibility(View.GONE);
                holder.btnComplete.setVisibility(View.VISIBLE);
            } else {
                holder.btnConfirm.setVisibility(View.VISIBLE);
                holder.btnCancle.setVisibility(View.VISIBLE);
                holder.btnComplete.setVisibility(View.GONE);
            }
        }

        // Sự kiện cho nút "Xác nhận"
        holder.btnConfirm.setOnClickListener(v -> {

            // Chuyển sang DetailActivity
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("REQUEST_ID", request.getId());
            intent.putExtra("ADDRESS", request.getAddress());
            intent.putExtra("PROBLEM", request.getProblem());
            intent.putExtra("VEHICLE", request.getVehicle());
            intent.putExtra("PHONE", request.getPhone());
            intent.putExtra("TIME", request.getTime());
            intent.putExtra("SCENE_PHOTO", request.getScenePhoto());
            context.startActivity(intent);

            //  updateRequestStatus(request.getId()); // Gọi hàm cập nhật trạng thái
            updateRequestStatus(request.getId(), "ACCEPTED");
            request.setStatus("ACCEPTED");
            notifyItemChanged(position);
        });

        // Sự kiện cho nút không nhận
        holder.btnCancle.setOnClickListener(v -> {
            updateRequestStatus(request.getId(), "UNACCEPTABLE");
            request.setStatus("UNACCEPTABLE");
            removeItem(position); // Gọi phương thức xóa
        });

        // Sự kiện nút "Hoàn thành"
        holder.btnComplete.setOnClickListener(v -> {
            updateRequestStatus(request.getId(), "COMPLETED");
            request.setStatus("COMPLETED");

            // Xóa item khỏi danh sách nếu trạng thái là COMPLETED
            removeItem(position);
        });


        // Sử dụng Glide để tải ảnh từ URL
        Glide.with(context)
                .load(request.getScenePhoto()) // Link ảnh từ Firebase
//                .placeholder(R.drawable.placeholder) // Ảnh mặc định khi đang tải
//                .error(R.drawable.error_image) // Ảnh hiển thị khi lỗi
                .into(holder.imgScenePhoto); // ImageView hiển thị ảnh
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddress, tvProblem, tvVehicle, tvStatus, tvPhone, tvId, tv_Email, tvTime;
        ImageView imgScenePhoto;

        Button btnConfirm ; // Thêm nút "Xác nhận"
        Button btnCancle;
        Button btnComplete;

        Button btnThemThongTin;



        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvProblem = itemView.findViewById(R.id.tvProblem);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvPhone = itemView.findViewById(R.id.tvPhone);   // Ánh xạ TextView phone
            btnConfirm = itemView.findViewById(R.id.btnConfirm); // ánh xạ nút
            btnCancle = itemView.findViewById(R.id.btnCancle);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            imgScenePhoto = itemView.findViewById(R.id.imgScenePhoto);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnThemThongTin = itemView.findViewById(R.id.bt_ThemThongTin);

        }
    }

    private void updateRequestStatus(int requestId, String newStatus) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Requests");
        databaseReference.child(String.valueOf(requestId)).child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Trạng thái đã được cập nhật thành " + newStatus + "!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Cập nhật trạng thái thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public void removeItem(int position) {
        requestList.remove(position); // Xóa khỏi danh sách
        notifyItemRemoved(position); // Thông báo RecyclerView cập nhật
        notifyItemRangeChanged(position, requestList.size()); // Cập nhật vị trí các item còn lại
    }
    //    public void setData(List<Request> requestList) {
//        // Sắp xếp theo ID giảm dần
//        Collections.sort(requestList, (r1, r2) -> Integer.compare(r2.getId(), r1.getId()));
//        this.requestList = requestList;
//        notifyDataSetChanged(); // Thông báo RecyclerView cập nhật
//    }
    public void setData(List<Request> requestList) {
        // Lọc ra những mục không có trạng thái COMPLETED
        this.requestList = new ArrayList<>();
        for (Request request : requestList) {
            if (!"COMPLETED".equals(request.getStatus()) && !"UNACCEPTABLE".equals(request.getStatus())) {
                this.requestList.add(request);
            }
        }
        notifyDataSetChanged(); // Thông báo RecyclerView cập nhật
    }

    public void setCompletedData(List<Request> allRequests) {
        // Chỉ thêm các mục có trạng thái "COMPLETED"
        this.requestList = new ArrayList<>();
        for (Request request : allRequests) {
            if ("COMPLETED".equals(request.getStatus())) {
                this.requestList.add(request);
            }
        }
        notifyDataSetChanged(); // Thông báo cập nhật giao diện
    }


}
