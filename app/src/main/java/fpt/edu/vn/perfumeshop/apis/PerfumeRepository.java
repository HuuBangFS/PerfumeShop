package fpt.edu.vn.perfumeshop.apis;

public class PerfumeRepository {
    public static PerfumeService getPerfumeService() {
        return ApiClient.getClient().create(PerfumeService.class);
    }
}
