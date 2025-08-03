package timofeyqa.rococo.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import timofeyqa.rococo.data.CountryEntity;

import java.util.List;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

    List<CountryEntity> findAll();
}
