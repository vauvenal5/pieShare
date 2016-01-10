package org.pieShare.pieShareApp.service.loginService;

import org.apache.commons.io.FileUtils;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieShareApp.service.userService.IUserService;
import org.pieShare.pieTools.piePlate.service.channel.SymmetricEncryptedChannel;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterManagmentServiceException;
import org.pieShare.pieTools.piePlate.service.cluster.exception.ClusterServiceException;
import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;
import org.pieShare.pieTools.pieUtilities.model.PlainTextPassword;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;
import org.pieShare.pieTools.pieUtilities.service.security.encodeService.api.IEncodeService;
import org.pieShare.pieTools.pieUtilities.service.security.pbe.IPasswordEncryptionService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.inject.Provider;
import org.pieShare.pieShareApp.service.database.api.IDatabaseService;

/**
 * Created by richy on 18.11.2015.
 */
public class UserTools {

    private String unsafeFile = "%s_unsafe";
    private String ivFile = "%s_IV";

    private IUserService userService;
    private IPasswordEncryptionService passwordEncryptionService;
    private IEncodeService encodeService;
    private Provider<SymmetricEncryptedChannel> symmetricEncryptedChannelProvider;
    private IClusterManagementService clusterManagementService;
    private IDatabaseService databaseService;
    private boolean useIv;
    private boolean activateAutoLogin;

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public void setPasswordEncryptionService(IPasswordEncryptionService passwordEncryptionService) {
        this.passwordEncryptionService = passwordEncryptionService;
    }

    public void setDatabaseService(IDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void setEncodeService(IEncodeService encodeService) {
        this.encodeService = encodeService;
    }

    public void setSymmetricEncryptedChannelProvider(Provider<SymmetricEncryptedChannel> symmetricEncryptedChannelProvider) {
        this.symmetricEncryptedChannelProvider = symmetricEncryptedChannelProvider;
    }

    public void setClusterManagementService(IClusterManagementService clusterManagementService) {
        this.clusterManagementService = clusterManagementService;
    }

    public UserTools() {
    }

    public boolean createUser(PlainTextPassword pp) {
        EncryptedPassword pwd1 = passwordEncryptionService.encryptPassword(pp);
        pp = null;
        try {
            PieLogger.trace(this.getClass(), "Creating user {}!", userService.getUser().getUserName());
            createNewPwdFile(pwd1, userService.getUser().getPieShareConfiguration().getPwdFile());
            userService.getUser().setHasPasswordFile(true);

            //This is only for Android
            if (userService.getUser().getUserName() != null) {
                databaseService.persistPieUser(userService.getUser());
            }
        } catch (Exception e) {
            PieLogger.error(this.getClass(), String.format("Error creating password file. Message: %s", e.getMessage()));
            return false;
        }
        return true;
    }

    public void setUseIv(boolean useIt) {
        useIv = useIt;
    }

    public boolean Login(PlainTextPassword pp) {
        return Login(pp, null);
    }

    public boolean Login(PlainTextPassword pp, String userName) {
        EncryptedPassword pwd1 = null;

        PieUser user = userService.getUser();

        if (user == null || user.getPieShareConfiguration() == null) {
            return false;
        }

        File pwdFile = user.getPieShareConfiguration().getPwdFile();

        if (pp != null) {
            pwd1 = passwordEncryptionService.encryptPassword(pp);
            pp = null;
        } else {
            try {
                File unencryptedFile = new File(String.format(unsafeFile, user.getPieShareConfiguration().getPwdFile().getCanonicalPath()));
                if (!unencryptedFile.exists()) {
                    return false;
                }
                pwd1 = passwordEncryptionService.getEncryptedPasswordFromExistingSecretKey(FileUtils.readFileToByteArray(unencryptedFile));

                File ivF = new File(user.getPieShareConfiguration().getPwdFile().getParent(), ivFile);
                if (ivF.exists()) {
                    pwd1.setIv(FileUtils.readFileToByteArray(ivF));
                }
            } catch (IOException e) {
                PieLogger.error(this.getClass(), String.format("error during auto login! Message: %s", e.getMessage()));
                return false;
            }
        }
        pwd1.setUseIv(useIv);

        if (pwdFile.exists()) {
            try {
                if (!Arrays.equals(encodeService.decrypt(pwd1, FileUtils.readFileToByteArray(pwdFile)), pwd1.getPassword())) {
                    return false; //throw new WrongPasswordException("The given password was wrong.");
                }

                File unencryptedFile = new File(String.format(unsafeFile, user.getPieShareConfiguration().getPwdFile().getCanonicalPath()));
                FileUtils.writeByteArrayToFile(unencryptedFile, pwd1.getPassword());

                if (pwd1.getIv() != null) {
                    FileUtils.writeByteArrayToFile(new File(userService.getUser().getPieShareConfiguration().getPwdFile().getParent(), ivFile), pwd1.getIv());
                }

            } catch (Exception ex) {
                PieLogger.info(this.getClass(), String.format("Wrong password, not possible to encrypt file! Messgae %s", ex.getMessage()));
                return false;
            }
        } else {
            PieLogger.info(this.getClass(), "Tried to login but no passwordFile was avaliable!");
            return false;
        }

        if (userService.getUser().getUserName() == null && userName != null) {
            userService.getUser().setUserName(userName);
        }

        databaseService.mergePieUser(userService.getUser());

        user.setPassword(pwd1);
        user.setHasPasswordFile(true);
        user.setIsLoggedIn(true);

        SymmetricEncryptedChannel channel = this.symmetricEncryptedChannelProvider.get();
        channel.setChannelId(user.getUserName());
        channel.setEncPwd(user.getPassword());

        try {
            this.clusterManagementService.registerChannel(user.getCloudName(), channel);

        } catch (ClusterManagmentServiceException e) {
            PieLogger.error(this.getClass(), String.format("Error in Register Channel. Message:  %s", e.getMessage()));
        }

        PieLogger.info(this.getClass(), "Login Successful");
        return true;
    }

    public boolean logout() {
        PieUser user = userService.getUser();
        user.setPassword(null);
        user.setIsLoggedIn(false);
        try {
            File unencryptedFile = new File(String.format(unsafeFile, user.getPieShareConfiguration().getPwdFile().getCanonicalPath()));
            unencryptedFile.delete();

            File ivF = new File(user.getPieShareConfiguration().getPwdFile().getParent(), ivFile);
            if (ivF.exists()) {
                ivF.delete();
            }

           this.clusterManagementService.disconnect(user.getCloudName());
        } catch (IOException e) {
            PieLogger.error(this.getClass(), String.format("Error in Logout. Message: %s", e.getMessage()));
            return false;
        } catch (ClusterServiceException e) {
            PieLogger.error(this.getClass(), String.format("Error in Logout. Message: %s", e.getMessage()));
            return false;
        }
        PieLogger.info(this.getClass(), "Logout Successful!");
        return true;
    }

    public void resetPassword() {
        logout();
        PieUser user = userService.getUser();
        user.getPieShareConfiguration().getPwdFile().delete();
        databaseService.removePieUser(user);
        user.setHasPasswordFile(false);
        PieLogger.info(this.getClass(), "Reset Password Successful!");
    }

    private void createNewPwdFile(EncryptedPassword passwordForEncoding, File pwdFile) throws Exception {
        if (pwdFile.exists()) {
            pwdFile.delete();
        }

        byte[] encr = encodeService.encrypt(passwordForEncoding, passwordForEncoding.getPassword());
        FileUtils.writeByteArrayToFile(pwdFile, encr);
    }
}
