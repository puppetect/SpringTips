package com.example.mybatis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class MybatisApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisApplication.class, args);
	}

	@Bean
	CommandLineRunner demo (CarMapper carMapper) {
		return args -> {
			List<Car> cars = Arrays.asList(
					new Car("Honda", "Civic", 1984, null),
					new Car("BMW", "330i", 2012, null),
					new Car("Infiniti", "Q50", 2014, null)
			);
			cars.forEach(car -> {
				carMapper.insert(car);
				System.out.println(car.toString());
			});
			System.out.println("-------------");
			carMapper.selectAll().forEach(System.out::println);

			System.out.println("-------------");
			carMapper.search("Honda", null ,0).forEach(System.out::println);

		};
	}

}

@Mapper
interface CarMapper {
	@Options(useGeneratedKeys=true)
	@Insert("insert into car(make, model, year) values (#{make}, #{model}, #{year})")
	void insert(Car car);
	@Select("select * from car")
	Collection<Car> selectAll();
	@Delete("delete from car where id = #{id}")
	void deleteById(long id);

	Collection<Car> search(@Param("make") String make, @Param("model") String model, @Param("year") int year);
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Car {
	private String make, model;
	private int year;
	private Long id;
}
