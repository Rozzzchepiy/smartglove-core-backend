package rozchepiy.dev.smartglovecorebackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rozchepiy.dev.smartglovecorebackend.model.GestureData;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;

import java.util.List;

@Repository
public interface GestureDataRepository extends MongoRepository<GestureData,String> {
    List<GestureData> findAllByModelId(String modelId);
}
