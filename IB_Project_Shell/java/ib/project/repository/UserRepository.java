package ib.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ib.project.model.User;

public interface UserRepository extends JpaRepository<User,Long>{

	User findByEmail(String email);

	
}
