package api.repository;

import api.Entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRespository extends JpaRepository<Manager,Integer> {
    Manager findByAccountAndPassword(String accout,String password);
    Manager findByAccount(String account);

}
