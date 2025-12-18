package com.asv.hotel.repositories;

import com.asv.hotel.entities.Room;
import com.asv.hotel.entities.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAll();

    @Query(value = "SELECT * FROM rooms WHERE number ILIKE :number ", nativeQuery = true)
    Optional<Room> findRoomByNumberLikeIgnoreCase(String number);

    @Query(value = "SELECT * FROM rooms WHERE type ILIKE :type ", nativeQuery = true)
    List<Room> findRoomByTypeLikeIgnoreCase(@Param("type") RoomType type);

    @Query(value = "SELECT * FROM rooms WHERE pricePerNight BETWEEN :min AND :max", nativeQuery = true)
    List<Room> findRoomByPricePerNightBetween(@Param("min") BigDecimal min,@Param("max") BigDecimal max);

    @Modifying
    @Query(value = "DELETE FROM rooms WHERE number ILIKE :number", nativeQuery = true)
    int deleteRoomByNumberLikeIgnoreCase(@Param("number") String number);

    @Modifying
    @Query(value = """
            UPDATE rooms SET number = :number, type = :type, description = :description, 
            capacity = :capacity, price_per_night = :pricePerNight, is_available = :isAvailable WHERE id = :roomId
            """, nativeQuery = true)
    int updateRoom(@Param("roomId") Long roomId, @Param("number") String number, @Param("type") RoomType type,
                   @Param("description") String description, @Param("capacity") Integer capacity,
                   @Param("pricePerNight") BigDecimal pricePerNight, @Param("isAvailable") Boolean isAvailable);

}
