package beerly.ansteph.beerlybiz.model;

/**
 * Created by loicstephan on 2018/06/20.
 */

public class User {

    int id;
    String firstName, lastName, username,created_at, contactNumber;



    public User() {
    }


    public User(int id, String firstName, String lastName, String username, String created_at, String contactNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.created_at = created_at;
        this.contactNumber = contactNumber;
    }


    public User(String firstName, String lastName, String username, String created_at, String contactNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.created_at = created_at;
        this.contactNumber = contactNumber;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
