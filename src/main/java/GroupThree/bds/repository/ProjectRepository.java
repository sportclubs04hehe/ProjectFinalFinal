package GroupThree.bds.repository;

import GroupThree.bds.entity.ProjectStatus;
import GroupThree.bds.entity.Projects;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Projects,Long> {
    @Query("SELECT DISTINCT p FROM Projects p JOIN p.propertyListings pl WHERE pl.district = :district AND pl.province = :province AND pl.price BETWEEN :minPrice AND :maxPrice AND p.projectStatus = :projectStatus")
    List<Projects> findProjectsByLocationAndPriceRangeAndStatus(
            @Param("district") String district,
            @Param("province") String province,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("projectStatus") ProjectStatus projectStatus);

    @NonNull
    @Override
    Page<Projects> findAll(@NonNull Pageable pageable);
    Page<Projects> findProjectsById (Long id, Pageable pageable);

}
