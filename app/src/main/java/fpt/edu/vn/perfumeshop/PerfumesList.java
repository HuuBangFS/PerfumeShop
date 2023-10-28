package fpt.edu.vn.perfumeshop;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fpt.edu.vn.perfumeshop.activities.ChatActivity;
import fpt.edu.vn.perfumeshop.apis.CustomerRepository;
import fpt.edu.vn.perfumeshop.apis.CustomerService;
import fpt.edu.vn.perfumeshop.apis.PerfumeRepository;
import fpt.edu.vn.perfumeshop.apis.PerfumeService;
import fpt.edu.vn.perfumeshop.app_services.CredentialService;
import fpt.edu.vn.perfumeshop.constants.AppConstants;
import fpt.edu.vn.perfumeshop.db.AppDatabase;
import fpt.edu.vn.perfumeshop.models.Cart;
import fpt.edu.vn.perfumeshop.models.Customer;
import fpt.edu.vn.perfumeshop.models.Perfume;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfumesList extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    static ListView listView;
    ArrayList<Perfume> arrayPerfumes;
    static ListAdapter adapter;
    static PerfumeService perfumeService;
    Customer adminAccount;
    CustomerService customerService;
    private static final String CHANNEL_ID = "notification_channel";
    private static final CharSequence CHANNEL_NAME = "Notification Channel";
    private static final String CHANNEL_DESCRIPTION = "Channel for notifications";
    CredentialService credentialService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfumes_list);

        perfumeService = PerfumeRepository.getPerfumeService();
        customerService = CustomerRepository.getCustomerService();
        listView = findViewById(R.id.listview);
        arrayPerfumes = new ArrayList<>();
        getAdminAccount(AppConstants.ADMIN_ACCOUNT);
        getAllPerfumes();
        Intent intent = getIntent();
        long idCus = intent.getLongExtra("idCustomer", -1);
        credentialService = new CredentialService(this);
        long userId = credentialService.getCurrentUserId();
        sendTotalProductInCartNotification(userId);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PerfumesList.this, PerfumeDetailActivity.class);
                intent.putExtra("id", arrayPerfumes.get(position).getId());
                intent.putExtra("idCus",idCus);
                startActivity(intent);
            }
        });
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.menu_home){
                startActivity(new Intent(PerfumesList.this, PerfumesList.class));
            }
            if(item.getItemId()==R.id.menu_order){
                startActivity(new Intent(PerfumesList.this, IndividualOrderActivity.class));
            }
            if(item.getItemId()==R.id.menu_map){
                startActivity(new Intent(PerfumesList.this, ViewMapActivity.class));
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllPerfumes();
    }

    void getAllPerfumes() {
        Call<Perfume[]> call = perfumeService.getAllPerfume();
        call.enqueue(new Callback<Perfume[]>() {
            @Override
            public void onResponse(Call<Perfume[]> call, Response<Perfume[]> response) {
                Perfume[] perfumes = response.body();
                if (perfumes == null) {
                    return;
                }

                //Clear the arrayPerfumes list
                arrayPerfumes.clear();

                for (Perfume perfume : perfumes) {
                    arrayPerfumes.add(perfume);
                }

                //Notify the adapter everywhen the data change
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                adapter = new ListAdapter(PerfumesList.this, R.layout.activity_list_item, arrayPerfumes);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Perfume[]> call, Throwable t) {
                Toast.makeText(PerfumesList.this, "Get perfumes fail", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_chat) {
            // start chat activity
            Intent intent = new Intent(PerfumesList.this, ChatActivity.class);
            intent.putExtra(AppConstants.USER_ID, adminAccount.getId());
            intent.putExtra(AppConstants.USER_UID, adminAccount.getUid());
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_cart) {
            // start view cat activity
            startActivity(new Intent(PerfumesList.this, ViewCartActivity.class));
        }
        else if (item.getItemId() == R.id.menu_logout) {
            // process for logout feature
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(PerfumesList.this, SignInActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void getAdminAccount(String adminEmail) {
        Call<Customer[]> call = customerService.getCustomerByEmail(adminEmail);
        call.enqueue(new Callback<Customer[]>() {
            @Override
            public void onResponse(Call<Customer[]> call, Response<Customer[]> response) {
                Customer[] result = response.body();
                if (result == null || result.length ==0) {
                    return;
                }
                adminAccount = result[0];
            }
            @Override
            public void onFailure(Call<Customer[]> call, Throwable t) {
                Log.d("Get admin account", t.getMessage());
            }
        });
    }

    private void sendTotalProductInCartNotification(long idCus) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        AppDatabase database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();

        AsyncTask<Void, Void, List<Cart>> databaseTask = new AsyncTask<Void, Void, List<Cart>>() {
            @Override
            protected List<Cart> doInBackground(Void... voids) {
                return database.cartDao().getAllPerfumesByUserID(idCus);
            }

            @Override
            protected void onPostExecute(List<Cart> perfumes) {
                super.onPostExecute(perfumes);
                showNotification(perfumes, idCus);
            }
        };

        databaseTask.execute();
    }

    private void showNotification(List<Cart> perfumes, long idCus) {
        int totalQuantity = 0;
        for (Cart cart : perfumes) {
            totalQuantity += cart.getQuantity();
        }

        if (totalQuantity == 0) {
            // No need to show notification if totalQuantity is 0
            return;
        }

        // Notification code
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                notificationBuilder = new Notification.Builder(this, CHANNEL_ID);
            } else {
                // Handle the case where getSystemService returned null
                return;
            }
        } else {
            notificationBuilder = new Notification.Builder(this);
        }

        Notification notification = notificationBuilder
                .setContentTitle("Perfumes need to be paid!")
                .setContentText("You have " + totalQuantity + " perfumes in the cart to check out. Check the cart!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColor(getResources().getColor(R.color.white))
                .build();

        NotificationManager notificationManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }
        if (notificationManager != null) {
            int notificationId = getNotificationId();
            notificationManager.notify(notificationId, notification);
        }
    }




    private int getNotificationId() {
        return (int) new Date().getTime();
    }
}