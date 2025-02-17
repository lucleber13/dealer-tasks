package com.cbcode.dealertasks.Valet.service;

import com.cbcode.dealertasks.Valet.model.DTOs.ValetDto;

public interface ValetService {

    ValetDto updateValetStatus(Long id, String valetStatus);
}
