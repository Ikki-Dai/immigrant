package com.ikki.immigrant.infrastructure.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

class LombokAndMapStructTest {


    @Test
    void test() {
        Car car = new Car();
        car.setMake("China");
        car.setType(CarType.SEDAN);
        car.setNumberOfSeats(3);
        CarDto carDto = CarMapper.INSTANCE.carToCarDto(car);
        System.out.println(carDto);
        Assertions.assertNotNull(carDto);
    }

    @Test
    void testEnum() {
        CarDto carDto = new CarDto();
        carDto.setMake("China");
        carDto.setType("SEDAN");
        carDto.setSeatCount(4);
        Car car = CarMapper.INSTANCE.carDtoToCar(carDto);
        Assertions.assertNotNull(car);
    }


    public enum CarType {
        SEDAN
    }

    @Mapper()
    public interface CarMapper {

        CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

        @Mapping(source = "numberOfSeats", target = "seatCount")
        CarDto carToCarDto(Car car);

        @Mapping(source = "seatCount", target = "numberOfSeats")
        Car carDtoToCar(CarDto carDto);
    }

    @Getter
    @Setter
    @ToString
    public static class Car {

        private String make;
        private int numberOfSeats;
        private CarType type;

        //constructor, getters, setters etc.
    }

    @Getter
    @Setter
    @ToString
    public static class CarDto {

        private String make;
        private int seatCount;
        private String type;

        //constructor, getters, setters etc.
    }

}
