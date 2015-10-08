package org.devgateway.importtool.dao;

import org.devgateway.importtool.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	User findByUsername(@Param("username") String username);

	User findById(@Param("id") Long id);

	List<User> findUsersByFirstNameOrLastNameOrUsername(
			@Param("firstName") String firstName,
			@Param("lastName") String lastName,
			@Param("username") String username);

}
