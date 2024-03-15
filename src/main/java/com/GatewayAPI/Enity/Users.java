package com.GatewayAPI.Enity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "users")
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;
    private String email;
    private String password;
    private String phone_number;
    private String full_name;
    private boolean gender;
    private Date dob;
    private String avatar;
    private Integer role_id;
}
