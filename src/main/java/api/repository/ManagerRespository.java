package api.repository;

import api.entity.Manager;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerRespository extends JpaRepository<Manager,Integer> {
    Manager findByAccountAndPassword(String accout,String password);
    Manager findByAccount(String account);

}
