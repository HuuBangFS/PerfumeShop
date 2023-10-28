package fpt.edu.vn.perfumeshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import fpt.edu.vn.perfumeshop.Helper.AppInfo;
import fpt.edu.vn.perfumeshop.Helper.CreateOrder;
import fpt.edu.vn.perfumeshop.apis.PerfumeRepository;
import fpt.edu.vn.perfumeshop.apis.PerfumeService;
import fpt.edu.vn.perfumeshop.apis.OderDetailRepository;
import fpt.edu.vn.perfumeshop.apis.OrderDetailService;
import fpt.edu.vn.perfumeshop.apis.OrderRepository;
import fpt.edu.vn.perfumeshop.apis.OrderService;
import fpt.edu.vn.perfumeshop.app_services.CredentialService;
import fpt.edu.vn.perfumeshop.db.AppDatabase;
import fpt.edu.vn.perfumeshop.db.AppExecutors;
import fpt.edu.vn.perfumeshop.models.Cart;
import fpt.edu.vn.perfumeshop.models.Perfume;
import fpt.edu.vn.perfumeshop.models.Order;
import fpt.edu.vn.perfumeshop.models.OrderDetail;
import fpt.edu.vn.perfumeshop.models.OrderStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;


public class CheckoutActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Button btnPayAfter;
    long userId;
    CredentialService credentialService;
    private AppDatabase database;
    private OrderService orderService;
    private OrderDetailService orderDetailService;
    Button btnPayNow;
    private PerfumeService perfumeService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        btnPayAfter = findViewById(R.id.checkout1);
        credentialService = new CredentialService(this);
        userId = credentialService.getCurrentUserId();
        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();
        orderService = OrderRepository.getOrderService();
        orderDetailService = OderDetailRepository.getOrderDetailService();
        perfumeService = PerfumeRepository.getPerfumeService();
        btnPayAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutAfter();
                deleteCart();
            }
        });
        btnPayNow = findViewById(R.id.checkout2);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payNow();
                deleteCart();
            }
        });
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.menu_home){
                startActivity(new Intent(CheckoutActivity.this, PerfumesList.class));
            }
            if(item.getItemId()==R.id.menu_order){
                startActivity(new Intent(CheckoutActivity.this, IndividualOrderActivity.class));
            }
            if(item.getItemId()==R.id.menu_map){
                startActivity(new Intent(CheckoutActivity.this, ViewMapActivity.class));
            }
            return true;
        });
    }
    private void checkoutAfter(){
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Cart> myCart = database.cartDao().getAllPerfumesByUserID(userId);
                List<Order> orderList = new ArrayList<>();
                Call<Order[]> call = orderService.getAllOrders();
                call.enqueue(new Callback<Order[]>() {
                    @Override
                    public void onResponse(Call<Order[]> call, Response<Order[]> response) {
                         if(response.body() == null){
                             int maxId = 0;
                             double total = 0;
                             for (Cart c:myCart) {
                                 total += c.getUnitPrice() *  c.getQuantity();
                             }
                            Order order = new Order(maxId + 1, new Date().toString(),new Date().toString(),total, OrderStatus.UNPAID.toString());
                            Call<Order> orderCreateCall = orderService.createOrder(order);
                            orderCreateCall.enqueue(new Callback<Order>() {
                                @Override
                                public void onResponse(Call<Order> call, Response<Order> response) {
                                    if(response.body() != null){
                                        for (Cart cart:myCart) {
                                            OrderDetail orderDetail = new OrderDetail(1,cart.getIdPerfume(),cart.getUnitPrice(),cart.getQuantity());
                                            Call<OrderDetail> orderDetailCall = orderDetailService.createOrderDetail(orderDetail);
                                            orderDetailCall.enqueue(new Callback<OrderDetail>() {
                                                @Override
                                                public void onResponse(Call<OrderDetail> call, Response<OrderDetail> response) {
                                                     if(response.body() != null){
                                                         boolean bool = true;
                                                         try {
                                                             AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                                                                                                              @Override
                                                                                                              public void run() {
                                                                                                                  database.cartDao().delete(cart);
                                                                                                              }
                                                                                                          });
                                                         } catch (Exception e){
                                                             Log.e("e",e.getMessage());
                                                         }
                                                     }
                                                }

                                                @Override
                                                public void onFailure(Call<OrderDetail> call, Throwable t) {

                                                }
                                            });
                                        }
                                        for (Cart cart:myCart) {
                                            Call<Perfume> perfumeCall = perfumeService.getPerfume(cart.getIdPerfume());
                                            perfumeCall.enqueue(new Callback<Perfume>() {
                                                @Override
                                                public void onResponse(Call<Perfume> call, Response<Perfume> response) {
                                                    Perfume perfume = response.body();
                                                    perfume.setUnitInStock(perfume.getUnitInStock() - cart.getQuantity());
                                                    Call<Perfume> perfumeCall2 = perfumeService.updatePerfume(cart.getIdPerfume(), perfume);
                                                    perfumeCall2.enqueue(new Callback<Perfume>() {
                                                        @Override
                                                        public void onResponse(Call<Perfume> call, Response<Perfume> response) {
                                                           response.body().getUnitInStock();
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Perfume> call, Throwable t) {

                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onFailure(Call<Perfume> call, Throwable t) {

                                                }
                                            });
                                        }
                                        Toast.makeText(CheckoutActivity.this,"Successfully",Toast.LENGTH_SHORT);
                                        Intent intent = new Intent(CheckoutActivity.this,ViewCartActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Order> call, Throwable t) {

                                }
                            });
                         } else {
                             List<Order> orders = new ArrayList<>();
                             int length = response.body().length;
                             Order[] orders1 = response.body();
                             for (int i = 0; i < response.body().length; i++) {
                                 orders.add(response.body()[i]);
                             }
                             long idMax = orders.stream().max(Comparator.comparingLong(o -> o.getId())).get().getId();
                             double total = 0;
                             for (Cart c:myCart) {
                                 total += c.getUnitPrice() *  c.getQuantity();
                             }
                             Order order = new Order(idMax + 1,userId, new Date().toString(),new Date().toString(),total, OrderStatus.UNPAID.toString());
                             final long orderId = idMax + 1;
                             Call<Order> orderCreateCall = orderService.createOrder(order);
                             orderCreateCall.enqueue(new Callback<Order>() {
                                 @Override
                                 public void onResponse(Call<Order> call, Response<Order> response) {
                                     if(response.body() != null){
                                         for (Cart cart:myCart) {
                                             OrderDetail orderDetail = new OrderDetail(orderId,cart.getIdPerfume(),cart.getUnitPrice(),cart.getQuantity());
                                             Call<OrderDetail> orderDetailCall = orderDetailService.createOrderDetail(orderDetail);
                                             orderDetailCall.enqueue(new Callback<OrderDetail>() {
                                                 @Override
                                                 public void onResponse(Call<OrderDetail> call, Response<OrderDetail> response) {
                                                     if(response.body() != null){
                                                         boolean bool = true;
                                                     }
                                                 }

                                                 @Override
                                                 public void onFailure(Call<OrderDetail> call, Throwable t) {

                                                 }
                                             });
                                         }
                                         for (Cart cart:myCart) {
                                             Call<Perfume> perfumeCall = perfumeService.getPerfume(cart.getIdPerfume());
                                             perfumeCall.enqueue(new Callback<Perfume>() {
                                                 @Override
                                                 public void onResponse(Call<Perfume> call, Response<Perfume> response) {
                                                     Perfume perfume = response.body();
                                                     perfume.setUnitInStock(perfume.getUnitInStock() - cart.getQuantity());
                                                     Call<Perfume> perfumeCall2 = perfumeService.updatePerfume(cart.getIdPerfume(), perfume);
                                                     perfumeCall2.enqueue(new Callback<Perfume>() {
                                                         @Override
                                                         public void onResponse(Call<Perfume> call, Response<Perfume> response) {

                                                         }

                                                         @Override
                                                         public void onFailure(Call<Perfume> call, Throwable t) {

                                                         }
                                                     });

                                                 }

                                                 @Override
                                                 public void onFailure(Call<Perfume> call, Throwable t) {

                                                 }
                                             });
                                         }
                                         Toast.makeText(CheckoutActivity.this,"Successfully",Toast.LENGTH_SHORT);
                                         Intent intent = new Intent(CheckoutActivity.this,ViewCartActivity.class);
                                         startActivity(intent);
                                     }
                                 }

                                 @Override
                                 public void onFailure(Call<Order> call, Throwable t) {

                                 }
                             });
                         }
                    }

                    @Override
                    public void onFailure(Call<Order[]> call, Throwable t) {

                    }
                });
            }
        });

    }
    private void deleteCart(){
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Cart> myCart = database.cartDao().getAllPerfumesByUserID(userId);
                for (Cart mCart:myCart) {
                    try {
                        database.cartDao().delete(mCart);
                    } catch (Exception e){
                        Log.e("tag",e.getMessage());
                    }
                }
            }
        });
    }
    private void checkoutNow(final List<Cart> carts){
        CreateOrder orderApi = new CreateOrder();
        try {
            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    double total = 0;
                    for (Cart c:carts) {
                        total += c.getUnitPrice() *  c.getQuantity();
                    }
                    total = (int) Math.round(total) * 20000;
                    String formatTotal = String.valueOf(total).replace(".0","");
                    try {
                        JSONObject data = orderApi.createOrder(formatTotal);
                        String code = data.getString("returncode");
                        if (code.equals("1")) {

                            String token = data.getString("zptranstoken");

                            ZaloPaySDK.getInstance().payOrder(CheckoutActivity.this, token, "demozpdk://app", new PayOrderListener() {
                                @Override
                                public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                                    Toast.makeText(CheckoutActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                                    AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            List<Order> orderList = new ArrayList<>();
                                            Call<Order[]> call = orderService.getAllOrders();
                                            call.enqueue(new Callback<Order[]>() {
                                                @Override
                                                public void onResponse(Call<Order[]> call, Response<Order[]> response) {
                                                    if(response.body() == null){
                                                        int maxId = 0;
                                                        double total = 0;
                                                        for (Cart c:carts) {
                                                            total += c.getUnitPrice() *  c.getQuantity();
                                                        }
                                                        Order order = new Order(maxId + 1,userId, new Date().toString(),new Date().toString(),total, OrderStatus.PAID.toString());
                                                        Call<Order> orderCreateCall = orderService.createOrder(order);
                                                        orderCreateCall.enqueue(new Callback<Order>() {
                                                            @Override
                                                            public void onResponse(Call<Order> call, Response<Order> response) {
                                                                if(response.body() == null){
                                                                    for (Cart cart:carts) {
                                                                        OrderDetail orderDetail = new OrderDetail(1,cart.getIdPerfume(),cart.getUnitPrice(),cart.getQuantity());
                                                                        Call<OrderDetail> orderDetailCall = orderDetailService.createOrderDetail(orderDetail);
                                                                        orderDetailCall.enqueue(new Callback<OrderDetail>() {
                                                                            @Override
                                                                            public void onResponse(Call<OrderDetail> call, Response<OrderDetail> response) {
                                                                                if(response.body() != null){
                                                                                    boolean bool = true;
                                                                                    try {
                                                                                        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                database.cartDao().delete(cart);
                                                                                            }
                                                                                        });
                                                                                    } catch (Exception e){
                                                                                        Log.e("e",e.getMessage());
                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<OrderDetail> call, Throwable t) {

                                                                            }
                                                                        });
                                                                    }
                                                                    for(Cart cart:carts){
                                                                        Call<Perfume> perfumeCall = perfumeService.getPerfume(cart.getIdPerfume());
                                                                        perfumeCall.enqueue(new Callback<Perfume>() {
                                                                            @Override
                                                                            public void onResponse(Call<Perfume> call, Response<Perfume> response) {
                                                                                Perfume perfume = response.body();
                                                                                perfume.setUnitInStock(perfume.getUnitInStock() - cart.getQuantity());
                                                                                Call<Perfume> perfumeCall2 = perfumeService.updatePerfume(cart.getIdPerfume(), perfume);
                                                                                perfumeCall2.enqueue(new Callback<Perfume>() {
                                                                                    @Override
                                                                                    public void onResponse(Call<Perfume> call, Response<Perfume> response) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onFailure(Call<Perfume> call, Throwable t) {

                                                                                    }
                                                                                });

                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<Perfume> call, Throwable t) {

                                                                            }
                                                                        });
                                                                    }
                                                                    Toast.makeText(CheckoutActivity.this,"Successfully",Toast.LENGTH_SHORT);
                                                                    Intent intent = new Intent(CheckoutActivity.this,ViewCartActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Order> call, Throwable t) {

                                                            }
                                                        });
                                                    } else {
                                                        List<Order> orders = new ArrayList<>();
                                                        int length = response.body().length;
                                                        Order[] orders1 = response.body();
                                                        for (int i = 0; i < response.body().length; i++) {
                                                            orders.add(response.body()[i]);
                                                        }
                                                        long idMax = orders.stream().max(Comparator.comparingLong(o -> o.getId())).get().getId();
                                                        double total = 0;
                                                        for (Cart c:carts) {
                                                            total += c.getUnitPrice() *  c.getQuantity();
                                                        }
                                                        Order order = new Order(idMax + 1,userId, new Date().toString(),new Date().toString(),total, OrderStatus.PAID.toString());
                                                        final long orderId = idMax + 1;
                                                        Call<Order> orderCreateCall = orderService.createOrder(order);
                                                            orderCreateCall.enqueue(new Callback<Order>() {
                                                            @Override
                                                            public void onResponse(Call<Order> call, Response<Order> response) {
                                                                Order od = response.body();
                                                                if(response.body() != null){
                                                                    for (Cart cart:carts) {
                                                                        OrderDetail orderDetail = new OrderDetail(orderId,cart.getIdPerfume(),cart.getUnitPrice(),cart.getQuantity());
                                                                        Call<OrderDetail> orderDetailCall = orderDetailService.createOrderDetail(orderDetail);
                                                                        orderDetailCall.enqueue(new Callback<OrderDetail>() {
                                                                            @Override
                                                                            public void onResponse(Call<OrderDetail> call, Response<OrderDetail> response) {
                                                                                if(response.body() != null){
                                                                                    boolean bool = true;
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<OrderDetail> call, Throwable t) {

                                                                            }
                                                                        });
                                                                    }
                                                                    for (Cart cart:carts) {
                                                                        Call<Perfume> perfumeCall = perfumeService.getPerfume(cart.getIdPerfume());
                                                                        perfumeCall.enqueue(new Callback<Perfume>() {
                                                                            @Override
                                                                            public void onResponse(Call<Perfume> call, Response<Perfume> response) {
                                                                                Perfume perfume = response.body();
                                                                                perfume.setUnitInStock(perfume.getUnitInStock() - cart.getQuantity());
                                                                                Call<Perfume> perfumeCall2 = perfumeService.updatePerfume(cart.getIdPerfume(), perfume);
                                                                                perfumeCall2.enqueue(new Callback<Perfume>() {
                                                                                    @Override
                                                                                    public void onResponse(Call<Perfume> call, Response<Perfume> response) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onFailure(Call<Perfume> call, Throwable t) {

                                                                                    }
                                                                                });

                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<Perfume> call, Throwable t) {

                                                                            }
                                                                        });
                                                                    }
                                                                    Toast.makeText(CheckoutActivity.this,"Successfully",Toast.LENGTH_SHORT);
                                                                    Intent intent = new Intent(CheckoutActivity.this,ViewCartActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Order> call, Throwable t) {
                                                                 Log.e("tag",t.getMessage());
                                                            }
                                                        });

                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Order[]> call, Throwable t) {

                                                }
                                            });
                                        }
                                    });

                                }

                                @Override
                                public void onPaymentCanceled(String zpTransToken, String appTransID) {
                                    Toast.makeText(CheckoutActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                                    Toast.makeText(CheckoutActivity.this, "Thanh toán thất bại" + zaloPayError.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (Exception e){
                            Log.e("error",e.getMessage());
                    }
                }
            });
        }catch (Exception e){

        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
    private void payNow(){

        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Cart> myCart = database.cartDao().getAllPerfumesByUserID(userId);
                checkoutNow(myCart);
            }
        });

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
            startActivity(new Intent(CheckoutActivity.this, ViewCartActivity.class));
        }
        else if (item.getItemId() == R.id.menu_logout) {
            // process for logout feature
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(CheckoutActivity.this, SignInActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}