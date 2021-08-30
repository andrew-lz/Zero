package com.example.zero.user;

import com.example.zero.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaRepository<User, Long> {
    Optional<User> findByEmailAndDeletedFalse(String email);

    List<User> findAllByDeletedFalseOrderByFirstNameAscLastNameAsc();

    Boolean existsByEmailAndDeletedFalse(String email);
}