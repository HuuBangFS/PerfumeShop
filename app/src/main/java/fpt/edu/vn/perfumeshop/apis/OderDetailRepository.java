package fpt.edu.vn.perfumeshop.apis;

public class OderDetailRepository {
    public static OrderDetailService getOrderDetailService() {
        return ApiClient2.getClient().create(OrderDetailService.class);
    }
}
