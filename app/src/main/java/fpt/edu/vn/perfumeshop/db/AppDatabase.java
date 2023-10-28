package fpt.edu.vn.perfumeshop.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import fpt.edu.vn.perfumeshop.dao.CartDao;
import fpt.edu.vn.perfumeshop.models.Cart;

@Database(entities = {Cart.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CartDao cartDao();
}
