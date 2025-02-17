package com.cbcode.dealertasks.Valet.service.impl;

import com.cbcode.dealertasks.ExceptionsConfig.NotAuthorizedAccessException;
import com.cbcode.dealertasks.ExceptionsConfig.ResourceNotFoundException;
import com.cbcode.dealertasks.Valet.model.DTOs.ValetDto;
import com.cbcode.dealertasks.Valet.model.Enums.ValetStatus;
import com.cbcode.dealertasks.Valet.model.Valet;
import com.cbcode.dealertasks.Valet.repository.ValetRepository;
import com.cbcode.dealertasks.Valet.service.ValetService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ValetServiceImpl implements ValetService {

    private static final Logger logger = LoggerFactory.getLogger(ValetServiceImpl.class);

    private final ValetRepository valetRepository;
    private final ModelMapper modelMapper;

    public ValetServiceImpl(ValetRepository valetRepository, ModelMapper modelMapper) {
        this.valetRepository = valetRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * @param id - valet id to update status for valet with id
     * @param valetStatus - new status to update valet to
     *                    (PENDING, IN_PROGRESS, DONE)
     *                    PENDING - default status
     *                    IN_PROGRESS - valet is in progress
     *                    DONE - valet is completed
     * @return - updated valet dto with new status
     */
    @Override
    public ValetDto updateValetStatus(Long id, String valetStatus) {
        logger.info("Updating valet status for valet with id: {}", id);

        // Check if user is authenticated
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            logger.error("User not authenticated");
            throw new NotAuthorizedAccessException("User not authenticated");
        }
        var principal = authentication.getPrincipal();

        Valet valet = valetRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Valet not found with id: {}", id);
                    return new ResourceNotFoundException("Valet not found with id: " + id);
                });
        try {
            ValetStatus status = ValetStatus.valueOf(valetStatus);

            if (valet.getStatus().equals(status)) {
                logger.error("Valet status is already set to: {}", status);
                return modelMapper.map(valet, ValetDto.class);
            }

            logger.info("User '{}' updating valet status for valet with id: {} from {} to {}", principal.toString(), id, valet.getValetEnum(), status);
            valet.setComments(valet.getComments());
            valet.setStatus(status);
            valetRepository.save(valet);

            logger.info("Valet status updated successfully");
            return modelMapper.map(valet, ValetDto.class);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid valet status: {}", valetStatus);
            throw new IllegalArgumentException("Invalid valet status: " + valetStatus);
        }
    }
}
