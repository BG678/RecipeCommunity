package com.recipecommunity.jwt;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JwtRequest)) return false;
        JwtRequest that = (JwtRequest) o;
        return getUsername().equals(that.getUsername()) &&
                getPassword().equals(that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword());
    }
}