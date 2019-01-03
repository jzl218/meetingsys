package api.vo;


import api.Entity.Account;
import lombok.Data;

import java.util.List;

@Data
public class MeetingDetailVO {
    List<Account> joiners;
}
