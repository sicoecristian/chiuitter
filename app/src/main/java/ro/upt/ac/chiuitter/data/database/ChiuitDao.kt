package ro.upt.ac.chiuitter.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ChiuitDao {

    @Query("SELECT * FROM chiuits")
    fun getAll(): List<ChiuitEntity>


    @Insert
    fun insertChiuit(chiuit: ChiuitEntity)


    @Delete
    fun deleteChiuit(chiut: ChiuitEntity)


}