package com.berkin.cryptoportfolio.repository;

import com.berkin.cryptoportfolio.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
