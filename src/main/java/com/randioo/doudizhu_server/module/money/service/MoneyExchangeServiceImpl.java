package com.randioo.doudizhu_server.module.money.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.dao.RoleDao;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.MoneyExchange.MoneyExchangeRequest;
import com.randioo.doudizhu_server.protocol.MoneyExchange.MoneyExchangeResponse;
import com.randioo.doudizhu_server.util.HttpConnection;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;

//@Service("moneyExchangeService")
public class MoneyExchangeServiceImpl extends ObserveBaseService implements MoneyExchangeService {

	//@Autowired
	private RoleDao roleDao;



	@Override
	public GeneratedMessage moneyExchange(Role role, MoneyExchangeRequest request) {
		boolean add = request.getAdd();
		int num = request.getNum();
		if(num % 1000 != 0 || num * 0.001 < 1){
			return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
		}
		if(add){			
			num *= 0.1;
		}else{
			num *= 0.085;
			//TODO 每天5W
			Date date;
			String time = roleDao.getMoneyExchangeTime(role.getAccount(), role.getRoleId());
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				date = df.parse(time);
				SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
				date = df2.parse(df2.format(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		if(exchangeMoney(role, num, add)){
    		return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()).build();
    	}else{
    		return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.NO_MONEY.getNumber()).build();
    	}
    	
		
	}
	
	private  boolean exchangeMoney(Role role, int money, boolean add){
		String url = /*"http://manager.app.randioo.com/gateway/MaJiang/changeMoney.php?key=f4f3f65d6d804d138043fbbd1843d510&&id=";*/
				"http://10.0.51.6/APPadmin/gateway/MaJiang/changeMoney.php?key=f4f3f65d6d804d138043fbbd1843d510&&id=";
    	String tpram = "";
    	tpram = tpram.concat(role.getAccount());
    	tpram = tpram.concat("&&money_num="+money);
    	tpram = tpram.concat("&&type="+(add?1:0));
    	System.out.println("URL"+url);
    	System.out.println("PRAM"+tpram);
    	HttpConnection conn = new HttpConnection(url,tpram);
    	conn.connect();
    	System.out.println(conn.result+""+conn.result.getClass().getName()+(conn.result.charAt(0) == '1'));
    	if(conn.result.charAt(0) == '1'){
    		return true;
    	}else{
    		return false;
    	}
	}

	

}
