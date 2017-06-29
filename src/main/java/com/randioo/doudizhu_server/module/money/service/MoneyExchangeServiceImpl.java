package com.randioo.doudizhu_server.module.money.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.doudizhu_server.module.login.service.LoginService;
import com.randioo.doudizhu_server.protocol.Error.ErrorCode;
import com.randioo.doudizhu_server.protocol.MoneyExchange.MoneyExchangeResponse;
import com.randioo.doudizhu_server.protocol.ServerMessage.SC;
import com.randioo.doudizhu_server.util.HttpConnection;
import com.randioo.randioo_server_base.config.GlobleConfig;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("moneyExchangeService")
public class MoneyExchangeServiceImpl extends ObserveBaseService implements MoneyExchangeService {

	
	@Autowired
	private LoginService loginService;

	@Override
	public void newRoleInit(Role role) {
		role.setMoneyExchangeTimeStr(TimeUtils.getCurrentTimeStr());
	}

	@Override
	public GeneratedMessage moneyExchange(Role role, boolean add, int num) {
		System.out.println("@@@"+role.getAccount());
		int max = role.getMoneyExchangeNum();
		String today = TimeUtils.getCurrentTimeStr();
		String time = role.getMoneyExchangeTimeStr();
		int addMoney = num;
		double charge = 0;
		if(num % 1000 != 0 || num * 0.001 < 1){
			return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
		}
		
		if(add){			
			charge = num * 0.01;
			if(role.getRandiooMoney() < charge){
				return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
			}
		}else{
			//TODO 每天5W
			
			if(time == null){
				max = num;
			}else{
				time = time.substring(0, 10);				
				if(today.equals(time)){
					if(max + num > 50000){
						return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
					}
					max += num;
				}
				else{					
					if(num > 50000){
						return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
					}
					max = num;
				}
			}
			
			
			charge = num * 0.0085;
			if(role.getMoney() < num){
				return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
			}
			
		}
		if(exchangeMoney(role, charge, add)){
			role.setMoney(role.getMoney()+(add?1:-1)*addMoney);
			if(!add){
				role.setMoneyExchangeTimeStr(today);
				role.setMoneyExchangeNum(max);
				System.out.println("@@@@"+today+"--"+max);
			}
    		return SC.newBuilder().setMoneyExchangeResponse(MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()).setRoleData(loginService.getRoleData(role))).build();
    	}else{
    		return SC.newBuilder().setMoneyExchangeResponse(MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.NO_MONEY.getNumber())).build();
    	}
    	
		
	}
	@Override
	public  boolean exchangeMoney(Role role, double money, boolean add){
		String url = GlobleConfig.String("URL")+"/gateway/MaJiang/changeMoney.php?key=f4f3f65d6d804d138043fbbd1843d510&&id=";
    	String tpram = "";
    	tpram = tpram.concat(role.getAccount());
    	tpram = tpram.concat("&&money_num="+money);
    	tpram = tpram.concat("&&type="+(add?0:1));
    	System.out.println("URL"+url);
    	System.out.println("PRAM"+tpram);
    	HttpConnection conn = new HttpConnection(url,tpram);
    	try {
			conn.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println(conn.result+""+conn.result.getClass().getName()+(conn.result.charAt(0) == '1'));
    	if(conn.result.charAt(0) == '1'){
    		return true;
    	}else{
    		return false;
    	}
	}

	

}
