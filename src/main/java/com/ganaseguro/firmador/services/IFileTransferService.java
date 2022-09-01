package com.ganaseguro.firmador.services;

public interface IFileTransferService {
    boolean uploadFile(String localFilePath, String remoteFilePath);
    boolean downloadFile(String localFilePath, String remoteFilePath);
}
