package POJO.Authorization;

public class RequestAuthorizationPOJO {

    private String email;
    private String password;

    public RequestAuthorizationPOJO() {
    }

    public RequestAuthorizationPOJO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
