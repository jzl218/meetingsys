package api.entity;

import lombok.Data;
import org.jsets.shiro.model.Account;

import javax.persistence.*;

@Entity
@Data
public class Manager implements Account {
    @Id
    private Integer id;

    @Column
    private String account;

    @Column
    private String password;


}
