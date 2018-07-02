package ib.project.service;

import java.util.List;

import ib.project.model.User;

public interface UserServiceInterface {

	User findById(Long id);
	
	User findByEmail(String email);
	
	List<User> findActiveByEmail(String email);
	
	List<User> findInactiveByEmail(String email);
	
	List<User> findAll();
	
	List<User> findByActiveTrue();
	
	List<User> findByActiveFalse();
	
	User save(User user);
	
}
