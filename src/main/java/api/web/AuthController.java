package api.web;

import api.Dto.AccountDto;
import api.Dto.AdminLoginDto;
import api.Dto.PassDto;
import api.Dto.UserDto;
import api.base.ImageInfo;
import api.Entity.Account;
import api.Entity.Manager;
import api.repository.AccountRepository;
import api.repository.ManagerRespository;
import api.sevice.AccountProviderImpl;
import api.sevice.FaceImpl;
import api.utils.BeanUtils;
import api.utils.PicUtil;
import api.utils.ResultUtil;
import api.vo.Result;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.enums.ImageFormat;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jsets.shiro.util.CryptoUtil;
import org.jsets.shiro.util.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    public String addId="6NicX292WuD2npxeHZPaCQSVbfuN6DionSMfu2XRdwep";
    public String sdkKet="HMXxTNqu3rnhbhGZB3wNURFCLVb4tiriTMXpxnytMKHM";
    @Autowired
    private ManagerRespository managerRespository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountProviderImpl accountProvider;
    @Autowired
    private FaceImpl face;






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
        String file= PicUtil.decode64(accountDto.getFace());
        ImageInfo imageInfo=face.getRGBData(new File(file));
        FaceEngine faceEngine=new FaceEngine();
        faceEngine.active(addId,sdkKet);
        faceEngine.init(face.getengineConfig());
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        faceEngine.detectFaces(imageInfo.getRgbData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList);
        FaceFeature faceFeature = new FaceFeature();
        faceEngine.extractFaceFeature(imageInfo.getRgbData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
        account.setFace(faceFeature.getFeatureData());
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
                , userDto.getId()
                , userDto.getId()
                , 10000*6*60*24*15l
                , "base"
                ,null
                , SignatureAlgorithm.HS512
        );
        Map map=new HashMap();
        map.put("id",userDto.getId());
        map.put("name",accountRepository.findByIdAndPassword(userDto.getId(),userDto.getPassword()).getName());
        map.put("jwt",jwt);
        return ResultUtil.Success(map);
    }



    @PostMapping("/updatepass")//TODO
    public Result updatePass(@RequestBody PassDto passDto){
        if (passDto.getOldpass().equals(accountProvider.getNowAccout().getPassword()))
        accountProvider.getNowAccout().setPassword(passDto.getNewpass());
        else return ResultUtil.Error("旧密码输入错误");
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

    @GetMapping("/deletebyid")
    public Result deleteById(String id,HttpServletRequest request){
        if (request.getSession().getAttribute("admin") == null) {
            return ResultUtil.Error("请先登录");
        }
        accountRepository.deleteAccountById(id);
        return ResultUtil.Success();

    }

}

