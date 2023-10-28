package fpt.edu.vn.perfumeshop.apis;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient2 {
    public static String baseURL = "https://6537b7bca543859d1bb0b253.mockapi.io/";
    public static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return  retrofit;
    }
}
