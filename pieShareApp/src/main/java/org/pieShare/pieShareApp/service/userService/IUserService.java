package org.pieShare.pieShareApp.service.userService;

import org.pieShare.pieShareApp.model.PieUser;

/**
 * This class is responsible for managing the logged in user.
 */
public interface IUserService {
	/**
	 * Returns the logged in user.
	 * @return the logged in user.
	 */
	PieUser getUser();
	
	/**
	 * Sets the logged in user.
	 * @param user the logged in user.
	 */
	void setUser(PieUser user);
}
