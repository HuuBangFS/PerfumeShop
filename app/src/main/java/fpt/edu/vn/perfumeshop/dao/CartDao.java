package fpt.edu.vn.perfumeshop.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.perfumeshop.models.Cart;

@Dao
public interface CartDao {
    @Query("SElECT * FROM cart")
    List<Cart> getAllPerfume();

    @Query("SELECT * FROM cart WHERE id IN (:cartId)")
    Cart getCartById(int cartId);

    @Insert
    void insert (Cart cart);

    @Update
    void update (Cart cart);

    @Delete
    void delete (Cart cart);
    @Query("SELECT Max(id) From cart")
    long maxId();

    @Query("SELECT * FROM cart WHERE idCustomer = :userId")
    List<Cart> getAllPerfumesByUserID(long userId);
}
