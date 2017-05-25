package com.randioo.doudizhu_server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.randioo.doudizhu_server.entity.bo.Role;
import com.randioo.randioo_server_base.annotation.MyBatisGameDaoAnnotation;
import com.randioo.randioo_server_base.db.BaseDao;

@MyBatisGameDaoAnnotation
public interface RoleDao extends BaseDao<Role> {
	Role get(@Param("account") String account, @Param("roleId") int id);

	List<String> getAllAccounts();

	List<String> getAllNames();
	
	String getMoneyExchangeTime(@Param("account") String account, @Param("roleId") int id);
	
	Integer moneyExchangeNum(@Param("account") String account, @Param("roleId") int id);

	public Integer getMaxRoleId();
}
