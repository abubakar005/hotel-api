package com.booking.recruitment.hotel.repository;

import com.booking.recruitment.hotel.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Optional<Hotel> findByIdAndDeleted(Long id, boolean deleted);

    String HAVERSINE_PART = "(6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) *" +
            " cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s.latitude))))";
    @Query(value = "SELECT TOP 3 * FROM Hotel s WHERE s.deleted = false and " + HAVERSINE_PART + " < :distance ORDER BY "+ HAVERSINE_PART + " DESC",nativeQuery = true)
    List<Hotel> searchNearestHotel(@Param("latitude") double latitude, @Param("longitude") double longitude,@Param("distance") double distanceWithInKM );
}
