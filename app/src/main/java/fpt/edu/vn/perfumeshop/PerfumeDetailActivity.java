package fpt.edu.vn.perfumeshop;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import fpt.edu.vn.perfumeshop.apis.PerfumeRepository;
import fpt.edu.vn.perfumeshop.apis.PerfumeService;
import fpt.edu.vn.perfumeshop.app_services.CredentialService;
import fpt.edu.vn.perfumeshop.db.AppDatabase;
import fpt.edu.vn.perfumeshop.db.AppExecutors;
import fpt.edu.vn.perfumeshop.models.Cart;
import fpt.edu.vn.perfumeshop.models.Perfume;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PerfumeDetailActivity extends AppCompatActivity {
    PerfumeService perfumeService;
    ImageView ivPerfume;
    TextView tvPerfumeName, tvPerfumeDescription, tvPerfumePrice;
    EditText etQuantity;
    ImageView ivAdd, ivPlus, ivMinus;
    CredentialService credentialService;
    BottomNavigationView bottomNavigationView;
    long userId;
    private static final String CHANNEL_ID = "notification_channel";
    private static final CharSequence CHANNEL_NAME = "Notification Channel";
    private static final String CHANNEL_DESCRIPTION = "Channel for notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfume_detail);
        ivPerfume = (ImageView) findViewById(R.id.ivPerfume);
        tvPerfumeName = (TextView) findViewById(R.id.tvPerfumeName);
        tvPerfumeDescription = (TextView) findViewById(R.id.tvPerfumeDescription);
        tvPerfumePrice = (TextView) findViewById(R.id.tvPerfumePrice);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        ivAdd = (ImageView) findViewById(R.id.ivAdd);
        ivPlus = (ImageView) findViewById(R.id.ivPlus);
        ivMinus = (ImageView) findViewById(R.id.ivMinus);
        perfumeService = PerfumeRepository.getPerfumeService();
        Intent intent = getIntent();
        long id = intent.getLongExtra("id", -1);
        if (id != -1) {
            viewPerfume(id);
        }
        credentialService = new CredentialService(PerfumeDetailActivity.this);
        long userId = credentialService.getCurrentUserId();
        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(etQuantity.getText().toString());
                quantity++;
                etQuantity.setText(quantity + "");
            }
        });
        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(etQuantity.getText().toString());
                quantity--;
                etQuantity.setText(quantity + "");
            }
        });
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                animateProductToCart(id, userId);
                insertCart(id, userId);
            }
        });
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                startActivity(new Intent(PerfumeDetailActivity.this, PerfumesList.class));
            }
            if (item.getItemId() == R.id.menu_order) {
                startActivity(new Intent(PerfumeDetailActivity.this, PerfumesList.class));
            }
            if (item.getItemId() == R.id.menu_map) {
                startActivity(new Intent(PerfumeDetailActivity.this, ViewMapActivity.class));
            }
            return true;
        });
    }

    public void viewPerfume(long id) {
        try {
            Call<Perfume> call = perfumeService.getPerfume(id);
            call.enqueue(new Callback<Perfume>() {
                @Override
                public void onResponse(Call<Perfume> call, Response<Perfume> response) {
                    if (response.body() != null) {
                        Perfume perfume = response.body();
                        Picasso.get().load(perfume.getImageUrl()).into(ivPerfume);
                        tvPerfumeName.setText(perfume.getPerfumeName());
                        tvPerfumeDescription.setText(perfume.getDescription());
                        tvPerfumePrice.setText("$ " + perfume.getUnitPrice());
                    }
                }

                @Override
                public void onFailure(Call<Perfume> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.d("Loi", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sub_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_cart) {
            // start view cat activity
            startActivity(new Intent(PerfumeDetailActivity.this, ViewCartActivity.class));
        } else if (item.getItemId() == R.id.menu_logout) {
            // process for logout feature
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(PerfumeDetailActivity.this, SignInActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void insertCart(long idPerfume, long idCustomer) {
        try {
            Call<Perfume> call = perfumeService.getPerfume(idPerfume);
            call.enqueue(new Callback<Perfume>() {
                @Override
                public void onResponse(Call<Perfume> call, Response<Perfume> response) {
                    if (response.body() != null) {
                        AppDatabase database = Room.databaseBuilder(getApplicationContext(),
                                AppDatabase.class, "app-database").build();
                        Perfume perfume = response.body();
                        int quantity = 0;
                        if (etQuantity.getText().toString().trim().equals("")) {
                            quantity = 0;
                        } else {
                            quantity = Integer.parseInt(etQuantity.getText().toString());
                        }
                        if (quantity > perfume.getUnitInStock()) {
                            Toast.makeText(PerfumeDetailActivity.this, "You can buy max " + perfume.getUnitInStock(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (quantity <= 0) {
                            etQuantity.setError("Quantity must be larger than 0");
                            return;
                        }
                        Cart cart = new Cart(1, perfume.getPerfumeName(), perfume.getDescription(), perfume.getImageUrl(), perfume.getUnitPrice(), quantity, idPerfume, idCustomer);
                        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    long maxId = database.cartDao().maxId();
                                    cart.setId(maxId + 1);
                                    int quantity = Integer.parseInt(etQuantity.getText().toString());
                                    sendAddToCartNotification(perfume.getPerfumeName(), quantity);
                                    finish();
                                    List<Cart> cartList = database.cartDao().getAllPerfume();
                                    long cartId = cart.getIdPerfume();
                                    List<Cart> cartx = cartList.stream().filter(c -> c.getIdPerfume() == cartId).collect(Collectors.toList());
                                    if (cartx.isEmpty()) {
                                        cart.setId(maxId + 1);
                                        database.cartDao().insert(cart);
                                        finish();
                                    } else {
                                        for (Cart cart : cartx) {
                                            cart.setQuantity(cart.getQuantity() + quantity);
                                            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    database.cartDao().update(cart);
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(PerfumeDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Perfume> call, Throwable t) {
                    Log.e("error", t.toString());
                }
            });
        } catch (Exception e) {
            Log.d("Loi", e.getMessage());
        }
    }

    private void sendAddToCartNotification(String perfumeName, int quantity) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            notificationBuilder = new Notification.Builder(this);
        }

        Notification notification = notificationBuilder
                .setContentTitle("Added to Cart!")
                .setContentText("Added " + quantity + " " + perfumeName + " to cart")
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