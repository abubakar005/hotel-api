package com.booking.recruitment.hotel.service.impl;

import com.booking.recruitment.hotel.exception.BadRequestException;
import com.booking.recruitment.hotel.exception.ElementNotFoundException;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.repository.HotelRepository;
import com.booking.recruitment.hotel.service.CityService;
import com.booking.recruitment.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class DefaultHotelService implements HotelService {

  private final HotelRepository hotelRepository;

  private final CityService cityService;

  @Autowired
  DefaultHotelService(HotelRepository hotelRepository, CityService cityService) {
    this.hotelRepository = hotelRepository;
    this.cityService = cityService;
  }

  @Override
  public List<Hotel> getAllHotels() {
    return hotelRepository.findAll();
  }

  @Override
  public List<Hotel> getHotelsByCity(Long cityId) {
    return hotelRepository.findAll().stream()
        .filter((hotel) -> cityId.equals(hotel.getCity().getId()))
        .collect(Collectors.toList());
  }

  @Override
  public Hotel createNewHotel(Hotel hotel) {
    if (hotel.getId() != null) {
      throw new BadRequestException("The ID must not be provided when creating a new Hotel");
    }

    return hotelRepository.save(hotel);
  }

  @Override
  public Hotel getHotelById(Long id) {
    Optional<Hotel> optHotel = hotelRepository.findByIdAndDeleted(id, false);

    if(optHotel.isPresent())
      return optHotel.get();
    else
      throw new ElementNotFoundException("Hotel does not exists with this id " + id);
  }

  @Override
  public void deleteHotelById(Long id) {

    Optional<Hotel> optHotel = hotelRepository.findByIdAndDeleted(id, false);

    if(optHotel.isPresent()) {
      Hotel hotel = optHotel.get();
      hotel.setDeleted(Boolean.TRUE);
      hotelRepository.save(hotel);
    } else
      throw new ElementNotFoundException("Hotel does not exists with this id " + id);
  }

  @Override
  public List<Hotel> searchNearestHotel(Long cityId, double distance) {
    if(cityId<1)
      throw new BadRequestException("City is invalid");

    if(distance<0)
      throw new BadRequestException("Distance is invalid");

    return Optional.ofNullable(cityService.getCityById(cityId))
            .map(cityForNearestHotel->{
              double latitude = cityForNearestHotel.getCityCentreLatitude();
              double longitude = cityForNearestHotel.getCityCentreLongitude();
              return hotelRepository.searchNearestHotel(latitude,longitude, distance);
            }).orElseGet(ArrayList::new);
  }

}
