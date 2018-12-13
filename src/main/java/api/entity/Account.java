package api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
    private String faceurl;

    @Override
    public String getAccount() {
        return id;
    }
}
