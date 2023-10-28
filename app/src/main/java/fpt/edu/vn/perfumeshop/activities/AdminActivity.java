package fpt.edu.vn.perfumeshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import fpt.edu.vn.perfumeshop.AdminOrderAcitivity;
import fpt.edu.vn.perfumeshop.R;
import fpt.edu.vn.perfumeshop.SignInActivity;


public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button btnChatScreen = findViewById(R.id.btnCustomerchat);
        btnChatScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, AdminChatListActivity.class));
            }
        });

        Button btnFlowerMng = findViewById(R.id.btnPerfumeMng);
        btnFlowerMng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, AdminPerfumeMngActivity.class));
            }
        });
        Button btnOrder = findViewById(R.id.viewAllOrder);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminOrderAcitivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_home_admin) {
            // profile setting processor
        }
        else if (item.getItemId() == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminActivity.this, SignInActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}