package ib.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ib.project.model.User;

public interface UserRepository extends JpaRepository<User,Long>{

	User findByEmail(String email);

	List<User> findAllByEmail(String email);

	@Query(value="SELECT * FROM users AS u WHERE u.active = true",nativeQuery=true)
	List<User> findByActiveTrue();

	@Query(value="SELECT * FROM users AS u WHERE u.active = false",nativeQuery=true)
	List<User> findByActiveFalse();
	
	@Query(value="SELECT * FROM users AS u WHERE u.active = true AND u.email LIKE ?",nativeQuery=true)
	List<User> findActiveByEmail(String email);
	
	@Query(value="SELECT * FROM users AS u WHERE u.active = false AND u.email LIKE ?",nativeQuery=true)
	List<User> findInactiveByEmail(String email);
	
}
