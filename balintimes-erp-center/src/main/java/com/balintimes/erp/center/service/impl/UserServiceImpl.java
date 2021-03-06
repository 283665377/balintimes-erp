package com.balintimes.erp.center.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.authc.credential.PasswordService;
import org.springframework.stereotype.Service;

import com.balintimes.erp.center.annotation.CustomerTransactional;
import com.balintimes.erp.center.dao.UserDao;
import com.balintimes.erp.center.model.User;
import com.balintimes.erp.center.service.UserService;
import com.balintimes.erp.center.tuples.TuplePage;

@Service("userService")
public class UserServiceImpl implements UserService {
	@Resource
	private PasswordService passwordService;
	@Resource
	private UserDao userDao;

	@CustomerTransactional
	public boolean create(User user) {
		user.setPassword(passwordService.encryptPassword(user.getPassword()));

		String postUidAry[] = user.getPostuid().split(",");
		String postNameAry[] = user.getPostname().split(",");
		User tempUser = new User();
		tempUser = user;
		tempUser.setPostuid("");
		tempUser.setPostname("");
		boolean isCreateSuccess = userDao.createUser(tempUser);

		for (int k = 0; k < postUidAry.length; k++) {
			User itemUser = new User();
			itemUser = user;
			itemUser.setPostuid(postUidAry[k]);
			itemUser.setPostname(postNameAry[k]);
			userDao.createUserPost(itemUser);
		}
		return isCreateSuccess;
	}

	public User getUser(String uid) {
		return userDao.getUser(uid);
	}

	public void throwEx() {
		throw new RuntimeException("alex xie com.balintimes.erp.center.exception!");
	}

	public List<User> GetUserList() {
		return userDao.GetUserList();
	}

	public boolean ExistsUserName(String username) {
		return userDao.ExistsUserName(username);
	}

	public boolean ExistsUserName(String username, String uid) {
		return userDao.ExistsUserName(username, uid);
	}

	@CustomerTransactional
	public void updateUser(User user) {
//		String postUidAry[] = user.getPostuid().split(",");
//		String postNameAry[] = user.getPostname().split(",");
//
//		for (int k = 0; k < postUidAry.length; k++) {
//			User itemUser = new User();
//			itemUser = user;
//			itemUser.setPostuid(postUidAry[k]);
//			itemUser.setPostname(postNameAry[k]);
//			this.userDao.updateUser(itemUser);
//		}
		this.userDao.updateUser(user);
	}

	@CustomerTransactional
	public void deleteUser(String uid, String employeename) {
		this.userDao.deleteUser(uid, employeename);
	}

	public String getUserPassword(String username) {
		return this.userDao.getUserPassword(username);
	}

	public User getUserByName(String username) {
		return this.userDao.getUserByName(username);
	}

	// public TuplePage<List<User>, Integer> GetUserList(String username, String
	// deptname, int page, int pageSize) {
	// return this.userDao.GetUserList(username, deptname, page, pageSize);
	// }
	public TuplePage<List<User>, Integer> GetUserList(String username,
			String employeename, String usertype, String isenable, int page,
			int pageSize) {
		return this.userDao.GetUserList(username, employeename, usertype,
				isenable, page, pageSize);
	}

	@CustomerTransactional
	public void UpdatePassword(String uID, String password) {
		this.userDao.UpdatePassword(uID,
				passwordService.encryptPassword(password));

	}

	@CustomerTransactional
	public String UpdatePassword(String uid, String oldpassword,
			String newpassword) throws Exception {

		User user = this.getUser(uid);
		String dbpassword = user.getPassword();
		oldpassword = this.passwordService.encryptPassword(oldpassword);
		if (dbpassword.equalsIgnoreCase(oldpassword) == false) {
			throw new Exception("用户密码录入错误！");

		}
		this.UpdatePassword(uid, newpassword);

		return user.getUsername();
	}

	@CustomerTransactional
	public void UpdateLastLogin(String username) {
		this.userDao.UpdateLastLogin(username);
	}

	//
	public User InitWebUserByName(String username) {
		return this.userDao.getUserByName(username);
	}

	public List<User> GetUserTreeList() {
		List<User> listUsers = userDao.GetUserTreeList();
		return listUsers;
	}

	public List<User> GetUserTreeSet(String employeeName) {
		List<User> listUsersSet = userDao.GetUserTreeSet(employeeName);
		return listUsersSet;
	}

	public User GetOneUser(String uid) {
		User oneUser = userDao.GetOneUser(uid);
		return oneUser;
	}

	public User GetOneUserParent(String partentuid) {
		User oneUserParent = userDao.GetOneUserParent(partentuid);
		return oneUserParent;
	}

	public boolean ExistsEmployeeName(String employeename) {
		return userDao.ExistsEmployeeName(employeename);
	}

	public List<User> GetUserByEmpName(String empName) {
		// TODO Auto-generated method stub
		return this.userDao.GetUserByEmpName(empName);
	}

	public List<User> GetUserTreeListByCondition(String username,
			String employeename, String postuid, String organizationuid) {
		// TODO Auto-generated method stub
		return userDao.GetUserTreeListByCondition(username, employeename,
				postuid, organizationuid);
	}

	@CustomerTransactional
	public void UpdateHeadByUser(String uid, String url) {
		// TODO Auto-generated method stub
		this.userDao.UpdateHeadByUser(uid, url);
	}

	
	public User ExistsUserUid(String useruid) {
		// TODO Auto-generated method stub
		return this.userDao.ExistsUserUid(useruid);
	}
	
	@CustomerTransactional
	public void CreateUserPost(User user) {		
		this.userDao.createUserPost(user);
	}
	
	@CustomerTransactional
	public void DeleteUserPost(String useruid) {
		this.userDao.deleteUserPost(useruid);		
	}

	
	public List<User> getUserParent(String parentuid) {		
		return this.userDao.getUserParent(parentuid);
	}
}
