package fpt.edu.vn.perfumeshop.apis;

import fpt.edu.vn.perfumeshop.models.Perfume;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PerfumeService {
    String PERFUMES = "PerfumeTbl";
    @GET(PERFUMES)
    Call<Perfume[]> getAllPerfume();

    @GET(PERFUMES + "/{id}")
    Call<Perfume> getPerfume(@Path("id") Object id);

    @POST(PERFUMES)
    Call<Perfume> createPerfume(@Body Perfume customer);

    @PUT(PERFUMES + "/{id}")
    Call<Perfume> updatePerfume(@Path("id") Object id, @Body Perfume customer);

    @DELETE(PERFUMES + "/{id}")
    Call<Perfume> deletePerfume(@Path("id") Object id);
}
