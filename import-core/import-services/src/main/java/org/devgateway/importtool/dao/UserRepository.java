package org.devgateway.importtool.dao;

import org.devgateway.importtool.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
// FIXME This is not in use remove before closing the feature

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	User findByUsername(@Param("username") String username);

	Optional<User> findById(@Param("id") Long id);

	List<User> findUsersByFirstNameOrLastNameOrUsername(
			@Param("firstName") String firstName,
			@Param("lastName") String lastName,
			@Param("username") String username);

}
