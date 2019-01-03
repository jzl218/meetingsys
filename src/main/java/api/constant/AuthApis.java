package api.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

//表示需要认证的api
@AllArgsConstructor
@Getter
@ToString

public enum AuthApis {

	ADDMEETING(1,"/api/meeting/add"),
	USELECTROOMBYID(2,"/api/room/uselectbyid"),
	USLELECTROOMBYSIZE(3,"/api/room/uselectbysize"),
	USLELECTALLROOM(4,"/api/room/selectAll"),
	UPDATEPASS(5,"/api/auth/updatepass"),
	MEETINGADD(6,"/api/meeting/add"),
	MEETINGJOIN(7,"/api/meeting/join"),
	USELECTMEETINGBYTIME(8,"/api/room/selectbytime"),
	SELECT_ORDER(9,"/api/meeting/selectorder"),
	SELECT_JOIN(10,"/api/meeting/selectjoin"),
	GET_ORDER_COUNT(11,"/api/meeting/getordercount"),
	GET_JOIN_COUNT(12,"/api/meeting/getjoincount"),
	GET_MEETING_DETAIL(13,"/api/meeting/selectbyinvitecode"),
	GET_BY_ALL(14,"/api/room/selectbyall"),
	Q(15,"/api/meeting/selectallorder"),
	SELECT_All_JOIN(16,"/api/meeting/selectalljoin"),
	QQ(17,"/api/meeting/selectbyorder"),
	SELECT_All_JOINC(18,"/api/meeting/selectbyjoin"),
	SELECT_All_JOINCD(19,"/api/meeting/selectall"),
	GET_JOINERS(20,"api/meeting/getjoiner"),
	DELETE_JOINER(21,"api/meeting/deletejoiner")
	;

	/** ?~? 表示教师角色权限*/

	private Integer id;
	private String url;
}
