package dhbw.einpro.blogengine.impl;

import java.util.Objects;

import dhbw.einpro.blogengine.interfaces.IUser;

/**
 * Klasse enthält Informationen zu einem Benutzer des Blog-Systems
 */
public class User implements Comparable<User>, IUser {

    private String firstName;
    private String lastName;
    private String email;

    // Pflicht-Konstruktor (wird vom BlogEngineHelper verwendet)
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // ===== IUser =====

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

    // ===== Comparable =====

    /**
     * Vergleich: Nachname → Vorname → Email
     */
    @Override
    public int compareTo(User o) {
        if (o == null) {
            return 1;
        }

        int last = nullSafe(this.lastName).compareToIgnoreCase(nullSafe(o.lastName));
        if (last != 0) {
            return last;
        }

        int first = nullSafe(this.firstName).compareToIgnoreCase(nullSafe(o.firstName));
        if (first != 0) {
            return first;
        }

        return nullSafe(this.email).compareToIgnoreCase(nullSafe(o.email));
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    // ===== equals / hashCode =====
    // Gleichheit basiert auf Email + Vorname + Nachname

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IUser)) return false;

        IUser other = (IUser) obj;

        return Objects.equals(normalize(email), normalize(other.getEmail())) &&
                Objects.equals(normalize(firstName), normalize(other.getFirstName())) &&
                Objects.equals(normalize(lastName), normalize(other.getLastName()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                normalize(email),
                normalize(firstName),
                normalize(lastName)
        );
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }
}

