package ib.project.service;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ib.project.model.User;
import ib.project.repository.UserRepository;

@Service
public class UserService implements UserServiceInterface{

	
	@Autowired
	private UserRepository userRepository;
	

	@Override
	public User findById(Long id) {
		return userRepository.findOne(id);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public List<User> findByActiveTrue() {
		return userRepository.findByActiveTrue();
	}

	@Override
	public List<User> findByActiveFalse() {
		return userRepository.findByActiveFalse();
	}

	@Override
	public List<User> findActiveByEmail(String email) {
		return userRepository.findActiveByEmail(email);
	}

	@Override
	public List<User> findInactiveByEmail(String email) {
		return userRepository.findInactiveByEmail(email);
	}
	
	
}
