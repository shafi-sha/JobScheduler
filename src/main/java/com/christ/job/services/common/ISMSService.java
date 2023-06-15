package com.christ.job.services.common;

import com.christ.job.services.dto.common.ErpSmsDTO;

import java.util.List;

public interface ISMSService {

    public ErpSmsDTO sendMessage();

    public List<ErpSmsDTO> sendMessageList();
}
