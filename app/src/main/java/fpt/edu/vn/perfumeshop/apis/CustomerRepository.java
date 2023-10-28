package fpt.edu.vn.perfumeshop.apis;

public class CustomerRepository {
    public static CustomerService getCustomerService() {
        return ApiClient.getClient().create(CustomerService.class);
    }
}
