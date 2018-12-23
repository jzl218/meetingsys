package api.repository;

import api.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    Account findByName(String name);

    Account findByNameAndPassword(String name,String password);

    Account findByIdAndPassword(String id,String password);

    Account findById(String id);

    List<Account> findByIdIn(List<String> ids);
    @Transactional
    void  deleteAccountById(String id);

}
