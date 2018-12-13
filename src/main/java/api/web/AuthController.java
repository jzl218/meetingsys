package api.web;

import api.Dto.AccountDto;
import api.Dto.AdminLoginDto;
import api.Dto.PassDto;
import api.Dto.UserDto;
import api.entity.Account;
import api.entity.Manager;
import api.repository.AccountRepository;
import api.repository.ManagerRespository;
import api.sevice.AccountProviderImpl;
import api.utils.BeanUtils;
import api.utils.PicUtil;
import api.utils.ResultUtil;
import api.vo.ManagerVO;
import api.vo.Result;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jsets.shiro.util.CryptoUtil;
import org.jsets.shiro.util.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private ManagerRespository managerRespository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountProviderImpl accountProvider;






    @PostMapping("/adminlogin")
    public Result adminLogin(@RequestBody AdminLoginDto adminLoginDto, HttpServletRequest session) {
        String account=adminLoginDto.getAccount();
        String password=adminLoginDto.getPassword();
        if (managerRespository.findByAccountAndPassword(account, password) == null) {
            return ResultUtil.Error("账号或用户名错误");
        }

        else {
            Manager manager = managerRespository.findByAccountAndPassword(account, password);
            session.getSession().setAttribute("admin", "manager");
            return ResultUtil.Success(manager.getAccount());

        }
    }


    @GetMapping("/adminlogout")
    public Result adminLoginout(String account,HttpServletRequest session){
        session.getSession().removeAttribute(account);
        return ResultUtil.Success();
    }



    @PostMapping("/register")
    public Result userRegister(@RequestBody AccountDto accountDto,HttpServletRequest session) throws IOException {
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        Account account=new Account();
        BeanUtils.copyProperties(accountDto,account);
        account.setFaceurl(PicUtil.decode64(accountDto.getFace()));
        if (accountRepository.save(account)!=null){
            return ResultUtil.Success();
        }
        else return  ResultUtil.Error("注册失败");
    }


    @PostMapping("/userlogin")
    public Result userLogin(@RequestBody UserDto userDto){
        if (accountRepository.findByIdAndPassword(userDto.getId(),userDto.getPassword())==null){
            return ResultUtil.Error("用户名或密码错误");
        }
        String jwt= CryptoUtil.issueJwt(
                ShiroUtils.getShiroProperties().getJwtSecretKey()
                , userDto.getId().toString()
                , userDto.getId().toString()
                , 10000*6*60*24*15l
                , "base"
                ,null
                , SignatureAlgorithm.HS512
        );
        Map map=new HashMap();
        map.put("id",userDto.getId());
        map.put("name",accountRepository.findByIdAndPassword(userDto.getId(),userDto.getPassword()).getAccount());
        map.put("jwt",jwt);
        return ResultUtil.Success(map);
    }



    @PostMapping("/updatepass")//TODO
    public Result updatePass(@RequestBody PassDto passDto){
        if (passDto.getOldepass().equals(accountProvider.getNowAccout().getPassword()))
        accountProvider.getNowAccout().setPassword(passDto.getNewpass());
        if (accountRepository.save(accountProvider.getNowAccout())!=null)
            return ResultUtil.Success();
        else
            return ResultUtil.Error("修改失败");
    }


    @PostMapping("/updateaccount")
    public Result updateAccount(@RequestBody Account account,HttpServletRequest session) {
        if (session.getSession().getAttribute("admin") == null) {
            return ResultUtil.Error("请先登录");
        }
        Account account1 = accountRepository.findById(account.getId());
        BeanUtils.copyProperties(account, account1);
        if (accountRepository.save(account1) != null)
            return ResultUtil.Success();
        else return ResultUtil.Error("失败");
    }



    @GetMapping("/selectall")
    public Result selectAll(HttpServletRequest request){
        if (request.getSession().getAttribute("admin") == null) {
            return ResultUtil.Error("请先登录");
        }
        return ResultUtil.Success(accountRepository.findAll());
    }



}
