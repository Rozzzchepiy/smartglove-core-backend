package rozchepiy.dev.smartglovecorebackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;

import java.util.List;

@Repository
public interface GestureModelRepository  extends MongoRepository<GestureModel,String> {
    List<GestureModel> findAllByUserId(String userId);

    List<GestureModel> findAllByIsDefaultTrue();
}
