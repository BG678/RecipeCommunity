package com.recipecommunity.jwt;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * POJO class that contains payload required to authenticate and register user
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
public class JwtRequest implements Serializable {
    private static final long serialVersionUID = 1234561L;
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public JwtRequest() {
    }

    public JwtRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}