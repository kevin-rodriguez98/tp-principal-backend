package com.java.tp_principal_backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
	private String legajo;
	private String password;
}
