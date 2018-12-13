package api.Dto;

import lombok.Data;

@Data
public class OrderDto {
    private int state;
    private int page;
    private int size;
}
