package com.cbcode.dealertasks.Workshop.service;


import com.cbcode.dealertasks.Workshop.model.DTOs.WorkshopDto;
import com.cbcode.dealertasks.Workshop.model.Enums.WorkshopStatusEnum;

public interface WorkshopService {
    WorkshopDto updateWorkshopStatus(Long id, String workshopStatus);
}
