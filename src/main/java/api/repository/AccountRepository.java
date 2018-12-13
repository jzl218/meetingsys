package api.repository;

import api.entity.Account;
import api.sevice.AccountProviderImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    Account findByName(String name);

    Account findByNameAndPassword(String name,String password);

    Account findByIdAndPassword(String id,String password);

    Account findById(String id);


}
