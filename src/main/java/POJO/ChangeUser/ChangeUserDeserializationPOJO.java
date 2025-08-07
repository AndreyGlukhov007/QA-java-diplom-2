package POJO.ChangeUser;

import POJO.CreateUser.CreateUserDeserializationPOJO.User;

public class ChangeUserDeserializationPOJO {

    private Boolean success;
    private User user;

    public ChangeUserDeserializationPOJO() {
    }

    public ChangeUserDeserializationPOJO(Boolean success, User user) {
        this.success = success;
        this.user = user;
    }

    public Boolean getSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
