package api.Entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Device {
    @Id
    private Integer id;

    @Column
    private Integer state;
}
