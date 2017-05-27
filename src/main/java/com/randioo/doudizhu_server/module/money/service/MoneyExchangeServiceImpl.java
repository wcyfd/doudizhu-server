package com.randioo.doudizhu_server.module.money.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.dao.RoleDao;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.MoneyExchange.MoneyExchangeResponse;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.HttpConnection;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.utils.TimeUtils;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;

@Service("moneyExchangeService")
public class MoneyExchangeServiceImpl extends ObserveBaseService implements MoneyExchangeService {

	@Autowired
	private RoleDao roleDao;



	@Override
	public GeneratedMessage moneyExchange(Role role, boolean add, int num) {
		System.out.println("@@@"+role.getAccount());
		Integer max = roleDao.moneyExchangeNum(role.getAccount(), role.getRoleId());
		int randiooMoney = num;
		if(max == null){
			max = 0;
		}
		if(num % 1000 != 0 || num * 0.001 < 1){
			return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
		}
		if(add){			
			num *= 0.1;
		}else{
			//TODO 每天5W
			String today = TimeUtils.getCurrentTimeStr();
			Date date;
			Date todayDate;
			String time = roleDao.getMoneyExchangeTime(role.getAccount(), role.getRoleId());			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				if(time == null){
					max = num;
				}else{
					date = df.parse(time);
					SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
					date = df2.parse(df2.format(date));
					todayDate = df2.parse(today);
					if(todayDate.compareTo(date) == 0){
						if(max + num > 50000){
							return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
						}
						else{
							max += num;
						}
					}
					else{
						max = num;
					}
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			num *= 0.085;
			
		}
		if(exchangeMoney(role, num, add)){
			role.setMoney(role.getMoney()+(add?1:-1)*randiooMoney);
			if(!add){
				roleDao.updateLimit(max, TimeUtils.getDetailTimeStr(), role.getRoleId());
			}
			roleDao.update(role);
			RoleCache.putNewRole(role);
    		return SC.newBuilder().setMoneyExchangeResponse(MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber())).build();
    	}else{
    		return SC.newBuilder().setMoneyExchangeResponse(MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.NO_MONEY.getNumber())).build();
    	}
    	
		
	}
	
	public  boolean exchangeMoney(Role role, int money, boolean add){
		String url = /*"http://manager.app.randioo.com/gateway/MaJiang/changeMoney.php?key=f4f3f65d6d804d138043fbbd1843d510&&id=";*/
				"http://10.0.51.6/APPadmin/gateway/MaJiang/changeMoney.php?key=f4f3f65d6d804d138043fbbd1843d510&&id=";
    	String tpram = "";
    	tpram = tpram.concat(role.getAccount());
    	tpram = tpram.concat("&&money_num="+money);
    	tpram = tpram.concat("&&type="+(add?0:1));
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
