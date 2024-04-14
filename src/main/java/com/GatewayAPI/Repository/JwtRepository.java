package com.GatewayAPI.Repository;

import com.GatewayAPI.Enity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRepository extends JpaRepository<Users,Integer> {
    @Query(value = "Select u.email from Users u where u.email = ?1 and u.role_id = ?2")
    String findEmail(String email, Integer roleId);
}
