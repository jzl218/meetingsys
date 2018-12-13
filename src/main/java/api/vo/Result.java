package api.vo;



import lombok.Data;

@Data

public class Result {
	private Integer code;

	private String msg;

	private Object data;
	

	public interface Success {}
	
	public interface Error {}
}
