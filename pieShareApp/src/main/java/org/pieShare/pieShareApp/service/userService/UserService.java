package org.pieShare.pieShareApp.service.userService;

import org.pieShare.pieShareApp.model.PieUser;

public class UserService implements IUserService {
	private PieUser user;
	
	public void setUser(PieUser user) {
		this.user = user;
	}
	
	@Override
	public PieUser getUser() {
		return user;
	}
}
