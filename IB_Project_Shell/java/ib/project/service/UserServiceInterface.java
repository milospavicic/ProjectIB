package ib.project.service;

import java.util.List;

import ib.project.model.User;

public interface UserServiceInterface {

	User findById(Long id);
	
	User findByEmail(String email);
	
	List<User> findAll();
	
	User save(User user);
	
}
