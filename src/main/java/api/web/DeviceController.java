package api.web;

import api.Dto.DeviceDto;
import api.Entity.Device;
import api.repository.DeviceRepository;
import api.utils.ResultUtil;
import api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    private DeviceRepository deviceRepository;

    @GetMapping("/add")
    public Result addRoom(int device, HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        Device deviceadd =new Device();
        deviceadd.setId(device);
        if (deviceRepository.save(deviceadd)!=null)
            return  ResultUtil.Success(deviceadd);
        else {return ResultUtil.Error("添加失败");}
    }


    @GetMapping("/delete")
    public Result deleteRoom(int device,HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        Device deldevice=deviceRepository.findById(device);
        deviceRepository.delete(deldevice);
        return ResultUtil.Success(deldevice);
    }

    @PostMapping("/update")
    public Result updateRoom(@RequestBody DeviceDto deviceDto, HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        Device updevice=deviceRepository.findById(deviceDto.getDevice());
        updevice.setId(deviceDto.getNewdevice());
        if (deviceRepository.save(updevice)!=null)
            return ResultUtil.Success(deviceDto.getNewdevice());
        else return ResultUtil.Error("更新失败");
    }


    @GetMapping("/select")
    public Result selectRoom(HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        List<Device> devices=deviceRepository.findAll();
        return ResultUtil.Success(devices);
    }







}
