package fpt.edu.vn.perfumeshop.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import fpt.edu.vn.perfumeshop.R;
import fpt.edu.vn.perfumeshop.SignInActivity;
import fpt.edu.vn.perfumeshop.adapters.PerfumeMngAdapter;
import fpt.edu.vn.perfumeshop.apis.PerfumeRepository;
import fpt.edu.vn.perfumeshop.apis.PerfumeService;
import fpt.edu.vn.perfumeshop.models.Perfume;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPerfumeMngActivity extends AppCompatActivity {
    PerfumeService perfumeService;
    ListView lvPerfumes;
    ArrayList<Perfume> perfumeList;
    PerfumeMngAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_perfume_mng);

        perfumeService = PerfumeRepository.getPerfumeService();
        lvPerfumes = findViewById(R.id.lvPerfumeMng);
        perfumeList = new ArrayList<>();
        adapter = new PerfumeMngAdapter(perfumeList, R.layout.perfume_mng_row, AdminPerfumeMngActivity.this);
        lvPerfumes.setAdapter(adapter);
        loadPerfume();

        ImageView imgAdd = findViewById(R.id.imgAddPerfume);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminPerfumeMngActivity.this, InsertUpdatePerfumeActivity.class));
            }
        });

        lvPerfumes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Perfume perfume = (Perfume) adapter.getItem(position);
                dialogDeletePerfume(perfume);
                return false;
            }
        });
    }

    private void dialogDeletePerfume(Perfume perfume) {
        AlertDialog.Builder dialogXoa = new AlertDialog.Builder(this);
        dialogXoa.setMessage("Do you want to delete perfume "+ perfume.getPerfumeName()+"?");
        dialogXoa.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePerfume(perfume.getId());
            }
        });
        dialogXoa.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogXoa.show();
    }

    private void deletePerfume(long id) {
        Call<Perfume> call = perfumeService.deletePerfume(id);
        call.enqueue(new Callback<Perfume>() {
            @Override
            public void onResponse(Call<Perfume> call, Response<Perfume> response) {
                Toast.makeText(AdminPerfumeMngActivity.this, "Delete perfume"+id+"successfully!", Toast.LENGTH_SHORT).show();
                loadPerfume();
            }
            @Override
            public void onFailure(Call<Perfume> call, Throwable t) {
                Toast.makeText(AdminPerfumeMngActivity.this, "Delete perfume failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPerfume() {
        Call<Perfume[]> call = perfumeService.getAllPerfume();
        call.enqueue(new Callback<Perfume[]>() {
            @Override
            public void onResponse(Call<Perfume[]> call, Response<Perfume[]> response) {
                Perfume[] perfumes = response.body();
                if (perfumes == null || perfumes.length == 0) {
                    return;
                }
                perfumeList.clear();
                for ( Perfume f: perfumes) {
                    perfumeList.add(f);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Perfume[]> call, Throwable t) {
                Toast.makeText(AdminPerfumeMngActivity.this, "Load perfume list failed!", Toast.LENGTH_SHORT).show();
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
            // admin home
            startActivity(new Intent(AdminPerfumeMngActivity.this, AdminActivity.class));
        }
        else if (item.getItemId() == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminPerfumeMngActivity.this, SignInActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}