package rozchepiy.dev.smartglovecorebackend.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rozchepiy.dev.smartglovecorebackend.dto.request.GestureSummaryDto;
import rozchepiy.dev.smartglovecorebackend.model.GestureData;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;

import java.util.List;

@Repository
public interface GestureDataRepository extends MongoRepository<GestureData,String> {
    List<GestureData> findAllByModelId(String modelId);
    @Aggregation(pipeline = {
            "{ '$match': { 'modelId': ?0 } }",
            "{ '$group': { '_id': '$label', 'count': { '$sum': 1 } } }",
            "{ '$project': { 'label': '$_id', 'count': 1, '_id': 0 } }"
    })
    List<GestureSummaryDto> getGestureSummariesByModelId(String modelId);
}
