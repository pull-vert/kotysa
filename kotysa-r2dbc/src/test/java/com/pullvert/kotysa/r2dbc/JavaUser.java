/*
 * Copyright 2019 the original author or authors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.pullvert.kotysa.r2dbc;

import com.pullvert.kotysa.annotations.Nullable;

import java.util.Objects;

/**
 * Basic Entity
 * @author Fred Montariol
 */
public class JavaUser {
    private String login;
    private String firstname;
    private String lastname;
    private boolean isAdmin;
    private String alias;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Nullable
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JavaUser javaUser = (JavaUser) o;

        if (isAdmin != javaUser.isAdmin) {
            return false;
        }
        if (!Objects.equals(login, javaUser.login)) {
            return false;
        }
        if (!Objects.equals(firstname, javaUser.firstname)) {
            return false;
        }
        if (!Objects.equals(lastname, javaUser.lastname)) {
            return false;
        }
        return Objects.equals(alias, javaUser.alias);

    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (isAdmin ? 1 : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }
}
