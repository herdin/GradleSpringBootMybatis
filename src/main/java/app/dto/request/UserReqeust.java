package app.dto.request;

public class UserReqeust {
    private int id;
    private String name;
    public UserReqeust(){}
    public UserReqeust(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserReqeust{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
