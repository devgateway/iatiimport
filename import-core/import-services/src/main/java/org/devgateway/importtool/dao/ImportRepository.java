package org.devgateway.importtool.dao;

import org.devgateway.importtool.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ImportRepository extends PagingAndSortingRepository<User, Long> {


}
