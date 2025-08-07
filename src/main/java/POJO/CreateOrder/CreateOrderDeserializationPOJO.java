package POJO.CreateOrder;

import java.util.List;

public class CreateOrderDeserializationPOJO {

    private boolean success;
    private List<Data> data;

    public CreateOrderDeserializationPOJO(){};

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

}
