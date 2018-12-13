package api.utils;


import api.vo.Result;

public class ResultUtil {

    public static Result Success(Object data){
        Result result=new Result();
        result.setCode(0);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    public static Result Success(){
        Result result=new Result();
        result.setCode(0);
        result.setMsg("成功");
        return result;
    }

    public static Result Error(String msg){
        Result result=new Result();
        result.setCode(1);
        result.setMsg(msg);
        return result;
    }

}
