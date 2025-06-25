package rice_monkey.Image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rice_monkey.Image.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {}

