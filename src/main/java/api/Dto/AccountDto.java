package api.Dto;

import lombok.Data;

import javax.persistence.Column;


@Data
public class AccountDto {

    private  String id;


    private String password;


    private String name;


    private String faceImg;


    private String phonenum;


    private String email;
}
