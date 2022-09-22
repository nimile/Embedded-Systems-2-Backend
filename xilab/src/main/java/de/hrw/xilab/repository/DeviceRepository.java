package de.hrw.xilab.repository;

import de.hrw.xilab.model.wrapper.DeviceWrapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends CrudRepository<DeviceWrapper, Integer> {
    List<DeviceWrapper> findAll();

    Optional<DeviceWrapper> findByUuid(String uuid);
}
