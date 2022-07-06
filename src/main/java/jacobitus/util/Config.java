/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jacobitus.util;

//import bo.firmadigital.jacobitus4.VariablesSession;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

/**
 *
 * @author ADSIB
 */


public class Config {
    protected Properties options;
    protected File user;
    protected File fileOptions;
    protected File token;

    public Config() {
        try {



            options = new Properties();
            user = new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "Jacobitus/AQUISPE");
            fileOptions = new File(user, "jacobitus.properties");
            if (user.exists()) {
                if (fileOptions.exists()) {
                    options.load(new FileInputStream(fileOptions));
                }
                token = new File(user, "softoken.p12");
                if (!token.exists()) {
                    token = null;
                }
            } else {
                token = null;
            }
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo obtener las opciones.");
        }
    }

    public boolean isProxyEnabled() {
        String proxy = options.getProperty("proxy");
        return proxy != null && proxy.equals("true");
    }

    public void setProxyEnabled(boolean proxy) {
        if (proxy) {
            options.setProperty("proxy", "true");
        } else {
            options.setProperty("proxy", "false");
        }
    }

    public String getProxyIP() {
        if (isProxyEnabled()) {
            return options.getProperty("proxyIP");
        } else {
            return "Ninguna";
        }
    }

    public void setProxyIP(String ip) {
        options.setProperty("proxyIP", ip);
    }

    public String getProxyPort() {
        if (isProxyEnabled()) {
            return options.getProperty("proxyPort");
        } else {
            return "3128";
        }
    }

    public void setProxyPort(String port) {
        options.setProperty("proxyPort", port);
    }

    public boolean isSecondaryPortEnabled() {
        String proxy = options.getProperty("secondaryPort");
        return proxy != null && proxy.equals("true");
    }

    public void setSecondaryPortEnabled(boolean secondaryPort) {
        if (secondaryPort) {
            options.setProperty("secondaryPort", "true");
        } else {
            options.setProperty("secondaryPort", "false");
        }
    }

    public boolean isTertiaryPortEnabled() {
        String proxy = options.getProperty("tertiaryPort");
        return proxy != null && proxy.equals("true");
    }

    public void setTertiaryPortEnabled(boolean tertiaryPort) {
        if (tertiaryPort) {
            options.setProperty("tertiaryPort", "true");
        } else {
            options.setProperty("tertiaryPort", "false");
        }
    }

    public File getToken() {
        return token;
    }

    public String getTokenToCreate() {
        if (!user.exists()) {
            if (!user.mkdir()) {
                throw new RuntimeException("No se pudo crear el directorio " + user);
            }
        }
        token = new File(user, "softoken.p12");
        return token.getPath();
    }

    public File getDriver() {
        String driver = options.getProperty("driver");
        if (driver != null) {
            File file = new File(driver);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    public void setDriver(File file) {
        if (file == null) {
            options.remove("driver");
        } else {
            options.setProperty("driver", file.getPath());
        }
    }

    public String getHsmCloud() {
        if (options.containsKey("hsmCloud")) {
            return options.getProperty("hsmCloud");
        } else {
            return "https://desarrollo.adsib.gob.bo/cloud_hsm/services/api/v1/hsm";
        }
    }

    public void setHsmCloud(String url) {
        if (url == null) {
            options.remove("hsmCloud");
        } else {
            options.setProperty("hsmCloud", url);
        }
    }

    public String getHsmJWT() {
        if (options.containsKey("hsmJWT")) {
            return options.getProperty("hsmJWT");
        } else {
            return null;
        }
    }

    public void setHsmJWT(String jwt) {
        if (jwt == null || jwt.trim().equals("")) {
            options.remove("hsmJWT");
        } else {
            options.setProperty("hsmJWT", jwt);
        }
    }

    public String getHsmType() {
        if (options.containsKey("hsmType")) {
            return options.getProperty("hsmType");
        } else {
            return "HSM";
        }
    }

    public void setHsmType(String type) {
        if (type == null) {
            options.remove("hsmType");
        } else {
            options.setProperty("hsmType", type);
        }
    }

    public void save() {
        try {
            if (!user.exists()) {
                if (!user.mkdir()) {
                    throw new RuntimeException("No se pudo crear el directorio " + user);
                }
            }
            options.store(new FileWriter(fileOptions), "ADSIB - Jacobitus options");
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public File getConversorFile() {
        if (!user.exists()) {
            if (!user.mkdir()) {
                throw new RuntimeException("No se pudo crear el directorio " + user);
            }
        }
        return new File(user, "ConversorPdf.jar");
    }

    public URLClassLoader getConversor() {
        File jar = new File(user, "ConversorPdf.jar");
        if (jar.exists()) {
            try {
                URLClassLoader child = new URLClassLoader(
                        new URL[] {jar.toURI().toURL()},
                        this.getClass().getClassLoader()
                );
                return child;
            } catch (MalformedURLException ex) {
                return null;
            }
        }
        return null;
    }
}
