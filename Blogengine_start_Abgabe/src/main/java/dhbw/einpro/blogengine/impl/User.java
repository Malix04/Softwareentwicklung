package dhbw.einpro.blogengine.impl;

import java.util.Objects;

import dhbw.einpro.blogengine.interfaces.IUser;


public class User implements Comparable<User>, IUser {

    private String email;
    private String firstName;
    private String lastName;


    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }



    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String p_email) {
        this.email = p_email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String p_firstName) {
        this.firstName = p_firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String p_lastName) {
        this.lastName = p_lastName;
    }

    @Override
    public int compareTo(User o) {
        if (o == null) {
            // nicht-null > null
            return 1;
        }

        int cmp = compareStrings(this.lastName, o.lastName);
        if (cmp != 0) {
            return cmp;
        }

        cmp = compareStrings(this.firstName, o.firstName);
        if (cmp != 0) {
            return cmp;
        }

        return compareStrings(this.email, o.email);
    }

    private static int compareStrings(String a, String b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }

        return a.compareTo(b);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        return Objects.equals(email, other.email)
                && Objects.equals(firstName, other.firstName)
                && Objects.equals(lastName, other.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, firstName, lastName);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}

