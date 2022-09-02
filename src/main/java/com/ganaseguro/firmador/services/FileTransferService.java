package com.ganaseguro.firmador.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.ganaseguro.firmador.dto.ResponseDto;
import com.ganaseguro.firmador.utils.constantes.ConstDiccionarioMensajeFirma;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.springframework.stereotype.Service;

@Service
public class FileTransferService implements IFileTransferService {

    private Logger logger = LoggerFactory.getLogger(FileTransferService.class);

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private Integer port;

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.sessionTimeout}")
    private Integer sessionTimeout;

    @Value("${sftp.channelTimeout}")
    private Integer channelTimeout;

    @Override
    public boolean uploadFile(String localFilePath, String remoteFilePath) {
        ChannelSftp channelSftp = createChannelSftp();
        try {
            channelSftp.put(localFilePath, remoteFilePath);
            return true;
        } catch(SftpException ex) {
            logger.error("Error upload file", ex);
        } finally {
            disconnectChannelSftp(channelSftp);
        }

        return false;
    }

    @Override
    public ResponseDto downloadFile( String localFilePath, String remoteFilePath) {
        ResponseDto resp = new ResponseDto();
        ChannelSftp channelSftp = createChannelSftp();
        OutputStream outputStream;
        try {
            //File file = new File(localFilePath);
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("softoken_tem").getFile() + "/softoken.p12");


            outputStream = new FileOutputStream(file);
            channelSftp.get(remoteFilePath, outputStream);
            file.createNewFile();
            resp.setCodigo(ConstDiccionarioMensajeFirma.COD1000);
            return resp;
        } catch(SftpException | IOException ex) {
            resp.setCodigo(ConstDiccionarioMensajeFirma.COD2000);
            resp.setMensaje(ConstDiccionarioMensajeFirma.COD2000_MENSAJE+" : "+ex);
            return resp;
        }catch (Exception ex){
            resp.setCodigo(ConstDiccionarioMensajeFirma.COD2000);
            resp.setMensaje(ConstDiccionarioMensajeFirma.COD2000_MENSAJE+" : "+ex);
            return resp;
        }
        finally {
            disconnectChannelSftp(channelSftp);
        }

    }


    private ChannelSftp createChannelSftp() {
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect(sessionTimeout);
            Channel channel = session.openChannel("sftp");
            channel.connect(channelTimeout);
            return (ChannelSftp) channel;
        } catch(JSchException ex) {
            logger.error("Create ChannelSftp error", ex);
        }

        return null;
    }

    private void disconnectChannelSftp(ChannelSftp channelSftp) {
        try {
            if( channelSftp == null)
                return;

            if(channelSftp.isConnected())
                channelSftp.disconnect();

            if(channelSftp.getSession() != null)
                channelSftp.getSession().disconnect();

        } catch(Exception ex) {
            logger.error("SFTP disconnect error", ex);
        }
    }
}
