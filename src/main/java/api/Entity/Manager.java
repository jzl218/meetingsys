package api.Entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Manager{
    @Id
    private Integer id;

    @Column
    private String account;

    @Column
    private String password;


}
