package api.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Account implements org.jsets.shiro.model.Account {
    @Id
    private String id;

    @Column
    private String password;

    @Column
    private String name;

    @Column

    private byte[] face;

    @Column
    private String faceurl;

    @Column
    private String phonenum;

    @Column
    private String email;

    @Override
    public String getAccount() {
        return id;
    }
}
