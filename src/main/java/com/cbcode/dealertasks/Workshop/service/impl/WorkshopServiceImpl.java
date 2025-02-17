package com.cbcode.dealertasks.Workshop.service.impl;

import com.cbcode.dealertasks.ExceptionsConfig.NotAuthorizedAccessException;
import com.cbcode.dealertasks.ExceptionsConfig.ResourceNotFoundException;
import com.cbcode.dealertasks.Workshop.model.DTOs.WorkshopDto;
import com.cbcode.dealertasks.Workshop.model.Enums.WorkshopStatusEnum;
import com.cbcode.dealertasks.Workshop.model.Workshop;
import com.cbcode.dealertasks.Workshop.repository.WorkshopRepository;
import com.cbcode.dealertasks.Workshop.service.WorkshopService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WorkshopServiceImpl implements WorkshopService {

    private static final Logger logger = LoggerFactory.getLogger(WorkshopServiceImpl.class);

    private final WorkshopRepository workshopRepository;
    private final ModelMapper modelMapper;

    public WorkshopServiceImpl(WorkshopRepository workshopRepository, ModelMapper modelMapper) {
        this.workshopRepository = workshopRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * @param id - workshop id to update
     * @param workshopStatus - new status to update workshop to
     *                      (PENDING, IN_PROGRESS, DONE)
     *                       PENDING - default status
     *                       IN_PROGRESS - workshop is in progress
     *                       DONE - workshop is completed
     * @return - updated workshop dto with new status
     */
    @Override
    public WorkshopDto updateWorkshopStatus(Long id, String workshopStatus) {
        logger.info("Updating workshop status for workshop with id: {}", id);

        // Check if user is authenticated
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            logger.error("User not authenticated");
            throw new NotAuthorizedAccessException("User not authenticated");
        }
        var principal = authentication.getPrincipal();

        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Workshop not found with id: {}", id);
                    return new ResourceNotFoundException("Workshop not found with id: " + id);
                });
        try {
            WorkshopStatusEnum status = WorkshopStatusEnum.valueOf(workshopStatus);

            if (workshop.getWorkshopStatusEnum().equals(status)) {
                logger.error("Workshop status is already set to: {}", status);
                return modelMapper.map(workshop, WorkshopDto.class);
            }

            logger.info("User '{}' updating workshop status for workshop with id: {} from {} to {}", principal.toString(), id, workshop.getWorkshopStatusEnum(), status);
            workshop.setComments(workshop.getComments());
            workshop.setWorkshopStatusEnum(status);
            workshopRepository.save(workshop);

            logger.info("Workshop status updated successfully");
            return modelMapper.map(workshop, WorkshopDto.class);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid workshop status: {}", workshopStatus);
            throw new IllegalArgumentException("Invalid workshop status: " + workshopStatus);
        }
    }
}
