package com.asuc.asucmobile.infrastructure.transformers;

import com.asuc.asucmobile.infrastructure.models.Resource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ResourceTransformer {

    /**
     * Transform Firebase QuerySnapshot to resource model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.Resource> resourceQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.Resource> resources = new ArrayList<>();
        Resource infraResource = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            infraResource = documentSnapshot.toObject(Resource.class);
            if (infraResource != null) {
                resources.add(resourceInfraDomainTransformer(infraResource));
            }
        }
        return resources;
    }


    /**
     * Transform single resource from infrastructure to model
     * @param resource
     * @return
     */
    public com.asuc.asucmobile.domain.models.Resource resourceInfraDomainTransformer(Resource resource) {
        return com.asuc.asucmobile.domain.models.Resource.builder()
                .resource(resource.getName())
                .description(resource.getDescription())
                .build();
    }

}
