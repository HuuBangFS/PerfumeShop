package fpt.edu.vn.perfumeshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import fpt.edu.vn.perfumeshop.R;
import fpt.edu.vn.perfumeshop.SignInActivity;
import fpt.edu.vn.perfumeshop.apis.PerfumeRepository;
import fpt.edu.vn.perfumeshop.apis.PerfumeService;
import fpt.edu.vn.perfumeshop.constants.AppConstants;
import fpt.edu.vn.perfumeshop.models.Perfume;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertUpdatePerfumeActivity extends AppCompatActivity {
    EditText etName, etPrice, etUnitInStock, etDescription, etImageUrl;
    Button btnSave, btnCancel;
    Perfume updatedPerfume;
    PerfumeService perfumeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_update_perfume);

        etName = findViewById(R.id.etPerfumeName);
        etPrice = findViewById(R.id.etPrice);
        etUnitInStock = findViewById(R.id.etUnitInStock);
        etDescription = findViewById(R.id.etDescription);
        etImageUrl = findViewById(R.id.etImageUrlPerfume);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        perfumeService = PerfumeRepository.getPerfumeService();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(AppConstants.UPDATED_PERFUME)) {
            updatedPerfume = (Perfume) intent.getSerializableExtra(AppConstants.UPDATED_PERFUME);
            generateUI(updatedPerfume);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Perfume perfume = getPerfumeObj();
                    perfume.setId(updatedPerfume.getId());
                    // update perfume
                    updatePerfume(perfume);
                }
            });
        } else {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // add perfume
                    Perfume perfume = getPerfumeObj();
                    addPerfume(perfume);
                }
            });
        }
    }

    private void addPerfume(Perfume perfume) {
        Call<Perfume> call = perfumeService.createPerfume(perfume);
        call.enqueue(new Callback<Perfume>() {
            @Override
            public void onResponse(Call<Perfume> call, Response<Perfume> response) {
                if (response.body() == null) {
                    return;
                }
                Toast.makeText(InsertUpdatePerfumeActivity.this, "Insert perfume successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(InsertUpdatePerfumeActivity.this, AdminPerfumeMngActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<Perfume> call, Throwable t) {
                Toast.makeText(InsertUpdatePerfumeActivity.this, "Insert perfume failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePerfume(Perfume perfume) {
        Call<Perfume> call = perfumeService.updatePerfume(updatedPerfume.getId(), perfume);
        call.enqueue(new Callback<Perfume>() {
            @Override
            public void onResponse(Call<Perfume> call, Response<Perfume> response) {
                if (response.body() == null) {
                    return;
                }
                Toast.makeText(InsertUpdatePerfumeActivity.this, "Update perfume successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(InsertUpdatePerfumeActivity.this, AdminPerfumeMngActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<Perfume> call, Throwable t) {
                Toast.makeText(InsertUpdatePerfumeActivity.this, "Update perfume failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateUI(Perfume perfume) {
        etName.setText(perfume.getPerfumeName());
        etPrice.setText(perfume.getUnitPrice()+"");
        etUnitInStock.setText(perfume.getUnitInStock()+"");
        etDescription.setText(perfume.getDescription());
        etImageUrl.setText(perfume.getImageUrl());
    }
    private Perfume getPerfumeObj() {
        Perfume perfume = new Perfume();
        perfume.setPerfumeName(etName.getText().toString());
        perfume.setUnitPrice(Double.parseDouble(etPrice.getText().toString()));
        perfume.setUnitInStock(Integer.parseInt(etUnitInStock.getText().toString()));
        perfume.setDescription(etDescription.getText().toString());
        perfume.setImageUrl(etImageUrl.getText().toString());
        return perfume;
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
            startActivity(new Intent(InsertUpdatePerfumeActivity.this, SignInActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}