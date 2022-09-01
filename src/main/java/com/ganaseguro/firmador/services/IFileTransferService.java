package com.ganaseguro.firmador.services;

import com.ganaseguro.firmador.dto.ResponseDto;

public interface IFileTransferService {
    boolean uploadFile(String localFilePath, String remoteFilePath);
    ResponseDto downloadFile(String localFilePath, String remoteFilePath);
}
