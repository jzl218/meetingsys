package api.sevice;

import api.repository.AccountRepository;
import api.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.AuthenticationException;
import org.jsets.shiro.model.Account;
import org.jsets.shiro.service.ShiroAccountProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service

@Setter
public class AccountProviderImpl implements ShiroAccountProvider {
    @Autowired
    private AccountRepository accountRepository;



    private api.Entity.Account nowAccout;

    @Override
    public Account loadAccount(String account) throws AuthenticationException {
        return nowAccout;
    }

    @Override
    public Set<String> loadRoles(String account) {
        String jwt= JsonUtils.getAccount(account);
        nowAccout=accountRepository.findById(jwt);
        Set<String> roles=new HashSet<>();
        roles.add("base");
        return roles;
    }

    @Override
    public Set<String> loadPermissions(String s) {
        return null;
    }

}
